package busnovatech;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * muestra el menu general y conecta los modulos de los demas integrantes
 * @author dfran
 */
public class MenuPrincipal {

    private Configuracion config;
    private String usuario;
    private ListaTiquetes tiquetes;
    private int siguienteNumero;
    private ListaBuses buses;
    private ArchivoEstado archivoEstado;
    private GestionTiquetes gestionTiquetes;
    private GestionBuses gestionBuses;

    // recibe los datos que se cargaron al iniciar el programa
    public MenuPrincipal(Configuracion config, String usuario,
            ListaTiquetes tiquetes, int siguienteNumero, ListaBuses buses,
            ArchivoEstado archivoEstado) {

        this.config = config;
        this.usuario = usuario;
        this.tiquetes = tiquetes;
        this.siguienteNumero = siguienteNumero;
        this.buses = buses;
        this.archivoEstado = archivoEstado;

        gestionTiquetes = new GestionTiquetes();
        gestionBuses = new GestionBuses(buses);
    }

    // repite el menu hasta que el usuario decida salir
    public void iniciar() {

        boolean continuar;
        continuar = true;

        while (continuar) {
            String opcion;
            opcion = JOptionPane.showInputDialog(
                    "--- " + config.getNombreTerminal() + " ---\n"
                    + "Usuario: " + usuario + "\n\n"
                    + "1) Registrar tiquete\n"
                    + "2) Consultar filas\n"
                    + "3) Iniciar atencion del siguiente tiquete (abordar)\n"
                    + "4) Atender tiquete: servicio y cobro\n"
                    + "5) Consultar historial de atendidos\n"
                    + "6) Consultar tipo de cambio (BCCR)\n"
                    + "7) Ver configuracion\n"
                    + "8) Salir\n\n"
                    + "Seleccione una opcion");

            if (opcion == null) {
                continuar = false;

            } else {
                opcion = opcion.trim();

                switch (opcion) {
                    case "1":
                        continuar = registrarTiquete();
                        break;

                    case "2":
                        mostrarFilas();
                        break;

                    case "3":
                        continuar = abordar();
                        break;

                    case "4":
                        mostrarPendiente("la atencion con servicio y cobro");
                        break;

                    case "5":
                        mostrarPendiente("el historial de atendidos");
                        break;

                    case "6":
                        mostrarPendiente("la consulta del tipo de cambio");
                        break;

                    case "7":
                        JOptionPane.showMessageDialog(null, describirConfiguracion());
                        break;

                    case "8":
                        continuar = false;
                        break;

                    default:
                        JOptionPane.showMessageDialog(null, "Seleccione una opcion del 1 al 8");
                        break;
                }
            }
        }
    }

    // avisa que la opcion todavia depende de otro integrante
    private void mostrarPendiente(String modulo) {

        JOptionPane.showMessageDialog(null,
                "Esta opcion todavia no esta disponible: " + modulo);
    }

