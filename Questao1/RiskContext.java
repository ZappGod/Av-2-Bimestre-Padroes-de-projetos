package Questao1;

import java.time.LocalDate;
import java.util.Map;

public class RiskContext {
    private final LocalDate valuationDate;
    private final Map<String, Double> positions; // símbolo -> posição (quantidade ou valor)
    private final double confidenceLevel;
    private final int horizonDays;
    private final Map<String, Object> additionalParameters; // parâmetros arbitrários

    public RiskContext(LocalDate valuationDate,
                       Map<String, Double> positions,
                       double confidenceLevel,
                       int horizonDays,
                       Map<String, Object> additionalParameters) {
        this.valuationDate = valuationDate;
        this.positions = positions;
        this.confidenceLevel = confidenceLevel;
        this.horizonDays = horizonDays;
        this.additionalParameters = additionalParameters;
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public Map<String, Double> getPositions() {
        return positions;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public int getHorizonDays() {
        return horizonDays;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    @Override
    public String toString() {
        return "RiskContext{" +
                "valuationDate=" + valuationDate +
                ", positions=" + positions +
                ", confidenceLevel=" + confidenceLevel +
                ", horizonDays=" + horizonDays +
                ", additionalParameters=" + additionalParameters +
                '}';
    }
}