/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import urbanroute.dominio.Estacion;
import urbanroute.estructuras.Nodo;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;
import urbanroute.hash.TablaHash;

/**
 *
 * @author leo.uzgil@gmail.com
 * Escribe el estado de la TablaHash (estaciones) y del Grafo (conexiones) al CSV.
 * Cada conexión no dirigida se escribe una sola vez.
 */
public class EscritorCSV {

    private ParserCSV parser;

    /** @param parser parser usado para formatear */
    public EscritorCSV(ParserCSV parser) {
        this.parser = parser;
    }

    /**
     * Guarda estaciones y conexiones en sus archivos (ambos se sobrescriben).
     *
     * @param diccionario destino de las estaciones
     * @param conexiones  destino de las conexiones
     * @param tablaHash   estaciones
     * @param grafo       conexiones
     * @throws IOException si ocurre un error de escritura
     */
    public void guardar(File diccionario, File conexiones, TablaHash tablaHash, Grafo grafo) throws IOException {
        guardarDiccionario(diccionario, tablaHash, grafo);
        guardarConexiones(conexiones, grafo);
    }

    /**
     * Escribe el diccionario de estaciones en el orden de los vértices del grafo.
     *
     * @throws IOException si ocurre un error de escritura
     */
    private void guardarDiccionario(File archivo, TablaHash tablaHash, Grafo grafo) throws IOException {
        BufferedWriter bw = abrir(archivo);
        bw.write(ParserCSV.ENCABEZADO_ESTACIONES);
        bw.newLine();
        Nodo<Estacion> n = grafo.getVertices().getCabeza();
        while (n != null) {
            Estacion e = tablaHash.buscar(n.getDato().getIdEstacion());
            if (e != null) {
                bw.write(parser.formatearEstacion(e));
                bw.newLine();
            }
            n = n.getSiguiente();
        }
        bw.close();
    }

    /**
     * Escribe las conexiones (una vez cada conexión no dirigida).
     *
     * @throws IOException si ocurre un error de escritura
     */
    private void guardarConexiones(File archivo, Grafo grafo) throws IOException {
        BufferedWriter bw = abrir(archivo);
        bw.write(ParserCSV.ENCABEZADO_CONEXIONES);
        bw.newLine();
        Nodo<Arista> a = grafo.obtenerAristasUnicas().getCabeza();
        while (a != null) {
            bw.write(parser.formatearArista(a.getDato()));
            bw.newLine();
            a = a.getSiguiente();
        }
        bw.close();
    }

    /** @return escritor UTF-8 sobre el archivo (lo sobrescribe) */
    private BufferedWriter abrir(File archivo) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), LectorCSV.CODIFICACION));
    }
}
