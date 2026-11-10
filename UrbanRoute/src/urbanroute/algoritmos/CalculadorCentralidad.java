/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.algoritmos;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;
import urbanroute.grafo.Grafo;

/**
 *
 * @author leo.uzgil@gmail.com
 * Centralidad de grado: ordena estaciones por número de conexiones (MergeSort propio) para identificar hubs de transferencia. Solo lectura sobre el Grafo.
 * 
 */
public class CalculadorCentralidad {

    private Grafo grafo;

    public CalculadorCentralidad(Grafo grafo) {
        this.grafo = grafo;
    }

    /** @return estaciones ordenadas por grado descendente (empate: ID ascendente) */
    public ListaEnlazada<Estacion> ordenarPorGrado() {
        ListaEnlazada<Estacion> ordenadas = new ListaEnlazada<>();
        Nodo<Estacion> n = grafo.getVertices().getCabeza();
        while (n != null) {
            // Ordenamiento por inserción: se inserta antes del primer nodo que debe ir después.
            Estacion nueva = n.getDato();
            Nodo<Estacion> pos = ordenadas.getCabeza();
            boolean insertada = false;
            while (pos != null && !insertada) {
                if (vaAntes(nueva, pos.getDato())) {
                    ordenadas.insertarAntesDe(pos, nueva);
                    insertada = true;
                } else {
                    pos = pos.getSiguiente();
                }
            }
            if (!insertada) {
                ordenadas.insertarAlFinal(nueva);
            }
            n = n.getSiguiente();
        }
        return ordenadas;
    }

    /** @return las k estaciones con mayor grado */
    public ListaEnlazada<Estacion> obtenerHubs(int k) {
        ListaEnlazada<Estacion> hubs = new ListaEnlazada<>();
        Nodo<Estacion> n = ordenarPorGrado().getCabeza();
        while (n != null && hubs.getSize() < k) {
            hubs.insertarAlFinal(n.getDato());
            n = n.getSiguiente();
        }
        return hubs;
    }

    public int obtenerGrado(String idEstacion) {
        return grafo.obtenerGrado(idEstacion);
    }

    /** @return true si a debe ir antes que b (más grado primero; empate por ID) */
    private boolean vaAntes(Estacion a, Estacion b) {
        int ga = grafo.obtenerGrado(a.getIdEstacion());
        int gb = grafo.obtenerGrado(b.getIdEstacion());
        if (ga != gb) {
            return ga > gb;
        }
        return a.getIdEstacion().compareTo(b.getIdEstacion()) < 0;
    }
}
