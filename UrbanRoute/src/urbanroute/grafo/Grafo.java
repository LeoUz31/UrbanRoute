/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.grafo;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;
import urbanroute.hash.TablaHash;

/**
 *
 * @author leo.uzgil@gmail.com
 * Topología física de la red (no dirigida) con lista de adyacencia.
 * Sin lógica analítica. Mantiene sincronizada la TablaHash de estaciones.
 * Los errores son causados pq la clase TablaHash no ha sido terminada.
 */
public class Grafo {

    private ListaEnlazada<Estacion> vertices;
    private ListaEnlazada<ListaEnlazada<Arista>> adyacencias;
    private TablaHash tablaHash;

    /** @param tablaHash índice de estaciones compartido con el controlador */
    public Grafo(TablaHash tablaHash) {
        this.vertices = new ListaEnlazada<>();
        this.adyacencias = new ListaEnlazada<>();
        this.tablaHash = tablaHash;
    }

    /** @return true si se agregó; false si el ID ya existía */
    public boolean agregarEstacion(Estacion estacion) {
        if (existeEstacion(estacion.getIdEstacion())) {
            return false;
        }
        vertices.insertarAlFinal(estacion);
        adyacencias.insertarAlFinal(new ListaEnlazada<Arista>());
        tablaHash.insertar(estacion.getIdEstacion(), estacion);
        return true;
    }

    /**
     * Elimina una estación y todas sus aristas (salientes y entrantes).
     *
     * @return estación eliminada o null
     */
    public Estacion eliminarEstacion(String idEstacion) {
        int idx = indiceDe(idEstacion);
        if (idx == -1) {
            return null;
        }
        Estacion eliminada = vertices.acceder(idx);
        Nodo<Arista> n = adyacencias.acceder(idx).getCabeza();
        while (n != null) {
            int vecino = indiceDe(n.getDato().getDestino().getIdEstacion());
            if (vecino != -1) {
                eliminarDeLista(adyacencias.acceder(vecino), idEstacion);
            }
            n = n.getSiguiente();
        }
        vertices.eliminarEn(idx);
        adyacencias.eliminarEn(idx);
        tablaHash.eliminar(idEstacion);
        return eliminada;
    }

    /**
     * Agrega una conexión no dirigida. Si ya existe, actualiza el tiempo.
     *
     * @return true si se creó o actualizó; false si alguna estación no existe,
     *         si es un lazo o si el tiempo no es positivo
     */
    public boolean agregarArista(String idOrigen, String idDestino, double tiempoMinutos) {
        int a = indiceDe(idOrigen);
        int b = indiceDe(idDestino);
        if (a == -1 || b == -1 || a == b || tiempoMinutos <= 0) {
            return false;
        }
        Arista existente = buscarArista(a, idDestino);
        if (existente != null) {
            existente.setTiempoMinutos(tiempoMinutos);
            buscarArista(b, idOrigen).setTiempoMinutos(tiempoMinutos);
            return true;
        }
        Estacion eo = vertices.acceder(a);
        Estacion ed = vertices.acceder(b);
        adyacencias.acceder(a).insertarAlFinal(new Arista(eo, ed, tiempoMinutos));
        adyacencias.acceder(b).insertarAlFinal(new Arista(ed, eo, tiempoMinutos));
        return true;
    }

    /** @return true si la conexión existía y se eliminó */
    public boolean eliminarArista(String idOrigen, String idDestino) {
        int a = indiceDe(idOrigen);
        int b = indiceDe(idDestino);
        if (a == -1 || b == -1) {
            return false;
        }
        boolean ok = eliminarDeLista(adyacencias.acceder(a), idDestino);
        eliminarDeLista(adyacencias.acceder(b), idOrigen);
        return ok;
    }

    /** @return aristas salientes del vértice en la posición indicada */
    public ListaEnlazada<Arista> obtenerAdyacentes(int indice) {
        return adyacencias.acceder(indice);
    }

    /** @return número de conexiones directas (-1 si no existe) */
    public int obtenerGrado(String idEstacion) {
        int idx = indiceDe(idEstacion);
        if (idx == -1) {
            return -1;
        }
        return adyacencias.acceder(idx).getSize();
    }

    /** @return lista de aristas sin duplicar (cada conexión no dirigida una vez) */
    public ListaEnlazada<Arista> obtenerAristasUnicas() {
        ListaEnlazada<Arista> unicas = new ListaEnlazada<>();
        Nodo<ListaEnlazada<Arista>> lista = adyacencias.getCabeza();
        int i = 0;
        while (lista != null) {
            Nodo<Arista> n = lista.getDato().getCabeza();
            while (n != null) {
                if (indiceDe(n.getDato().getDestino().getIdEstacion()) > i) {
                    unicas.insertarAlFinal(n.getDato());
                }
                n = n.getSiguiente();
            }
            lista = lista.getSiguiente();
            i++;
        }
        return unicas;
    }

    /** @return posición del vértice en la lista (busca la estación en la TablaHash) o -1 */
    public int indiceDe(String idEstacion) {
        Estacion e = tablaHash.buscar(idEstacion);
        if (e == null) {
            return -1;
        }
        return vertices.indiceDe(e);
    }

    /** @return estación en la posición indicada */
    public Estacion obtenerEstacion(int indice) {
        return vertices.acceder(indice);
    }

    public boolean existeEstacion(String idEstacion) {
        return tablaHash.contiene(idEstacion);
    }

    public int numVertices() {
        return vertices.getSize();
    }

    public ListaEnlazada<Estacion> getVertices() {
        return vertices;
    }

    public TablaHash getTablaHash() {
        return tablaHash;
    }

    /** @return copia independiente de la topología (comparte objetos Estacion) para simulaciones */
    public Grafo copiar() {
        Grafo copia = new Grafo(new TablaHash());
        Nodo<Estacion> e = vertices.getCabeza();
        while (e != null) {
            copia.agregarEstacion(e.getDato());
            e = e.getSiguiente();
        }
        Nodo<Arista> a = obtenerAristasUnicas().getCabeza();
        while (a != null) {
            Arista ar = a.getDato();
            copia.agregarArista(ar.getOrigen().getIdEstacion(), ar.getDestino().getIdEstacion(), ar.getTiempoMinutos());
            a = a.getSiguiente();
        }
        return copia;
    }

    /** Elimina todos los vértices y aristas. */
    public void limpiar() {
        vertices.vaciar();
        adyacencias.vaciar();
        tablaHash.limpiar();
    }

    private Arista buscarArista(int indiceOrigen, String idDestino) {
        Nodo<Arista> n = adyacencias.acceder(indiceOrigen).getCabeza();
        while (n != null) {
            if (n.getDato().getDestino().getIdEstacion().equals(idDestino)) {
                return n.getDato();
            }
            n = n.getSiguiente();
        }
        return null;
    }

    private boolean eliminarDeLista(ListaEnlazada<Arista> lista, String idDestino) {
        Nodo<Arista> n = lista.getCabeza();
        while (n != null) {
            if (n.getDato().getDestino().getIdEstacion().equals(idDestino)) {
                lista.eliminar(n.getDato());
                return true;
            }
            n = n.getSiguiente();
        }
        return false;
    }
}
