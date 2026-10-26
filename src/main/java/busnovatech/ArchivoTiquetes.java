/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author grego
 */
public class ArchivoTiquetes {

    private String contenido;
    private int posicion;
    private int siguienteNumero;

    // convierte los datos de un tiquete a texto json
    public String convertirJson(Tiquete tiquete) {

        String texto;
        texto = "{\n";
        texto += "  \"numero\": " + tiquete.getNumero() + ",\n";
        texto += "  \"nombre\": \"" + escaparTexto(tiquete.getNombre()) + "\",\n";
        texto += "  \"identificacion\": \"" + escaparTexto(tiquete.getIdentificacion()) + "\",\n";
        texto += "  \"edad\": " + tiquete.getEdad() + ",\n";
        texto += "  \"moneda\": \"" + escaparTexto(tiquete.getMoneda()) + "\",\n";
        texto += "  \"horaCompra\": " + tiquete.getHoraCompra() + ",\n";
        texto += "  \"horaAbordaje\": " + tiquete.getHoraAbordaje() + ",\n";
        texto += "  \"servicio\": \"" + escaparTexto(tiquete.getServicio()) + "\",\n";
        texto += "  \"tipoBus\": \"" + escaparTexto(tiquete.getTipoBus()) + "\"\n";
        texto += "}";

        return texto;
    }

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

    // guarda los tiquetes y el siguiente numero disponible
    public void guardar(ListaTiquetes lista, int siguienteNumero) throws IOException {

        String texto;
        texto = "{\n";
        texto += "  \"siguienteNumero\": " + siguienteNumero + ",\n";
        texto += "  \"tiquetes\": [\n";

        NodoTiquete actual;
        actual = lista.getPrimero();

        while (actual != null) {
            Tiquete tiquete;
            tiquete = actual.getTiquete();

            String datos;
            datos = convertirJson(tiquete);

            texto += datos;

            actual = actual.getSiguiente();

            if (actual != null) {
                texto += ",\n";
            }
        }

        texto += "\n  ]\n";
        texto += "}\n";

        Path temporal;
        temporal = Path.of("tiquetes.tmp");

        Path archivo;
        archivo = Path.of("tiquetes.json");

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
            throw new IOException("El archivo esta incompleto");
        }

        char caracter;
        caracter = contenido.charAt(posicion);

        if (caracter != esperado) {
            throw new IOException("Se esperaba el caracter " + esperado);
        }

