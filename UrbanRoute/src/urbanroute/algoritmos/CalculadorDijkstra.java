/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.algoritmos;

import urbanroute.dominio.Ruta;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 *
 * @author leo.uzgil@gmail.com
 * Calcula la ruta de menor tiempo entre dos estaciones (Dijkstra con ColaPrioridad).
 * Solo lectura sobre el Grafo
 */
public class CalculadorDijkstra {

    private Grafo grafo;

    public CalculadorDijkstra(Grafo grafo) {
        this.grafo = grafo;
    }

    /** @return ruta óptima; vacía si no existe camino o alguna estación no existe */
    public Ruta calcularRuta(String idOrigen, String idDestino) {
        Ruta ruta = new Ruta();
        int origen = grafo.indiceDe(idOrigen);
        int destino = grafo.indiceDe(idDestino);
        if (origen == -1 || destino == -1) {
            return ruta;
        }
        int n = grafo.numVertices();
        // distancia -1 significa "todavía no alcanzado"; previo -1 significa "sin anterior".
        ListaEnlazada<Double> distancia = new ListaEnlazada<>();
        ListaEnlazada<Integer> previo = new ListaEnlazada<>();
        ListaEnlazada<Boolean> visitado = new ListaEnlazada<>();
        int i = 0;
        while (i < n) {
            distancia.insertarAlFinal(-1.0);
            previo.insertarAlFinal(-1);
            visitado.insertarAlFinal(false);
            i++;
        }
        distancia.modificar(origen, 0.0);

        boolean terminado = false;
        while (!terminado) {
            // Búsqueda lineal del vértice no visitado con menor distancia.
            int u = -1;
            i = 0;
            while (i < n) {
                if (!visitado.acceder(i) && distancia.acceder(i) != -1) {
                    if (u == -1 || distancia.acceder(i) < distancia.acceder(u)) {
                        u = i;
                    }
                }
                i++;
            }
            if (u == -1 || u == destino) {
                terminado = true;
            } else {
                visitado.modificar(u, true);
                Nodo<Arista> nodo = grafo.obtenerAdyacentes(u).getCabeza();
                while (nodo != null) {
                    Arista a = nodo.getDato();
                    int v = grafo.indiceDe(a.getDestino().getIdEstacion());
                    double nueva = distancia.acceder(u) + a.getTiempoMinutos();
                    if (!visitado.acceder(v)) {
                        if (distancia.acceder(v) == -1 || nueva < distancia.acceder(v)) {
                            distancia.modificar(v, nueva);
                            previo.modificar(v, u);
                        }
                    }
                    nodo = nodo.getSiguiente();
                }
            }
        }

        if (distancia.acceder(destino) == -1) {
            return ruta;
        }
        reconstruir(ruta, previo, destino);
        ruta.setTiempoTotalMinutos(distancia.acceder(destino));
        return ruta;
    }

    /** Recursivo: primero agrega el camino hasta el anterior y luego la estación v. */
    private void reconstruir(Ruta ruta, ListaEnlazada<Integer> previo, int v) {
        if (v == -1) {
            return;
        }
        reconstruir(ruta, previo, previo.acceder(v));
        ruta.agregarEstacion(grafo.obtenerEstacion(v));
    }
}
