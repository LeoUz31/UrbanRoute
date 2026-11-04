package urbanroute.controlador;

import java.io.File;
import java.io.IOException;
import urbanroute.dominio.Estacion;
import urbanroute.dominio.Ruta;
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;
import urbanroute.hash.TablaHash;
import urbanroute.motores.AnalizadorConectividad;
import urbanroute.motores.CalculadorCentralidad;
import urbanroute.motores.CalculadorDijkstra;
import urbanroute.persistencia.EscritorCSV;
import urbanroute.persistencia.LectorCSV;
import urbanroute.persistencia.ParserCSV;

/**
 * Mediador MVC: orquesta carga/guardado, edición de la red y motores algorítmicos.
 * No conoce Swing; la vista consulta {@link #hayCambiosPendientes()} para las alertas.
 */
public class ControladorRed {

    private TablaHash tablaHash;
    private Grafo grafo;
    private boolean cambiosPendientes;

    private ParserCSV parser;
    private LectorCSV lector;   
    private EscritorCSV escritor;
    private File archivoActual;

    /** Crea un controlador con red vacía. */
    public ControladorRed() {
        this.tablaHash = new TablaHash();
        this.grafo = new Grafo(tablaHash);
        this.parser = new ParserCSV();
        this.lector = new LectorCSV(parser);
        this.escritor = new EscritorCSV(parser);
        this.cambiosPendientes = false;
    }

    // ---------------------------------------------------------------- Persistencia

    /**
     * Carga la red desde un CSV (reemplaza la actual). Las líneas inválidas no abortan la carga.
     * Estaciones referenciadas en conexiones y no declaradas se crean solo con su ID.
     *
     * @param archivo archivo CSV
     * @return mensajes de error por línea inválida (vacío si todo OK)
     * @throws IOException si no se puede leer el a
     */
    public ArregloDinamico<String> cargarRed(File archivo) throws IOException {
        ArregloDinamico<String> lineas = lector.leer(archivo);
        ArregloDinamico<String> errores = new ArregloDinamico<>();
        ArregloDinamico<Arista> conexiones = new ArregloDinamico<>();
        grafo.limpiar();

        for (int i = 0; i < lineas.tamanio(); i++) {
            String linea = lineas.obtener(i);
            if (parser.esIgnorable(linea)) {
                continue;
            }
            try {
                if (parser.esEstacion(linea)) {
                    grafo.agregarEstacion(parser.parsearEstacion(linea));
                } else if (parser.esConexion(linea)) {
                    conexiones.agregar(parser.parsearArista(linea));
                } else {
                    errores.agregar("Línea " + (i + 1) + ": formato desconocido -> " + linea);
                }
            } catch (IllegalArgumentException ex) {
                errores.agregar("Línea " + (i + 1) + ": " + ex.getMessage());
            }
        }
        for (int i = 0; i < conexiones.tamanio(); i++) {
            Arista a = conexiones.obtener(i);
            try {
                agregarSiNoExiste(a.getOrigen().getIdEstacion());
                agregarSiNoExiste(a.getDestino().getIdEstacion());
                grafo.agregarArista(a.getOrigen().getIdEstacion(), a.getDestino().getIdEstacion(), a.getTiempoMinutos());
            } catch (IllegalArgumentException ex) {
                errores.agregar("Conexión " + a + ": " + ex.getMessage());
            }
        }
        archivoActual = archivo;
        cambiosPendientes = false;
        return errores;
    }

    /**
     * Guarda la red en el archivo cargado.
     *
     * @throws IOException           si falla la escritura
     * @throws IllegalStateException si no hay archivo asociado
     */
    public void guardarRed() throws IOException {
        if (archivoActual == null) {
            throw new IllegalStateException("No hay archivo asociado; use guardarRed(File)");
        }
        guardarRed(archivoActual);
    }

    /**
     * @param archivo destino
     * @throws IOException si falla la escritura
     */
    public void guardarRed(File archivo) throws IOException {
        escritor.guardar(archivo, tablaHash, grafo);
        archivoActual = archivo;
        cambiosPendientes = false;
    }

    // ---------------------------------------------------------------- Edición

