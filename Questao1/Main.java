package Questao1;

import Questao1.factory.RiskCalculatorFactory;
import Questao1.factory.RiskCalculatorFactory.Type;
import Questao1.processor.RiskProcessor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


// Demonstração de troca dinâmica de algoritmos em tempo de execução.

public class Main {
    public static void main(String[] args) {
        // Monta um contexto complexo
        Map<String, Double> positions = new HashMap<>();
        positions.put("AAPL", 10000.0);
        positions.put("GOOG", 15000.0);

        Map<String, Object> extras = new HashMap<>();
        extras.put("shockPercent", 25.0); // usado pelo StressTestCalculator

        RiskContext context = new RiskContext(LocalDate.now(), positions, 0.99, 10, extras);

        // Cria processor com VaR inicial via factory (cliente não conhece implementação concreta)
        RiskProcessor processor = new RiskProcessor(RiskCalculatorFactory.create(Type.VAR));

        // Processa com VaR
        System.out.println(processor.process(context));

        // Troca para Expected Shortfall em tempo de execução
        processor.setCalculator(RiskCalculatorFactory.create(Type.ES));
        System.out.println(processor.process(context));

        // Troca para Stress Testing
        processor.setCalculator(RiskCalculatorFactory.create(Type.STRESS));
        System.out.println(processor.process(context));

        // Demonstra que podemos voltar a qualquer algoritmo
        processor.setCalculator(RiskCalculatorFactory.create(Type.VAR));
        System.out.println(processor.process(context));

    }
}
