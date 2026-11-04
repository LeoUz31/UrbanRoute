package urbanroute.persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;
import urbanroute.hash.TablaHash;

/**
 * Escribe el estado de la TablaHash (estaciones) y del Grafo (conexiones) al CSV.
 * Cada conexión no dirigida se escribe una sola vez.
 */
public class EscritorCSV {

    private final ParserCSV parser;

    /** @param parser parser usado para formatear */
    public EscritorCSV(ParserCSV parser) {
        this.parser = parser;
    }

    /**
     * @param archivo   destino (se sobrescribe)
     * @param tablaHash estaciones
     * @param grafo     conexiones
     * @throws IOException si ocurre un error de escritura
     */
    public void guardar(File archivo, TablaHash tablaHash, Grafo grafo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            bw.write(ParserCSV.SECCION_ESTACIONES);
            bw.newLine();
            bw.write(ParserCSV.ENCABEZADO_ESTACIONES);
            bw.newLine();
            ArregloDinamico<Estacion> estaciones = grafo.getVertices();
            for (int i = 0; i < estaciones.tamanio(); i++) {
                Estacion e = tablaHash.buscar(estaciones.obtener(i).getIdEstacion());
                if (e != null) {
                    bw.write(parser.formatearEstacion(e));
                    bw.newLine();
                }
            }
            bw.write(ParserCSV.SECCION_CONEXIONES);
            bw.newLine();
            bw.write(ParserCSV.ENCABEZADO_CONEXIONES);
            bw.newLine();
            ArregloDinamico<Arista> aristas = grafo.obtenerAristasUnicas();
            for (int i = 0; i < aristas.tamanio(); i++) {
                bw.write(parser.formatearArista(aristas.obtener(i)));
                bw.newLine();
            }
        }
    }
}