    /**
     * @param estacion estación nueva
     * @return true si se agregó (false si el ID ya existía)
     */
    public boolean agregarEstacion(Estacion estacion) {
        boolean ok = grafo.agregarEstacion(estacion);
        cambiosPendientes |= ok;
        return ok;
    }

    /**
     * @param idEstacion ID
     * @return true si se eliminó
     */
    public boolean eliminarEstacion(String idEstacion) {
        boolean ok = grafo.eliminarEstacion(idEstacion) != null;
        cambiosPendientes |= ok;
        return ok;
    }

    /**
     * @param idOrigen  ID origen
     * @param idDestino ID destino
     * @param tiempo    minutos (positivo)
     * @return true si se creó/actualizó
     */
    public boolean agregarConexion(String idOrigen, String idDestino, double tiempo) {
        boolean ok = grafo.agregarArista(idOrigen, idDestino, tiempo);
        cambiosPendientes |= ok;
        return ok;
    }

    /**
     * @param idOrigen  ID origen
     * @param idDestino ID destino
     * @return true si existía y se eliminó
     */
    public boolean eliminarConexion(String idOrigen, String idDestino) {
        boolean ok = grafo.eliminarArista(idOrigen, idDestino);
        cambiosPendientes |= ok;
        return ok;
    }

    // ---------------------------------------------------------------- Análisis

    /**
     * @param idOrigen  ID origen
     * @param idDestino ID destino
     * @return ruta óptima (vacía si no existe)
     */
    public Ruta calcularRutaOptima(String idOrigen, String idDestino) {
        return calcularRutaOptima(grafo, idOrigen, idDestino);
    }

    /**
     * @param red       grafo a usar (real o simulado)
     * @param idOrigen  ID origen
     * @param idDestino ID destino
     * @return ruta óptima (vacía si no existe)
     */
    public Ruta calcularRutaOptima(Grafo red, String idOrigen, String idDestino) {
        return new CalculadorDijkstra(red).calcularRuta(idOrigen, idDestino);
    }

    /** @return subredes de la red actual */
    public ArregloDinamico<ArregloDinamico<Estacion>> detectarSubredes() {
        return detectarSubredes(grafo);
    }

    /**
     * @param red grafo a usar (real o simulado)
     * @return subredes
     */
    public ArregloDinamico<ArregloDinamico<Estacion>> detectarSubredes(Grafo red) {
        return new AnalizadorConectividad(red).obtenerSubredes();
    }

    /**
     * @param k cantidad de hubs
     * @return top-k estaciones por grado
     */
    public ArregloDinamico<Estacion> obtenerHubs(int k) {
        return new CalculadorCentralidad(grafo).obtenerHubs(k);
    }

    /**
     * Simula el cierre de una estación sin modificar la red real.
     *
     * @param idEstacion ID a cerrar
     * @return copia del grafo sin la estación
     */
    public Grafo simularCierreEstacion(String idEstacion) {
        Grafo copia = grafo.copiar();
        copia.eliminarEstacion(idEstacion);
        return copia;
    }

    /**
     * Simula el cierre de todas las estaciones de una línea sin modificar la red real.
     *
     * @param linea nombre de la línea
     * @return copia del grafo sin las estaciones de la línea
     */
    public Grafo simularCierreLinea(String linea) {
        Grafo copia = grafo.copiar();
        ArregloDinamico<Estacion> todas = tablaHash.obtenerTodas();
        for (int i = 0; i < todas.tamanio(); i++) {
            Estacion e = todas.obtener(i);
            if (e.getLinea().equalsIgnoreCase(linea)) {
                copia.eliminarEstacion(e.getIdEstacion());
            }
        }
        return copia;
    }

    // ---------------------------------------------------------------- Estado

    /** @return true si hay modificaciones sin guardar */
    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }

    /** @return estaciones registradas (para poblar combos) */
    public ArregloDinamico<Estacion> obtenerEstaciones() {
        return tablaHash.obtenerTodas();
    }

    public TablaHash getTablaHash() { return tablaHash; }
    public Grafo getGrafo() { return grafo; }
    public LectorCSV getLector() { return lector; }
    public File getArchivoActual() { return archivoActual; }

    private void agregarSiNoExiste(String id) {
        if (!grafo.existeEstacion(id)) {
            grafo.agregarEstacion(new Estacion(id));
        }
    }
}
