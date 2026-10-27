package busnovatech;

/**
 * mantiene los usuarios que pueden iniciar sesion
 * @author dfran
 */
public class ListaUsuarios {

    private NodoUsuario primero;
    private NodoUsuario ultimo;

    // crea la lista vacia
    public ListaUsuarios() {
        primero = null;
        ultimo = null;
    }

    // agrega un usuario al final sin repetir el nombre
    public boolean agregar(Usuario usuario) {

        if (usuario == null) {
            return false;
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().equals("")
                || usuario.getClave() == null || usuario.getClave().equals("")) {
            return false;
        }

        Usuario encontrado;
        encontrado = buscar(usuario.getNombre());

        if (encontrado != null) {
            return false;
        }

        NodoUsuario nuevo;
        nuevo = new NodoUsuario(usuario);

        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        return true;
    }

    // busca un usuario por su nombre sin distinguir mayusculas
    public Usuario buscar(String nombre) {

        if (nombre == null) {
            return null;
        }

        NodoUsuario actual;
        actual = primero;

        while (actual != null) {
            Usuario usuario;
            usuario = actual.getUsuario();

            if (usuario.getNombre().equalsIgnoreCase(nombre.trim())) {
                return usuario;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // comprueba el nombre y la contrasena de un inicio de sesion
    public boolean validar(String nombre, String clave) {

        Usuario usuario;
        usuario = buscar(nombre);

        if (usuario == null || clave == null) {
            return false;
        }

        return usuario.getClave().equals(clave);
    }

    // cuenta los usuarios registrados
    public int contar() {

        int cantidad;
        cantidad = 0;

        NodoUsuario actual;
        actual = primero;

        while (actual != null) {
            cantidad++;
            actual = actual.getSiguiente();
        }

        return cantidad;
    }

    // permite recorrer los usuarios de la lista
    public NodoUsuario getPrimero() {
        return primero;
    }
}
