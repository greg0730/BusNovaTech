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
 * guarda las colas y la atencion de los buses
 * @author grego
 */
public class ArchivoColas {
    
        private String contenido;
    private int posicion;
        private Path carpeta;

    // usa la carpeta habitual del programa
    public ArchivoColas(){
        carpeta=Path.of(".");
    }

    // permite elegir donde guardar los archivos
    public ArchivoColas(Path carpeta){
        this.carpeta = carpeta;
    }

    // guarda los buses y sus tiquetes pendientes
    public void guardar(ListaBuses buses, ListaTiquetes pendientes) throws IOException{

        if(buses == null || pendientes == null){
            throw new IOException("Falta la lista de buses o de tiquetes");
        }

        ListaTiquetes registrados;
        registrados=new ListaTiquetes();

        String texto;
        texto="{\n";
        texto += "  \"buses\": [\n";

        NodoBus actual;
        actual=buses.getPrimero();

        while(actual != null){
            Bus bus;
            bus =actual.getBus();

            texto+= "    {\n";
            texto += "      \"numeroBus\": "+bus.getNumero()+",\n";

            Tiquete atendido;
            atendido=bus.getEnAtencion();

            int numeroAtencion;
            numeroAtencion =0;

            if(atendido != null){
                comprobarTiquete(atendido, bus, pendientes, registrados, true);
                numeroAtencion=atendido.getNumero();
            }

            texto+= "      \"enAtencion\": "+numeroAtencion+",\n";
            texto += "      \"espera\": [";

            NodoTiquete nodo;
            nodo=bus.getCola().getFrente();

            while(nodo != null){
                Tiquete tiquete;
                tiquete = nodo.getTiquete();

                comprobarTiquete(tiquete, bus, pendientes, registrados, false);

                texto+=tiquete.getNumero();
                nodo =nodo.getSiguiente();

                if(nodo != null){
                    texto += ", ";
                }
            }

            texto+= "]\n";
            texto += "    }";

            actual=actual.getSiguiente();

            if(actual != null){
                texto+=",";
            }

            texto += "\n";
        }

        texto+="  ]\n";
        texto += "}\n";

        comprobarPendientes(pendientes, registrados);

        Path temporal;
        temporal=carpeta.resolve("colas.tmp");

        Path archivo;
        archivo = carpeta.resolve("colas.json");

        Files.writeString(temporal, texto, StandardCharsets.UTF_8);
        Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
    }

    // revisa que el tiquete corresponda al bus y no este repetido
    private void comprobarTiquete(Tiquete tiquete, Bus bus,
            ListaTiquetes pendientes, ListaTiquetes registrados,
            boolean enAtencion) throws IOException{

        if(tiquete == null){
            throw new IOException("Hay un nodo sin tiquete");
        }

        Tiquete original;
        original=pendientes.buscar(tiquete.getNumero());

        if(original == null){
            throw new IOException("El tiquete "+tiquete.getNumero()+" no esta en pendientes");
        }

        if(original != tiquete){
            throw new IOException("El bus debe usar el mismo objeto tiquete de la lista de pendientes");
        }

        if(!bus.getTipo().equals(tiquete.getTipoBus())){
            throw new IOException("El tiquete no corresponde al tipo de bus");
        }

        if(enAtencion){
            if(tiquete.getHoraAbordaje() < 0){
                throw new IOException("Un tiquete en atencion no tiene hora de abordaje");
            }
        }else{
            if(tiquete.getHoraAbordaje() != -1){
                throw new IOException("Un tiquete en espera ya tiene hora de abordaje");
            }
        }

        boolean agregado;
        agregado =registrados.agregar(tiquete);

        if(!agregado){
            throw new IOException("El tiquete "+tiquete.getNumero()+" aparece mas de una vez");
        }
    }

    // verifica que todos los pendientes tengan un bus asignado
    private void comprobarPendientes(ListaTiquetes pendientes,
            ListaTiquetes registrados) throws IOException{

        NodoTiquete actual;
        actual=pendientes.getPrimero();

        while(actual != null){
            int numero;
            numero =actual.getTiquete().getNumero();

            Tiquete encontrado;
            encontrado= registrados.buscar(numero);

            if(encontrado == null){
                throw new IOException("El tiquete "+numero+" no tiene un bus asignado");
            }

            actual = actual.getSiguiente();
        }
    }
    
