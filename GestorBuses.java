import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Administra los buses, sus colas y la asignación de tiquetes.
 *
 * Supuestos de integración (a acordar con el grupo):
 *  - Tiquete tiene int getId() (único) y String getTipoBus() ("preferencial", "directo" o "normal").
 *  - colas.json guarda solo los ids de los tiquetes; los datos completos están en tiquetes.json.
 *
 * @author Gabriel
 */
public class GestorBuses {
    public static final int MINIMO_BUSES = 3; // 1 preferencial + 1 directo + al menos 1 normal

    private ListaBuses buses;
    private AtencionListener listener;

    /**
     * Crea los buses según la configuración: 1 preferencial, 1 directo y el resto normales.
     * @param cantidadBuses cantidad total (leída de config.json), mínimo 3
     */
    public GestorBuses(int cantidadBuses) {
        if (cantidadBuses < MINIMO_BUSES) {
            throw new IllegalArgumentException("La cantidad de buses debe ser al menos " + MINIMO_BUSES);
        }
        buses = new ListaBuses();
        buses.agregar(new Bus(1, Bus.PREFERENCIAL));
        buses.agregar(new Bus(2, Bus.DIRECTO));
        for (int i = 3; i <= cantidadBuses; i++) {
            buses.agregar(new Bus(i, Bus.NORMAL));
        }
    }

    public ListaBuses getBuses() { return buses; }

    /** La persona 4 registra aquí quién atiende los tiquetes. */
    public void setListener(AtencionListener listener) { this.listener = listener; }

    /**
     * Asigna el tiquete según su tipo de bus. Si llega a una fila vacía y el inspector
     * está disponible, inicia la atención automáticamente.
     * @return el bus asignado
     */
    public Bus asignar(Tiquete t) {
        Bus destino;
        String tipo = t.getTipoBus();
        if (tipo.equals(Bus.PREFERENCIAL)) {
            destino = buses.buscarPorTipo(Bus.PREFERENCIAL);
        } else if (tipo.equals(Bus.DIRECTO)) {
            destino = buses.buscarPorTipo(Bus.DIRECTO);
        } else {
            destino = buses.normalConMenosPersonas();
        }
        destino.getCola().encolar(t);
        if (destino.getCola().getTamano() == 1 && !destino.isInspectorOcupado()) {
            atenderSiguiente(destino);
        }
        return destino;
    }

    /**
     * Saca el siguiente tiquete de la fila del bus y marca al inspector como ocupado.
     * @return el tiquete, o null si la fila está vacía o el inspector está ocupado
     */
    public Tiquete obtenerSiguiente(Bus bus) {
        if (bus.isInspectorOcupado() || bus.getCola().estaVacia()) {
            return null;
        }
        bus.setInspectorOcupado(true);
        return bus.getCola().desencolar();
    }

    /** Toma el siguiente de la fila y se lo pasa al listener (persona 4). */
    public void atenderSiguiente(Bus bus) {
        Tiquete t = obtenerSiguiente(bus);
        if (t != null && listener != null) {
            listener.atender(bus, t);
        }
    }

    /**
     * La persona 4 la llama al terminar de atender (abordó o rechazó el pago).
     * Deja al inspector disponible y, si hay más gente en fila, atiende al siguiente.
     */
    public void liberarInspector(Bus bus) {
        bus.setInspectorOcupado(false);
        atenderSiguiente(bus);
    }

    /** Retira un tiquete de cualquier fila por su id. @return true si lo encontró. */
    public boolean retirarTiquete(int id) {
        NodoBus actual = buses.getCabeza();
        while (actual != null) {
            if (actual.getDato().getCola().remover(id)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /** Texto con todas las filas, para mostrar en el menú. */
    public String consultarFilas() {
        StringBuilder sb = new StringBuilder();
        NodoBus actual = buses.getCabeza();
        while (actual != null) {
            Bus b = actual.getDato();
            sb.append("Bus ").append(b.getNumero()).append(" (").append(b.getTipo()).append(") - ")
              .append(b.isInspectorOcupado() ? "inspector ocupado" : "inspector disponible")
              .append(" - ").append(b.getCola().getTamano()).append(" en fila\n");
            Tiquete[] fila = b.getCola().aArreglo();
            for (int i = 0; i < fila.length; i++) {
                sb.append("   ").append(i + 1).append(". tiquete #").append(fila[i].getId()).append("\n");
            }
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }

    /** Guarda la distribución y el orden de las filas en colas.json. */
    public void guardar(String ruta) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"buses\": [\n");
        NodoBus actual = buses.getCabeza();
        while (actual != null) {
            Bus b = actual.getDato();
            sb.append("    {\"numero\":").append(b.getNumero())
              .append(",\"tipo\":\"").append(b.getTipo()).append("\"")
              .append(",\"inspectorOcupado\":").append(b.isInspectorOcupado())
              .append(",\"cola\":[").append(b.getCola().idsComoTexto()).append("]}");
            actual = actual.getSiguiente();
            sb.append(actual != null ? ",\n" : "\n");
        }
        sb.append("  ]\n}\n");
        Files.write(Paths.get(ruta), sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Reconstruye las filas desde colas.json, enlazando cada id con el tiquete real
     * de la lista de pendientes (cargada por la persona 2 desde tiquetes.json).
     * Si el archivo no existe, las filas quedan vacías.
     * @param pendientes arreglo de tiquetes pendientes (puede traer posiciones null)
     * @return true si cargó el archivo; false si no existe o no se pudo leer
     */
    public boolean cargar(String ruta, Tiquete[] pendientes) {
        String contenido;
        try {
            if (!Files.exists(Paths.get(ruta))) {
                return false;
            }
            contenido = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return false;
        }
        Pattern p = Pattern.compile(
            "\"numero\"\\s*:\\s*(\\d+)\\s*,\\s*\"tipo\"\\s*:\\s*\"(\\w+)\"\\s*,"
            + "\\s*\"inspectorOcupado\"\\s*:\\s*(true|false)\\s*,\\s*\"cola\"\\s*:\\s*\\[([^\\]]*)\\]");
        Matcher m = p.matcher(contenido);
        while (m.find()) {
            Bus bus = buses.buscarPorNumero(Integer.parseInt(m.group(1)));
            if (bus == null) {
                continue; // la configuración actual tiene menos buses
            }
            String ids = m.group(4).trim();
            if (!ids.isEmpty()) {
                String[] partes = ids.split(",");
                for (int i = 0; i < partes.length; i++) {
                    Tiquete t = buscarEn(pendientes, Integer.parseInt(partes[i].trim()));
                    if (t != null) {
                        bus.getCola().encolar(t);
                    }
                }
            }
            // Se restaura el estado del inspector solo si quedan personas en fila;
            // un inspector "ocupado" sin tiquete en atención se considera disponible.
            bus.setInspectorOcupado(false);
        }
        return true;
    }

    private Tiquete buscarEn(Tiquete[] arr, int id) {
        if (arr == null) {
            return null;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].getId() == id) {
                return arr[i];
            }
        }
        return null;
    }
}
