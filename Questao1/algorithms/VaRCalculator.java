package Questao1.algorithms;

import Questao1.RiskCalculator;
import Questao1.RiskContext;

import java.util.Map;

// Implementação dummy de Value at Risk (VaR).
// Decisão: VaR é um algoritmo independente, implementa RiskCalculator.
// O método calculate usa apenas o RiskContext; para um cálculo real poderia usar séries temporais, etc.

public class VaRCalculator implements RiskCalculator {

    @Override
    public String calculate(RiskContext context) {
        // Dummy: soma posições, aplica fórmula fictícia para gerar um "VaR"
        double exposure = context.getPositions().values().stream().mapToDouble(Double::doubleValue).sum();
        double var = exposure * context.getConfidenceLevel() * 0.01 * context.getHorizonDays(); // fórmula inventada
        return String.format("VaR (dummy) - exposure=%.2f, confidence=%.2f, horizon=%d -> VaR=%.2f",
                exposure, context.getConfidenceLevel(), context.getHorizonDays(), var);
    }

    @Override
    public String getName() {
        return "Value at Risk (VaR)";
    }
}