package Questao4;

import Questao4.chain.ValidatorChain;
import Questao4.model.NFEDocument;
import Questao4.model.ValidationResult;
import Questao4.repo.InMemoryDatabase;
import Questao4.validators.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

// Demonstração de execução da cadeia com exemplos diferentes.

public class Main {
    public static void main(String[] args) {
        // repo em memória
        InMemoryDatabase db = new InMemoryDatabase();

        // instância de validadores com timeouts por validador
        SchemaValidator schema = new SchemaValidator(Duration.ofSeconds(2));
        CertificateValidator cert = new CertificateValidator(Duration.ofSeconds(2));
        FiscalRulesValidator fiscal = new FiscalRulesValidator(Duration.ofSeconds(3));
        DatabaseValidator database = new DatabaseValidator(db, Duration.ofSeconds(2));
        SefazValidator sefaz = new SefazValidator(Duration.ofSeconds(4));

        List validators = Arrays.asList(schema, cert, fiscal, database, sefaz);

        ValidatorChain chain = new ValidatorChain(validators, 3); // circuit breaker threshold = 3

        // 1) Documento bom (deve passar tudo)
        NFEDocument good = new NFEDocument("0001", "<nfe><valid>true</valid><valor>100.0</valor></nfe>", "ABC123",
                Instant.now().plusSeconds(3600), false);
        good.getMetadata().put("sefaStatus", "OK");

        System.out.println("=== Executando cadeia para documento bom ===");
        List<ValidationResult> results1 = chain.execute(good);
        results1.forEach(System.out::println);
        System.out.println("Documento após cadeia: " + good);

        // 2) Documento com certificado expirado (cert falhar -> fiscal e sefaz não executam)
        NFEDocument certExpired = new NFEDocument("0002", "<nfe><valid>true</valid><valor>50.0</valor></nfe>", "DEF456",
                Instant.now().minusSeconds(10), false);
        certExpired.getMetadata().put("sefaStatus", "OK");

        System.out.println("\n=== Documento com certificado expirado ===");
        List<ValidationResult> results2 = chain.execute(certExpired);
        results2.forEach(System.out::println);
        System.out.println("Documento após cadeia: " + certExpired);

        // 3) Documento com DB duplicado scenario + subsequent failure causing rollback
        // Insere número previamente para simular duplicidade
        db.insert("0003");

        NFEDocument duplicate = new NFEDocument("0003", "<nfe><valid>true</valid><valor>200.0</valor></nfe>", "GHI789",
                Instant.now().plusSeconds(3600), false);
        duplicate.getMetadata().put("sefaStatus", "NOK"); // Sefaz falhará

        System.out.println("\n=== Documento duplicado (DB) com Sefaz NOK -> DB não inserirá (duplicado) ===");
        List<ValidationResult> results3 = chain.execute(duplicate);
        results3.forEach(System.out::println);
        System.out.println("Documento após cadeia: " + duplicate);

        // 4) Documento que causa DB insert e depois Sefaz fail -> rollback expected
        NFEDocument toRollback = new NFEDocument("0004", "<nfe><valid>true</valid><valor>400.0</valor></nfe>", "JKL012",
                Instant.now().plusSeconds(3600), false);
        toRollback.getMetadata().put("sefaStatus", "NOK"); // Sefaz falhar
        System.out.println("\n=== Documento que será inserido então rollback porque SEFAZ falha ===");
        List<ValidationResult> results4 = chain.execute(toRollback);
        results4.forEach(System.out::println);
        System.out.println("Documento após cadeia: " + toRollback);
        System.out.println("DB contains 0004? " + db.exists("0004"));

        // 5) Documento que triggers multiple failures to open circuit breaker
        NFEDocument badMany = new NFEDocument("0005", "<nfe><valid>false</valid></nfe>", "ZZZ999",
                Instant.now().minusSeconds(1000), true); // revogado + schema invalid
        badMany.getMetadata().put("sefaStatus", "NOK");

        System.out.println("\n=== Documento com muitas falhas (circuit breaker demo) ===");
        List<ValidationResult> results5 = chain.execute(badMany);
        results5.forEach(System.out::println);

        chain.shutdown();
    }
}