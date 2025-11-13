package Questao2.moderno;

// Interface moderna e simplificada do processador de transações.
// Essa é a interface esperada pelos sistemas novos.
// Segue princípios SOLID (Interface Segregation — métodos pequenos e claros).

public interface ProcessadorTransacoes {
    RespostaTransacao autorizar(String cartao, double valor, String moeda);
}