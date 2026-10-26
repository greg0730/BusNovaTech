/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 *
 * @author grego
 */
public class ListaTiquetes {
        private NodoTiquete primero;
    private NodoTiquete ultimo;

    // crea la lista vacia
    public ListaTiquetes(){
        primero=null;
        ultimo = null;
    }

    // agrega un tiquete al final sin repetir su numero
    public boolean agregar(Tiquete tiquete){

        if(tiquete == null){
            return false;
        }

        Tiquete encontrado;
        encontrado= buscar(tiquete.getNumero());

        if(encontrado != null){
            return false;
        }

        NodoTiquete nuevo;
        nuevo =new NodoTiquete(tiquete);

        if(primero == null){
            primero=nuevo;
            ultimo= nuevo;
        }else{
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        return true;
    }

    // busca un tiquete por su numero
    public Tiquete buscar(int numero){

        NodoTiquete actual;
        actual= primero;

        while(actual != null){
            Tiquete tiquete;
            tiquete =actual.getTiquete();

            if(tiquete.getNumero() == numero){
                return tiquete;
            }

            actual= actual.getSiguiente();
        }

        return null;
    }

    // permite iniciar el recorrido de la lista
    public NodoTiquete getPrimero(){
        return primero;
    }
}
