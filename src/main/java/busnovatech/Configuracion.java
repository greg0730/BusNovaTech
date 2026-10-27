package busnovatech;

/**
 * guarda el nombre de la terminal, la cantidad de buses y los usuarios
 * @author dfran
 */
public class Configuracion {

    public static final int MIN_BUSES = 3;
    public static final int MAX_BUSES = 99;
    public static final int MIN_USUARIOS = 2;

    private String nombreTerminal;
    private int cantidadBuses;
    private ListaUsuarios usuarios;

    // crea la configuracion con la lista de usuarios vacia
    public Configuracion(String nombreTerminal, int cantidadBuses) {

        if (nombreTerminal == null || nombreTerminal.trim().equals("")) {
            throw new IllegalArgumentException("El nombre de la terminal no puede estar vacio");
        }

        if (cantidadBuses < MIN_BUSES || cantidadBuses > MAX_BUSES) {
            throw new IllegalArgumentException("La cantidad de buses debe estar entre " + MIN_BUSES + " y " + MAX_BUSES);
        }

        this.nombreTerminal = nombreTerminal.trim();
        this.cantidadBuses = cantidadBuses;
        usuarios = new ListaUsuarios();
    }

    // consulta el nombre de la terminal
    public String getNombreTerminal() {
        return nombreTerminal;
    }

    // consulta la cantidad de buses configurada
    public int getCantidadBuses() {
        return cantidadBuses;
    }

    // permite consultar y registrar usuarios
    public ListaUsuarios getUsuarios() {
        return usuarios;
    }
}
