package Questao4.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// DTO representando uma NF-e simplificada.
// Imutabilidade parcial: campos podem ser alterados por validadores que modificam documento (ex.: cálculo de impostos).
// SRP: apenas porta dados do documento.

public class NFEDocument {
    private final String numero;
    private final String xml; // conteúdo XML (simulado)
    private final String certificadoSerial; // identificador do certificado
    private Instant certificadoExpiracao;
    private boolean certificadoRevogado;
    private final Map<String, Object> metadata = new HashMap<>();

    // campos que podem ser ajustados por validadores
    private Double impostoCalculado;

    public NFEDocument(String numero, String xml, String certificadoSerial, Instant certificadoExpiracao, boolean certificadoRevogado) {
        this.numero = numero;
        this.xml = xml;
        this.certificadoSerial = certificadoSerial;
        this.certificadoExpiracao = certificadoExpiracao;
        this.certificadoRevogado = certificadoRevogado;
    }

    public String getNumero() {
        return numero;
    }

    public String getXml() {
        return xml;
    }

    public String getCertificadoSerial() {
        return certificadoSerial;
    }

    public Instant getCertificadoExpiracao() {
        return certificadoExpiracao;
    }

    public boolean isCertificadoRevogado() {
        return certificadoRevogado;
    }

    public Double getImpostoCalculado() {
        return impostoCalculado;
    }

    public void setImpostoCalculado(Double impostoCalculado) {
        this.impostoCalculado = impostoCalculado;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setCertificadoExpiracao(Instant certificadoExpiracao) {
        this.certificadoExpiracao = certificadoExpiracao;
    }

    public void setCertificadoRevogado(boolean certificadoRevogado) {
        this.certificadoRevogado = certificadoRevogado;
    }

    @Override
    public String toString() {
        return "NFEDocument{" +
                "numero='" + numero + '\'' +
                ", impostoCalculado=" + impostoCalculado +
                ", certificadoSerial='" + certificadoSerial + '\'' +
                ", certificadoExpiracao=" + certificadoExpiracao +
                ", certificadoRevogado=" + certificadoRevogado +
                ", metadata=" + metadata +
                '}';
    }
}