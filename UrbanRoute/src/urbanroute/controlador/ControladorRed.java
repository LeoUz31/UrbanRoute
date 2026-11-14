package urbanroute.controlador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.File;
import java.io.IOException;
import urbanroute.dominio.Estacion;
import urbanroute.dominio.Ruta;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;
import urbanroute.hash.TablaHash;
import urbanroute.algoritmos.AnalizadorRedes;
import urbanroute.algoritmos.CalculadorCentralidad;
import urbanroute.algoritmos.CalculadorDijkstra;
import urbanroute.persistencia.EscritorCSV;
import urbanroute.persistencia.LectorCSV;
import urbanroute.persistencia.ParserCSV;

/**
 *
 * @author leo.uzgil@gmail.com
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
    /** Archivo del diccionario de estaciones cargado (null si la red es nueva). */
    private File archivoDiccionario;
    /** Archivo de conexiones del grafo cargado (null si la red es nueva). */
    private File archivoConexiones;

    /** BFS de la red real; guarda su último resultado mientras la red no cambie. */
    private AnalizadorRedes analizadorReal;
    /** Copia de la red con los cierres aplicados (null = sin simulación). */
    private Grafo redSimulada;
    /** BFS de la red simulada (null = sin simulación). */
    private AnalizadorRedes analizadorSimulado;
    /** Estaciones cerradas en la simulación actual. */
    private ListaEnlazada<Estacion> cerradas;

    /** Crea un controlador con una red vacía (sin máximo de estaciones). */
    public ControladorRed() {
        this.tablaHash = new TablaHash();
        this.grafo = new Grafo(tablaHash);
        this.parser = new ParserCSV();
        this.lector = new LectorCSV(parser);
        this.escritor = new EscritorCSV(parser);
        this.cambiosPendientes = false;
        this.analizadorReal = new AnalizadorRedes(grafo);
        restablecerSimulacion();
    }

    // ---------------------------------------------------------------- Persistencia

    /**
     * Carga la red desde los dos archivos del dataset (reemplaza la actual):
     * primero el diccionario (estaciones → TablaHash y vértices) y luego las conexiones (aristas).
     * Ambos archivos se leen antes de tocar la red: si uno falla, la red actual se conserva.
     * Las líneas inválidas no abortan la carga; una conexión que nombra un ID que no está
     * en el diccionario se reporta como error (no se crean estaciones incompletas).
     *
     * @param diccionario archivo de estaciones (ID_Estacion,Nombre,Linea,Zona,Capacidad)
     * @param conexiones  archivo de conexiones (Origen,Destino,Tiempo_Minutos)
     * @return mensajes de error por línea inválida (vacío si todo OK)
     * @throws IOException si no se puede leer alguno de los archivos
     */
    public ListaEnlazada<String> cargarRed(File diccionario, File conexiones) throws IOException {
        ListaEnlazada<String> lineasEstaciones = lector.leer(diccionario);
        ListaEnlazada<String> lineasConexiones = lector.leer(conexiones);
        ListaEnlazada<String> errores = new ListaEnlazada<>();
        restablecerSimulacion();
        grafo.limpiar();
        cargarEstaciones(lineasEstaciones, diccionario.getName(), errores);
        cargarConexiones(lineasConexiones, conexiones.getName(), errores);
        archivoDiccionario = diccionario;
        archivoConexiones = conexiones;
        cambiosPendientes = false;
        return errores;
    }

    /**
     * Guarda la red en los archivos cargados (formato original de cada uno).
     *
     * @return false si no hay archivos asociados (usar guardarRedComo)
     * @throws IOException si falla la escritura
     */
    public boolean guardarRed() throws IOException {
        if (archivoDiccionario == null || archivoConexiones == null) {
            return false;
        }
        guardarRedComo(archivoDiccionario, archivoConexiones);
        return true;
    }

    /**
     * Guarda la red en dos archivos nuevos y los deja como archivos actuales.
     *
     * @param diccionario destino de las estaciones
     * @param conexiones  destino de las conexiones
     * @throws IOException si falla la escritura
     */
    public void guardarRedComo(File diccionario, File conexiones) throws IOException {
        escritor.guardar(diccionario, conexiones, tablaHash, grafo);
        archivoDiccionario = diccionario;
        archivoConexiones = conexiones;
        cambiosPendientes = false;
    }

    /** Agrega al grafo las estaciones del diccionario; acumula los errores por línea. */
    private void cargarEstaciones(ListaEnlazada<String> lineas, String nombreArchivo, ListaEnlazada<String> errores) {
        int i = 1;
        Nodo<String> nodo = lineas.getCabeza();
        while (nodo != null) {
            String linea = nodo.getDato();
            if (!parser.esIgnorable(linea)) {
                Estacion e = parser.parsearEstacion(linea);
                if (e == null) {
                    errores.insertarAlFinal(nombreArchivo + " · Línea " + i + ": " + parser.error);
                } else if (!grafo.agregarEstacion(e)) {
                    errores.insertarAlFinal(nombreArchivo + " · Línea " + i + ": ID repetido -> " + linea);
                }
            }
            nodo = nodo.getSiguiente();
            i++;
        }
    }

    /** Agrega al grafo las conexiones; acumula los errores por línea. */
    private void cargarConexiones(ListaEnlazada<String> lineas, String nombreArchivo, ListaEnlazada<String> errores) {
        int i = 1;
        Nodo<String> nodo = lineas.getCabeza();
        while (nodo != null) {
            String linea = nodo.getDato();
            if (!parser.esIgnorable(linea)) {
                Arista a = parser.parsearArista(linea);
                String prefijo = nombreArchivo + " · Línea " + i + ": ";
                if (a == null) {
                    errores.insertarAlFinal(prefijo + parser.error);
                } else {
                    String origen = a.getOrigen().getIdEstacion();
                    String destino = a.getDestino().getIdEstacion();
                    if (!grafo.existeEstacion(origen)) {
                        errores.insertarAlFinal(prefijo + "estación «" + origen + "» no está en el diccionario");
                    } else if (!grafo.existeEstacion(destino)) {
                        errores.insertarAlFinal(prefijo + "estación «" + destino + "» no está en el diccionario");
                    } else if (!grafo.agregarArista(origen, destino, a.getTiempoMinutos())) {
                        errores.insertarAlFinal(prefijo + "lazo o tiempo no positivo -> " + linea);
                    }
                }
            }
            nodo = nodo.getSiguiente();
            i++;
        }
    }

    // ---------------------------------------------------------------- Edición

    // Toda modificación de la red real descarta la simulación de cierres (deja de ser válida).

    /** @return true si se agregó (false si el ID ya existía) */
    public boolean agregarEstacion(Estacion estacion) {
        boolean ok = grafo.agregarEstacion(estacion);
        if (ok) {
            registrarCambio();
        }
        return ok;
    }

    /** @return true si se eliminó */
    public boolean eliminarEstacion(String idEstacion) {
        boolean ok = grafo.eliminarEstacion(idEstacion) != null;
        if (ok) {
            registrarCambio();
        }
        return ok;
    }

    /** @return true si se creó/actualizó */
    public boolean agregarConexion(String idOrigen, String idDestino, double tiempo) {
        boolean ok = grafo.agregarArista(idOrigen, idDestino, tiempo);
        if (ok) {
            registrarCambio();
        }
        return ok;
    }

    /** @return true si existía y se eliminó */
    public boolean eliminarConexion(String idOrigen, String idDestino) {
        boolean ok = grafo.eliminarArista(idOrigen, idDestino);
        if (ok) {
            registrarCambio();
        }
        return ok;
    }

    /** Marca cambios pendientes de guardar y descarta la simulación. */
    private void registrarCambio() {
        cambiosPendientes = true;
        restablecerSimulacion();
    }

    // ---------------------------------------------------------------- Análisis

    /**
     * Ruta óptima (Dijkstra) en la red activa: la simulada si hay estaciones cerradas,
     * si no la real.
     *
     * @return ruta óptima (vacía si no existe)
     */
    public Ruta calcularRutaOptima(String idOrigen, String idDestino) {
        return new CalculadorDijkstra(redActiva()).calcularRuta(idOrigen, idDestino);
    }

    /**
     * Subredes (BFS) de la red activa. El BFS solo se ejecuta si esa red cambió desde
     * el último cálculo; si no, se devuelve el resultado guardado en el AnalizadorRedes.
     *
     * @return subredes (lista compartida: no modificar)
     */
    public ListaEnlazada<ListaEnlazada<Estacion>> detectarSubredes() {
        if (redSimulada == null) {
            return analizadorReal.obtenerSubredes();
        }
        return analizadorSimulado.obtenerSubredes();
    }

    /** @return top-k estaciones por grado (red real) */
    public ListaEnlazada<Estacion> obtenerHubs(int k) {
        return new CalculadorCentralidad(grafo).obtenerHubs(k);
    }

    // ---------------------------------------------------------------- Simulación de cierres

    /**
     * Cierra una estación en la simulación (los cierres se acumulan). La red real no cambia:
     * el primer cierre crea una copia del grafo y su propio AnalizadorRedes.
     *
     * @param idEstacion estación a cerrar
     * @return true si se cerró; false si no existe o ya estaba cerrada
     */
    public boolean cerrarEstacionSimulada(String idEstacion) {
        Estacion e = tablaHash.buscar(idEstacion);
        if (e == null || cerradas.contiene(e)) {
            return false;
        }
        if (redSimulada == null) {
            redSimulada = grafo.copiar();
            analizadorSimulado = new AnalizadorRedes(redSimulada);
        }
        redSimulada.eliminarEstacion(idEstacion);
        cerradas.insertarAlFinal(e);
        return true;
    }

    /**
     * Cierra en la simulación todas las estaciones de una línea.
     *
     * @param linea nombre de la línea (sin distinguir mayúsculas)
     * @return número de estaciones cerradas
     */
    public int cerrarLineaSimulada(String linea) {
        int total = 0;
        Nodo<Estacion> n = tablaHash.obtenerTodas().getCabeza();
        while (n != null) {
            Estacion e = n.getDato();
            if (e.getLinea().equalsIgnoreCase(linea) && cerrarEstacionSimulada(e.getIdEstacion())) {
                total++;
            }
            n = n.getSiguiente();
        }
        return total;
    }

    /** Descarta la simulación: vuelven a abrirse todas las estaciones. */
    public void restablecerSimulacion() {
        redSimulada = null;
        analizadorSimulado = null;
        cerradas = new ListaEnlazada<>();
    }

    /** @return true si hay al menos una estación cerrada en la simulación */
    public boolean haySimulacion() {
        return redSimulada != null;
    }

    /** @return estaciones cerradas en la simulación (lista compartida: no modificar) */
    public ListaEnlazada<Estacion> getCerradas() {
        return cerradas;
    }

    /** @return la red simulada si existe; si no, la real */
    private Grafo redActiva() {
        if (redSimulada == null) {
            return grafo;
        }
        return redSimulada;
    }

    // ---------------------------------------------------------------- Estado

    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }

    /** @return estaciones registradas (para poblar combos) */
    public ListaEnlazada<Estacion> obtenerEstaciones() {
        return tablaHash.obtenerTodas();
    }

    public TablaHash getTablaHash() { return tablaHash; }
    public Grafo getGrafo() { return grafo; }
    public LectorCSV getLector() { return lector; }

    /** @return archivo del diccionario de estaciones (null si no hay) */
    public File getArchivoDiccionario() { return archivoDiccionario; }

    /** @return archivo de conexiones del grafo (null si no hay) */
    public File getArchivoConexiones() { return archivoConexiones; }
}
