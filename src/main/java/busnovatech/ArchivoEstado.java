/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * coordina el guardado de los tiquetes y sus colas
 * @author grego
 */
public class ArchivoEstado {

    private Path carpeta;
    private Path preparacion;
    private ListaTiquetes pendientes;
    private ListaBuses buses;
    private int siguienteNumero;

    // establece la carpeta donde se guardan los datos
    public ArchivoEstado(Path carpeta){
        this.carpeta=carpeta;
        preparacion = carpeta.resolve("guardado-pendiente");
    }

    // recupera un guardado interrumpido y carga los datos completos
    public void cargar(ListaBuses configurados) throws IOException{
        Files.createDirectories(carpeta);
        recuperar(configurados);

        ArchivoTiquetes archivoTiquetes;
        archivoTiquetes=new ArchivoTiquetes(carpeta);

        ListaTiquetes cargados;
        cargados = archivoTiquetes.cargar();

        ArchivoColas archivoColas;
        archivoColas=new ArchivoColas(carpeta);

        ListaBuses cargadosBuses;
        cargadosBuses =archivoColas.cargar(configurados, cargados);

        pendientes=cargados;
        buses = cargadosBuses;
        siguienteNumero=archivoTiquetes.getSiguienteNumero();
    }

    // prepara y comprueba ambos archivos antes de reemplazarlos
    public void guardar(ListaTiquetes lista, ListaBuses listaBuses,
            int siguiente) throws IOException{

        if(lista == null || listaBuses == null || siguiente < 1){
            throw new IOException("Los datos para guardar estan incompletos");
        }

        recuperar(listaBuses);
        Files.createDirectories(preparacion);

        ArchivoTiquetes archivoTiquetes;
        archivoTiquetes=new ArchivoTiquetes(preparacion);
        archivoTiquetes.guardar(lista, siguiente);

        ArchivoColas archivoColas;
        archivoColas =new ArchivoColas(preparacion);
        archivoColas.guardar(listaBuses, lista);

        comprobarPreparacion(listaBuses);

        // la marca indica que los dos archivos ya estan completos
        Files.writeString(preparacion.resolve("listo.txt"), "listo", StandardCharsets.UTF_8);
        recuperar(listaBuses);

        pendientes=lista;
        buses =listaBuses;
        siguienteNumero=siguiente;
    }

    // termina de copiar un guardado que ya habia sido preparado
    private void recuperar(ListaBuses configurados) throws IOException{

        String marca;
        try{
            marca=Files.readString(preparacion.resolve("listo.txt"), StandardCharsets.UTF_8);
        }catch(NoSuchFileException error){
            return;
        }

        if(!marca.equals("listo")){
            throw new IOException("La marca de guardado esta incompleta, conserve la carpeta guardado-pendiente para revisar los datos");
        }

        comprobarPreparacion(configurados);
        instalar("tiquetes.json");
        instalar("colas.json");

        // solo se quita la marca despues de copiar los dos archivos
        Files.delete(preparacion.resolve("listo.txt"));
    }

    // comprueba que los archivos preparados representen el mismo estado
    private void comprobarPreparacion(ListaBuses configurados) throws IOException{

        if(!Files.isRegularFile(preparacion.resolve("tiquetes.json"))
                || !Files.isRegularFile(preparacion.resolve("colas.json"))){
            throw new IOException("Falta un archivo del guardado pendiente");
        }

        ArchivoTiquetes archivoTiquetes;
        archivoTiquetes=new ArchivoTiquetes(preparacion);

        ListaTiquetes copia;
        copia =archivoTiquetes.cargar();

        ArchivoColas archivoColas;
        archivoColas=new ArchivoColas(preparacion);
        archivoColas.cargar(configurados, copia);
    }

    // reemplaza un archivo usando una copia temporal
    private void instalar(String nombre) throws IOException{

        Path origen;
        origen=preparacion.resolve(nombre);

        Path temporal;
        temporal =carpeta.resolve(nombre+".recuperando");

        Path destino;
        destino=carpeta.resolve(nombre);

        Files.copy(origen, temporal, StandardCopyOption.REPLACE_EXISTING);
        Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    // consulta los tiquetes recuperados
    public ListaTiquetes getPendientes(){
        return pendientes;
    }

    // consulta los buses recuperados
    public ListaBuses getBuses(){
        return buses;
    }

    // consulta el siguiente numero disponible
    public int getSiguienteNumero(){
        return siguienteNumero;
    }
}
