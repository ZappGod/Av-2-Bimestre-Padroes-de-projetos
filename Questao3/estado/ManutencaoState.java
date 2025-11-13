package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;


// Estado de MANUTENÇÃO (override temporário).
// - Deve sobrepor temporariamente os estados normais.
// - Ao sair de manutenção, retorna ao estado anterior salvo (restaurado pelo contexto).
// Decisão: Manutenção é implementada como estado explícito para simplificar comportamento override.

public class ManutencaoState implements EstadoUsina {

    private final String motivo;

    public ManutencaoState(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public void enter(UsinaNuclear usina, SensorData data) {
        usina.log("Entrou em MODO MANUTENÇÃO (" + motivo + "). Operações automáticas suspensas.");
    }

    @Override
    public void onSensorUpdate(UsinaNuclear usina, SensorData data) {
        // Durante manutenção não realizamos transições automáticas,
        // mas podemos registrar leituras e permitir ações manuais.
        usina.log("Manutenção: leitura recebida, nenhum transition automático executado.");
    }

    @Override
    public String nome() {
        return "MANUTENCAO";
    }
}