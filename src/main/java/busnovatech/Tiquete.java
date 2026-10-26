/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busnovatech;

/**
 *
 * @author grego
 */
public class Tiquete {
    
    private int numero;
    private String nombre;
    private String identificacion;
    private int edad;
    private String moneda;
    private long horaCompra;
    private long horaAbordaje;
    private String servicio;
    private String tipoBus;

    
        // crea el tiquete con los datos del cliente
    public Tiquete(int numero, String nombre, String identificacion,
            int edad, String moneda, String tipoBus){

        this.numero = numero;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.edad = edad;
        this.moneda = moneda;
        this.tipoBus = tipoBus;

        horaCompra = System.currentTimeMillis();
        horaAbordaje = -1;
        servicio = "NA";
    }

    // consulta el numero del tiquete
    public int getNumero(){
        return numero;
    }

    // consulta el nombre del cliente
    public String getNombre(){
        return nombre;
    }

    // consulta la identificacion del cliente
    public String getIdentificacion(){
        return identificacion;
    }

    // consulta la edad del cliente
    public int getEdad(){
        return edad;
    }

    // consulta la moneda elegida
    public String getMoneda(){
        return moneda;
    }

    // consulta la hora de compra
    public long getHoraCompra(){
        return horaCompra;
    }

    // consulta la hora de abordaje
    public long getHoraAbordaje(){
        return horaAbordaje;
    }

    // consulta el servicio seleccionado
    public String getServicio(){
        return servicio;
    }

    // consulta el tipo de bus
    public String getTipoBus(){
        return tipoBus;
    }

    // recupera la hora original al cargar desde el archivo
    public void setHoraCompra(long horaCompra){
        this.horaCompra = horaCompra;
    }

    // actualiza la hora de abordaje
    public void setHoraAbordaje(long horaAbordaje){
        this.horaAbordaje = horaAbordaje;
    }

    // actualiza el servicio durante la atencion
    public void setServicio(String servicio){
        this.servicio = servicio;
    }
    
}
