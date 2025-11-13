package Questao4.model;


// Resultado de uma validação.
// - success: validação passou
// - mensagem: descrição
// - skipNext: instrução para pular o próximo validador (condicional)
// - fatal: encerra cadeia imediatamente (também conta como falha)
// Projetado para ser facilmente estendido com códigos e detalhes.

public class ValidationResult {
    private final boolean success;
    private final String mensagem;
    private final boolean skipNext;
    private final boolean fatal;

    private ValidationResult(boolean success, String mensagem, boolean skipNext, boolean fatal) {
        this.success = success;
        this.mensagem = mensagem;
        this.skipNext = skipNext;
        this.fatal = fatal;
    }

    public static ValidationResult ok(String msg) {
        return new ValidationResult(true, msg, false, false);
    }

    public static ValidationResult ok(String msg, boolean skipNext) {
        return new ValidationResult(true, msg, skipNext, false);
    }

    public static ValidationResult fail(String msg) {
        return new ValidationResult(false, msg, false, false);
    }

    public static ValidationResult failFatal(String msg) {
        return new ValidationResult(false, msg, false, true);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isSkipNext() {
        return skipNext;
    }

    public boolean isFatal() {
        return fatal;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "success=" + success +
                ", mensagem='" + mensagem + '\'' +
                ", skipNext=" + skipNext +
                ", fatal=" + fatal +
                '}';
    }
}