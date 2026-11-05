package urbanroute.controlador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import urbanroute.estructuras.Arreglo;
import java.io.File;
import java.io.IOException;
import urbanroute.dominio.Estacion;
import urbanroute.dominio.Ruta;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;
import urbanroute.hash.TablaHash;
import urbanroute.algoritmos.AnalizadorConectividad;
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

    /**
     * Crea un controlador con red vacía.
     *
     * @param maxEstaciones máximo de estaciones de la red (fijo; se da una sola vez)
     */
    public ControladorRed(int maxEstaciones) {
        this.tablaHash = new TablaHash();
        this.grafo = new Grafo(maxEstaciones, tablaHash);
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
     * @throws IOException si no se puede leer el archivo
     */
    public Arreglo<String> cargarRed(File archivo) throws IOException {
        Arreglo<String> lineas = lector.leer(archivo);
        // Cada línea produce como mucho un error o una conexión.
        Arreglo<String> errores = new Arreglo<>(lineas.size());
        Arreglo<Arista> conexiones = new Arreglo<>(lineas.size());
        grafo.limpiar();

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.obtener(i);
            if (parser.esIgnorable(linea)) {
                continue;
            }
            if (parser.esEstacion(linea)) {
                Estacion e = parser.parsearEstacion(linea);
                if (e == null) {
                    errores.agregar("Línea " + (i + 1) + ": " + parser.error);
                } else {
                    if (!grafo.agregarEstacion(e)) {
                        errores.agregar("Línea " + (i + 1) + ": ID repetido o máximo de estaciones (" + grafo.getMax() + ") alcanzado");
                    }
                }
            } else if (parser.esConexion(linea)) {
                Arista a = parser.parsearArista(linea);
                if (a == null) {
                    errores.agregar("Línea " + (i + 1) + ": " + parser.error);
                } else {
                    conexiones.agregar(a);
                }
            } else {
                errores.agregar("Línea " + (i + 1) + ": formato desconocido -> " + linea);
            }
        }
        for (int i = 0; i < conexiones.size(); i++) {
            Arista a = conexiones.obtener(i);
            agregarSiNoExiste(a.getOrigen().getIdEstacion());
            agregarSiNoExiste(a.getDestino().getIdEstacion());
            if (!grafo.agregarArista(a.getOrigen().getIdEstacion(), a.getDestino().getIdEstacion(), a.getTiempoMinutos())) {
                errores.agregar("Conexión " + a.imprimir() + ": lazo, tiempo no positivo o máximo de estaciones alcanzado");
            }
        }
        archivoActual = archivo;
        cambiosPendientes = false;
        return errores;
    }

    /**
     * Guarda la red en el archivo cargado.
     *
     * @return false si no hay archivo asociado (usar guardarRed(File))
     * @throws IOException si falla la escritura
     */
    public boolean guardarRed() throws IOException {
        if (archivoActual == null) {
            return false;
        }
        guardarRed(archivoActual);
        return true;
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
    public Arreglo<Arreglo<Estacion>> detectarSubredes() {
        return detectarSubredes(grafo);
    }

    /**
     * @param red grafo a usar (real o simulado)
     * @return subredes
     */
    public Arreglo<Arreglo<Estacion>> detectarSubredes(Grafo red) {
        return new AnalizadorConectividad(red).obtenerSubredes();
    }

    /**
     * @param k cantidad de hubs
     * @return top-k estaciones por grado
     */
    public Arreglo<Estacion> obtenerHubs(int k) {
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
        Arreglo<Estacion> todas = tablaHash.obtenerTodas();
        for (int i = 0; i < todas.size(); i++) {
            Estacion e = todas.obtener(i);
            if (Texto.igualesSinMayusculas(e.getLinea(), linea)) {
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
    public Arreglo<Estacion> obtenerEstaciones() {
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
