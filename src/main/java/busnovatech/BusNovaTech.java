/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package busnovatech;

import javax.swing.JOptionPane;

/**
 *
 * @author grego
 */
public class BusNovaTech {

    public static void main(String[] args) {
        
        //Inicio del main
                GestionTiquetes gestion;
        gestion= new GestionTiquetes();

        Tiquete tiquete;
        tiquete =gestion.crearTiquete(1);

        if(tiquete != null){
            String mensaje;
            mensaje="Numero de tiquete: "+tiquete.getNumero()
                    + "\nNombre: "+tiquete.getNombre()
                 +"\nIdentificacion: " + tiquete.getIdentificacion()
                 + "\nEdad: "+tiquete.getEdad()
                       + "\nMoneda: " +tiquete.getMoneda()
                   +"\nTipo de bus: "+ tiquete.getTipoBus()
                    + "\nServicio: "+tiquete.getServicio()
                    +"\nHora de abordaje: " + tiquete.getHoraAbordaje();

            JOptionPane.showMessageDialog(null, mensaje);

        }else{
            JOptionPane.showMessageDialog(null, "Se cancelo la creacion del tiquete");
        }
        //Fin del main
    }
}
