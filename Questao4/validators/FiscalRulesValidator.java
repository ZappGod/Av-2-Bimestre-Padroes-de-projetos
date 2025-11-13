package Questao4.validators;

import Questao4.core.AbstractValidator;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;

import java.time.Duration;

// Validador de regras fiscais (cálculo de impostos).
// - Deve ser executado apenas se validadores anteriores passaram (a cadeia garante isso).
// - Este validador modifica o documento (setImpostoCalculado) — portanto a cadeia deve suportar rollback se necessário.
// Nota: não implementamos interface Rollbackable aqui para centralizar rollback no DatabaseValidator conforme requisito,
// mas poderíamos fazê-lo caso mais validadores modifiquem o documento.

public class FiscalRulesValidator extends AbstractValidator {

    public FiscalRulesValidator(Duration timeout) {
        super(timeout);
    }

    @Override
    public ValidationResult validate(NFEDocument doc) {
        // Dummy: se xml contém "<valor>X</valor>" extrair valor (simulação)
        // Aqui colocamos imposto = 10% do "valor" simulado.
        try {
            String xml = doc.getXml();
            if (xml == null || !xml.contains("<valor>")) {
                return ValidationResult.fail("Campo valor ausente");
            }
            // extrai número simples entre tags (dummy)
            int start = xml.indexOf("<valor>") + "<valor>".length();
            int end = xml.indexOf("</valor>");
            String valStr = xml.substring(start, end).trim();
            double valor = Double.parseDouble(valStr);
            double imposto = valor * 0.10; // 10% dummy
            doc.setImpostoCalculado(imposto);
            return ValidationResult.ok("Imposto calculado: " + imposto);
        } catch (Exception e) {
            return ValidationResult.fail("Erro ao calcular impostos: " + e.getMessage());
        }
    }
}