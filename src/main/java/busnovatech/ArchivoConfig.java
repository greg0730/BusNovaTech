package busnovatech;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * guarda y recupera la configuracion y las credenciales en json
 * @author dfran
 */
public class ArchivoConfig {

    private String contenido;
    private int posicion;

    // adapta los caracteres especiales para guardarlos en json
    private String escaparTexto(String texto) {

        String resultado;
        resultado = "";

        for (int i = 0; i < texto.length(); i++) {
            char caracter;
            caracter = texto.charAt(i);

            switch (caracter) {
                case '"':
                    resultado += "\\\"";
                    break;

                case '\\':
                    resultado += "\\\\";
                    break;

                case '\n':
                    resultado += "\\n";
                    break;

                case '\r':
                    resultado += "\\r";
                    break;

                case '\t':
                    resultado += "\\t";
                    break;

                default:
                    if (caracter < 32) {
                        String codigo;
                        codigo = String.format("\\u%04x", (int) caracter);
                        resultado += codigo;
                    } else {
                        resultado += caracter;
                    }
                    break;
            }
        }

        return resultado;
    }

    // revisa los datos antes de escribir el archivo
    private void validarConfiguracion(Configuracion config) throws IOException {

        if (config == null) {
            throw new IOException("Falta la configuracion");
        }

        if (config.getNombreTerminal() == null || config.getNombreTerminal().trim().equals("")) {
            throw new IOException("Falta el nombre de la terminal");
        }

        if (config.getCantidadBuses() < Configuracion.MIN_BUSES
                || config.getCantidadBuses() > Configuracion.MAX_BUSES) {
            throw new IOException("La cantidad de buses debe estar entre "
                    + Configuracion.MIN_BUSES + " y " + Configuracion.MAX_BUSES);
        }

        if (config.getUsuarios().contar() < Configuracion.MIN_USUARIOS) {
            throw new IOException("Debe haber al menos " + Configuracion.MIN_USUARIOS + " usuarios");
        }
    }

    // guarda la configuracion y los usuarios
    public void guardar(Configuracion config) throws IOException {

        validarConfiguracion(config);

        String texto;
        texto = "{\n";
        texto += "  \"terminal\": \"" + escaparTexto(config.getNombreTerminal()) + "\",\n";
        texto += "  \"cantidadBuses\": " + config.getCantidadBuses() + ",\n";
        texto += "  \"usuarios\": [\n";

        NodoUsuario actual;
        actual = config.getUsuarios().getPrimero();

        while (actual != null) {
            Usuario usuario;
            usuario = actual.getUsuario();

            texto += "    {\"usuario\": \"" + escaparTexto(usuario.getNombre())
                    + "\", \"clave\": \"" + escaparTexto(usuario.getClave()) + "\"}";

            actual = actual.getSiguiente();

            if (actual != null) {
                texto += ",";
            }

            texto += "\n";
        }

        texto += "  ]\n";
        texto += "}\n";

        Path temporal;
        temporal = Path.of("config.tmp");

        Path archivo;
        archivo = Path.of("config.json");

        Files.writeString(temporal, texto, StandardCharsets.UTF_8);
        Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
    }

    // avanza sobre los espacios y saltos de linea del json
    private void saltarEspacios() {

        while (posicion < contenido.length()) {
            char caracter;
            caracter = contenido.charAt(posicion);

            if (caracter == ' ' || caracter == '\n'
                    || caracter == '\r' || caracter == '\t') {
                posicion++;
            } else {
                return;
            }
        }
    }

    // verifica que aparezca el separador esperado
    private void esperar(char esperado) throws IOException {

        saltarEspacios();

        if (posicion >= contenido.length()) {
            throw new IOException("El archivo de configuracion esta incompleto");
        }

        char caracter;
        caracter = contenido.charAt(posicion);

        if (caracter != esperado) {
            throw new IOException("Se esperaba el caracter " + esperado + " en config.json");
        }

        posicion++;
    }

    // consulta el siguiente caracter sin avanzar la lectura
    private char verSiguiente() throws IOException {

        saltarEspacios();

        if (posicion >= contenido.length()) {
            throw new IOException("El archivo de configuracion esta incompleto");
        }

        char caracter;
        caracter = contenido.charAt(posicion);

        return caracter;
    }

