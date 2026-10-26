/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package busnovatech;

import javax.swing.JOptionPane;
import java.io.IOException;

/**
 *
 * @author grego
 */
public class BusNovaTech {

    public static void main(String[] args) {

        //Inicio del main
        GestionTiquetes gestion;
        gestion = new GestionTiquetes();

        ArchivoTiquetes archivo;
        archivo = new ArchivoTiquetes();

        ListaTiquetes lista;
        int siguienteNumero;

        try {
            lista = archivo.cargar();
            siguienteNumero = archivo.getSiguienteNumero();

        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, "No se pudieron cargar los tiquetes: " + error.getMessage());
            return;
        }

        boolean continuar;
        continuar = true;

        while (continuar) {
            String opcion;
            opcion = JOptionPane.showInputDialog(
                    "TIQUETES\n"
                    + "1) Crear tiquete\n"
                    + "2) Consultar tiquetes\n"
                    + "3) Salir\n"
                    + "4) Retirar tiquete de prueba");

            if (opcion == null) {
                continuar = false;

            } else {
                opcion = opcion.trim();

                switch (opcion) {
                    case "1":
                        if (siguienteNumero == Integer.MAX_VALUE) {
                            JOptionPane.showMessageDialog(null, "Se alcanzo el limite de numeros de tiquete");
                            break;
                        }

                        Tiquete tiquete;
                        tiquete = gestion.crearTiquete(siguienteNumero);

                        if (tiquete == null) {
                            JOptionPane.showMessageDialog(null, "Se cancelo la creacion del tiquete");
                            break;
                        }

                        boolean agregado;
                        agregado = lista.agregar(tiquete);

                        if (!agregado) {
                            JOptionPane.showMessageDialog(null, "No se pudo agregar el tiquete");
                            break;
                        }

                        int nuevoConsecutivo;
                        nuevoConsecutivo = siguienteNumero + 1;

                        try {
                            archivo.guardar(lista, nuevoConsecutivo);
                            siguienteNumero = nuevoConsecutivo;

                            JOptionPane.showMessageDialog(null, "Tiquete guardado con el numero " + tiquete.getNumero());

                        } catch (IOException error) {
                            JOptionPane.showMessageDialog(null, "No se pudo completar el guardado: " + error.getMessage() + "\nEl programa se cerrara para revisar el archivo");
                            return;
                        }
                        break;

                    case "2":
                        NodoTiquete actual;
                        actual = lista.getPrimero();

                        if (actual == null) {
                            JOptionPane.showMessageDialog(null, "No hay tiquetes registrados");

                        } else {
                            String mensaje;
                            mensaje = "TIQUETES REGISTRADOS\n";

                            while (actual != null) {
                                Tiquete guardado;
                                guardado = actual.getTiquete();

                                mensaje += "\nNumero: " + guardado.getNumero()
                                        + "\nNombre: " + guardado.getNombre()
                                        + "\nIdentificacion: " + guardado.getIdentificacion()
                                        + "\nEdad: " + guardado.getEdad()
                                        + "\nMoneda: " + guardado.getMoneda()
                                        + "\nTipo de bus: " + guardado.getTipoBus()
                                        + "\nServicio: " + guardado.getServicio()
                                        + "\nHora de compra: " + guardado.getHoraCompra()
                                        + "\nHora de abordaje: " + guardado.getHoraAbordaje()
                                        + "\n--------------------\n";

                                actual = actual.getSiguiente();
                            }

                            JOptionPane.showMessageDialog(null, mensaje);
                        }
                        break;

                    case "3":
                        continuar = false;
                        break;

                    case "4": {
                        String numeroTexto;
                        numeroTexto = JOptionPane.showInputDialog("Ingrese el numero del tiquete que desea retirar");

                        if (numeroTexto == null) {
                            break;
                        }

                        int numeroRetirar;

                        try {
                            numeroRetirar = Integer.parseInt(numeroTexto.trim());
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar un numero entero");
                            break;
                        }

                        Tiquete retirado;
                        retirado = lista.retirar(numeroRetirar);

                        if (retirado == null) {
                            JOptionPane.showMessageDialog(null, "No existe un tiquete con ese numero");
                            break;
                        }

                        try {
                            archivo.guardar(lista, siguienteNumero);
                            JOptionPane.showMessageDialog(null, "Se retiro el tiquete " + retirado.getNumero());
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "No se pudo guardar el retiro. El programa se cerrara para revisar el archivo: " + e.getMessage());
                            return;
                        }

                        break;
                    }

                    default:
                        JOptionPane.showMessageDialog(null, "Seleccione una opcion del 1 al 45");
                        break;
                }
            }
        }
        //Fin del main
    }
}
