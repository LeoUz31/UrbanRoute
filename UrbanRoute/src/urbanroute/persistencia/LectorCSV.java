/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.persistencia;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import urbanroute.estructuras.Arreglo;

/**
 *
 * @author leo.uzgil@gmail.com
 * Selección de archivo (JFileChooser) y lectura de líneas del CSV.

 */
public class LectorCSV {

    private final ParserCSV parser;

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
    public Arreglo<String> leer(File archivo) throws IOException {
        // Primera pasada: contar las líneas para fijar el máximo del arreglo.
        int total = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"));
        String linea = br.readLine();
        while (linea != null) {
            total++;
            linea = br.readLine();
        }
        br.close();

        // Segunda pasada: guardar las líneas.
        Arreglo<String> lineas = new Arreglo<>(total);
        br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"));
        linea = br.readLine();
        while (linea != null) {
            lineas.agregar(linea);
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
