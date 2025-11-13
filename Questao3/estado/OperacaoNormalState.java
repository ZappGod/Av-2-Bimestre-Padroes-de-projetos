package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;

import java.time.Instant;


// Estado OPERACAO_NORMAL.
// Regra: OPERACAO_NORMAL -> ALERTA_AMARELO se temperatura > 300°C
// Permite retorno a DESLIGADA ou permanece em normal se tudo ok.

public class OperacaoNormalState implements EstadoUsina {

    @Override
    public void enter(UsinaNuclear usina, SensorData data) {
        usina.log("Entrou em OPERACAO_NORMAL");
        // reset indicadores de alerta anteriores
        usina.limparTemposDeCondicao();
    }

    @Override
    public void onSensorUpdate(UsinaNuclear usina, SensorData data) {
        double temp = data.getTemperaturaC();

        if (temp > 300.0) {
            usina.log("Temperatura excedeu 300°C -> requisitando ALERTA_AMARELO");
            if (!usina.preventirCicloPerigoso("ALERTA_AMARELO", data.getTimestamp())) {
                usina.setEstado(new AlertaAmareloState(), data);
            } else {
                usina.log("Transição para ALERTA_AMARELO bloqueada por prevenção de ciclo.");
            }
            return;
        }

        // Exemplo: se radiacao for crítica, podemos ir direto para vermelho? (negado pela regra)
        // mantemos operação normal.
    }

    @Override
    public String nome() {
        return "OPERACAO_NORMAL";
    }
}