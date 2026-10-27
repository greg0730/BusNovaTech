package busnovatech;

/**
 * guarda las credenciales de un usuario
 * @author dfran
 */
public class Usuario {

    private String nombre;
    private String clave;

    // crea el usuario con su nombre y contrasena
    public Usuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
    }

    // consulta el nombre de usuario
    public String getNombre() {
        return nombre;
    }

    // consulta la contrasena
    public String getClave() {
        return clave;
    }
}
