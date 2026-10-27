package urbanroute.grafo;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.hash.TablaHash;

/**
 * Topología física de la red (no dirigida) con lista de adyacencia.
 * Sin lógica analítica. Mantiene sincronizada la TablaHash de estaciones.
 */
public class Grafo {

    private final ArregloDinamico<Estacion> vertices;
    private final ArregloDinamico<ArregloDinamico<Arista>> adyacencias;
    private final TablaHash tablaHash;

    /** Crea un grafo vacío con su propia TablaHash. */
    public Grafo() {
        this(new TablaHash());
    }

    /**
     * @param tablaHash índice de estaciones compartido con el controlador
     */
    public Grafo(TablaHash tablaHash) {
        this.vertices = new ArregloDinamico<>();
        this.adyacencias = new ArregloDinamico<>();
        this.tablaHash = tablaHash;
    }

    /**
     * Agrega una estación (vértice) si no existe.
     *
     * @param estacion estación
     * @return true si se agregó
     */
    public boolean agregarEstacion(Estacion estacion) {
        if (existeEstacion(estacion.getIdEstacion())) {
            return false;
        }
        vertices.agregar(estacion);
        adyacencias.agregar(new ArregloDinamico<>());
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
        ArregloDinamico<Arista> salientes = adyacencias.obtener(idx);
        for (int i = 0; i < salientes.tamanio(); i++) {
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
     * @return true si se creó o actualizó
     * @throws IllegalArgumentException si alguna estación no existe o es un lazo
     */
    public boolean agregarArista(String idOrigen, String idDestino, double tiempoMinutos) {
        int a = indiceDe(idOrigen);
        int b = indiceDe(idDestino);
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Estación inexistente: " + (a < 0 ? idOrigen : idDestino));
        }
        if (a == b) {
            throw new IllegalArgumentException("No se permiten lazos: " + idOrigen);
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
    public ArregloDinamico<Arista> obtenerAdyacentes(String idEstacion) {
        int idx = indiceDe(idEstacion);
        return idx < 0 ? new ArregloDinamico<>() : adyacencias.obtener(idx);
    }

    /**
     * @param indice índice del vértice
     * @return aristas salientes del vértice
     */
    public ArregloDinamico<Arista> obtenerAdyacentes(int indice) {
        return adyacencias.obtener(indice);
    }

    /**
     * @param idEstacion ID
     * @return número de conexiones directas (-1 si no existe)
     */
    public int obtenerGrado(String idEstacion) {
        int idx = indiceDe(idEstacion);
        return idx < 0 ? -1 : adyacencias.obtener(idx).tamanio();
    }

    /** @return lista de aristas sin duplicar (cada conexión no dirigida una vez) */
    public ArregloDinamico<Arista> obtenerAristasUnicas() {
        ArregloDinamico<Arista> unicas = new ArregloDinamico<>();
        for (int i = 0; i < vertices.tamanio(); i++) {
            ArregloDinamico<Arista> lista = adyacencias.obtener(i);
            for (int j = 0; j < lista.tamanio(); j++) {
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

    /** @return número de vértices */
    public int numVertices() {
        return vertices.tamanio();
    }

    /** @return vértices del grafo */
    public ArregloDinamico<Estacion> getVertices() {
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
        Grafo copia = new Grafo(new TablaHash());
        for (int i = 0; i < vertices.tamanio(); i++) {
            copia.agregarEstacion(vertices.obtener(i));
        }
        ArregloDinamico<Arista> aristas = obtenerAristasUnicas();
        for (int i = 0; i < aristas.tamanio(); i++) {
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
        ArregloDinamico<Arista> lista = adyacencias.obtener(indiceOrigen);
        for (int i = 0; i < lista.tamanio(); i++) {
            if (lista.obtener(i).getDestino().getIdEstacion().equals(idDestino)) {
                return lista.obtener(i);
            }
        }
        return null;
    }

    private boolean eliminarDeLista(ArregloDinamico<Arista> lista, String idDestino) {
        for (int i = 0; i < lista.tamanio(); i++) {
            if (lista.obtener(i).getDestino().getIdEstacion().equals(idDestino)) {
                lista.eliminar(i);
                return true;
            }
        }
        return false;
    }
}
