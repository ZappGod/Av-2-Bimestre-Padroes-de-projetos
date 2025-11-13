package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;


// Estado EMERGENCIA.
// - Unidirecional por projeto (apenas pessoal autorizado ou procedimentos específicos podem reverter).
// - Aqui simplificamos: só uma entidade externa pode resetar o sistema (método usina.resetFromEmergency()).

public class EmergenciaState implements EstadoUsina {

    @Override
    public void enter(UsinaNuclear usina, SensorData data) {
        usina.log("!!! EMERGENCIA ATIVADA !!!");
    }

    @Override
    public void onSensorUpdate(UsinaNuclear usina, SensorData data) {
        // durante emergência, não permitimos transições automáticas para estados normais
        usina.log("EMERGENCIA: aguardando procedimento manual de recuperação.");
    }

    @Override
    public String nome() {
        return "EMERGENCIA";
    }
}