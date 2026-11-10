package urbanroute.dominio;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;
/**
 *
 * @author leo.uzgil@gmail.com
 * Resultado de una ruta: secuencia de estaciones y tiempo total.
 */
public class Ruta {

    private ListaEnlazada<Estacion> estaciones;
    private double tiempoTotalMinutos;

    public Ruta() {
        estaciones = new ListaEnlazada<>();
        tiempoTotalMinutos = 0;
    }

    /** @param estacion estación a añadir al final del trayecto */
    public void agregarEstacion(Estacion estacion) {
        estaciones.insertarAlFinal(estacion);
    }

    /** @return estaciones en orden de recorrido */
    public ListaEnlazada<Estacion> getEstaciones() {
        return estaciones;
    }

    public double getTiempoTotalMinutos() {
        return tiempoTotalMinutos;
    }

    public void setTiempoTotalMinutos(double tiempoTotalMinutos) {
        this.tiempoTotalMinutos = tiempoTotalMinutos;
    }

    /** @return true si la ruta no tiene estaciones (destino inalcanzable) */
    public boolean esVacia() {
        return estaciones.estaVacia();
    }

    public String imprimir() {
        if (esVacia()) {
            return "Ruta inexistente";
        }
        String texto = "";
        Nodo<Estacion> n = estaciones.getCabeza();
        while (n != null) {
            if (n != estaciones.getCabeza()) {
                texto = texto + " -> ";
            }
            texto = texto + n.getDato().getNombre();
            n = n.getSiguiente();
        }
        texto = texto + " [" + tiempoTotalMinutos + " min]";
        return texto;
    }
}
