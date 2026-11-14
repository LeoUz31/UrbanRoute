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
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import urbanroute.estructuras.ListaEnlazada;

/**
 *
 * @author leo.uzgil@gmail.com
 * Selección de archivo (JFileChooser) y lectura de líneas del CSV.

 */
public class LectorCSV {

    /** Codificación de los archivos del dataset (admite Ñ y tildes). */
    public static final String CODIFICACION = "UTF-8";

    private ParserCSV parser;

    /** @param parser parser asociado */
    public LectorCSV(ParserCSV parser) {
        this.parser = parser;
    }

    /**
     * Abre un JFileChooser filtrado a .csv/.txt.
     *
     * @param padre componente padre del diálogo (puede ser null)
     * @param titulo título del diálogo (indica qué archivo se pide)
     * @return archivo elegido o null si se canceló
     */
    public File seleccionarArchivo(Component padre, String titulo) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(titulo);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv", "txt"));
        if (selector.showOpenDialog(padre) == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile();
        }
        return null;
    }

    /**
     * Lee todas las líneas del archivo (UTF-8). Quita la marca BOM inicial si existe
     * (la agregan algunos editores como Excel o el Bloc de notas).
     *
     * @param archivo archivo de texto
     * @return líneas leídas
     * @throws IOException si ocurre un error de lectura
     */
    public ListaEnlazada<String> leer(File archivo) throws IOException {
        ListaEnlazada<String> lineas = new ListaEnlazada<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), CODIFICACION));
        String linea = br.readLine();
        if (linea != null) {
            linea = quitarBOM(linea);
        }
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

    /** @return la línea sin el carácter BOM (U+FEFF) inicial */
    private String quitarBOM(String linea) {
        if (linea.length() == 0 || linea.charAt(0) != '﻿') {
            return linea;
        }
        String resto = "";
        int i = 1;
        while (i < linea.length()) {
            resto = resto + linea.charAt(i);
            i++;
        }
        return resto;
    }
}