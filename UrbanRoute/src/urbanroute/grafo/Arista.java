/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.grafo;

import urbanroute.dominio.Estacion;

/**
 *
 * @author leo.uzgil@gmail.com
 * Enlace ponderado entre dos estaciones.
 */
public class Arista {

    private final Estacion origen;
    private final Estacion destino;
    private double tiempoMinutos;

    /**
     * @param origen        estación origen
     * @param destino       estación destino
     * @param tiempoMinutos peso (debe ser positivo)
     * @throws IllegalArgumentException si el tiempo no es positivo
     */
    public Arista(Estacion origen, Estacion destino, double tiempoMinutos) {
        if (tiempoMinutos <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser positivo: " + tiempoMinutos);
        }
        this.origen = origen;
        this.destino = destino;
        this.tiempoMinutos = tiempoMinutos;
    }

    public Estacion getOrigen() { return origen; }
    public Estacion getDestino() { return destino; }
    public double getTiempoMinutos() { return tiempoMinutos; }

    /**
     * @param tiempoMinutos nuevo peso (positivo)
     */
    public void setTiempoMinutos(double tiempoMinutos) {
        if (tiempoMinutos <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser positivo: " + tiempoMinutos);
        }
        this.tiempoMinutos = tiempoMinutos;
    }

    @Override
    public String toString() {
        return origen.getIdEstacion() + " -> " + destino.getIdEstacion() + " (" + tiempoMinutos + " min)";
    }
}

