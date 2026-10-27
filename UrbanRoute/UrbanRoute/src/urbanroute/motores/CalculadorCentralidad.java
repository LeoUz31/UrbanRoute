package urbanroute.motores;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.grafo.Grafo;

/**
 * Centralidad de grado: ordena estaciones por número de conexiones (MergeSort propio)
 * para identificar hubs de transferencia. Solo lectura sobre el Grafo.
 */
public class CalculadorCentralidad {

    private final Grafo grafo;

    /** @param grafo red a analizar */
    public CalculadorCentralidad(Grafo grafo) {
        this.grafo = grafo;
    }

    /** @return estaciones ordenadas por grado descendente (empate: ID ascendente) */
    public ArregloDinamico<Estacion> ordenarPorGrado() {
        int n = grafo.numVertices();
        Estacion[] est = new Estacion[n];
        int[] grados = new int[n];
        for (int i = 0; i < n; i++) {
            est[i] = grafo.obtenerEstacion(i);
            grados[i] = grafo.obtenerAdyacentes(i).tamanio();
        }
        mergeSort(est, grados, new Estacion[n], new int[n], 0, n - 1);
        ArregloDinamico<Estacion> res = new ArregloDinamico<>(Math.max(1, n));
        for (int i = 0; i < n; i++) {
            res.agregar(est[i]);
        }
        return res;
    }

    /**
     * @param k cantidad de hubs solicitados
     * @return las k estaciones con mayor grado
     */
    public ArregloDinamico<Estacion> obtenerHubs(int k) {
        ArregloDinamico<Estacion> ordenadas = ordenarPorGrado();
        ArregloDinamico<Estacion> hubs = new ArregloDinamico<>();
        for (int i = 0; i < k && i < ordenadas.tamanio(); i++) {
            hubs.agregar(ordenadas.obtener(i));
        }
        return hubs;
    }

    /**
     * @param idEstacion ID
     * @return grado de la estación
     */
    public int obtenerGrado(String idEstacion) {
        return grafo.obtenerGrado(idEstacion);
    }

    private void mergeSort(Estacion[] e, int[] g, Estacion[] te, int[] tg, int ini, int fin) {
        if (ini >= fin) {
            return;
        }
        int mid = (ini + fin) / 2;
        mergeSort(e, g, te, tg, ini, mid);
        mergeSort(e, g, te, tg, mid + 1, fin);
        int i = ini, j = mid + 1, k = ini;
        while (i <= mid && j <= fin) {
            if (vaAntes(e[i], g[i], e[j], g[j])) {
                te[k] = e[i]; tg[k++] = g[i++];
            } else {
                te[k] = e[j]; tg[k++] = g[j++];
            }
        }
        while (i <= mid) { te[k] = e[i]; tg[k++] = g[i++]; }
        while (j <= fin) { te[k] = e[j]; tg[k++] = g[j++]; }
        for (k = ini; k <= fin; k++) {
            e[k] = te[k];
            g[k] = tg[k];
        }
    }

    private boolean vaAntes(Estacion a, int ga, Estacion b, int gb) {
        if (ga != gb) {
            return ga > gb;
        }
        return a.getIdEstacion().compareTo(b.getIdEstacion()) <= 0;
    }
}
