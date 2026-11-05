/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.grafo;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.Arreglo;
import urbanroute.hash.TablaHash;

/**
 *
 * @author leo.uzgil@gmail.com
 * Topología física de la red (no dirigida) con lista de adyacencia.
 * Sin lógica analítica. Mantiene sincronizada la TablaHash de estaciones.
 * Los errores son causados pq la clase TablaHash no ha sido terminada.
 */
public class Grafo {

    private final int Max;
    private final Arreglo<Estacion> vertices;
    private final Arreglo<Arreglo<Arista>> adyacencias;
    private final TablaHash tablaHash;

    /**
     * @param maxEstaciones máximo de estaciones (fijo; se da una sola vez)
     * @param tablaHash     índice de estaciones compartido con el controlador
     */
    public Grafo(int maxEstaciones, TablaHash tablaHash) {
        this.Max = maxEstaciones;
        this.vertices = new Arreglo<>(maxEstaciones);
        this.adyacencias = new Arreglo<>(maxEstaciones);
        this.tablaHash = tablaHash;
    }

    /**
     * Agrega una estación (vértice) si no existe y si hay espacio.
     *
     * @param estacion estación
     * @return true si se agregó; false si ya existía o se alcanzó el máximo
     */
    public boolean agregarEstacion(Estacion estacion) {
        if (existeEstacion(estacion.getIdEstacion()) || vertices.estaLleno()) {
            return false;
        }
        vertices.agregar(estacion);
        // Una estación puede conectarse como mucho con todas las demás.
        adyacencias.agregar(new Arreglo<>(Max));
        tablaHash.insertar(estacion.getIdEstacion(), estacion);
        return true;
    }

    /**
     * Elimina una estación y todas sus aristas (salientes y entrantes).
     *
     * @param idEstacion ID
     * @return estación eliminada o null
     */
    public Estacion eliminarEstacion(String idEstacion) {
        int idx = indiceDe(idEstacion);
        if (idx < 0) {
            return null;
        }
        Estacion eliminada = vertices.obtener(idx);
        Arreglo<Arista> salientes = adyacencias.obtener(idx);
        for (int i = 0; i < salientes.size(); i++) {
            int vecino = indiceDe(salientes.obtener(i).getDestino().getIdEstacion());
            if (vecino >= 0) {
                eliminarDeLista(adyacencias.obtener(vecino), idEstacion);
            }
        }
        vertices.eliminar(idx);
        adyacencias.eliminar(idx);
        tablaHash.eliminar(idEstacion);
        return eliminada;
    }

    /**
     * Agrega una conexión no dirigida. Si ya existe, actualiza el tiempo.
     *
     * @param idOrigen      ID origen
     * @param idDestino     ID destino
     * @param tiempoMinutos peso positivo
     * @return true si se creó o actualizó; false si alguna estación no existe,
     *         si es un lazo o si el tiempo no es positivo
     */
    public boolean agregarArista(String idOrigen, String idDestino, double tiempoMinutos) {
        int a = indiceDe(idOrigen);
        int b = indiceDe(idDestino);
        if (a < 0 || b < 0 || a == b || tiempoMinutos <= 0) {
            return false;
        }
        Arista existente = buscarArista(a, idDestino);
        if (existente != null) {
            existente.setTiempoMinutos(tiempoMinutos);
            buscarArista(b, idOrigen).setTiempoMinutos(tiempoMinutos);
            return true;
        }
        Estacion eo = vertices.obtener(a);
        Estacion ed = vertices.obtener(b);
        adyacencias.obtener(a).agregar(new Arista(eo, ed, tiempoMinutos));
        adyacencias.obtener(b).agregar(new Arista(ed, eo, tiempoMinutos));
        return true;
    }

    /**
     * Elimina la conexión no dirigida entre dos estaciones.
     *
     * @param idOrigen  ID origen
     * @param idDestino ID destino
     * @return true si existía
     */
    public boolean eliminarArista(String idOrigen, String idDestino) {
        int a = indiceDe(idOrigen);
        int b = indiceDe(idDestino);
        if (a < 0 || b < 0) {
            return false;
        }
        boolean ok = eliminarDeLista(adyacencias.obtener(a), idDestino);
        eliminarDeLista(adyacencias.obtener(b), idOrigen);
        return ok;
    }

