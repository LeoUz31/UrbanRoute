package urbanroute;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;

/**
 * Utilidades compartidas por los paneles: combos de estaciones, bordes y textos.
 * Los combos muestran nombres; la estación real se obtiene por posición
 * en la ListaEnlazada usada para llenarlos (misma posición ↔ mismo ítem).
 */
public class UtilVista {

    /** Color de líneas y bordes. */
    public static final Color COLOR_LINEA = new Color(210, 214, 220);

    /** Texto de referencia para el ancho de los combos (evita que crezcan con nombres largos). */
    private static final String PROTOTIPO_COMBO = "XXXXXXXXX";

    /**
     * Vacía el combo y lo llena con los nombres de las estaciones, manteniendo
     * seleccionada la estación previa si sigue existiendo.
     *
     * @param combo combo a llenar
     * @param estaciones estaciones en el orden en que se mostrarán
     * @param previa estación seleccionada antes del refresco (puede ser null)
     */
    public static void llenarCombo(JComboBox<String> combo, ListaEnlazada<Estacion> estaciones, Estacion previa) {
        combo.setPrototypeDisplayValue(PROTOTIPO_COMBO);
        combo.removeAllItems();
        int seleccion = -1;
        int i = 0;
        Nodo<Estacion> n = estaciones.getCabeza();
        while (n != null) {
            combo.addItem(n.getDato().getNombre());
            if (previa != null && n.getDato().getIdEstacion().equals(previa.getIdEstacion())) {
                seleccion = i;
            }
            n = n.getSiguiente();
            i++;
        }
        if (seleccion != -1) {
            combo.setSelectedIndex(seleccion);
        }
    }

    /**
     * @param combo combo de estaciones
     * @param estaciones lista con la que se llenó el combo
     * @return estación seleccionada o null si no hay selección
     */
    public static Estacion seleccionada(JComboBox<String> combo, ListaEnlazada<Estacion> estaciones) {
        int indice = combo.getSelectedIndex();
        if (indice < 0) {
            return null;
        }
        return estaciones.acceder(indice);
    }

    /**
     * @param estaciones estaciones en cualquier orden
     * @return nueva lista ordenada por nombre (inserción con insertarAntesDe; empate por ID)
     */
    public static ListaEnlazada<Estacion> ordenarPorNombre(ListaEnlazada<Estacion> estaciones) {
        ListaEnlazada<Estacion> ordenadas = new ListaEnlazada<>();
        Nodo<Estacion> n = estaciones.getCabeza();
        while (n != null) {
            Estacion e = n.getDato();
            Nodo<Estacion> pos = ordenadas.getCabeza();
            while (pos != null && !vaAntes(e, pos.getDato())) {
                pos = pos.getSiguiente();
            }
            if (pos == null) {
                ordenadas.insertarAlFinal(e);
            } else {
                ordenadas.insertarAntesDe(pos, e);
            }
            n = n.getSiguiente();
        }
        return ordenadas;
    }

    /**
     * Aplica el estilo de sección (borde con título) a un panel.
     *
     * @param panel panel de la sección
     * @param titulo título del borde
     */
    public static void estiloSeccion(JComponent panel, String titulo) {
        TitledBorder borde = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_LINEA), titulo);
        borde.setTitleFont(borde.getTitleFont().deriveFont(Font.BOLD));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8),
                BorderFactory.createCompoundBorder(borde, BorderFactory.createEmptyBorder(4, 8, 6, 8))));
    }

    /**
     * Aplica el estilo de encabezado de panel (negrita, gris).
     *
     * @param etiqueta etiqueta del encabezado
     */
    public static void estiloEncabezado(JLabel etiqueta) {
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD));
        etiqueta.setForeground(new Color(90, 98, 110));
    }

    /**
     * @param texto texto a validar
     * @return true si el texto contiene una coma (rompería el formato CSV)
     */
    public static boolean tieneComa(String texto) {
        int i = 0;
        while (i < texto.length()) {
            if (texto.charAt(i) == ',') {
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * @param lista estaciones
     * @return nombres separados por comas
     */
    public static String nombres(ListaEnlazada<Estacion> lista) {
        String texto = "";
        Nodo<Estacion> n = lista.getCabeza();
        while (n != null) {
            if (n != lista.getCabeza()) {
                texto = texto + ", ";
            }
            texto = texto + n.getDato().getNombre();
            n = n.getSiguiente();
        }
        return texto;
    }

    /**
     * Completa el texto con espacios a la derecha (para columnas en texto monoespaciado).
     *
     * @param texto texto original
     * @param ancho ancho deseado
     * @return texto con al menos {@code ancho} caracteres
     */
    public static String rellenar(String texto, int ancho) {
        String r = texto;
        while (r.length() < ancho) {
            r = r + " ";
        }
        return r;
    }

    /** @return true si a debe ir antes que b (nombre ascendente; empate por ID) */
    private static boolean vaAntes(Estacion a, Estacion b) {
        int c = a.getNombre().compareTo(b.getNombre());
        if (c != 0) {
            return c < 0;
        }
        return a.getIdEstacion().compareTo(b.getIdEstacion()) < 0;
    }
}
