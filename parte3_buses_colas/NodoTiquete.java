/**
 * Nodo de la lista enlazada que usa ColaTiquetes.
 * @author Gabriel
 */
public class NodoTiquete {
    private Tiquete dato;
    private NodoTiquete siguiente;

    public NodoTiquete(Tiquete dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Tiquete getDato() { return dato; }
    public NodoTiquete getSiguiente() { return siguiente; }
    public void setSiguiente(NodoTiquete siguiente) { this.siguiente = siguiente; }
}
