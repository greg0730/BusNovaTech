public class ListaBuses {
    private NodoBus cabeza;
    private NodoBus cola;
    private int tamano;

    public ListaBuses() {
        cabeza = null;
        cola = null;
        tamano = 0;
    }

    public int getTamano() { return tamano; }

    public NodoBus getCabeza() { return cabeza; }

    public void agregar(Bus bus) {
        NodoBus nuevo = new NodoBus(bus);
        if (cola == null) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
        }
        cola = nuevo;
        tamano++;
    }

    public Bus buscarPorNumero(int numero) {
        NodoBus actual = cabeza;
        while (actual != null) {
            if (actual.getDato().getNumero() == numero) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public Bus buscarPorTipo(String tipo) {
        NodoBus actual = cabeza;
        while (actual != null) {
            if (actual.getDato().getTipo().equals(tipo)) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public Bus normalConMenosPersonas() {
        Bus mejor = null;
        NodoBus actual = cabeza;
        while (actual != null) {
            Bus b = actual.getDato();
            if (b.getTipo().equals(Bus.NORMAL)
                    && (mejor == null || b.getCola().getTamano() < mejor.getCola().getTamano())) {
                mejor = b;
            }
            actual = actual.getSiguiente();
        }
        return mejor;
    }
}
