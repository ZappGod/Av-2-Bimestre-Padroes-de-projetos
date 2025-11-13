package Questao3;

import Questao3.estado.EstadoUsina;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


// Contexto principal (State context) da usina.

// Responsabilidades:
// - Mantém o estado atual e histórico
// - Fornece APIs para trocar estado (setEstado) e para colocar em manutenção
// - Contém helpers para prevenção de ciclos e marcação de início de condições (timers lógicos)
// Decisões de design documentadas:

// - State Pattern: encapsula comportamentos por estado (evita if/else espalhado)
// - Histórico de transições (lista de pares <nome, timestamp>) para prevenir ciclos perigosos
// - Flags como 'visitouAlertaVermelho' para impor pré-condições de transição (EMERGENCIA somente após VERMELHO)
// - SRP: Usina trata orquestração; validações específicas ficam nos estados.
// Notas de thread-safety: métodos públicos sincronizados para simples proteção; em produção, design mais fino seria necessário.

public class UsinaNuclear {

    private EstadoUsina estado;
    private EstadoUsina estadoAnteriorAntesManutencao; // para restaurar pós-manutenção
    private final Deque<Map.Entry<String, Instant>> historico; // histórico de transições (nome, instante)
    private final Map<String, Instant> inicioCondicoes; // marca quando uma condição iniciou (ex: AMARELO)
    private boolean visitouAlertaVermelho; // regra: só permite EMERGENCIA se já passou por VERMELHO

    // parâmetros de prevenção de ciclo:
    private static final Duration WINDOW_CICLO = Duration.ofSeconds(120); // janela para detectar ciclo
    private static final int MAX_HISTORY_CHECK = 6;

    public UsinaNuclear(EstadoUsina estadoInicial) {
        this.estado = estadoInicial;
        this.historico = new ArrayDeque<>();
        this.inicioCondicoes = new HashMap<>();
        this.visitouAlertaVermelho = false;
        Instant now = Instant.now();
        registrarHistorico(estado.nome(), now);
        this.estado.enter(this, new SensorData(0,0,0,true, now));
    }

    // Processa leitura de sensores (entrada principal do sistema).
    
    public synchronized void onSensorUpdate(SensorData data) {
        log("Recebendo sensor: " + data);
        // se estivermos em manutenção, o estado será ManutencaoState e irá ignorar transições automáticas
        estado.onSensorUpdate(this, data);
    }
   
    // Troca o estado atual - centraliza logging e histórico.
    
    public synchronized void setEstado(EstadoUsina novoEstado, SensorData data) {
        if (novoEstado == null) throw new IllegalArgumentException("estado nulo");

        // Prevenção fundamental: se já estamos em EMERGENCIA e tentam setar outro estado automaticamente, bloqueamos
        if (this.estado != null && "EMERGENCIA".equals(this.estado.nome()) && !"MANUTENCAO".equals(novoEstado.nome())) {
            log("Estamos em EMERGENCIA. Somente procedimento manual pode alterar o estado.");
            return;
        }

        // evita transição para o mesmo estado (redundante)
        if (this.estado != null && this.estado.nome().equals(novoEstado.nome())) {
            log("Solicitação de transição para mesmo estado: ignorando.");
            return;
        }

        // sair do estado atual
        if (this.estado != null) {
            this.estado.exit(this);
        }

        // entrar no novo estado
        EstadoUsina antigo = this.estado;
        this.estado = novoEstado;

        registrarHistorico(novoEstado.nome(), data.getTimestamp());

        novoEstado.enter(this, data);
    }

    // Coloca a usina em modo manutenção (override). Guarda o estado atual e entra em ManutencaoState.
    
    public synchronized void enableManutencao(String motivo, SensorData data) {
        if ("MANUTENCAO".equals(estado.nome())) {
            log("Já em manutenção.");
            return;
        }
        log("Ativando manutenção: " + motivo);
        this.estadoAnteriorAntesManutencao = this.estado;
        setEstado(new Questao3.estado.ManutencaoState(motivo), data);
    }

    // Sai do modo manutenção e restaura o estado anterior salvo.
    
