/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 *
 * @author grego
 */
public class ColaTiquetes {

    private NodoTiquete frente;
    private NodoTiquete ultimo;

    // crea una cola vacia
    public ColaTiquetes() {
        frente = null;
        ultimo = null;
    }

    // agrega un tiquete al final de la cola
    public boolean agregar(Tiquete tiquete) {

        if (tiquete == null) {
            return false;
        }

        NodoTiquete actual;
        actual = frente;

        while (actual != null) {
            if (actual.getTiquete().getNumero() == tiquete.getNumero()) {
                return false;
            }

            actual = actual.getSiguiente();
        }

        NodoTiquete nuevo;
        nuevo = new NodoTiquete(tiquete);

        if (frente == null) {
            frente = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        return true;
    }

    // consulta el primero sin retirarlo
    public Tiquete consultar() {

        if (frente == null) {
            return null;
        }

        Tiquete tiquete;
        tiquete = frente.getTiquete();

        return tiquete;
    }

    // retira el primer tiquete de la cola
    public Tiquete retirar() {

        if (frente == null) {
            return null;
        }

        NodoTiquete retirado;
        retirado = frente;
        frente = frente.getSiguiente();

        if (frente == null) {
            ultimo = null;
        }

        retirado.setSiguiente(null);

        Tiquete tiquete;
        tiquete = retirado.getTiquete();

        return tiquete;
    }

    // retira un tiquete especifico de la fila
    public Tiquete retirar(int numero){

        NodoTiquete actual;
        actual=frente;

        NodoTiquete anterior;
        anterior = null;

        while(actual != null){
            Tiquete tiquete;
            tiquete=actual.getTiquete();

            if(tiquete.getNumero() == numero){
                if(anterior == null){
                    frente =actual.getSiguiente();
                }else{
                    anterior.setSiguiente(actual.getSiguiente());
                }

                if(actual == ultimo){
                    ultimo=anterior;
                }

                actual.setSiguiente(null);
                return tiquete;
            }

            anterior=actual;
            actual = actual.getSiguiente();
        }

        return null;
    }

    // cuenta los tiquetes que esperan
    public int contar() {

        int cantidad;
        cantidad = 0;

        NodoTiquete actual;
        actual = frente;

        while (actual != null) {
            cantidad++;
            actual = actual.getSiguiente();
        }

        return cantidad;
    }

    // indica si la cola esta vacia
    public boolean estaVacia() {
        return frente == null;
    }
    
        // comprueba si un tiquete ya esta en la cola
    public boolean contiene(int numero){

        NodoTiquete actual;
        actual=frente;

        while(actual != null){
            if(actual.getTiquete().getNumero() == numero){
                return true;
            }

            actual =actual.getSiguiente();
        }

        return false;
    }
    
        // permite recorrer la cola sin retirar los tiquetes
    public NodoTiquete getFrente(){
        return frente;
    }

}
