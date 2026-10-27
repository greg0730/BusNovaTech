/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * comprueba el guardado sin modificar los datos de la prueba manual
 * @author grego
 */
public class PruebaGuardadoEstado {

    private static int comprobaciones;

    // ejecuta las pruebas en una carpeta temporal independiente
    public static void main(String[] args) throws IOException{
        comprobaciones=0;

        Path carpeta;
        carpeta=Files.createTempDirectory("busnovatech-prueba-");
        System.out.println("Carpeta de prueba: "+carpeta);

        ListaBuses configurados;
        configurados=crearBuses();

        ArchivoEstado archivo;
        archivo =new ArchivoEstado(carpeta);
        archivo.cargar(configurados);
        comprobar(archivo.getPendientes().getPrimero() == null, "inicio sin tiquetes");
        comprobar(archivo.getSiguienteNumero() == 1, "numeracion inicial");

        ListaTiquetes pendientes;
        pendientes=archivo.getPendientes();

        ListaBuses buses;
        buses = archivo.getBuses();

        GestionBuses gestion;
        gestion=new GestionBuses(buses);

        Tiquete primero;
        primero=new Tiquete(1, "Ana \"Maria\" \\ prueba\nsegunda linea", "101", 20, "Colones", "N");
        agregar(pendientes, gestion, primero, 3);
        agregar(pendientes, gestion, new Tiquete(2, "Luis", "102", 30, "Dolares", "N"), 3);
        agregar(pendientes, gestion, new Tiquete(3, "Eva", "103", 40, "Colones", "N"), 4);
        agregar(pendientes, gestion, new Tiquete(4, "Juan", "104", 50, "Colones", "N"), 4);
        agregar(pendientes, gestion, new Tiquete(5, "Rosa", "105", 70, "Colones", "P"), 1);
        agregar(pendientes, gestion, new Tiquete(6, "Pedro", "106", 25, "Dolares", "D"), 2);

        comprobar(!pendientes.agregar(primero), "rechaza numeros repetidos");
        comprobar(gestion.asignar(primero) == null, "rechaza asignar dos veces el mismo tiquete");
        comprobar(buses.buscar(3).getEnAtencion() == primero, "atencion automatica");
        comprobar(buses.buscar(3).getCola().consultar().getNumero() == 2, "cola del bus 3");
        comprobar(buses.buscar(4).getCola().consultar().getNumero() == 4, "cola del bus 4");
        comprobar(buses.buscar(3).iniciarAtencion() == null, "inspector ocupado no retira otro tiquete");
        comprobar(buses.buscar(3).contarEnEspera() == 1, "conserva la cola mientras atiende");

        long hora;
        hora=primero.getHoraAbordaje();
        archivo.guardar(pendientes, buses, 7);

        ArchivoEstado cargado;
        cargado=new ArchivoEstado(carpeta);
        cargado.cargar(configurados);

        comprobar(cargado.getSiguienteNumero() == 7, "conserva el siguiente numero");
        comprobar(cargado.getPendientes().buscar(1).getNombre().equals(primero.getNombre()), "recupera los caracteres especiales");
        comprobar(cargado.getPendientes().buscar(1).getHoraAbordaje() == hora, "conserva la hora de abordaje");
        comprobar(cargado.getPendientes().buscar(2).getHoraAbordaje() == -1, "el tiquete en espera no tiene abordaje");
        comprobar(cargado.getBuses().buscar(3).getEnAtencion() == cargado.getPendientes().buscar(1), "comparte el mismo objeto cargado");
        comprobar(cargado.getBuses().buscar(3).getCola().consultar().getNumero() == 2, "recupera la cola del bus 3");
        comprobar(cargado.getBuses().buscar(4).getCola().consultar().getNumero() == 4, "recupera la cola del bus 4");
        comprobar(cargado.getBuses().buscar(1).getEnAtencion().getNumero() == 5, "recupera el bus preferencial");
        comprobar(cargado.getBuses().buscar(2).getEnAtencion().getNumero() == 6, "recupera el bus directo");

        String tiquetesAntes;
        tiquetesAntes=Files.readString(carpeta.resolve("tiquetes.json"));
        String colasAntes;
        colasAntes=Files.readString(carpeta.resolve("colas.json"));

        // intenta guardar un tiquete que todavia no tiene bus
        Tiquete sinBus;
        sinBus=new Tiquete(7, "Sin asignar", "107", 20, "Colones", "N");
        pendientes.agregar(sinBus);
        boolean rechazado;
        rechazado=false;
        try{
            archivo.guardar(pendientes, buses, 8);
        }catch(IOException error){
            rechazado=true;
        }
        comprobar(rechazado, "rechaza datos incompletos");
        comprobar(Files.readString(carpeta.resolve("tiquetes.json")).equals(tiquetesAntes), "conserva tiquetes tras un guardado rechazado");
        comprobar(Files.readString(carpeta.resolve("colas.json")).equals(colasAntes), "conserva colas tras un guardado rechazado");
        pendientes.retirar(7);

        // deja preparada una operacion y copia solamente su primer archivo
        Tiquete nuevo;
        nuevo=new Tiquete(7, "Nuevo", "107", 21, "Colones", "N");
        agregar(pendientes, gestion, nuevo, 3);

        Path preparacion;
        preparacion=carpeta.resolve("guardado-pendiente");
        ArchivoTiquetes preparadosTiquetes;
        preparadosTiquetes=new ArchivoTiquetes(preparacion);
        preparadosTiquetes.guardar(pendientes, 8);
        ArchivoColas preparadasColas;
        preparadasColas=new ArchivoColas(preparacion);
        preparadasColas.guardar(buses, pendientes);
        Files.writeString(preparacion.resolve("listo.txt"), "listo", StandardCharsets.UTF_8);
        Files.copy(preparacion.resolve("tiquetes.json"), carpeta.resolve("tiquetes.json"), StandardCopyOption.REPLACE_EXISTING);

        ArchivoEstado recuperado;
        recuperado=new ArchivoEstado(carpeta);
        recuperado.cargar(configurados);
        comprobar(recuperado.getSiguienteNumero() == 8, "recupera la numeracion del guardado interrumpido");
        comprobar(recuperado.getPendientes().buscar(7) != null, "recupera el nuevo tiquete");
        comprobar(recuperado.getBuses().buscar(3).contarEnEspera() == 2, "completa el archivo de colas pendiente");
        NodoTiquete frente;
        frente=recuperado.getBuses().buscar(3).getCola().getFrente();
        comprobar(frente.getTiquete().getNumero() == 2 && frente.getSiguiente().getTiquete().getNumero() == 7, "respeta el orden despues de recuperar");
        comprobar(!Files.exists(preparacion.resolve("listo.txt")), "retira la marca al terminar");

        // comprueba que un archivo preparado incompleto no reemplace los datos
        tiquetesAntes=Files.readString(carpeta.resolve("tiquetes.json"));
        colasAntes=Files.readString(carpeta.resolve("colas.json"));
        Files.writeString(preparacion.resolve("listo.txt"), "listo", StandardCharsets.UTF_8);
        Files.delete(preparacion.resolve("colas.json"));
        rechazado=false;
        try{
            recuperado.cargar(configurados);
        }catch(IOException error){
            rechazado=true;
        }
        comprobar(rechazado, "detiene la carga si falta parte del guardado pendiente");
        comprobar(Files.readString(carpeta.resolve("tiquetes.json")).equals(tiquetesAntes), "no sobrescribe tiquetes con una preparacion incompleta");
        comprobar(Files.readString(carpeta.resolve("colas.json")).equals(colasAntes), "no sobrescribe colas con una preparacion incompleta");

        System.out.println("PRUEBAS COMPLETADAS: "+comprobaciones);
        System.out.println("Los datos de prueba-integracion no fueron modificados");
    }

    // crea los buses utilizados solamente por esta prueba
    private static ListaBuses crearBuses(){
        ListaBuses buses;
        buses=new ListaBuses();
        buses.agregar(new Bus(1, "P"));
        buses.agregar(new Bus(2, "D"));
        buses.agregar(new Bus(3, "N"));
        buses.agregar(new Bus(4, "N"));
        return buses;
    }

    // registra un tiquete y verifica el bus seleccionado
    private static void agregar(ListaTiquetes pendientes, GestionBuses gestion,
            Tiquete tiquete, int numeroBus){
        comprobar(pendientes.agregar(tiquete), "registra el tiquete "+tiquete.getNumero());
        Bus bus;
        bus=gestion.asignar(tiquete);
        comprobar(bus != null && bus.getNumero() == numeroBus, "asigna el bus del tiquete "+tiquete.getNumero());
    }

    // detiene la prueba si un resultado no coincide con lo esperado
    private static void comprobar(boolean resultado, String mensaje){
        if(!resultado){
            throw new IllegalStateException("FALLO: "+mensaje);
        }
        comprobaciones++;
        System.out.println("OK: "+mensaje);
    }
}
