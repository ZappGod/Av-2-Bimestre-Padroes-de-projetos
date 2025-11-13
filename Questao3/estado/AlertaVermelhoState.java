package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;

// Estado ALERTA_VERMELHO.
// Regras:
// - ALERTA_VERMELHO -> EMERGENCIA se sistema de resfriamento falhar
// - Pode permitir retroceder para AMARELO se condições melhorarem (bidirecional)
// Importante: EMERGENCIA só pode ser ativado se usina já passou por ALERTA_VERMELHO (controlado no contexto).

public class AlertaVermelhoState implements EstadoUsina {

    @Override
    public void enter(UsinaNuclear usina, SensorData data) {
        usina.log("Entrou em ALERTA_VERMELHO");
        // marca que já visitou vermelho (necessário para EMERGENCIA)
        usina.setVisitouAlertaVermelho(true);
        // registra tempo de início
        usina.marcarInicioCondicao("VERMELHO", data.getTimestamp());
    }

    @Override
    public void onSensorUpdate(UsinaNuclear usina, SensorData data) {
        // se falha no resfriamento -> EMERGENCIA (unidirecional)
        if (!data.isSistemaResfriamentoOk()) {
            usina.log("Falha no sistema de resfriamento detectada -> requisitando EMERGENCIA");
            // EMERGENCIA só se permitido após ter estado em VERMELHO (já foi setado no enter)
            if (usina.podeIrParaEmergencia()) {
                usina.setEstado(new EmergenciaState(), data);
            } else {
                usina.log("Não é possível ir para EMERGENCIA: requisito de passagem por ALERTA_VERMELHO não cumprido.");
            }
            return;
        }

        // se temperaturas caem, permitimos voltar para AMARELO (bidirecional)
        if (data.getTemperaturaC() < 400.0) {
            usina.log("Temperatura abaixo de 400°C -> retornar para ALERTA_AMARELO");
            if (!usina.preventirCicloPerigoso("ALERTA_AMARELO", data.getTimestamp())) {
                usina.setEstado(new AlertaAmareloState(), data);
            } else {
                usina.log("Transição bloqueada por prevenção de ciclo.");
            }
        }
    }

    @Override
    public String nome() {
        return "ALERTA_VERMELHO";
    }
}