    /**
     * @param idEstacion ID
     * @return aristas salientes de la estación (vacío si no existe)
     */
    public Arreglo<Arista> obtenerAdyacentes(String idEstacion) {
        int idx = indiceDe(idEstacion);
        return idx < 0 ? new Arreglo<Arista>(0) : adyacencias.obtener(idx);
    }

    /**
     * @param indice índice del vértice
     * @return aristas salientes del vértice
     */
    public Arreglo<Arista> obtenerAdyacentes(int indice) {
        return adyacencias.obtener(indice);
    }

    /**
     * @param idEstacion ID
     * @return número de conexiones directas (-1 si no existe)
     */
    public int obtenerGrado(String idEstacion) {
        int idx = indiceDe(idEstacion);
        return idx < 0 ? -1 : adyacencias.obtener(idx).size();
    }

    /** @return lista de aristas sin duplicar (cada conexión no dirigida una vez) */
    public Arreglo<Arista> obtenerAristasUnicas() {
        // Cada conexión aparece dos veces en las listas: el total es la suma de grados / 2.
        int total = 0;
        for (int i = 0; i < vertices.size(); i++) {
            total = total + adyacencias.obtener(i).size();
        }
        Arreglo<Arista> unicas = new Arreglo<>(total / 2);
        for (int i = 0; i < vertices.size(); i++) {
            Arreglo<Arista> lista = adyacencias.obtener(i);
            for (int j = 0; j < lista.size(); j++) {
                Arista ar = lista.obtener(j);
                if (indiceDe(ar.getDestino().getIdEstacion()) > i) {
                    unicas.agregar(ar);
                }
            }
        }
        return unicas;
    }

    /**
     * Retorna la posición interna del vértice. Valida existencia en O(1) con la TablaHash.
     *
     * @param idEstacion ID
     * @return índice o -1
     */
    public int indiceDe(String idEstacion) {
        Estacion e = tablaHash.buscar(idEstacion);
        return e == null ? -1 : vertices.indiceDe(e);
    }

    /**
     * @param indice índice interno
     * @return estación en esa posición
     */
    public Estacion obtenerEstacion(int indice) {
        return vertices.obtener(indice);
    }

    /**
     * @param idEstacion ID
     * @return estación o null (búsqueda O(1))
     */
    public Estacion obtenerEstacion(String idEstacion) {
        return tablaHash.buscar(idEstacion);
    }

    /**
     * @param idEstacion ID
     * @return true si existe
     */
    public boolean existeEstacion(String idEstacion) {
        return tablaHash.contiene(idEstacion);
    }

    /** @return máximo de estaciones */
    public int getMax() {
        return Max;
    }

    /** @return número de vértices */
    public int numVertices() {
        return vertices.size();
    }

    /** @return vértices del grafo */
    public Arreglo<Estacion> getVertices() {
        return vertices;
    }

    /** @return TablaHash asociada */
    public TablaHash getTablaHash() {
        return tablaHash;
    }

    /**
     * Copia profunda de la topología (comparte objetos Estacion) para simulaciones.
     *
     * @return nuevo Grafo independiente
     */
    public Grafo copiar() {
        Grafo copia = new Grafo(Max, new TablaHash());
        for (int i = 0; i < vertices.size(); i++) {
            copia.agregarEstacion(vertices.obtener(i));
        }
        Arreglo<Arista> aristas = obtenerAristasUnicas();
        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.obtener(i);
            copia.agregarArista(a.getOrigen().getIdEstacion(), a.getDestino().getIdEstacion(), a.getTiempoMinutos());
        }
        return copia;
    }

    /** Elimina todos los vértices y aristas. */
    public void limpiar() {
        vertices.limpiar();
        adyacencias.limpiar();
        tablaHash.limpiar();
    }

    private Arista buscarArista(int indiceOrigen, String idDestino) {
        Arreglo<Arista> lista = adyacencias.obtener(indiceOrigen);
        for (int i = 0; i < lista.size(); i++) {
            if (Texto.iguales(lista.obtener(i).getDestino().getIdEstacion(), idDestino)) {
                return lista.obtener(i);
            }
        }
        return null;
    }

    private boolean eliminarDeLista(Arreglo<Arista> lista, String idDestino) {
        for (int i = 0; i < lista.size(); i++) {
            if (Texto.iguales(lista.obtener(i).getDestino().getIdEstacion(), idDestino)) {
                lista.eliminar(i);
                return true;
            }
        }
        return false;
    }
}