    // pide los datos del tiquete y lo registra; devuelve false si el programa debe cerrarse
    private boolean registrarTiquete() {

        if (siguienteNumero == Integer.MAX_VALUE) {
            JOptionPane.showMessageDialog(null, "Se alcanzo el limite de numeros de tiquete");
            return true;
        }

        Tiquete tiquete;
        tiquete = gestionTiquetes.crearTiquete(siguienteNumero);

        if (tiquete == null) {
            JOptionPane.showMessageDialog(null, "Se cancelo la creacion del tiquete");
            return true;
        }

        try {
            String mensaje;
            mensaje = procesarTiquete(tiquete);

            if (mensaje == null) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo asignar el tiquete a un bus. No se registro");
            } else {
                JOptionPane.showMessageDialog(null, mensaje);
            }

            return true;

        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, "No se pudo completar el guardado: "
                    + error.getMessage() + "\nEl programa se cerrara para revisar los archivos");
            return false;
        }
    }

    // registra el tiquete como pendiente, lo asigna a un bus y guarda los archivos
    String procesarTiquete(Tiquete tiquete) throws IOException {

        boolean agregado;
        agregado = tiquetes.agregar(tiquete);

        if (!agregado) {
            return null;
        }

        Bus bus;
        bus = gestionBuses.asignar(tiquete);

        if (bus == null) {
            tiquetes.retirar(tiquete.getNumero());
            return null;
        }

        siguienteNumero = tiquete.getNumero() + 1;
        guardarDatos();

        String mensaje;
        mensaje = "Tiquete " + tiquete.getNumero() + " registrado en el bus "
                + bus.getNumero() + " (" + nombreTipo(bus.getTipo()) + ")\n";

        if (bus.getEnAtencion() == tiquete) {
            mensaje += "El inspector estaba disponible: el tiquete paso a atencion";
        } else {
            mensaje += "Personas esperando en este bus: " + bus.contarEnEspera();
        }

        return mensaje;
    }

    // pide el numero del bus e inicia la atencion de su siguiente tiquete
    private boolean abordar() {

        String texto;
        texto = JOptionPane.showInputDialog("Ingrese el numero del bus");

        if (texto == null) {
            return true;
        }

        int numeroBus;

        try {
            numeroBus = Integer.parseInt(texto.trim());
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un numero entero");
            return true;
        }

        try {
            JOptionPane.showMessageDialog(null, iniciarAtencion(numeroBus));
            return true;

        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, "No se pudo completar el guardado: "
                    + error.getMessage() + "\nEl programa se cerrara para revisar los archivos");
            return false;
        }
    }

    // pasa el siguiente tiquete de la fila al inspector del bus si esta disponible
    String iniciarAtencion(int numeroBus) throws IOException {

        Bus bus;
        bus = buses.buscar(numeroBus);

        if (bus == null) {
            return "No existe el bus " + numeroBus;
        }

        if (!bus.inspectorLibre()) {
            return "El inspector del bus " + numeroBus + " esta ocupado con el tiquete "
                    + bus.getEnAtencion().getNumero();
        }

        Tiquete tiquete;
        tiquete = gestionBuses.abordar(numeroBus);

        if (tiquete == null) {
            return "No hay tiquetes esperando en el bus " + numeroBus;
        }

        guardarDatos();

        return "Inicio la atencion del tiquete " + tiquete.getNumero()
                + " (" + tiquete.getNombre() + ") en el bus " + numeroBus;
    }

    // guarda los tiquetes y las colas para que los archivos coincidan
    private void guardarDatos() throws IOException {

        archivoEstado.guardar(tiquetes, buses, siguienteNumero);
    }

    // muestra las filas en una ventana con desplazamiento
    private void mostrarFilas() {

        JTextArea area;
        area = new JTextArea(describirFilas(), 18, 40);
        area.setEditable(false);

        JOptionPane.showMessageDialog(null, new JScrollPane(area),
                "Filas de la terminal", JOptionPane.INFORMATION_MESSAGE);
    }

    // arma el texto con el inspector y la fila de cada bus
    String describirFilas() {

        String texto;
        texto = "";

        NodoBus actual;
        actual = buses.getPrimero();

        while (actual != null) {
            Bus bus;
            bus = actual.getBus();

            texto += "BUS " + bus.getNumero() + " (" + nombreTipo(bus.getTipo()) + ")\n";

            Tiquete enAtencion;
            enAtencion = bus.getEnAtencion();

            if (enAtencion == null) {
                texto += "Inspector: disponible\n";
            } else {
                texto += "Inspector: ocupado con el tiquete " + enAtencion.getNumero()
                        + " (" + enAtencion.getNombre() + ")\n";
            }

            texto += "En espera: " + bus.contarEnEspera() + "\n";

            NodoTiquete nodo;
            nodo = bus.getCola().getFrente();

            int posicion;
            posicion = 1;

            while (nodo != null) {
                Tiquete espera;
                espera = nodo.getTiquete();

                texto += "   " + posicion + ") Tiquete " + espera.getNumero()
                        + " - " + espera.getNombre() + "\n";

                nodo = nodo.getSiguiente();
                posicion++;
            }

            texto += "\n";
            actual = actual.getSiguiente();
        }

        return texto;
    }

    // arma el texto con la configuracion sin mostrar las contrasenas
    String describirConfiguracion() {

        String texto;
        texto = "Terminal: " + config.getNombreTerminal() + "\n";
        texto += "Cantidad de buses: " + config.getCantidadBuses() + "\n";
        texto += "Usuarios registrados: " + config.getUsuarios().contar() + "\n";

        NodoUsuario actual;
        actual = config.getUsuarios().getPrimero();

        while (actual != null) {
            texto += "- " + actual.getUsuario().getNombre() + "\n";
            actual = actual.getSiguiente();
        }

        return texto;
    }

    // convierte la letra del tipo de bus en su nombre
    private String nombreTipo(String tipo) {

        if ("P".equals(tipo)) {
            return "Preferencial";
        }

        if ("D".equals(tipo)) {
            return "Directo";
        }

        return "Normal";
    }
}