    public synchronized void disableManutencao(SensorData data) {
        if (estadoAnteriorAntesManutencao == null) {
            log("Nenhum estado anterior para restaurar.");
            return;
        }
        log("Desativando manutenção e restaurando estado anterior: " + estadoAnteriorAntesManutencao.nome());
        setEstado(estadoAnteriorAntesManutencao, data);
        estadoAnteriorAntesManutencao = null;
    }

    // Procedimento manual para resetar emergência (apenas exemplo).
    
    public synchronized void resetFromEmergency(SensorData data) {
        if (!"EMERGENCIA".equals(estado.nome())) {
            log("Não estamos em emergência; reset não aplicável.");
            return;
        }
        // Em produção haveria muitos checks, procedimentos, logs de auditoria, etc.
        log("Procedimento manual: reset de EMERGENCIA para DESLIGADA.");
        setEstado(new Questao3.estado.DesligadaState(), data);
        // limpar flag de visita a vermelho
        this.visitouAlertaVermelho = false;
    }

    // Marca o início de uma condição (ex: AMARELO, VERMELHO)
    
    public synchronized void marcarInicioCondicao(String chave, Instant instante) {
        inicioCondicoes.put(chave, instante);
    }

    public synchronized Instant getInicioCondicao(String chave) {
        return inicioCondicoes.get(chave);
    }

    public synchronized void limparTemposDeCondicao() {
        inicioCondicoes.clear();
    }

    // Registra histórico de transições (nome, instante).
    
    private synchronized void registrarHistorico(String nome, Instant instante) {
        historico.addFirst(new AbstractMap.SimpleEntry<>(nome, instante));
        // limitamos o tamanho
        while (historico.size() > 50) {
            historico.removeLast();
        }
        log("Histórico atualizado: " + nome + " @ " + instante);
    }

    // Evita transições circulares perigosas.
    // Estratégia simples:
    // - Detecta padrões A->B->A dentro de uma janela de tempo (WINDOW_CICLO) e bloqueia a nova transição A.
    // - Isso previne "flapping" rápido entre estados críticos.
    // Em sistemas reais, a política pode ser mais elaborada (contagens, cool-downs, etc.)

    public synchronized boolean preventirCicloPerigoso(String targetStateName, Instant now) {
        // coleta um prefixo do histórico
        List<Map.Entry<String, Instant>> lista = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Instant> e : historico) {
            lista.add(e);
            count++;
            if (count >= MAX_HISTORY_CHECK) break;
        }

        // procura padrão X -> Y -> X (onde X == targetStateName)
        // i.e., se recentemente tivemos target, depois outro, e agora tentamos voltar ao target muito rápido
        for (int i = 0; i + 2 < lista.size(); i++) {
            String a = lista.get(i).getKey();
            String b = lista.get(i + 1).getKey();
            String c = lista.get(i + 2).getKey();
            Instant timeA = lista.get(i).getValue();
            Instant timeC = lista.get(i + 2).getValue();

            if (a.equals(targetStateName) && !a.equals(b) && c.equals(a)) {
                // encontramos uma repetição A B A; avaliar janela de tempo entre a e c
                Duration delta = Duration.between(timeC, now).abs();
                if (delta.compareTo(WINDOW_CICLO) <= 0) {
                    log("Detectado possível ciclo perigoso (" + a + "->" + b + "->" + c + ") dentro de janela " + WINDOW_CICLO.getSeconds() + "s");
                    return true;
                }
            }
        }

        // nenhum ciclo detectado
        return false;
    }

    // Marca que a usina passou por ALERTA_VERMELHO.
    
    public synchronized void setVisitouAlertaVermelho(boolean visitou) {
        this.visitouAlertaVermelho = visitou;
    }

    public synchronized boolean podeIrParaEmergencia() {
        return visitouAlertaVermelho;
    }

    public synchronized String getEstadoNome() {
        return estado.nome();
    }

    // Logging centralizado (em produção usar framework de logs).
    
    public synchronized void log(String msg) {
        System.out.println("[" + Instant.now() + "] [USINA] [" + getEstadoNomeSafe() + "] " + msg);
    }

    private synchronized String getEstadoNomeSafe() {
        return estado == null ? "NULL" : estado.nome();
    }
}