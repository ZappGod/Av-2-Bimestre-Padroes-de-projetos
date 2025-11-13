package Questao4.validators;

import Questao4.core.AbstractValidator;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;

import java.time.Duration;

// Validador de schema XML contra XSD (dummy).
// Decisão: aqui apenas simulamos validação do XML; timeout configurável.

public class SchemaValidator extends AbstractValidator {

    public SchemaValidator(Duration timeout) {
        super(timeout);
    }

    @Override
    public ValidationResult validate(NFEDocument doc) {
        // Dummy: se xml contém "<valid>true</valid>" consideramos válido
        if (doc.getXml() != null && doc.getXml().contains("<valid>true</valid>")) {
            return ValidationResult.ok("Schema OK");
        } else {
            // se schema inválido, falha e pede para pular próximos validadores desnecessários
            return ValidationResult.fail("Schema inválido");
        }
    }
}