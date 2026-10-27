package busnovatech;

/**
 * conecta un usuario con el siguiente nodo
 * @author dfran
 */
public class NodoUsuario {

    private Usuario usuario;
    private NodoUsuario siguiente;

    // crea un nodo que guarda un usuario
    public NodoUsuario(Usuario usuario) {
        this.usuario = usuario;
        siguiente = null;
    }

    // consulta el usuario guardado
    public Usuario getUsuario() {
        return usuario;
    }

    // consulta el siguiente nodo
    public NodoUsuario getSiguiente() {
        return siguiente;
    }

    // conecta el nodo con el siguiente
    public void setSiguiente(NodoUsuario siguiente) {
        this.siguiente = siguiente;
    }
}
