package Questao1.factory;

import Questao1.RiskCalculator;
import Questao1.algorithms.ExpectedShortfallCalculator;
import Questao1.algorithms.StressTestCalculator;
import Questao1.algorithms.VaRCalculator;

// Factory simples para criar instâncias de RiskCalculator.

// Justificativas:
// - Encapsula a criação das estratégias (abreviação para o cliente).
// - Facilita injeção/registrar novos algoritmos sem alterar cliente.
// - Permite trocar implementação concreta (ex: via configuração) sem expor detalhes.

// Em aplicações maiores, essa factory pode buscar implementações via reflection ou um registry.
public class RiskCalculatorFactory {

    public enum Type {
        VAR,
        ES,
        STRESS
    }

    public static RiskCalculator create(Type type) {
        switch (type) {
            case VAR:
                return new VaRCalculator();
            case ES:
                return new ExpectedShortfallCalculator();
            case STRESS:
                return new StressTestCalculator();
            default:
                throw new IllegalArgumentException("Tipo desconhecido: " + type);
        }
    }
}