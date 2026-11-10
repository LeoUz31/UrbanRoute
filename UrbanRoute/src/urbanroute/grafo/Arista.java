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

    private Estacion origen;
    private Estacion destino;
    private double tiempoMinutos;

    /**
     * @param origen        estación origen
     * @param destino       estación destino
     * @param tiempoMinutos peso (el Grafo valida que sea positivo)
     */
    public Arista(Estacion origen, Estacion destino, double tiempoMinutos) {
        this.origen = origen;
        this.destino = destino;
        this.tiempoMinutos = tiempoMinutos;
    }

    public Estacion getOrigen() { return origen; }
    public Estacion getDestino() { return destino; }
    public double getTiempoMinutos() { return tiempoMinutos; }

    /**
     * @param tiempoMinutos nuevo peso (positivo); si no es positivo se imprime "error"
     */
    public void setTiempoMinutos(double tiempoMinutos) {
        if (tiempoMinutos <= 0) {
            System.out.print("error");
        } else {
            this.tiempoMinutos = tiempoMinutos;
        }
    }

    public String imprimir() {
        return origen.getIdEstacion() + " -> " + destino.getIdEstacion() + " (" + tiempoMinutos + " min)";
    }
}
