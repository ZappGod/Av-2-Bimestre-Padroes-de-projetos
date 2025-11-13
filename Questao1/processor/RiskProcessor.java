package Questao1.processor;

import Questao1.RiskCalculator;
import Questao1.RiskContext;

// Classe de alto nível que processa risco delegando ao RiskCalculator (Strategy).
// Segue DIP: depende da abstração RiskCalculator, não das implementações.
// Responsabilidade: orquestrar execução, logging, validações, métricas.
// Permite trocar algoritmo em tempo de execução via setCalculator().

public class RiskProcessor {

    private RiskCalculator calculator;

    public RiskProcessor(RiskCalculator initialCalculator) {
        this.calculator = initialCalculator;
    }

    /**
     * Troca o algoritmo em tempo de execução.
     */
    public void setCalculator(RiskCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Executa o cálculo delegando para a estratégia corrente.
     * Poderíamos adicionar métricas, auditoria, etc., aqui sem poluir as estratégias.
     */
    public String process(RiskContext context) {
        if (calculator == null) {
            throw new IllegalStateException("Nenhum RiskCalculator configurado.");
        }
        // Pre-processamento comum (ex: validações) poderia ficar aqui.
        String result = calculator.calculate(context);
        // Pós-processamento (ex: armazenar log, persistir resultado) também poderia ficar aqui.
        return String.format("[%s] Resultado: %s", calculator.getName(), result);
    }
}