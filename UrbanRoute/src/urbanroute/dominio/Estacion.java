package urbanroute.dominio;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



/**
 *
 * @author leo.uzgil@gmail.com
 * Contenedor pasivo de los datos de una estación.
 */
public class Estacion {

    private String idEstacion;
    private String nombre;
    private String linea;
    private String zona;
    private int capacidadPasajerosHora;

    public Estacion(String idEstacion, String nombre, String linea, String zona, int capacidadPasajerosHora) {
        this.idEstacion = idEstacion;
        this.nombre = nombre;
        this.linea = linea;
        this.zona = zona;
        this.capacidadPasajerosHora = capacidadPasajerosHora;
    }

    public String getIdEstacion() { return idEstacion; }
    public void setIdEstacion(String idEstacion) { this.idEstacion = idEstacion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getLinea() { return linea; }
    public void setLinea(String linea) { this.linea = linea; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public int getCapacidadPasajerosHora() { return capacidadPasajerosHora; }
    public void setCapacidadPasajerosHora(int capacidadPasajerosHora) { this.capacidadPasajerosHora = capacidadPasajerosHora; }

    public String imprimir() {
        return nombre + " (" + idEstacion + ")";
    }
}
