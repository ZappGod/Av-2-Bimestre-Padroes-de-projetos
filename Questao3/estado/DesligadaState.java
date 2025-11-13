package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;


// Estado DESLIGADA: usina está fora de operação.
// - Pode transitar para OPERACAO_NORMAL se parâmetros forem seguros (exemplo).

public class DesligadaState implements EstadoUsina {

    @Override
    public void onSensorUpdate(UsinaNuclear usina, SensorData data) {
        // Em desligamento, só permitimos ligar se condições seguras forem atendidas.
        if (data.getTemperaturaC() < 100 && data.getNivelRadiacao() < 1.0) {
            if (!usina.preventirCicloPerigoso("OPERACAO_NORMAL", data.getTimestamp())) {
                usina.setEstado(new OperacaoNormalState(), data);
            } else {
                usina.log("Transição bloqueada (possível ciclo perigoso).");
            }
        } else {
            usina.log("Desligada: condições não seguras para ligar.");
        }
    }

    @Override
    public String nome() {
        return "DESLIGADA";
    }
}