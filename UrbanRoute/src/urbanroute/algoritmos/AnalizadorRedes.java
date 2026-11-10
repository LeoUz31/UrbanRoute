/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.algoritmos;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Cola;
import urbanroute.estructuras.Nodo;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 *
 * @author leo.uzgil@gmail.com
 * Detecta subredes (componentes conexos) mediante BFS con Cola propia.
 * Solo lectura sobre el Grafo.
 */
public class AnalizadorRedes {

    private Grafo grafo;

    /** @param grafo red a analizar */
    public AnalizadorRedes(Grafo grafo) {
        this.grafo = grafo;
    }

    /** @return lista de subredes; cada una es la lista de sus estaciones */
    public ListaEnlazada<ListaEnlazada<Estacion>> obtenerSubredes() {
        ListaEnlazada<ListaEnlazada<Estacion>> subredes = new ListaEnlazada<>();
        ListaEnlazada<Estacion> visitadas = new ListaEnlazada<>();
        Nodo<Estacion> n = grafo.getVertices().getCabeza();
        while (n != null) {
            if (!visitadas.contiene(n.getDato())) {
                subredes.insertarAlFinal(bfs(n.getDato(), visitadas));
            }
            n = n.getSiguiente();
        }
        return subredes;
    }

    /** @return número de subredes */
    public int contarSubredes() {
        return obtenerSubredes().getSize();
    }

    /** @return true si existe camino entre ambas estaciones */
    public boolean estanConectadas(String idA, String idB) {
        int a = grafo.indiceDe(idA);
        int b = grafo.indiceDe(idB);
        if (a == -1 || b == -1) {
            return false;
        }
        ListaEnlazada<Estacion> visitadas = new ListaEnlazada<>();
        bfs(grafo.obtenerEstacion(a), visitadas);
        return visitadas.contiene(grafo.obtenerEstacion(b));
    }

    /**
     * Recorrido BFS desde una estación.
     *
     * @param inicio    estación inicial
     * @param visitadas estaciones ya marcadas (compartida entre recorridos)
     * @return estaciones alcanzadas
     */
    private ListaEnlazada<Estacion> bfs(Estacion inicio, ListaEnlazada<Estacion> visitadas) {
        ListaEnlazada<Estacion> componente = new ListaEnlazada<>();
        Cola<Estacion> cola = new Cola<>();
        visitadas.insertarAlFinal(inicio);
        cola.encolar(inicio);
        while (!cola.esta_vacia()) {
            Estacion u = cola.desencolar();
            componente.insertarAlFinal(u);
            Nodo<Arista> a = grafo.obtenerAdyacentes(grafo.indiceDe(u.getIdEstacion())).getCabeza();
            while (a != null) {
                Estacion v = grafo.getTablaHash().buscar(a.getDato().getDestino().getIdEstacion());
                if (v != null && !visitadas.contiene(v)) {
                    visitadas.insertarAlFinal(v);
                    cola.encolar(v);
                }
                a = a.getSiguiente();
            }
        }
        return componente;
    }
}
