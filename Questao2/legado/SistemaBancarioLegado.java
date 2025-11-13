package Questao2.legado;

import java.util.HashMap;
import java.util.Random;

// Simula um sistema bancário legado com interface procedural e uso de HashMap.
// Este código representa o legado que não pode ser alterado.

// Ele exige:
// - Campos obrigatórios: "codigoMoeda" (int), "cartao", "valor", "canal".
// - Retorna uma HashMap com "status" e "mensagem".
public class SistemaBancarioLegado {

    public HashMap<String, Object> processarTransacao(HashMap<String, Object> parametros) {
        HashMap<String, Object> resposta = new HashMap<>();

        if (!parametros.containsKey("codigoMoeda") ||
            !parametros.containsKey("cartao") ||
            !parametros.containsKey("valor") ||
            !parametros.containsKey("canal")) {

            resposta.put("status", "ERRO");
            resposta.put("mensagem", "Campos obrigatórios ausentes no legado.");
            return resposta;
        }

        // Simula autorização (dummy)
        double valor = (double) parametros.get("valor");
        boolean autorizado = new Random().nextBoolean();

        resposta.put("status", autorizado ? "APROVADO" : "NEGADO");
        resposta.put("mensagem", autorizado
                ? "Transação aprovada pelo sistema legado."
                : "Transação negada pelo sistema legado.");

        return resposta;
    }
}