package Questao3;

import Questao3.estado.*;

import java.time.Instant;

/**
 * Demonstração sequencial (simula leituras ao longo do tempo).
 *
 * Observação: para testes reais, substitua por chamadas com timestamps reais e dados reais de sensores.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // inicia usina em OPERACAO_NORMAL
        UsinaNuclear usina = new UsinaNuclear(new OperacaoNormalState());

        // t0: normal
        Instant t0 = Instant.now();
        usina.onSensorUpdate(new SensorData(290.0, 1.0, 0.05, true, t0));

        // t1: supera 300°C -> deve ir para AMARELO
        Instant t1 = t0.plusSeconds(1);
        usina.onSensorUpdate(new SensorData(310.0, 1.1, 0.05, true, t1));

        // t2: sobe para >400°C e permanece: precisamos simular 31 segundos de permanência acima de 400
        Instant t2 = t1.plusSeconds(1);
        usina.onSensorUpdate(new SensorData(410.0, 1.2, 0.06, true, t2));

        // simula 31 segundos depois com temperatura ainda >400
        Instant t3 = t2.plusSeconds(31);
        usina.onSensorUpdate(new SensorData(420.0, 1.3, 0.07, true, t3));

        // agora estamos em ALERTA_VERMELHO; se sistema de resfriamento falhar -> EMERGENCIA
        Instant t4 = t3.plusSeconds(1);
        usina.onSensorUpdate(new SensorData(430.0, 1.4, 0.09, false, t4));

        // Tentativa de retornar automaticamente para AMARELO após emergência (deve ser bloqueada)
        Instant t5 = t4.plusSeconds(10);
        usina.onSensorUpdate(new SensorData(350.0, 1.0, 0.05, false, t5));

        // Agora faz-se procedimento manual para resetar emergência
        Instant t6 = t5.plusSeconds(5);
        usina.resetFromEmergency(new SensorData(100.0, 0.8, 0.01, true, t6));

        // Reativa manutenção (override). Durante manutenção, transições automáticas são suprimidas.
        Instant t7 = t6.plusSeconds(2);
        usina.enableManutencao("Inspeção anual", new SensorData(100.0, 0.8, 0.01, true, t7));

        // Envia leitura que normalmente causaria alerta, mas manutenção impede transição
        Instant t8 = t7.plusSeconds(1);
        usina.onSensorUpdate(new SensorData(350.0, 1.0, 0.02, true, t8));

        // Sair da manutenção e restaurar estado anterior
        Instant t9 = t8.plusSeconds(1);
        usina.disableManutencao(new SensorData(120.0, 0.9, 0.01, true, t9));

        // Continua com leituras normais
        Instant t10 = t9.plusSeconds(1);
        usina.onSensorUpdate(new SensorData(280.0, 0.9, 0.01, true, t10));
    }
}