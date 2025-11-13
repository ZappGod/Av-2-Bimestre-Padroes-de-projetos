package Questao4.validators;

import Questao4.core.AbstractValidator;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;
import Questao4.repo.InMemoryDatabase;

import java.time.Duration;

// Validador de banco de dados (duplicidade).
// - Insere o número na "base" se não existir.
// - Implementa rollback manual: fornece método público rollbackInsert
//   que será chamado pela cadeia se validações subsequentes falharem.
// Justificativa: este validador é o que altera o estado externo (persistência).
// Em sistemas reais, use transações e commit/rollback no banco, mas para o requisito
// demonstramos rollback explícito.

public class DatabaseValidator extends AbstractValidator {

    private final InMemoryDatabase db;
    private boolean inserted = false;

    public DatabaseValidator(InMemoryDatabase db, Duration timeout) {
        super(timeout);
        this.db = db;
    }

    @Override
    public ValidationResult validate(NFEDocument doc) {
        String numero = doc.getNumero();
        if (db.exists(numero)) {
            return ValidationResult.fail("Número duplicado: " + numero);
        }
        // "inserção" (efetiva)
        db.insert(numero);
        inserted = true;
        return ValidationResult.ok("Inserido na base: " + numero);
    }

    // Rollback manual da inserção feita por este validador.
    // Chamado pela cadeia se houver falha posterior.
    
    public void rollbackInsert(NFEDocument doc) {
        if (inserted) {
            db.delete(doc.getNumero());
            inserted = false;
        }
    }
}