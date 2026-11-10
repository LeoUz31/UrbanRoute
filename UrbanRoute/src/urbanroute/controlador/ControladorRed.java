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
    private File archivoActual;

    /** Crea un controlador con una red vacía (sin máximo de estaciones). */
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
     * @return mensajes de error por línea inválida (vacío si todo OK)
     * @throws IOException si no se puede leer el archivo
     */
    public ListaEnlazada<String> cargarRed(File archivo) throws IOException {
        ListaEnlazada<String> lineas = lector.leer(archivo);
        ListaEnlazada<String> errores = new ListaEnlazada<>();
        ListaEnlazada<Arista> conexiones = new ListaEnlazada<>();
        grafo.limpiar();

        int i = 0;
        Nodo<String> nodoLinea = lineas.getCabeza();
        while (nodoLinea != null) {
            String linea = nodoLinea.getDato();
            if (!parser.esIgnorable(linea)) {
                if (parser.esEstacion(linea)) {
                    Estacion e = parser.parsearEstacion(linea);
                    if (e == null) {
                        errores.insertarAlFinal("Línea " + (i + 1) + ": " + parser.error);
                    } else if (!grafo.agregarEstacion(e)) {
                        errores.insertarAlFinal("Línea " + (i + 1) + ": ID repetido");
                    }
                } else if (parser.esConexion(linea)) {
                    Arista a = parser.parsearArista(linea);
                    if (a == null) {
                        errores.insertarAlFinal("Línea " + (i + 1) + ": " + parser.error);
                    } else {
                        conexiones.insertarAlFinal(a);
                    }
                } else {
                    errores.insertarAlFinal("Línea " + (i + 1) + ": formato desconocido -> " + linea);
                }
            }
            nodoLinea = nodoLinea.getSiguiente();
            i++;
        }

        Nodo<Arista> nodoConexion = conexiones.getCabeza();
        while (nodoConexion != null) {
            Arista a = nodoConexion.getDato();
            agregarSiNoExiste(a.getOrigen().getIdEstacion());
            agregarSiNoExiste(a.getDestino().getIdEstacion());
            if (!grafo.agregarArista(a.getOrigen().getIdEstacion(), a.getDestino().getIdEstacion(), a.getTiempoMinutos())) {
                errores.insertarAlFinal("Conexión " + a.imprimir() + ": lazo o tiempo no positivo");
            }
            nodoConexion = nodoConexion.getSiguiente();
        }
        archivoActual = archivo;
        cambiosPendientes = false;
        return errores;
    }

    /**
     * Guarda la red en el archivo cargado.
     *
     * @return false si no hay archivo asociado (usar guardarRedComo)
     * @throws IOException si falla la escritura
     */
    public boolean guardarRed() throws IOException {
        if (archivoActual == null) {
            return false;
        }
        guardarRedComo(archivoActual);
        return true;
    }

    /** @throws IOException si falla la escritura */
    public void guardarRedComo(File archivo) throws IOException {
        escritor.guardar(archivo, tablaHash, grafo);
        archivoActual = archivo;
        cambiosPendientes = false;
    }

    // ---------------------------------------------------------------- Edición

    /** @return true si se agregó (false si el ID ya existía) */
    public boolean agregarEstacion(Estacion estacion) {
        boolean ok = grafo.agregarEstacion(estacion);
        if (ok) {
            cambiosPendientes = true;
        }
        return ok;
    }

    /** @return true si se eliminó */
    public boolean eliminarEstacion(String idEstacion) {
        boolean ok = grafo.eliminarEstacion(idEstacion) != null;
        if (ok) {
            cambiosPendientes = true;
        }
        return ok;
    }

    /** @return true si se creó/actualizó */
    public boolean agregarConexion(String idOrigen, String idDestino, double tiempo) {
        boolean ok = grafo.agregarArista(idOrigen, idDestino, tiempo);
        if (ok) {
            cambiosPendientes = true;
        }
        return ok;
    }

    /** @return true si existía y se eliminó */
    public boolean eliminarConexion(String idOrigen, String idDestino) {
        boolean ok = grafo.eliminarArista(idOrigen, idDestino);
        if (ok) {
            cambiosPendientes = true;
        }
        return ok;
    }

    // ---------------------------------------------------------------- Análisis

    /** @return ruta óptima en la red real (vacía si no existe) */
    public Ruta calcularRutaOptima(String idOrigen, String idDestino) {
        return new CalculadorDijkstra(grafo).calcularRuta(idOrigen, idDestino);
    }

    /** @return ruta óptima en una red simulada (vacía si no existe) */
    //Dijkstra no implementado
    public Ruta calcularRutaSimulada(Grafo red, String idOrigen, String idDestino) {
        return new CalculadorDijkstra(red).calcularRuta(idOrigen, idDestino);
    }

    /** @return subredes de la red real */
    public ListaEnlazada<ListaEnlazada<Estacion>> detectarSubredes() {
        return new AnalizadorRedes(grafo).obtenerSubredes();
    }

    /** @return subredes de una red simulada */
    public ListaEnlazada<ListaEnlazada<Estacion>> detectarSubredesSimuladas(Grafo red) {
        return new AnalizadorRedes(red).obtenerSubredes();
    }

    /** @return top-k estaciones por grado */
    public ListaEnlazada<Estacion> obtenerHubs(int k) {
        return new CalculadorCentralidad(grafo).obtenerHubs(k);
    }

    /** Simula el cierre de una estación sin modificar la red real. */
    public Grafo simularCierreEstacion(String idEstacion) {
        Grafo copia = grafo.copiar();
        copia.eliminarEstacion(idEstacion);
        return copia;
    }

    /** Simula el cierre de todas las estaciones de una línea sin modificar la red real. */
    public Grafo simularCierreLinea(String linea) {
        Grafo copia = grafo.copiar();
        Nodo<Estacion> n = tablaHash.obtenerTodas().getCabeza();
        while (n != null) {
            Estacion e = n.getDato();
            if (e.getLinea().equalsIgnoreCase(linea)) {
                copia.eliminarEstacion(e.getIdEstacion());
            }
            n = n.getSiguiente();
        }
        return copia;
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
    public File getArchivoActual() { return archivoActual; }

    private void agregarSiNoExiste(String id) {
        if (!grafo.existeEstacion(id)) {
            grafo.agregarEstacion(new Estacion(id, id, "", "", 0));
        }
    }
}
