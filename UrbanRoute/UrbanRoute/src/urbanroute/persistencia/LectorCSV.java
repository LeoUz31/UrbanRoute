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
import urbanroute.estructuras.ArregloDinamico;

/**
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
        return selector.showOpenDialog(padre) == JFileChooser.APPROVE_OPTION
                ? selector.getSelectedFile() : null;
    }

    /**
     * Lee todas las líneas del archivo.
     *
     * @param archivo archivo de texto
     * @return líneas leídas
     * @throws IOException si ocurre un error de lectura
     */
    public ArregloDinamico<String> leer(File archivo) throws IOException {
        ArregloDinamico<String> lineas = new ArregloDinamico<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.agregar(linea);
            }
        }
        return lineas;
    }

    /** @return parser asociado */
    public ParserCSV getParser() {
        return parser;
    }
}
