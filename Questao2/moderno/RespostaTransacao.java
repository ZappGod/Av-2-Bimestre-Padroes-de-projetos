package Questao2.moderno;

// Representa a resposta padronizada do sistema moderno.
// Em sistemas reais, poderia conter mais metadados (autorização, ID, etc.)

public class RespostaTransacao {
    private final String status;
    private final String mensagem;

    public RespostaTransacao(String status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public String getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return "RespostaTransacao{status='" + status + "', mensagem='" + mensagem + "'}";
    }
}