        // recupera las colas usando los buses configurados y los tiquetes cargados
    public ListaBuses cargar(ListaBuses configurados,
            ListaTiquetes pendientes) throws IOException{

        if(configurados == null || pendientes == null){
            throw new IOException("Falta la configuracion o la lista de pendientes");
        }

        Path archivo;
        archivo=carpeta.resolve("colas.json");

        try{
            contenido =Files.readString(archivo, StandardCharsets.UTF_8);
        }catch(java.nio.file.NoSuchFileException error){

            if(pendientes.getPrimero() != null){
                throw new IOException("Hay tiquetes pendientes pero falta colas.json");
            }

            return copiarBuses(configurados);
        }

        posicion=0;

        ListaBuses recuperados;
        recuperados =new ListaBuses();

        ListaTiquetes registrados;
        registrados=new ListaTiquetes();

        esperar("{");
        esperar("\"buses\"");
        esperar(":");
        esperar("[");

        if(verSiguiente() != ']'){
            boolean continuar;
            continuar=true;

            while(continuar){
                esperar("{");
                esperar("\"numeroBus\"");
                esperar(":");

                int numeroBus;
                numeroBus=leerNumero();

                Bus original;
                original =configurados.buscar(numeroBus);

                if(original == null){
                    throw new IOException("El bus "+numeroBus+" no existe en la configuracion");
                }

                Bus bus;
                bus=new Bus(numeroBus, original.getTipo());

                boolean agregado;
                agregado=recuperados.agregar(bus);

                if(!agregado){
                    throw new IOException("Hay un bus repetido en el archivo");
                }

                esperar(",");
                esperar("\"enAtencion\"");
                esperar(":");

                int numeroAtencion;
                numeroAtencion =leerNumero();

                if(numeroAtencion != 0){
                    Tiquete tiquete;
                    tiquete=pendientes.buscar(numeroAtencion);

                    if(tiquete == null){
                        throw new IOException("No existe el tiquete en atencion "+numeroAtencion);
                    }

                    comprobarTiquete(tiquete, bus, pendientes, registrados, true);

                    boolean restaurado;
                    restaurado =bus.restaurarAtencion(tiquete);

                    if(!restaurado){
                        throw new IOException("No se pudo recuperar la atencion del bus");
                    }
                }

                esperar(",");
                esperar("\"espera\"");
                esperar(":");
                esperar("[");

                if(verSiguiente() != ']'){
                    boolean quedanTiquetes;
                    quedanTiquetes=true;

                    while(quedanTiquetes){
                        int numeroTiquete;
                        numeroTiquete =leerNumero();

                        Tiquete tiquete;
                        tiquete=pendientes.buscar(numeroTiquete);

                        if(tiquete == null){
                            throw new IOException("No existe el tiquete en espera "+numeroTiquete);
                        }

                        comprobarTiquete(tiquete, bus, pendientes, registrados, false);

                        boolean encolado;
                        encolado=bus.agregarTiquete(tiquete);

                        if(!encolado){
                            throw new IOException("No se pudo recuperar un tiquete de la cola");
                        }

                        if(verSiguiente() == ']'){
                            quedanTiquetes = false;
                        }else{
                            esperar(",");
                        }
                    }
                }

                esperar("]");
                esperar("}");

                if(verSiguiente() == ']'){
                    continuar=false;
                }else{
                    esperar(",");
                }
            }
        }

        esperar("]");
        esperar("}");
        saltarEspacios();

        if(posicion != contenido.length()){
            throw new IOException("Hay contenido adicional al final del archivo");
        }

        NodoBus actual;
        actual=configurados.getPrimero();

        while(actual != null){
            int numero;
            numero =actual.getBus().getNumero();

            if(recuperados.buscar(numero) == null){
                throw new IOException("Falta el bus "+numero+" en colas.json");
            }

            actual=actual.getSiguiente();
        }

        comprobarPendientes(pendientes, registrados);

        return recuperados;
    }

    // crea buses vacios a partir de la configuracion
    private ListaBuses copiarBuses(ListaBuses configurados){

        ListaBuses copia;
        copia=new ListaBuses();

        NodoBus actual;
        actual =configurados.getPrimero();

        while(actual != null){
            Bus original;
            original=actual.getBus();

            Bus nuevo;
            nuevo =new Bus(original.getNumero(), original.getTipo());

            copia.agregar(nuevo);
            actual= actual.getSiguiente();
        }

        return copia;
    }

    // avanza sobre los espacios y saltos de linea
    private void saltarEspacios(){

        while(posicion < contenido.length()){
            char caracter;
            caracter=contenido.charAt(posicion);

            if(caracter == ' ' || caracter == '\n'
                    || caracter == '\r' || caracter == '\t'){
                posicion++;
            }else{
                return;
            }
        }
    }

    // comprueba un campo o separador del archivo
    private void esperar(String esperado) throws IOException{

        saltarEspacios();

        if(!contenido.startsWith(esperado, posicion)){
            throw new IOException("Se esperaba "+esperado+" en la posicion "+posicion);
        }

        posicion += esperado.length();
    }

    // consulta el siguiente caracter sin consumirlo
    private char verSiguiente() throws IOException{

        saltarEspacios();

        if(posicion >= contenido.length()){
            throw new IOException("El archivo de colas esta incompleto");
        }

        return contenido.charAt(posicion);
    }

    // lee un numero entero que no sea negativo
    private int leerNumero() throws IOException{

        saltarEspacios();

        int inicio;
        inicio=posicion;

        while(posicion < contenido.length()){
            char caracter;
            caracter =contenido.charAt(posicion);

            if(caracter >= '0' && caracter <= '9'){
                posicion++;
            }else{
                break;
            }
        }

        if(inicio == posicion){
            throw new IOException("Se esperaba un numero entero no negativo");
        }

        if(posicion - inicio > 1 && contenido.charAt(inicio) == '0'){
            throw new IOException("Hay un numero con ceros iniciales");
        }

        String texto;
        texto=contenido.substring(inicio, posicion);

        try{
            int numero;
            numero =Integer.parseInt(texto);

            return numero;
        }catch(NumberFormatException error){
            throw new IOException("Un numero supera el rango permitido");
        }
    }
}
