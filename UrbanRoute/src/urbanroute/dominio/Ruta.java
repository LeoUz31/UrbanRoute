package urbanroute.dominio;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import urbanroute.estructuras.ArregloDinamico;
/**
 *
 * @author leo.uzgil@gmail.com
 * Resultado de una ruta: secuencia de estaciones y tiempo total.
 */
public class Ruta {

    private final ArregloDinamico<Estacion> estaciones;
    private double tiempoTotalMinutos;

    /** Crea una ruta vacía. */
    public Ruta() {
        estaciones = new ArregloDinamico<>();
        tiempoTotalMinutos = 0;
    }

    /**
     * @param estacion estación a añadir al final del trayecto
     */
    public void agregarEstacion(Estacion estacion) {
        estaciones.agregar(estacion);
    }

    /** @return estaciones en orden de recorrido */
    public ArregloDinamico<Estacion> getEstaciones() {
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

    @Override
    public String toString() {
        if (esVacia()) {
            return "Ruta inexistente";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < estaciones.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(estaciones.obtener(i).getNombre());
        }
        sb.append(" [").append(tiempoTotalMinutos).append(" min]");
        return sb.toString();
    }
}
