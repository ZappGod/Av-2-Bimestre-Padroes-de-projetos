package Questao4.core;

import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;

import java.time.Duration;

// Interface que todos os validadores devem implementar.
// Interface Segregation: simples e focada.

public interface Validator {
    
    // Executa validação sobre o documento. Deve ser habilitado com timeout externo pela cadeia.
    
    ValidationResult validate(NFEDocument doc) throws Exception;
    
    // Nome amigável do validador.
    
    String name();
    
    // Timeout recomendado para este validador; usado pela cadeia para executar com limite.

    Duration timeout();
}