package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;

import java.time.Duration;
import java.time.Instant;

// Estado ALERTA_AMARELO.
// Regra: ALERTA_AMARELO -> ALERTA_VERMELHO se temperatura > 400°C por mais de 30 segundos.
// Pode retornar para OPERACAO_NORMAL se temperatura baixar.

public class AlertaAmareloState implements EstadoUsina {

    private static final Duration LIMIAR_PARA_VERMELHO = Duration.ofSeconds(30);

    @Override
    public void enter(UsinaNuclear usina, SensorData data) {
        usina.log("Entrou em ALERTA_AMARELO");
        // marca o início da condição amarela (se ainda não marcado)
        if (usina.getInicioCondicao("AMARELO") == null) {
            usina.marcarInicioCondicao("AMARELO", data.getTimestamp());
        }
    }

    @Override
    public void onSensorUpdate(UsinaNuclear usina, SensorData data) {
        double temp = data.getTemperaturaC();

        if (temp <= 300.0) {
            usina.log("Temperatura voltou abaixo de 300°C -> retornando a OPERACAO_NORMAL");
            if (!usina.preventirCicloPerigoso("OPERACAO_NORMAL", data.getTimestamp())) {
                usina.setEstado(new OperacaoNormalState(), data);
            } else {
                usina.log("Transição bloqueada por prevenção de ciclo.");
            }
            return;
        }

        // se temperatura > 400, verificar duração contínua
        if (temp > 400.0) {
            Instant inicio = usina.getInicioCondicao("AMARELO");
            if (inicio == null) {
                usina.marcarInicioCondicao("AMARELO", data.getTimestamp());
                return;
            }
            Duration duracao = Duration.between(inicio, data.getTimestamp());
            usina.log("Tempo em >400°C: " + duracao.getSeconds() + "s");
            if (duracao.compareTo(LIMIAR_PARA_VERMELHO) >= 0) {
                usina.log("Condição mantida >400°C por mais de 30s -> ALERTA_VERMELHO");
                if (!usina.preventirCicloPerigoso("ALERTA_VERMELHO", data.getTimestamp())) {
                    usina.setEstado(new AlertaVermelhoState(), data);
                } else {
                    usina.log("Transição para ALERTA_VERMELHO bloqueada por prevenção de ciclo.");
                }
            }
        } else {
            // se entre 300 e 400, mantém o timer iniciado (não reinicia)
            usina.log("ALERTA_AMARELO: temperatura entre 300 e 400.");
        }
    }

    @Override
    public String nome() {
        return "ALERTA_AMARELO";
    }
}