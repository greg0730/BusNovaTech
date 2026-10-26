/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 *
 * @author grego
 */
public class NodoTiquete {

    private Tiquete tiquete;
    private NodoTiquete siguiente;

    // crea un nodo que guarda un tiquete
    public NodoTiquete(Tiquete tiquete) {
        this.tiquete = tiquete;
        siguiente = null;
    }

    // consulta el tiquete guardado
    public Tiquete getTiquete() {
        return tiquete;
    }

    // consulta el siguiente nodo
    public NodoTiquete getSiguiente() {
        return siguiente;
    }

    // conecta el nodo con el siguiente
    public void setSiguiente(NodoTiquete siguiente) {
        this.siguiente = siguiente;
    }
}
