/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

import javax.swing.JOptionPane;

/**
 *
 * @author grego
 */
public class GestionTiquetes {
        // solicita los datos y crea un tiquete
    public Tiquete crearTiquete(int numero){

        String nombre;
        String identificacion;
        String moneda;
        String tipoBus;
        int edad;

        nombre = pedirTexto("Ingrese el nombre del cliente");

        if(nombre == null){
            return null;
        }

        identificacion = pedirTexto("Ingrese la identificacion del cliente");

        if(identificacion == null){
            return null;
        }

        edad = pedirEdad();

        if(edad == -1){
            return null;
        }

        moneda = pedirMoneda();

        if(moneda == null){
            return null;
        }

        tipoBus = pedirTipoBus();

        if(tipoBus == null){
            return null;
        }

        Tiquete tiquete;
        tiquete = new Tiquete(numero, nombre, identificacion,
                edad, moneda, tipoBus);

        return tiquete;
    }

    // solicita un texto y verifica que no este vacio
    private String pedirTexto(String mensaje){

        String texto;

        while(true){
            texto = JOptionPane.showInputDialog(mensaje);

            if(texto == null){
                return null;
            }

            texto = texto.trim();

            if(texto.equals("")){
                JOptionPane.showMessageDialog(null,
                        "Debe completar este dato");
            }else{
                return texto;
            }
        }
    }

    // solicita una edad entera que no sea negativa
    private int pedirEdad(){

        String entrada;
        int edad;

        while(true){
            entrada = JOptionPane.showInputDialog(
                    "Ingrese la edad del cliente");

            if(entrada == null){
                return -1;
            }

            entrada = entrada.trim();

            try{
                edad = Integer.parseInt(entrada);

                if(edad < 0){
                    JOptionPane.showMessageDialog(null,
                            "La edad no puede ser negativa");
                }else{
                    return edad;
                }
            }catch(NumberFormatException error){
                JOptionPane.showMessageDialog(null,
                        "Ingrese una edad en numeros enteros");
            }
        }
    }

    // permite elegir la moneda de pago
    private String pedirMoneda(){

        String opcion;
        String moneda;

        while(true){
            opcion = JOptionPane.showInputDialog(
                    "Seleccione la moneda\n"
                    + "1) Colones\n"
                    + "2) Dolares");

            if(opcion == null){
                return null;
            }

            opcion = opcion.trim();

            switch(opcion){
                case "1":
                    moneda = "Colones";
                    break;

                case "2":
                    moneda = "Dolares";
                    break;

                default:
                    moneda = null;
                    break;
            }

            if(moneda != null){
                return moneda;
            }

            JOptionPane.showMessageDialog(null,
                    "Seleccione una opcion del 1 al 2");
        }
    }

    // permite elegir un unico tipo de bus
    private String pedirTipoBus(){

        String opcion;
        String tipoBus;

        while(true){
            opcion = JOptionPane.showInputDialog(
                    "Seleccione el tipo de bus\n"
                   + "1) Preferencial\n"
                  + "2) Directo\n"
                  + "3) Normal");

            if(opcion == null){
                return null;
            }

            opcion = opcion.trim();

            switch(opcion){
                case "1":
                    tipoBus = "P";
                    break;

                case "2":
                    tipoBus = "D";
                    break;

                case "3":
                    tipoBus = "N";
                    break;

                default:
                    tipoBus = null;
                    break;
            }

            if(tipoBus != null){
                return tipoBus;
            }

            JOptionPane.showMessageDialog(null, "Seleccione una opcion del 1 al 3");
        }
    }
}
