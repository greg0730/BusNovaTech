/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 *
 * @author grego
 */
public class PruebaBus {
    
    // comprueba la cola y el estado del inspector

    public static void main(String[] args) {

        Bus bus;
        bus = new Bus(1, "N");

        Tiquete primero;
        primero = new Tiquete(1, "Ana", "101", 20, "Colones", "N");

        Tiquete segundo;
        segundo = new Tiquete(2, "Luis", "102", 25, "Colones", "N");

        Tiquete directo;
        directo = new Tiquete(3, "Maria", "103", 30, "Colones", "D");

        String resultado;
        resultado = "PRUEBA DEL BUS\n";

        boolean agregado;
        agregado = bus.agregarTiquete(primero);
        resultado += "\nAcepta primer tiquete normal: " + agregado;

        agregado = bus.agregarTiquete(segundo);
        resultado += "\nAcepta segundo tiquete normal: " + agregado;

        agregado = bus.agregarTiquete(directo);
        resultado += "\nAcepta tiquete directo: " + agregado;
        resultado += "\nPersonas en espera: " + bus.contarEnEspera();

        Tiquete atendido;
        atendido = bus.iniciarAtencion();

        if (atendido != null) {
            resultado += "\nTiquete en atencion: " + atendido.getNumero();
            resultado += "\nHora de abordaje registrada: " + (atendido.getHoraAbordaje() >= 0);
        }

        resultado += "\nInspector libre: " + bus.inspectorLibre();
        resultado += "\nPersonas que siguen esperando: " + bus.contarEnEspera();

        Tiquete intento;
        intento = bus.iniciarAtencion();
        resultado += "\nBloquea otra atencion: " + (intento == null);

        bus.finalizarAtencion();
        resultado += "\nInspector libre al finalizar: " + bus.inspectorLibre();

        atendido = bus.iniciarAtencion();

        if (atendido != null) {
            resultado += "\nSiguiente tiquete atendido: " + atendido.getNumero();
        }

        javax.swing.JOptionPane.showMessageDialog(null, resultado);
    }

}
