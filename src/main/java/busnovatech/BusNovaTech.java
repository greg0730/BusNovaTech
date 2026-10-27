/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package busnovatech;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;

/**
 * inicia el programa: carga los datos, valida el usuario y abre el menu
 * @author grego
 */
public class BusNovaTech {

    // crea los buses segun la configuracion: un preferencial, un directo y los demas normales
    static ListaBuses crearBuses(int cantidad) {

        ListaBuses lista;
        lista = new ListaBuses();

        for (int numero = 1; numero <= cantidad; numero++) {
            String tipo;

            if (numero == 1) {
                tipo = "P";
            } else if (numero == 2) {
                tipo = "D";
            } else {
                tipo = "N";
            }

            lista.agregar(new Bus(numero, tipo));
        }

        return lista;
    }

    // coordina el inicio: configuracion, tiquetes, colas, inicio de sesion y menu
    public static void main(String[] args) {

        ArchivoConfig archivoConfig;
        archivoConfig = new ArchivoConfig();

        GestionConfiguracion gestionConfig;
        gestionConfig = new GestionConfiguracion();

        Configuracion config;

        try {
            config = archivoConfig.cargar();

            if (config == null) {
                config = gestionConfig.crearConfiguracion();

                if (config == null) {
                    JOptionPane.showMessageDialog(null,
                            "La configuracion es necesaria. No se guardo nada");
                    return;
                }

                archivoConfig.guardar(config);
                JOptionPane.showMessageDialog(null, "Configuracion guardada en config.json");
            }

        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar o guardar la configuracion: "
                    + error.getMessage() + "\nConserve config.json para revisar los datos");
            return;
        }

        String usuario;
        usuario = gestionConfig.iniciarSesion(config);

        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "No se pudo iniciar sesion. Se cerrara el programa");
            return;
        }

        ListaBuses configurados;
        configurados = crearBuses(config.getCantidadBuses());

        ArchivoEstado archivoEstado;
        archivoEstado = new ArchivoEstado(Path.of("datos-terminal"));

        try {
            archivoEstado.cargar(configurados);
        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, "No se pudieron cargar los datos: "
                    + error.getMessage() + "\nConserve los archivos para revisarlos");
            return;
        }

        ListaTiquetes tiquetes;
        tiquetes = archivoEstado.getPendientes();

        ListaBuses buses;
        buses = archivoEstado.getBuses();

        int siguienteNumero;
        siguienteNumero = archivoEstado.getSiguienteNumero();

        MenuPrincipal menu;
        menu = new MenuPrincipal(config, usuario, tiquetes, siguienteNumero,
                buses, archivoEstado);

        menu.iniciar();
    }
}
