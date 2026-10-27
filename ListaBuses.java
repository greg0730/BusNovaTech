/**
 * Lista enlazada simple de buses (sin colecciones de Java).
 * @author Gabriel
 */
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

    /** Agrega un bus al final de la lista. */
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

    /** Busca un bus por su número, o null si no existe. */
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

    /** Devuelve el primer bus del tipo indicado (útil para preferencial y directo, que son únicos). */
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

    /** Devuelve el bus normal con menos personas en fila (el primero en caso de empate). */
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
