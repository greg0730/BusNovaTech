/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 * mantiene los buses y permite comparar sus colas
 * @author grego
 */
public class ListaBuses {

    private NodoBus primero;
    private NodoBus ultimo;

    // crea la lista vacia
    public ListaBuses(){
        primero=null;
        ultimo =null;
    }

    // agrega un bus sin repetir numero ni tipos exclusivos
    public boolean agregar(Bus bus){

        if(bus == null){
            return false;
        }

        NodoBus actual;
        actual=primero;

        while(actual != null){
            Bus guardado;
            guardado = actual.getBus();

            if(guardado.getNumero() == bus.getNumero()){
                return false;
            }

            if(!bus.getTipo().equals("N")){
                if(guardado.getTipo().equals(bus.getTipo())){
                    return false;
                }
            }

            actual= actual.getSiguiente();
        }

        NodoBus nuevo;
        nuevo =new NodoBus(bus);

        if(primero == null){
            primero=nuevo;
            ultimo = nuevo;
        }else{
            ultimo.setSiguiente(nuevo);
            ultimo=nuevo;
        }

        return true;
    }

    // busca un bus por su numero
    public Bus buscar(int numero){

        NodoBus actual;
        actual =primero;

        while(actual != null){
            Bus bus;
            bus=actual.getBus();

            if(bus.getNumero() == numero){
                return bus;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // elige el bus del tipo solicitado con menos personas esperando
    public Bus seleccionar(String tipo){

        Bus elegido;
        elegido=null;

        NodoBus actual;
        actual = primero;

        while(actual != null){
            Bus bus;
            bus= actual.getBus();

            if(bus.getTipo().equals(tipo)){
                if(elegido == null){
                    elegido =bus;
                }else{
                    if(bus.contarEnEspera() < elegido.contarEnEspera()){
                        elegido=bus;
                    }
                }
            }

            actual=actual.getSiguiente();
        }

        return elegido;
    }

    // permite recorrer los buses de la lista
    public NodoBus getPrimero(){
        return primero;
    }
}
