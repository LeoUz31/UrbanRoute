package urbanroute;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;

/**
 * Diálogos de la aplicación (JOptionPane): alertas, errores e información.
 * Clase de utilidad: todos sus métodos son estáticos.
 */
public class MensajesError {

    /** Respuesta: guardar los cambios antes de continuar. */
    public static final int GUARDAR = 0;
    /** Respuesta: continuar sin guardar. */
    public static final int NO_GUARDAR = 1;
    /** Respuesta: cancelar la operación. */
    public static final int CANCELAR = 2;

    /**
     * 3.1 · Alerta de datos sin guardar (WARNING_MESSAGE).
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @return GUARDAR, NO_GUARDAR o CANCELAR (cerrar el diálogo equivale a CANCELAR)
     */
    public static int confirmarCambiosSinGuardar(Component padre) {
        UIManager.put("OptionPane.yesButtonText", "Guardar");
        UIManager.put("OptionPane.noButtonText", "No guardar");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        int respuesta = JOptionPane.showConfirmDialog(padre,
                "Hay cambios en la red que no se han guardado en el archivo.\n"
                + "¿Desea guardarlos antes de continuar?",
                "Cambios sin guardar",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        UIManager.put("OptionPane.yesButtonText", null);
        UIManager.put("OptionPane.noButtonText", null);
        UIManager.put("OptionPane.cancelButtonText", null);
        if (respuesta == JOptionPane.YES_OPTION) {
            return GUARDAR;
        }
        if (respuesta == JOptionPane.NO_OPTION) {
            return NO_GUARDAR;
        }
        return CANCELAR;
    }

    /**
     * 3.2 · Error de parseo del archivo (ERROR_MESSAGE) con el detalle de las líneas omitidas.
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param errores mensajes de error por línea inválida
     * @param estaciones estaciones cargadas
     * @param conexiones conexiones cargadas
     */
    public static void mostrarErroresParseo(Component padre, ListaEnlazada<String> errores,
            int estaciones, int conexiones) {
        String detalle = "";
        Nodo<String> n = errores.getCabeza();
        while (n != null) {
            detalle = detalle + n.getDato() + "\n";
            n = n.getSiguiente();
        }
        JTextArea areaDetalle = new JTextArea(detalle, 6, 40);
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaDetalle.setCaretPosition(0);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("<html><b>Se omitieron " + errores.getSize()
                + " líneas con formato inválido.</b><br>Cargadas: " + estaciones
                + " estaciones · " + conexiones + " conexiones · Omitidas: "
                + errores.getSize() + "</html>"), BorderLayout.NORTH);
        panel.add(new JScrollPane(areaDetalle), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(padre, panel, "Error de lectura del archivo",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 3.3 · Ruta inexistente entre dos estaciones (INFORMATION_MESSAGE).
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param origen nombre de la estación de origen
     * @param destino nombre de la estación de destino
     */
    public static void mostrarRutaInexistente(Component padre, String origen, String destino) {
        JOptionPane.showMessageDialog(padre,
                "No existe una ruta entre «" + origen + "» y «" + destino + "».\n"
                + "Las estaciones pertenecen a subredes distintas o hay una estación cerrada.",
                "Ruta no disponible",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Confirmación antes de sobrescribir un archivo existente (WARNING_MESSAGE).
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param nombreArchivo nombre del archivo que se sobrescribiría
     * @return true si el usuario acepta sobrescribir
     */
    public static boolean confirmarSobrescribir(Component padre, String nombreArchivo) {
        int respuesta = JOptionPane.showConfirmDialog(padre,
                "El archivo «" + nombreArchivo + "» ya existe.\n¿Desea reemplazarlo?",
                "Confirmar guardado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /**
     * Confirmación Sí/No genérica (WARNING_MESSAGE), p. ej. antes de eliminar una estación.
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param titulo título del diálogo
     * @param mensaje pregunta a mostrar
     * @return true si el usuario responde Sí
     */
    public static boolean confirmar(Component padre, String titulo, String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(padre, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /**
     * Error genérico (archivo ilegible, escritura fallida, etc.).
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param titulo título del diálogo
     * @param mensaje descripción del error
     */
    public static void mostrarError(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Advertencia por datos de entrada inválidos en los formularios.
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param mensaje descripción del problema
     */
    public static void mostrarAdvertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Mensaje informativo.
     *
     * @param padre ventana sobre la que se muestra el diálogo
     * @param titulo título del diálogo
     * @param mensaje texto a mostrar
     */
    public static void mostrarInformacion(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}
