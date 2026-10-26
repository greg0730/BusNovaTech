/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 *
 * @author grego
 */
public class PruebaCola {
        // comprueba el orden de salida de los tiquetes
    public static void main(String[] args){

        ColaTiquetes cola;
        cola=new ColaTiquetes();

        Tiquete primero;
        primero = new Tiquete(1, "Ana", "101", 20, "Colones", "N");

        Tiquete segundo;
        segundo=new Tiquete(2, "Luis", "102", 25, "Colones", "N");

        cola.agregar(primero);
        cola.agregar(segundo);

        String resultado;
        resultado="PRUEBA DE LA COLA\n";

        resultado += "\nCantidad inicial: "+cola.contar();

        boolean repetido;
        repetido= cola.agregar(primero);
        resultado+= "\nAcepta numero repetido: " +repetido;

        Tiquete consultado;
        consultado =cola.consultar();

        if(consultado != null){
            resultado += "\nPrimero en espera: "+consultado.getNumero();
        }

        resultado+= "\nCantidad despues de consultar: "+ cola.contar();

        Tiquete retirado;
        retirado=cola.retirar();

        if(retirado != null){
            resultado += "\nPrimero que sale: " +retirado.getNumero();
        }

        retirado = cola.retirar();

        if(retirado != null){
            resultado+= "\nSegundo que sale: "+retirado.getNumero();
        }

        resultado += "\nCola vacia: "+cola.estaVacia();

        retirado= cola.retirar();
        resultado+= "\nRetirar de cola vacia devuelve null: "+(retirado == null);

        cola.agregar(segundo);

        resultado += "\nCantidad al agregar nuevamente: "+cola.contar();

        consultado=cola.consultar();

        if(consultado != null){
            resultado+= "\nNuevo primero: " + consultado.getNumero();
        }

        javax.swing.JOptionPane.showMessageDialog(null, resultado);
    }
}
