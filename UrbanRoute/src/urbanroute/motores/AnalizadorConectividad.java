package urbanroute.motores;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.estructuras.Cola;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 * Detecta subredes (componentes conexos) mediante BFS con Cola propia.
 * Solo lectura sobre el Grafo.
 */
public class AnalizadorConectividad {

    private final Grafo grafo;

    /** @param grafo red a analizar */
    public AnalizadorConectividad(Grafo grafo) {
        this.grafo = grafo;
    }

    /** @return lista de subredes; cada una es la lista de sus estaciones */
    public ArregloDinamico<ArregloDinamico<Estacion>> obtenerSubredes() {
        ArregloDinamico<ArregloDinamico<Estacion>> subredes = new ArregloDinamico<>();
        boolean[] visitado = new boolean[grafo.numVertices()];
        for (int i = 0; i < grafo.numVertices(); i++) {
            if (!visitado[i]) {
                subredes.agregar(bfs(i, visitado));
            }
        }
        return subredes;
    }

    /** @return número de subredes */
    public int contarSubredes() {
        return obtenerSubredes().tamanio();
    }

    /**
     * @param idA ID estación A
     * @param idB ID estación B
     * @return true si existe camino entre ambas
     */
    public boolean estanConectadas(String idA, String idB) {
        int a = grafo.indiceDe(idA);
        int b = grafo.indiceDe(idB);
        if (a < 0 || b < 0) {
            return false;
        }
        boolean[] visitado = new boolean[grafo.numVertices()];
        bfs(a, visitado);
        return visitado[b];
    }

    /**
     * Recorrido BFS desde un vértice.
     *
     * @param inicio   índice inicial
     * @param visitado marcas compartidas
     * @return estaciones alcanzadas
     */
    private ArregloDinamico<Estacion> bfs(int inicio, boolean[] visitado) {
        ArregloDinamico<Estacion> componente = new ArregloDinamico<>();
        Cola<Integer> cola = new Cola<>();
        visitado[inicio] = true;
        cola.encolar(inicio);
        while (!cola.estaVacia()) {
            int u = cola.desencolar();
            componente.agregar(grafo.obtenerEstacion(u));
            ArregloDinamico<Arista> ady = grafo.obtenerAdyacentes(u);
            for (int i = 0; i < ady.tamanio(); i++) {
                int v = grafo.indiceDe(ady.obtener(i).getDestino().getIdEstacion());
                if (v >= 0 && !visitado[v]) {
                    visitado[v] = true;
                    cola.encolar(v);
                }
            }
        }
        return componente;
    }
}
