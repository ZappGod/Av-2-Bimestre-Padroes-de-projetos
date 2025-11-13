package Questao4.validators;

import Questao4.core.AbstractValidator;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;

import java.time.Duration;
import java.time.Instant;

// Validador de certificado (expiração e revogação) - dummy.
// - Verifica campos do NFEDocument (expiracao e revogado).

public class CertificateValidator extends AbstractValidator {

    public CertificateValidator(Duration timeout) {
        super(timeout);
    }

    @Override
    public ValidationResult validate(NFEDocument doc) {
        if (doc.isCertificadoRevogado()) {
            return ValidationResult.failFatal("Certificado revogado");
        }
        Instant exp = doc.getCertificadoExpiracao();
        if (exp == null || exp.isBefore(Instant.now())) {
            return ValidationResult.fail("Certificado expirado");
        }
        return ValidationResult.ok("Certificado válido");
    }
}