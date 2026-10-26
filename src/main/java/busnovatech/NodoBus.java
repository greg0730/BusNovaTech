/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 * conecta un bus con el siguiente nodo
 * @author grego
 */
public class NodoBus {

    private Bus bus;
    private NodoBus siguiente;

    // crea un nodo con los datos del bus
    public NodoBus(Bus bus){
        this.bus=bus;
        siguiente = null;
    }

    // consulta el bus guardado
    public Bus getBus(){
        return bus;
    }

    // consulta el siguiente nodo
    public NodoBus getSiguiente(){
        return siguiente;
    }

    // conecta el nodo con otro
    public void setSiguiente(NodoBus siguiente){
        this.siguiente =siguiente;
    }
}
