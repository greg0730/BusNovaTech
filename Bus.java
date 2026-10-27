/**
 * Bus con su cola de tiquetes y el estado de su inspector.
 * Tipos válidos: "preferencial", "directo", "normal".
 * @author Gabriel
 */
public class Bus {
    public static final String PREFERENCIAL = "preferencial";
    public static final String DIRECTO = "directo";
    public static final String NORMAL = "normal";

    private int numero;
    private String tipo;
    private ColaTiquetes cola;
    private boolean inspectorOcupado;

    public Bus(int numero, String tipo) {
        this.numero = numero;
        this.tipo = tipo;
        this.cola = new ColaTiquetes();
        this.inspectorOcupado = false;
    }

    public int getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public ColaTiquetes getCola() { return cola; }
    public boolean isInspectorOcupado() { return inspectorOcupado; }
    public void setInspectorOcupado(boolean ocupado) { this.inspectorOcupado = ocupado; }
}
