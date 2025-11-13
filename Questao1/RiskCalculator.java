package Questao1;

import Questao1.RiskContext;

// Interface de estratégia para cálculo de risco.

// Justificativa:
// - Strategy Pattern: permite trocar o algoritmo em tempo de execução sem que o cliente conheça a implementação.
// - Segue Liskov: implementações concretas substituem a interface.
// - Interface é pequena (Interface Segregation).

public interface RiskCalculator {
    /**
     * Executa o cálculo de risco usando o contexto fornecido.
     * Pode retornar um objeto complexo em implementações reais; aqui retorna String com resultado dummy.
     *
     * @param context dados financeiros e parâmetros
     * @return resultado do cálculo (dummy)
     */
    String calculate(RiskContext context);

    
    // Nome amigável do algoritmo.
    
    String getName();
}
