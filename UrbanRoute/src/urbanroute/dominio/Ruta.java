package urbanroute.dominio;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import urbanroute.estructuras.Arreglo;
/**
 *
 * @author leo.uzgil@gmail.com
 * Resultado de una ruta: secuencia de estaciones y tiempo total.
 */
public class Ruta {

    private final Arreglo<Estacion> estaciones;
    private double tiempoTotalMinutos;

    /** @param maxEstaciones máximo de estaciones del trayecto (fijo) */
    public Ruta(int maxEstaciones) {
        estaciones = new Arreglo<>(maxEstaciones);
        tiempoTotalMinutos = 0;
    }

    /**
     * @param estacion estación a añadir al final del trayecto
     */
    public void agregarEstacion(Estacion estacion) {
        estaciones.agregar(estacion);
    }

    /** @return estaciones en orden de recorrido */
    public Arreglo<Estacion> getEstaciones() {
        return estaciones;
    }

    /** @return tiempo total en minutos */
    public double getTiempoTotalMinutos() {
        return tiempoTotalMinutos;
    }

    /** @param tiempoTotalMinutos tiempo total en minutos */
    public void setTiempoTotalMinutos(double tiempoTotalMinutos) {
        this.tiempoTotalMinutos = tiempoTotalMinutos;
    }

    /** @return true si la ruta no tiene estaciones (destino inalcanzable) */
    public boolean esVacia() {
        return estaciones.estaVacio();
    }

    public String imprimir() {
        String texto;
        if (esVacia()) {
            return "Ruta inexistente";
        }
        texto = "";
        for (int i = 0; i < estaciones.size(); i++) {
            if (i > 0) {
                texto = texto + " -> ";
            }
            texto = texto + estaciones.obtener(i).getNombre();
        }
        texto = texto + " [" + tiempoTotalMinutos + " min]";
        return texto;
    }
}
