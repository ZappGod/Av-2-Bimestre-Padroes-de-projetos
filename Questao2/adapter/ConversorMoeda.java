package Questao2.adapter;

import java.util.HashMap;
import java.util.Map;

// Classe utilitária responsável por conversão de moedas entre formato moderno e legado.

// Justificativa:
// - Segue SRP (Single Responsibility Principle): só lida com a lógica de codificação de moedas.
// - Mantida separada do Adapter principal para facilitar testes e extensão.

public class ConversorMoeda {

    private static final Map<String, Integer> CODIGOS = new HashMap<>();
    private static final Map<Integer, String> REVERSO = new HashMap<>();

    static {
        CODIGOS.put("USD", 1);
        CODIGOS.put("EUR", 2);
        CODIGOS.put("BRL", 3);

        // Mapeamento reverso
        REVERSO.put(1, "USD");
        REVERSO.put(2, "EUR");
        REVERSO.put(3, "BRL");
    }

    public static int paraCodigo(String moeda) {
        return CODIGOS.getOrDefault(moeda.toUpperCase(), 0); // 0 = desconhecida
    }

    public static String paraNome(int codigo) {
        return REVERSO.getOrDefault(codigo, "DESCONHECIDA");
    }
}