/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

/**
 * permite probar la integracion de tiquetes y colas
 * @author grego
 */
public class PruebaIntegracion {

    // inicia la prueba y muestra las opciones disponibles
    public static void main(String[] args){

        Path carpeta;
        carpeta=Path.of("prueba-integracion");

        ListaBuses configurados;
        configurados =new ListaBuses();

        // estos cuatro buses se usan solamente en esta prueba
        configurados.agregar(new Bus(1, "P"));
        configurados.agregar(new Bus(2, "D"));
        configurados.agregar(new Bus(3, "N"));
        configurados.agregar(new Bus(4, "N"));

        ArchivoEstado archivo;
        archivo=new ArchivoEstado(carpeta);

        try{
            archivo.cargar(configurados);
        }catch(IOException error){
            JOptionPane.showMessageDialog(null, "No se pudieron cargar los datos\n"+error.getMessage()+"\nConserve los archivos para revisarlos");
            return;
        }

        ListaTiquetes pendientes;
        pendientes=archivo.getPendientes();

        ListaBuses buses;
        buses = archivo.getBuses();

        int siguienteNumero;
        siguienteNumero=archivo.getSiguienteNumero();

        GestionTiquetes gestionTiquetes;
        gestionTiquetes=new GestionTiquetes();

        GestionBuses gestionBuses;
        gestionBuses =new GestionBuses(buses);

        boolean continuar;
        continuar=true;

        while(continuar){
            String opcion;
            opcion=JOptionPane.showInputDialog("PRUEBA DE INTEGRACION\n"
                    + "1) Crear tiquete\n"
                       +"2) Consultar buses\n"
                  + "3) Salir\n"
                       + "4) Buscar tiquete\n"
                    +"5) Consultar resumen");

            if(opcion == null){
                break;
            }

            opcion=opcion.trim();

            switch(opcion){
                case "1":{
                    if(siguienteNumero == Integer.MAX_VALUE){
                        JOptionPane.showMessageDialog(null, "Se alcanzo el limite de numeracion");
                        break;
                    }

                    Tiquete tiquete;
                    tiquete=gestionTiquetes.crearTiquete(siguienteNumero);

                    if(tiquete == null){
                        break;
                    }

                    boolean agregado;
                    agregado =pendientes.agregar(tiquete);

                    if(!agregado){
                        JOptionPane.showMessageDialog(null, "No se pudo registrar el tiquete");
                        break;
                    }

                    Bus bus;
                    bus=gestionBuses.asignar(tiquete);

                    if(bus == null){
                        pendientes.retirar(tiquete.getNumero());
                        JOptionPane.showMessageDialog(null, "No se pudo asignar un bus al tiquete");
                        break;
                    }

                    int nuevoNumero;
                    nuevoNumero =siguienteNumero+1;

                    try{
                        archivo.guardar(pendientes, buses, nuevoNumero);
                    }catch(IOException error){
                        JOptionPane.showMessageDialog(null, "No se pudo completar el guardado\n"+error.getMessage()+"\nLa prueba se cerrara, vuelva a abrirla para recuperar el estado\nRevise si el tiquete quedo registrado antes de crearlo otra vez");
                        return;
                    }

                    siguienteNumero=nuevoNumero;

                    String estado;
                    if(bus.getEnAtencion() == tiquete){
                        estado="En atencion";
                    }else{
                        estado = "En espera";
                    }

                    JOptionPane.showMessageDialog(null, "Tiquete guardado: "+tiquete.getNumero()+"\nCliente: "+tiquete.getNombre()+"\nBus asignado: "+bus.getNumero()+"\nEstado: "+estado);
                    break;
                }

                case "2":
                    consultarBuses(buses);
                    break;

                case "3":
                    continuar=false;
                    break;

                case "4":
                    buscarTiquete(pendientes, gestionBuses);
                    break;

                case "5":
                    mostrarResumen(buses, siguienteNumero);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Seleccione una opcion del 1 al 5");
                    break;
            }
        }
    }

