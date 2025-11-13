package Questao2;

import Questao2.adapter.SistemaBancarioAdapter;
import Questao2.legado.SistemaBancarioLegado;
import Questao2.moderno.ProcessadorTransacoes;
import Questao2.moderno.RespostaTransacao;

// Demonstração de uso do Adapter.
// Mostra que o sistema moderno interage sem conhecer os detalhes do legado.

public class Main {
    public static void main(String[] args) {
        SistemaBancarioLegado legado = new SistemaBancarioLegado();

        // O cliente moderno usa apenas a interface moderna
        ProcessadorTransacoes processador = new SistemaBancarioAdapter(legado);

        // Faz uma autorização moderna (Adapter converte internamente para o legado)
        RespostaTransacao resposta1 = processador.autorizar("1234-5678-9999-0000", 500.0, "BRL");
        System.out.println(resposta1);

        // Teste com moeda desconhecida
        RespostaTransacao resposta2 = processador.autorizar("4321-0000-9999-8888", 250.0, "JPY");
        System.out.println(resposta2);
    }
}