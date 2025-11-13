package Questao1.algorithms;

import Questao1.RiskCalculator;
import Questao1.RiskContext;

// Implementação dummy de Expected Shortfall (ES).
// Decisão: ES usa o mesmo contexto, mas calcula valor diferente.
// Demonstra que podemos ter implementações com comportamento distinto.

public class ExpectedShortfallCalculator implements RiskCalculator {

    @Override
    public String calculate(RiskContext context) {
        double exposure = context.getPositions().values().stream().mapToDouble(Double::doubleValue).sum();
        // Dummy: ES > VaR, usa potência para diferenciar
        double es = Math.pow(exposure * context.getConfidenceLevel() * 0.01, 1.1) * context.getHorizonDays();
        return String.format("Expected Shortfall (dummy) - exposure=%.2f -> ES=%.2f",
                exposure, es);
    }

    @Override
    public String getName() {
        return "Expected Shortfall (ES)";
    }
}