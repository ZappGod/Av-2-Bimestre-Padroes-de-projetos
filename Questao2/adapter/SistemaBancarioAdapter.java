package Questao2.adapter;

import Questao2.legado.SistemaBancarioLegado;
import Questao2.moderno.ProcessadorTransacoes;
import Questao2.moderno.RespostaTransacao;

import java.util.HashMap;
import java.util.Map;

// Adapter que conecta o sistema moderno com o sistema bancário legado.

// Justificativas de design:
// - Usa o padrão **Adapter**, convertendo a interface moderna para a interface legada e vice-versa.
// - Segue o princípio **Open/Closed**: novos formatos podem ser suportados sem alterar o legado.
// - Segue **Dependency Inversion**: depende de abstrações (interfaces), não de implementações.
// - Inclui tratamento de campo obrigatório adicional ("canal"), exigido apenas pelo legado.

public class SistemaBancarioAdapter implements ProcessadorTransacoes {

    private final SistemaBancarioLegado legado;

    public SistemaBancarioAdapter(SistemaBancarioLegado legado) {
        this.legado = legado;
    }

    @Override
    public RespostaTransacao autorizar(String cartao, double valor, String moeda) {
        // Conversão do formato moderno para o legado (modern -> legacy)
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cartao", cartao);
        parametros.put("valor", valor);
        parametros.put("codigoMoeda", ConversorMoeda.paraCodigo(moeda));

        // Campo obrigatório no legado, inexistente no moderno
        parametros.put("canal", "E-COMMERCE");

        // Chama o sistema legado
        Map<String, Object> respostaLegado = legado.processarTransacao(new HashMap<>(parametros));

        // Converte resposta legado -> moderno
        return converterResposta(respostaLegado);
    }

    /**
     * Converte a resposta do legado em formato moderno.
     *
     * @param respostaLegado mapa vindo do sistema legado
     * @return objeto RespostaTransacao moderno
     */
    private RespostaTransacao converterResposta(Map<String, Object> respostaLegado) {
        String status = (String) respostaLegado.getOrDefault("status", "INDEFINIDO");
        String mensagem = (String) respostaLegado.getOrDefault("mensagem", "Sem mensagem");
        return new RespostaTransacao(status, mensagem);
    }

    /**
     * Método adicional (opcional) para casos inversos:
     * converte um pedido moderno em mapa legado (útil em integrações bidirecionais).
     */
    public Map<String, Object> converterParaLegado(String cartao, double valor, String moeda) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cartao", cartao);
        parametros.put("valor", valor);
        parametros.put("codigoMoeda", ConversorMoeda.paraCodigo(moeda));
        parametros.put("canal", "MOBILE_APP");
        return parametros;
    }
}