package urbanroute.motores;

import urbanroute.dominio.Ruta;
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.estructuras.ColaPrioridad;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 * Calcula la ruta de menor tiempo entre dos estaciones (Dijkstra con ColaPrioridad).
 * Solo lectura sobre el Grafo.
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
        Ruta ruta = new Ruta();
        int origen = grafo.indiceDe(idOrigen);
        int destino = grafo.indiceDe(idDestino);
        if (origen < 0 || destino < 0) {
            return ruta;
        }
        int n = grafo.numVertices();
        double[] distancia = new double[n];
        int[] previo = new int[n];
        boolean[] visitado = new boolean[n];
        for (int i = 0; i < n; i++) {
            distancia[i] = Double.POSITIVE_INFINITY;
            previo[i] = -1;
        }
        distancia[origen] = 0;
        ColaPrioridad<Integer> cola = new ColaPrioridad<>();
        cola.insertar(origen, 0);

        while (!cola.estaVacia()) {
            int u = cola.extraerMinimo();
            if (visitado[u]) {
                continue;
            }
            visitado[u] = true;
            if (u == destino) {
                break;
            }
            ArregloDinamico<Arista> ady = grafo.obtenerAdyacentes(u);
            for (int i = 0; i < ady.tamanio(); i++) {
                Arista a = ady.obtener(i);
                int v = grafo.indiceDe(a.getDestino().getIdEstacion());
                double nueva = distancia[u] + a.getTiempoMinutos();
                if (!visitado[v] && nueva < distancia[v]) {
                    distancia[v] = nueva;
                    previo[v] = u;
                    cola.insertar(v, nueva);
                }
            }
        }

        if (distancia[destino] == Double.POSITIVE_INFINITY) {
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
