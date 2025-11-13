package Questao4.core;

import Questao4.model.NFEDocument;

import java.time.Duration;

// Classe base que fornece um timeout default; validações concretas podem sobrescrever.
// Não implementa validate (deixa para subclass).
// Ajuda a manter DRY e serve como ponto para hooks futuros.

public abstract class AbstractValidator implements Validator {

    private final Duration timeout;

    protected AbstractValidator(Duration timeout) {
        this.timeout = timeout;
    }

    protected AbstractValidator() {
        this.timeout = Duration.ofSeconds(5);
    }

    @Override
    public Duration timeout() {
        return timeout;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }
}
