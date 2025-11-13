package Questao3.estado;

import Questao3.UsinaNuclear;
import Questao3.SensorData;


// Interface do estado (State Pattern).
// Contrato mínimo: reagir a uma nova leitura de sensores, e hooks enter/exit.
// Razão: Strategy/State permite encapsular regras diferentes por estado e trocar dinamicamente.

public interface EstadoUsina {
    
    // Hook chamado ao receber nova leitura de sensores.
    // Implementações podem solicitar transição chamando usina.setEstado(novoEstado).
    
    void onSensorUpdate(UsinaNuclear usina, SensorData data);

    // Executado ao entrar no estado.

    default void enter(UsinaNuclear usina, SensorData data) { }

    // Executado ao sair do estado.
    
    default void exit(UsinaNuclear usina) { }

    // Nome amigável do estado.
    
    String nome();
}