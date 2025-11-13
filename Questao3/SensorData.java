package Questao3;

import java.time.Instant;

// DTO imutável que representa uma leitura de sensores em um dado instante.
// - Segue SRP (Single Responsibility): apenas porta dados.
// - Mantido imutável para facilitar raciocínio e uso concorrente.

public final class SensorData {
    private final double temperaturaC;    // °C
    private final double pressao;         // unidades arbitrárias
    private final double nivelRadiacao;   // unidades arbitrárias
    private final boolean sistemaResfriamentoOk;
    private final Instant timestamp;

    public SensorData(double temperaturaC, double pressao, double nivelRadiacao,
                      boolean sistemaResfriamentoOk, Instant timestamp) {
        this.temperaturaC = temperaturaC;
        this.pressao = pressao;
        this.nivelRadiacao = nivelRadiacao;
        this.sistemaResfriamentoOk = sistemaResfriamentoOk;
        this.timestamp = timestamp;
    }

    public double getTemperaturaC() {
        return temperaturaC;
    }

    public double getPressao() {
        return pressao;
    }

    public double getNivelRadiacao() {
        return nivelRadiacao;
    }

    public boolean isSistemaResfriamentoOk() {
        return sistemaResfriamentoOk;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "temperaturaC=" + temperaturaC +
                ", pressao=" + pressao +
                ", nivelRadiacao=" + nivelRadiacao +
                ", sistemaResfriamentoOk=" + sistemaResfriamentoOk +
                ", timestamp=" + timestamp +
                '}';
    }
}