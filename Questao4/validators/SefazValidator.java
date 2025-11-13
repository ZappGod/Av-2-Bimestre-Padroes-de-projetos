package Questao4.validators;

import Questao4.core.AbstractValidator;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;

import java.time.Duration;

// Validador de serviço SEFAZ (consulta online) - dummy.
// Deve ser executado apenas se validadores anteriores passaram.

public class SefazValidator extends AbstractValidator {

    public SefazValidator(Duration timeout) {
        super(timeout);
    }

    @Override
    public ValidationResult validate(NFEDocument doc) {
        // Dummy: se metadata contém "sefaStatus" = "OK" consideramos aprovado;
        Object status = doc.getMetadata().get("sefaStatus");
        if ("OK".equals(status)) {
            return ValidationResult.ok("SEFAZ OK");
        } else {
            return ValidationResult.fail("SEFAZ retornou negativa");
        }
    }
}