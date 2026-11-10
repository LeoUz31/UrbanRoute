/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.persistencia;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import urbanroute.estructuras.ListaEnlazada;

/**
 *
 * @author leo.uzgil@gmail.com
 * Selección de archivo (JFileChooser) y lectura de líneas del CSV.

 */
public class LectorCSV {

    private ParserCSV parser;

    /** @param parser parser asociado */
    public LectorCSV(ParserCSV parser) {
        this.parser = parser;
    }

    /**
     * Abre un JFileChooser filtrado a .csv/.txt.
     *
     * @param padre componente padre del diálogo (puede ser null)
     * @return archivo elegido o null si se canceló
     */
    public File seleccionarArchivo(Component padre) {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv", "txt"));
        if (selector.showOpenDialog(padre) == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile();
        }
        return null;
    }

    /**
     * Lee todas las líneas del archivo.
     *
     * @param archivo archivo de texto
     * @return líneas leídas
     * @throws IOException si ocurre un error de lectura
     */
    public ListaEnlazada<String> leer(File archivo) throws IOException {
        ListaEnlazada<String> lineas = new ListaEnlazada<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea = br.readLine();
        while (linea != null) {
            lineas.insertarAlFinal(linea);
            linea = br.readLine();
        }
        br.close();
        return lineas;
    }

    /** @return parser asociado */
    public ParserCSV getParser() {
        return parser;
    }
}
