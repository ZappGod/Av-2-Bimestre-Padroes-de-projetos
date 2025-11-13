package Questao1.algorithms;

import Questao1.RiskCalculator;
import Questao1.RiskContext;

import java.util.Map;

// Implementação dummy de Stress Testing.
// Decisão: Stress testing pode consultar parâmetros adicionais no RiskContext (ex: shocks).
// Mostra a vantagem do contexto flexível (additionalParameters).

public class StressTestCalculator implements RiskCalculator {

    @Override
    public String calculate(RiskContext context) {
        double exposure = context.getPositions().values().stream().mapToDouble(Double::doubleValue).sum();

        Object shockObj = context.getAdditionalParameters().getOrDefault("shockPercent", 10.0);
        double shockPercent = (shockObj instanceof Number) ? ((Number) shockObj).doubleValue() : 10.0;

        double shockedLoss = exposure * (shockPercent / 100.0);
        return String.format("Stress Test (dummy) - exposure=%.2f, shock=%.2f%% -> potentialLoss=%.2f",
                exposure, shockPercent, shockedLoss);
    }

    @Override
    public String getName() {
        return "Stress Testing";
    }
}