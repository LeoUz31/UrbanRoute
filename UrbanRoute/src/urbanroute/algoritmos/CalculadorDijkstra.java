/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.algoritmos;

import urbanroute.dominio.Ruta;
import urbanroute.estructuras.Arreglo;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 *
 * @author leo.uzgil@gmail.com
 * Calcula la ruta de menor tiempo entre dos estaciones (Dijkstra con ColaPrioridad).
 * Solo lectura sobre el Grafo
 */
public class CalculadorDijkstra {

    private final Grafo grafo;

    /** @param grafo red sobre la que se calcula */
    public CalculadorDijkstra(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * @param idOrigen  ID de la estación origen
     * @param idDestino ID de la estación destino
     * @return ruta óptima; vacía si no existe camino o alguna estación no existe
     */
    public Ruta calcularRuta(String idOrigen, String idDestino) {
        // Un camino pasa como mucho por todas las estaciones.
        Ruta ruta = new Ruta(grafo.numVertices());
        int origen = grafo.indiceDe(idOrigen);
        int destino = grafo.indiceDe(idDestino);
        if (origen < 0 || destino < 0) {
            return ruta;
        }
        int n = grafo.numVertices();
        double[] distancia = new double[n];
        int[] previo = new int[n];
        boolean[] visitado = new boolean[n];
        // distancia -1 significa "todavia no alcanzado".
        for (int i = 0; i < n; i++) {
            distancia[i] = -1;
            previo[i] = -1;
        }
        distancia[origen] = 0;
        boolean terminado = false;
        while (!terminado) {
            // Busqueda lineal del vertice no visitado con menor distancia.
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!visitado[i] && distancia[i] != -1
                        && (u == -1 || distancia[i] < distancia[u])) {
                    u = i;
                }
            }
            if (u == -1 || u == destino) {
                terminado = true;
            } else {
                visitado[u] = true;
                Arreglo<Arista> ady = grafo.obtenerAdyacentes(u);
                for (int i = 0; i < ady.size(); i++) {
                    Arista a = ady.obtener(i);
                    int v = grafo.indiceDe(a.getDestino().getIdEstacion());
                    double nueva = distancia[u] + a.getTiempoMinutos();
                    if (!visitado[v] && (distancia[v] == -1 || nueva < distancia[v])) {
                        distancia[v] = nueva;
                        previo[v] = u;
                    }
                }
            }
        }

        if (distancia[destino] == -1) {
            return ruta;
        }
        reconstruir(ruta, previo, destino);
        ruta.setTiempoTotalMinutos(distancia[destino]);
        return ruta;
    }

    private void reconstruir(Ruta ruta, int[] previo, int destino) {
        int largo = 0;
        for (int v = destino; v != -1; v = previo[v]) {
            largo++;
        }
        int[] camino = new int[largo];
        int k = largo - 1;
        for (int v = destino; v != -1; v = previo[v]) {
            camino[k--] = v;
        }
        for (int i = 0; i < largo; i++) {
            ruta.agregarEstacion(grafo.obtenerEstacion(camino[i]));
        }
    }
}