    // lee un texto entre comillas y recupera sus caracteres especiales
    private String leerTexto() throws IOException {

        esperar('"');

        String resultado;
        resultado = "";

        while (posicion < contenido.length()) {
            char caracter;
            caracter = contenido.charAt(posicion);
            posicion++;

            if (caracter == '"') {
                return resultado;
            }

            if (caracter == '\\') {
                if (posicion >= contenido.length()) {
                    throw new IOException("Hay un texto incompleto en config.json");
                }

                char especial;
                especial = contenido.charAt(posicion);
                posicion++;

                switch (especial) {
                    case '"':
                        resultado += '"';
                        break;

                    case '\\':
                        resultado += '\\';
                        break;

                    case '/':
                        resultado += '/';
                        break;

                    case 'n':
                        resultado += '\n';
                        break;

                    case 'r':
                        resultado += '\r';
                        break;

                    case 't':
                        resultado += '\t';
                        break;

                    case 'b':
                        resultado += '\b';
                        break;

                    case 'f':
                        resultado += '\f';
                        break;

                    case 'u':
                        resultado += leerUnicode();
                        break;

                    default:
                        throw new IOException("Hay un caracter especial incorrecto en config.json");
                }
            } else {
                if (caracter < 32) {
                    throw new IOException("Hay un caracter sin escapar en config.json");
                }

                resultado += caracter;
            }
        }

        throw new IOException("Faltan las comillas de cierre de un texto en config.json");
    }

    // recupera un caracter representado por cuatro digitos hexadecimales
    private char leerUnicode() throws IOException {

        if (posicion + 4 > contenido.length()) {
            throw new IOException("Hay un codigo de caracter incompleto");
        }

        int valor;
        valor = 0;

        for (int i = 0; i < 4; i++) {
            char caracter;
            caracter = contenido.charAt(posicion);

            int digito;
            digito = "0123456789abcdef".indexOf(Character.toLowerCase(caracter));

            if (digito == -1) {
                throw new IOException("Hay un codigo de caracter incorrecto");
            }

            valor = valor * 16 + digito;
            posicion++;
        }

        char resultado;
        resultado = (char) valor;

        return resultado;
    }

    // lee un numero entero que no sea negativo
    private int leerNumero() throws IOException {

        saltarEspacios();

        int inicio;
        inicio = posicion;

        while (posicion < contenido.length()) {
            char caracter;
            caracter = contenido.charAt(posicion);

            if (caracter >= '0' && caracter <= '9') {
                posicion++;
            } else {
                break;
            }
        }

        if (inicio == posicion) {
            throw new IOException("Se esperaba un numero entero no negativo");
        }

        if (posicion - inicio > 1 && contenido.charAt(inicio) == '0') {
            throw new IOException("Hay un numero con ceros iniciales");
        }

        String numeroTexto;
        numeroTexto = contenido.substring(inicio, posicion);

        try {
            int numero;
            numero = Integer.parseInt(numeroTexto);

            return numero;

        } catch (NumberFormatException error) {
            throw new IOException("Hay un numero fuera del rango permitido");
        }
    }

    // comprueba el nombre de un dato antes de leer su valor
    private void leerCampo(String esperado) throws IOException {

        String nombre;
        nombre = leerTexto();

        if (!nombre.equals(esperado)) {
            throw new IOException("Se esperaba el campo " + esperado);
        }

        esperar(':');
    }

    // carga la configuracion; devuelve null si todavia no existe config.json
    public Configuracion cargar() throws IOException {

        Path archivo;
        archivo = Path.of("config.json");

        try {
            contenido = Files.readString(archivo, StandardCharsets.UTF_8);

        } catch (NoSuchFileException error) {
            return null;
        }

        posicion = 0;

        esperar('{');
        leerCampo("terminal");

        String nombreTerminal;
        nombreTerminal = leerTexto();

        esperar(',');
        leerCampo("cantidadBuses");

        int cantidadBuses;
        cantidadBuses = leerNumero();

        if (nombreTerminal.trim().equals("")) {
            throw new IOException("Falta el nombre de la terminal en config.json");
        }

        if (cantidadBuses < Configuracion.MIN_BUSES || cantidadBuses > Configuracion.MAX_BUSES) {
            throw new IOException("La cantidad de buses debe estar entre "
                    + Configuracion.MIN_BUSES + " y " + Configuracion.MAX_BUSES);
        }

        Configuracion config;
        config = new Configuracion(nombreTerminal, cantidadBuses);

        esperar(',');
        leerCampo("usuarios");
        esperar('[');

        if (verSiguiente() != ']') {
            boolean continuar;
            continuar = true;

            while (continuar) {
                esperar('{');
                leerCampo("usuario");

                String nombre;
                nombre = leerTexto();

                esperar(',');
                leerCampo("clave");

                String clave;
                clave = leerTexto();

                esperar('}');

                boolean agregado;
                agregado = config.getUsuarios().agregar(new Usuario(nombre, clave));

                if (!agregado) {
                    throw new IOException("Hay un usuario repetido o sin datos en config.json");
                }

                if (verSiguiente() == ']') {
                    continuar = false;
                } else {
                    esperar(',');
                }
            }
        }

        esperar(']');
        esperar('}');

        saltarEspacios();

        if (posicion != contenido.length()) {
            throw new IOException("Hay contenido adicional al final de config.json");
        }

        validarConfiguracion(config);

        return config;
    }
}
