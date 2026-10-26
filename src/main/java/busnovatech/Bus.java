/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 * 
 * @author grego
 */
public class Bus {

    private int numero;
    private String tipo;
    private ColaTiquetes cola;
    private Tiquete enAtencion;

    // crea el bus con su cola vacia
    public Bus(int numero, String tipo){

        if(numero < 1){
            throw new IllegalArgumentException("El numero del bus debe ser positivo");
        }

        if(!"P".equals(tipo) && !"D".equals(tipo) && !"N".equals(tipo)){
            throw new IllegalArgumentException("El tipo de bus debe ser P, D o N");
        }

        this.numero=numero;
        this.tipo = tipo;
        cola=new ColaTiquetes();
        enAtencion =null;
    }

    // consulta el numero del bus
    public int getNumero(){
        return numero;
    }

    // consulta el tipo de bus
    public String getTipo(){
        return tipo;
    }

    // consulta la cantidad de personas en espera
    public int contarEnEspera(){
        return cola.contar();
    }

    // indica si el inspector puede atender
    public boolean inspectorLibre(){
        return enAtencion == null;
    }

    // consulta el tiquete que esta siendo atendido
    public Tiquete getEnAtencion(){
        return enAtencion;
    }

    // agrega un tiquete del mismo tipo que el bus
    public boolean agregarTiquete(Tiquete tiquete){

        if(tiquete == null){
            return false;
        }

        if(!tipo.equals(tiquete.getTipoBus())){
            return false;
        }

        if(enAtencion != null){
            if(enAtencion.getNumero() == tiquete.getNumero()){
                return false;
            }
        }

        boolean agregado;
        agregado=cola.agregar(tiquete);

        return agregado;
    }

    // pasa el primero de la cola al inspector libre
    public Tiquete iniciarAtencion(){

        if(!inspectorLibre()){
            return null;
        }

        Tiquete siguiente;
        siguiente =cola.retirar();

        if(siguiente == null){
            return null;
        }

        enAtencion=siguiente;
        enAtencion.setHoraAbordaje(System.currentTimeMillis());

        return enAtencion;
    }

    // libera al inspector y devuelve el tiquete que atendia
    public Tiquete finalizarAtencion(){

        Tiquete terminado;
        terminado=enAtencion;

        enAtencion = null;

        return terminado;
    }
}
