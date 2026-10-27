/**
 * Punto de conexión con la persona 4: se invoca cuando un tiquete pasa a atención.
 * La persona 4 implementa este método (cobro, servicio, hora de atención, etc.).
 * @author Gabriel
 */
public interface AtencionListener {
    void atender(Bus bus, Tiquete tiquete);
}