        posicion++;
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
                    throw new IOException("Hay un texto incompleto en el archivo");
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
                        throw new IOException("Hay un caracter especial incorrecto");
                }
            } else {
                if (caracter < 32) {
                    throw new IOException("Hay un caracter sin escapar en el texto");
                }

                resultado += caracter;
            }
        }

        throw new IOException("Faltan las comillas de cierre de un texto");
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

    // lee un numero entero del archivo
    private long leerNumero() throws IOException {

        saltarEspacios();

        int inicio;
        inicio = posicion;

        if (posicion < contenido.length()) {
            if (contenido.charAt(posicion) == '-') {
                posicion++;
            }
        }

        int inicioDigitos;
        inicioDigitos = posicion;

        while (posicion < contenido.length()) {
            char caracter;
            caracter = contenido.charAt(posicion);

            if (caracter >= '0' && caracter <= '9') {
                posicion++;
            } else {
                break;
            }
        }

        if (posicion == inicioDigitos) {
            throw new IOException("Se esperaba un numero entero");
        }

        if (posicion - inicioDigitos > 1
                && contenido.charAt(inicioDigitos) == '0') {
            throw new IOException("Hay un numero con ceros iniciales");
        }

        String numeroTexto;
        numeroTexto = contenido.substring(inicio, posicion);

        try {
            long numero;
            numero = Long.parseLong(numeroTexto);

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
    // carga los tiquetes pendientes y recupera el consecutivo

    public ListaTiquetes cargar() throws IOException {

        Path archivo;
        archivo = Path.of("tiquetes.json");

        ListaTiquetes lista;
        lista = new ListaTiquetes();

        try {
            contenido = Files.readString(archivo, StandardCharsets.UTF_8);

        } catch (NoSuchFileException error) {
            siguienteNumero = 1;
            return lista;
        }

        posicion = 0;

        esperar('{');
        leerCampo("siguienteNumero");

        int consecutivo;
        consecutivo = leerEntero();

        if (consecutivo < 1) {
            throw new IOException("El consecutivo debe ser mayor que cero");
        }

        esperar(',');
        leerCampo("tiquetes");
        esperar('[');

        if (verSiguiente() != ']') {
            boolean continuar;
            continuar = true;

            while (continuar) {
                Tiquete tiquete;
                tiquete = leerTiquete();

                if (tiquete.getNumero() >= consecutivo) {
                    throw new IOException("El consecutivo no corresponde a los tiquetes guardados");
                }

                boolean agregado;
                agregado = lista.agregar(tiquete);

                if (!agregado) {
                    throw new IOException("Hay numeros de tiquete repetidos");
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
            throw new IOException("Hay contenido adicional al final del archivo");
        }

        siguienteNumero = consecutivo;

        return lista;
    }

    // consulta el siguiente caracter sin avanzar la lectura
    private char verSiguiente() throws IOException {

        saltarEspacios();

        if (posicion >= contenido.length()) {
            throw new IOException("El archivo esta incompleto");
        }

        char caracter;
        caracter = contenido.charAt(posicion);

        return caracter;
    }

    // lee un numero que pueda guardarse en una variable int
    private int leerEntero() throws IOException {

        long valor;
        valor = leerNumero();

        if (valor < Integer.MIN_VALUE || valor > Integer.MAX_VALUE) {
            throw new IOException("Un numero supera el rango permitido para int");
        }

        int numero;
        numero = (int) valor;

        return numero;
    }

    // reconstruye un tiquete con los datos guardados
    private Tiquete leerTiquete() throws IOException {

        esperar('{');
        leerCampo("numero");

        int numero;
        numero = leerEntero();

        esperar(',');
        leerCampo("nombre");

        String nombre;
        nombre = leerTexto();

        esperar(',');
        leerCampo("identificacion");

        String identificacion;
        identificacion = leerTexto();

        esperar(',');
        leerCampo("edad");

        int edad;
        edad = leerEntero();

        esperar(',');
        leerCampo("moneda");

        String moneda;
        moneda = leerTexto();

        esperar(',');
        leerCampo("horaCompra");

        long horaCompra;
        horaCompra = leerNumero();

        esperar(',');
        leerCampo("horaAbordaje");

        long horaAbordaje;
        horaAbordaje = leerNumero();

        esperar(',');
        leerCampo("servicio");

        String servicio;
        servicio = leerTexto();

        esperar(',');
        leerCampo("tipoBus");

        String tipoBus;
        tipoBus = leerTexto();

        esperar('}');

        if (numero < 1 || edad < 0) {
            throw new IOException("Hay un numero de tiquete o una edad incorrecta");
        }

        if (nombre.trim().equals("") || identificacion.trim().equals("")) {
            throw new IOException("Hay un tiquete sin nombre o identificacion");
        }

        if (!moneda.equals("Colones") && !moneda.equals("Dolares")) {
            throw new IOException("Hay una moneda incorrecta");
        }

        if (!tipoBus.equals("P") && !tipoBus.equals("D") && !tipoBus.equals("N")) {
            throw new IOException("Hay un tipo de bus incorrecto");
        }

        if (horaCompra < 0 || horaAbordaje < -1) {
            throw new IOException("Hay una hora incorrecta");
        }

        if (servicio.trim().equals("")) {
            throw new IOException("Hay un tiquete sin servicio");
        }

        Tiquete tiquete;
        tiquete = new Tiquete(numero, nombre, identificacion,
                edad, moneda, tipoBus);

        tiquete.setHoraCompra(horaCompra);
        tiquete.setHoraAbordaje(horaAbordaje);
        tiquete.setServicio(servicio);

        return tiquete;
    }

    // consulta el consecutivo recuperado al cargar el archivo
    public int getSiguienteNumero() {
        return siguienteNumero;
    }

}
