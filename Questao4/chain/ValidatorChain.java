package Questao4.chain;

import Questao4.core.Validator;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;
import Questao4.validators.DatabaseValidator;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

// Gerencia execução da cadeia de validadores.

// Responsabilidades:
// - Executar cada validador com timeout individual (ExecutorService + Future.get(timeout))
// - Aplicar regras condicionais: validadores 3 e 5 executam apenas se anteriores passaram
// - Implementar Circuit Breaker: interrompe cadeia após 3 failures cumulativos
// - Rastrear validadores que fizeram modificações externas (ex.: DatabaseValidator) para rollback quando houver falha posterior

// Justificativa de design:
// - Chain of Responsibility: sequencia flexível de validadores.
// - ExecutorService para timeouts: evita bloqueio indefinido em validações externas.
// - Rastreio de rollback específico (somente DatabaseValidator) conforme requisito.

// Observação: em produção, preferir injeção e um scheduler dedicado para timeouts mais robusto.

public class ValidatorChain {

    private final List<Validator> validators;
    private final ExecutorService executor;
    private final int circuitBreakerThreshold;

    public ValidatorChain(List<Validator> validators, int circuitBreakerThreshold) {
        this.validators = new ArrayList<>(validators);
        this.executor = Executors.newCachedThreadPool();
        this.circuitBreakerThreshold = circuitBreakerThreshold;
    }

    /**
     * Executa a cadeia. Retorna lista de resultados por validador.
     */
    public List<ValidationResult> execute(NFEDocument doc) {
        List<ValidationResult> results = new ArrayList<>();
        int failureCount = 0;

        // rastreia DatabaseValidator (especificidade do requisito) e quaisquer rollbackables se preciso
        DatabaseValidator dbValidator = null;
        boolean dbExecuted = false;

        for (int i = 0; i < validators.size(); i++) {
            Validator v = validators.get(i);

            // Regras restritivas: validar 3 e 5 apenas se todos anteriores passaram.
            // mapas: index 2 => FiscalRules (3rd), index 4 => Sefaz (5th)
            if ((i == 2 || i == 4) && failureCount > 0) {
                // requisitos: 3 e 5 devem ser executados somente se anteriores passaram
                results.add(ValidationResult.fail("Pulando " + v.name() + " porque validadores anteriores falharam"));
                continue;
            }

            // Execute com timeout
            Future<ValidationResult> future = executor.submit(() -> {
                try {
                    return v.validate(doc);
                } catch (Exception e) {
                    // Captura exceções do validador como falha
                    return ValidationResult.fail("Exceção em " + v.name() + ": " + e.getMessage());
                }
            });

            ValidationResult res;
            try {
                res = future.get(v.timeout().toMillis(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException te) {
                future.cancel(true);
                res = ValidationResult.fail("Timeout em " + v.name());
            } catch (ExecutionException ee) {
                res = ValidationResult.fail("Erro em " + v.name() + ": " + ee.getCause().getMessage());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                res = ValidationResult.fail("Interrompido em " + v.name());
            }

            results.add(res);

            // Contabiliza falhas para circuit breaker
            if (!res.isSuccess()) {
                failureCount++;
            }

            // Se este validador é DatabaseValidator, guarde referência para rollback potencial
            if (v instanceof DatabaseValidator) {
                dbValidator = (DatabaseValidator) v;
                dbExecuted = res.isSuccess();
            }

            // If validator requested skipNext, skip next
            if (res.isSkipNext()) {
                i++; // pula o próximo
                if (i < validators.size()) {
                    results.add(ValidationResult.ok("Pulou " + validators.get(i).name() + " por instrução de " + v.name()));
                }
            }

            // Circuit breaker: se limite atingido, interrompe
            if (failureCount >= circuitBreakerThreshold) {
                // faz rollback do DB se já executado
                if (dbValidator != null && dbExecuted) {
                    dbValidator.rollbackInsert(doc);
                    // adiciona registro de rollback ao results (informativo)
                    results.add(ValidationResult.fail("Rollback aplicado na base pelo motivo: circuit breaker ativado"));
                }
                results.add(ValidationResult.failFatal("Circuit breaker acionado após " + failureCount + " falhas. Interrompendo cadeia."));
                break;
            }

            // Se validador retornou fatal, interrompe imediatamente (e aplica rollback se necessário)
            if (res.isFatal()) {
                if (dbValidator != null && dbExecuted) {
                    dbValidator.rollbackInsert(doc);
                    results.add(ValidationResult.fail("Rollback aplicado na base pelo motivo: validação fatal"));
                }
                break;
            }

            // Se falhou e é anterior ao fiscal/SEFAZ, e fiscal/SEFAZ dependem de todos anteriores passarem, we already handle above.
            // Continue a cadeia se não houver motivo de parada.
        }

        return results;
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}