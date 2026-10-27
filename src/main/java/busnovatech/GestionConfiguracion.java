package busnovatech;

import javax.swing.JOptionPane;

/**
 * solicita y valida los datos de la configuracion y el inicio de sesion
 * @author dfran
 */
public class GestionConfiguracion {

    // solicita los datos de la primera configuracion; devuelve null si se cancela
    public Configuracion crearConfiguracion() {

        JOptionPane.showMessageDialog(null,
                "No existe configuracion. Se solicitaran los datos de la terminal");

        String nombreTerminal;
        nombreTerminal = pedirTexto("Ingrese el nombre de la terminal");

        if (nombreTerminal == null) {
            return null;
        }

        int cantidadBuses;
        cantidadBuses = pedirCantidadBuses();

        if (cantidadBuses == -1) {
            return null;
        }

        Configuracion config;
        config = new Configuracion(nombreTerminal, cantidadBuses);

        while (config.getUsuarios().contar() < Configuracion.MIN_USUARIOS) {
            JOptionPane.showMessageDialog(null, "Registre el usuario "
                    + (config.getUsuarios().contar() + 1) + " de "
                    + Configuracion.MIN_USUARIOS + " obligatorios");

            boolean registrado;
            registrado = registrarUsuario(config.getUsuarios());

            if (!registrado) {
                return null;
            }
        }

        boolean masUsuarios;
        masUsuarios = true;

        while (masUsuarios) {
            int respuesta;
            respuesta = JOptionPane.showConfirmDialog(null,
                    "Desea registrar otro usuario?", "Usuarios",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                masUsuarios = registrarUsuario(config.getUsuarios());
            } else {
                masUsuarios = false;
            }
        }

        return config;
    }

    // solicita nombre y contrasena de un usuario nuevo
    private boolean registrarUsuario(ListaUsuarios usuarios) {

        String nombre;
        nombre = pedirNombreNuevo(usuarios);

        if (nombre == null) {
            return false;
        }

        String clave;
        clave = pedirClave("Ingrese la contrasena de " + nombre + " (minimo 4 caracteres)");

        if (clave == null) {
            return false;
        }

        usuarios.agregar(new Usuario(nombre, clave));

        JOptionPane.showMessageDialog(null, "Usuario " + nombre + " registrado");

        return true;
    }

    // solicita un nombre de usuario que todavia no exista
    private String pedirNombreNuevo(ListaUsuarios usuarios) {

        String nombre;

        while (true) {
            nombre = pedirTexto("Ingrese el nombre de usuario");

            if (nombre == null) {
                return null;
            }

            if (usuarios.buscar(nombre) == null) {
                return nombre;
            }

            JOptionPane.showMessageDialog(null, "Ese usuario ya existe");
        }
    }

    // solicita una contrasena de al menos cuatro caracteres
    private String pedirClave(String mensaje) {

        String clave;

        while (true) {
            clave = JOptionPane.showInputDialog(mensaje);

            if (clave == null) {
                return null;
            }

            if (clave.length() >= 4) {
                return clave;
            }

            JOptionPane.showMessageDialog(null,
                    "La contrasena debe tener al menos 4 caracteres");
        }
    }

    // solicita un texto y verifica que no este vacio
    private String pedirTexto(String mensaje) {

        String texto;

        while (true) {
            texto = JOptionPane.showInputDialog(mensaje);

            if (texto == null) {
                return null;
            }

            texto = texto.trim();

            if (texto.equals("")) {
                JOptionPane.showMessageDialog(null, "Debe completar este dato");
            } else {
                return texto;
            }
        }
    }

    // solicita la cantidad de buses dentro del rango permitido
    private int pedirCantidadBuses() {

        String entrada;
        int cantidad;

        while (true) {
            entrada = JOptionPane.showInputDialog("Ingrese la cantidad de buses (de "
                    + Configuracion.MIN_BUSES + " a " + Configuracion.MAX_BUSES + ")");

            if (entrada == null) {
                return -1;
            }

            entrada = entrada.trim();

            try {
                cantidad = Integer.parseInt(entrada);

                if (cantidad < Configuracion.MIN_BUSES || cantidad > Configuracion.MAX_BUSES) {
                    JOptionPane.showMessageDialog(null, "Ingrese un numero entre "
                            + Configuracion.MIN_BUSES + " y " + Configuracion.MAX_BUSES);
                } else {
                    return cantidad;
                }
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(null, "Ingrese la cantidad en numeros enteros");
            }
        }
    }

    // valida el inicio de sesion con tres intentos; devuelve el usuario o null
    public String iniciarSesion(Configuracion config) {

        for (int intento = 1; intento <= 3; intento++) {
            String nombre;
            nombre = JOptionPane.showInputDialog("INICIO DE SESION - "
                    + config.getNombreTerminal() + "\nUsuario:");

            if (nombre == null) {
                return null;
            }

            String clave;
            clave = JOptionPane.showInputDialog("Contrasena:");

            if (clave == null) {
                return null;
            }

            boolean valido;
            valido = config.getUsuarios().validar(nombre, clave);

            if (valido) {
                Usuario usuario;
                usuario = config.getUsuarios().buscar(nombre);

                return usuario.getNombre();
            }

            JOptionPane.showMessageDialog(null, "Usuario o contrasena incorrectos. Intento "
                    + intento + " de 3");
        }

        return null;
    }
}