    // muestra la atencion y el orden de espera de cada bus
    private static void consultarBuses(ListaBuses buses){

        NodoBus actual;
        actual=buses.getPrimero();

        while(actual != null){
            Bus bus;
            bus =actual.getBus();

            String texto;
            texto="BUS "+bus.getNumero()+"\nTipo: "+bus.getTipo()+"\n";

            Tiquete atendido;
            atendido=bus.getEnAtencion();

            if(atendido == null){
                texto += "Inspector libre\n";
            }else{
                texto+="En atencion: "+atendido.getNumero()+"\n";
                texto += "Cliente: "+atendido.getNombre()+"\n";
                texto+="Hora de abordaje: "+mostrarHora(atendido.getHoraAbordaje())+"\n";
            }

            texto += "Personas esperando: "+bus.contarEnEspera()+"\n";
            texto+="Orden de espera: ";

            NodoTiquete nodo;
            nodo=bus.getCola().getFrente();

            if(nodo == null){
                texto += "ninguno";
            }

            while(nodo != null){
                texto+=nodo.getTiquete().getNumero();
                nodo = nodo.getSiguiente();

                if(nodo != null){
                    texto+=" -> ";
                }
            }

            JOptionPane.showMessageDialog(null, texto);
            actual=actual.getSiguiente();
        }
    }

    // busca un tiquete pendiente y muestra su bus y estado
    private static void buscarTiquete(ListaTiquetes pendientes,
            GestionBuses gestionBuses){

        String entrada;
        entrada=JOptionPane.showInputDialog("Ingrese el numero del tiquete");

        if(entrada == null){
            return;
        }

        int numero;
        try{
            numero=Integer.parseInt(entrada.trim());
        }catch(NumberFormatException error){
            JOptionPane.showMessageDialog(null, "Ingrese un numero entero valido");
            return;
        }

        if(numero < 1){
            JOptionPane.showMessageDialog(null, "El numero debe ser positivo");
            return;
        }

        Tiquete tiquete;
        tiquete=pendientes.buscar(numero);

        if(tiquete == null){
            JOptionPane.showMessageDialog(null, "No hay un tiquete pendiente con ese numero");
            return;
        }

        Bus bus;
        bus=gestionBuses.buscarBusDelTiquete(numero);

        if(bus == null){
            JOptionPane.showMessageDialog(null, "El tiquete no tiene un bus asignado, revise los datos");
            return;
        }

        String estado;
        if(bus.getEnAtencion() == tiquete){
            estado="En atencion";
        }else{
            estado ="En espera";
        }

        String texto;
        texto="Tiquete: "+tiquete.getNumero()+"\n";
        texto += "Nombre: "+tiquete.getNombre()+"\n";
        texto+="Identificacion: "+tiquete.getIdentificacion()+"\n";
        texto += "Edad: "+tiquete.getEdad()+"\n";
        texto+="Moneda: "+tiquete.getMoneda()+"\n";
        texto += "Tipo de bus: "+tiquete.getTipoBus()+"\n";
        texto+="Bus asignado: "+bus.getNumero()+"\n";
        texto += "Estado: "+estado+"\n";
        texto+="Servicio: "+tiquete.getServicio()+"\n";
        texto += "Compra: "+mostrarHora(tiquete.getHoraCompra())+"\n";
        texto+="Abordaje: "+mostrarHora(tiquete.getHoraAbordaje());

        JOptionPane.showMessageDialog(null, texto);
    }

    // cuenta los tiquetes y los inspectores ocupados
    private static void mostrarResumen(ListaBuses buses, int siguienteNumero){

        int cantidadBuses;
        cantidadBuses=0;

        int esperando;
        esperando =0;

        int enAtencion;
        enAtencion=0;

        NodoBus actual;
        actual =buses.getPrimero();

        while(actual != null){
            Bus bus;
            bus=actual.getBus();

            cantidadBuses++;
            esperando+=bus.contarEnEspera();

            if(!bus.inspectorLibre()){
                enAtencion++;
            }

            actual=actual.getSiguiente();
        }

        int total;
        total=esperando+enAtencion;

        JOptionPane.showMessageDialog(null, "Buses: "+cantidadBuses+"\nEn espera: "+esperando+"\nEn atencion: "+enAtencion+"\nTiquetes pendientes: "+total+"\nSiguiente numero: "+siguienteNumero);
    }

    // presenta la fecha usando la zona horaria de la computadora
    private static String mostrarHora(long hora){

        if(hora == -1){
            return "Todavia no aborda";
        }

        DateTimeFormatter formato;
        formato=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String texto;
        texto = Instant.ofEpochMilli(hora).atZone(ZoneId.systemDefault()).format(formato);

        return texto;
    }
}
