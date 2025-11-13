package Questao4.repo;

import java.util.HashSet;
import java.util.Set;

// Repositório em memória que simula persistência de números de NF-e.
// - DatabaseValidator usará este repo para "inserir" e eventualmente fazer rollback.
// - Em produção trocar por DAO real com transação.

public class InMemoryDatabase {
    private final Set<String> numeros = new HashSet<>();

    public synchronized boolean exists(String numero) {
        return numeros.contains(numero);
    }

    public synchronized void insert(String numero) {
        numeros.add(numero);
    }

    public synchronized void delete(String numero) {
        numeros.remove(numero);
    }
}