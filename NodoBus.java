public class NodoBus {
    private Bus dato;
    private NodoBus siguiente;

    public NodoBus(Bus dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Bus getDato() { return dato; }
    public NodoBus getSiguiente() { return siguiente; }
    public void setSiguiente(NodoBus siguiente) { this.siguiente = siguiente; }
}
