/**
 * Cola FIFO de tiquetes implementada con nodos enlazados (sin colecciones de Java).
 * @author Gabriel
 */
public class ColaTiquetes {
    private NodoTiquete frente;
    private NodoTiquete fin;
    private int tamano;

    public ColaTiquetes() {
        frente = null;
        fin = null;
        tamano = 0;
    }

    public boolean estaVacia() { return frente == null; }

    public int getTamano() { return tamano; }

    /** Agrega un tiquete al final de la cola. */
    public void encolar(Tiquete t) {
        NodoTiquete nuevo = new NodoTiquete(t);
        if (fin == null) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamano++;
    }

    /** Saca y devuelve el primer tiquete, o null si la cola está vacía. */
    public Tiquete desencolar() {
        if (frente == null) {
            return null;
        }
        Tiquete t = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return t;
    }

    /** Devuelve el primer tiquete sin sacarlo, o null si está vacía. */
    public Tiquete verFrente() {
        return frente == null ? null : frente.getDato();
    }

    /**
     * Elimina de la cola el tiquete con el id dado (por ejemplo, si rechaza el pago).
     * @return true si lo encontró y lo eliminó
     */
    public boolean remover(int id) {
        NodoTiquete anterior = null;
        NodoTiquete actual = frente;
        while (actual != null) {
            if (actual.getDato().getId() == id) {
                if (anterior == null) {
                    frente = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == fin) {
                    fin = anterior;
                }
                tamano--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    /** Devuelve los tiquetes en orden de fila como arreglo. */
    public Tiquete[] aArreglo() {
        Tiquete[] arr = new Tiquete[tamano];
        NodoTiquete actual = frente;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.getDato();
            actual = actual.getSiguiente();
        }
        return arr;
    }

    /** Ids en orden de fila separados por coma, ej. "3,5,8" (se usa en colas.json). */
    public String idsComoTexto() {
        StringBuilder sb = new StringBuilder();
        NodoTiquete actual = frente;
        while (actual != null) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(actual.getDato().getId());
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }
}
