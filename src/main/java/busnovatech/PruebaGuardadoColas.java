/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;


import java.io.IOException;
import javax.swing.JOptionPane;
/**
 *
 * @author grego
 */
public class PruebaGuardadoColas {
        // comprueba la asignacion y el guardado de las colas
    public static void main(String[] args){

        ListaBuses buses;
        buses=new ListaBuses();

        Bus preferencial;
        preferencial =new Bus(1, "P");

        Bus directo;
        directo=new Bus(2, "D");

        Bus normal;
        normal = new Bus(3, "N");

        buses.agregar(preferencial);
        buses.agregar(directo);
        buses.agregar(normal);

        ListaTiquetes pendientes;
        pendientes=new ListaTiquetes();

        Tiquete primero;
        primero =new Tiquete(1, "Ana", "101", 20, "Colones", "N");

        Tiquete segundo;
        segundo=new Tiquete(2, "Luis", "102", 25, "Colones", "N");

        pendientes.agregar(primero);
        pendientes.agregar(segundo);

        GestionBuses gestion;
        gestion =new GestionBuses(buses);

        Bus asignadoPrimero;
        asignadoPrimero=gestion.asignar(primero);

        Bus asignadoSegundo;
        asignadoSegundo =gestion.asignar(segundo);

        if(asignadoPrimero == null || asignadoSegundo == null){
            JOptionPane.showMessageDialog(null, "No se pudieron asignar los tiquetes de prueba");
            return;
        }

        ArchivoColas archivo;
        archivo=new ArchivoColas();

                try{
            archivo.guardar(buses, pendientes);

            long horaOriginal;
            horaOriginal=primero.getHoraAbordaje();

            ArchivoColas lector;
            lector =new ArchivoColas();

            ListaBuses recuperados;
            recuperados=lector.cargar(buses, pendientes);

            Bus busRecuperado;
            busRecuperado =recuperados.buscar(3);

            if(busRecuperado == null){
                JOptionPane.showMessageDialog(null, "No se recupero el bus 3");
                return;
            }

            Tiquete atendido;
            atendido=busRecuperado.getEnAtencion();

            Tiquete esperando;
            esperando =busRecuperado.getCola().consultar();

            if(atendido == null || esperando == null){
                JOptionPane.showMessageDialog(null, "Falta el tiquete en atencion o el que esperaba");
                return;
            }

            String resultado;
            resultado="PRUEBA DE RECUPERACION\n";

            resultado += "\nBus recuperado: "+busRecuperado.getNumero();
            resultado+= "\nTiquete en atencion: " +atendido.getNumero();
            resultado += "\nPersonas esperando: "+busRecuperado.contarEnEspera();
            resultado+= "\nPrimero en espera: "+ esperando.getNumero();
            resultado += "\nInspector libre: "+busRecuperado.inspectorLibre();
            resultado+= "\nConserva hora original: "+(atendido.getHoraAbordaje() == horaOriginal);
            resultado += "\nUsa el tiquete de pendientes: "+(atendido == pendientes.buscar(1));

            JOptionPane.showMessageDialog(null, resultado);

        }catch(IOException error){
            JOptionPane.showMessageDialog(null, "Error al guardar o recuperar las colas: "+error.getMessage());
        }
    }
}
