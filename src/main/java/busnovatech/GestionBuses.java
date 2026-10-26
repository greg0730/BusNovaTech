/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 * asigna los tiquetes a los buses disponibles
 * @author grego
 */
public class GestionBuses {

    private ListaBuses buses;

    // recibe los buses de la terminal
    public GestionBuses(ListaBuses buses){

        if(buses == null){
            throw new IllegalArgumentException("Debe proporcionar la lista de buses");
        }

        this.buses=buses;
    }

    // comprueba que existan los buses necesarios
    public boolean configuracionCompleta(){

        Bus preferencial;
        preferencial =buses.seleccionar("P");

        Bus directo;
        directo=buses.seleccionar("D");

        Bus normal;
        normal = buses.seleccionar("N");

        if(preferencial == null || directo == null || normal == null){
            return false;
        }

        return true;
    }

    // busca en cual bus se encuentra un tiquete
    public Bus buscarBusDelTiquete(int numero){

        NodoBus actual;
        actual=buses.getPrimero();

        while(actual != null){
            Bus bus;
            bus =actual.getBus();

            if(bus.contieneTiquete(numero)){
                return bus;
            }

            actual= actual.getSiguiente();
        }

        return null;
    }

    // asigna el tiquete e inicia atencion si no hay nadie esperando
    public Bus asignar(Tiquete tiquete){

        if(tiquete == null || !configuracionCompleta()){
            return null;
        }

        if(tiquete.getHoraAbordaje() != -1){
            return null;
        }

        Bus existente;
        existente=buscarBusDelTiquete(tiquete.getNumero());

        if(existente != null){
            return null;
        }

        Bus elegido;
        elegido = buses.seleccionar(tiquete.getTipoBus());

        if(elegido == null){
            return null;
        }

        boolean atenderAhora;
        atenderAhora=elegido.inspectorLibre()
                && elegido.contarEnEspera() == 0;

        boolean agregado;
        agregado =elegido.agregarTiquete(tiquete);

        if(!agregado){
            return null;
        }

        if(atenderAhora){
            elegido.iniciarAtencion();
        }

        return elegido;
    }

    // inicia la atencion manual del siguiente tiquete
    public Tiquete abordar(int numeroBus){

        Bus bus;
        bus=buses.buscar(numeroBus);

        if(bus == null){
            return null;
        }

        Tiquete tiquete;
        tiquete = bus.iniciarAtencion();

        return tiquete;
    }
}
