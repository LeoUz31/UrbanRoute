package urbanroute;

import java.awt.Component;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import urbanroute.dominio.Estacion;
import urbanroute.dominio.Ruta;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 * Traduce el Grafo propio a un Graph de GraphStream (solo renderizado; no usa sus algoritmos).
 */
public class RenderizadorGraphStream {

    /**
     * Colores de las subredes separados por comas (se pasan a una lista en el constructor).
     * Sin rojo (reservado para la ruta óptima) ni gris (reservado para estaciones cerradas).
     */
    private static final String PALETA = "#2471a3,#27ae60,#8e44ad,#d68910,#17a589,#e67e22,#1abc9c,#34495e";

    private static final String ESTILO =
            "graph { padding: 40px; }"
            + "node { size: 18px; fill-color: #2471a3; text-size: 13px; text-alignment: under; }"
            + "node.ruta { fill-color: #c0392b; size: 24px; }"
            + "edge { fill-color: #95a5a6; size: 2px; text-size: 11px; }"
            + "edge.ruta { fill-color: #c0392b; size: 4px; }"
            + "node.cerrada { fill-color: #d5d8dc; stroke-mode: dots; stroke-color: #7f8c8d; stroke-width: 2px; text-color: #7f8c8d; }"
            + "edge.cerrada { fill-color: #d5d8dc; }";

    private Graph graph;
    private SwingViewer viewer;
    private ViewPanel vista;
    private ListaEnlazada<String> colores;

    /** Crea el renderizador con un grafo GraphStream vacío. */
    public RenderizadorGraphStream() {
        colores = new ListaEnlazada<>();
        String color = "";
        int i = 0;
        while (i < PALETA.length()) {
            if (PALETA.charAt(i) == ',') {
                colores.insertarAlFinal(color);
                color = "";
            } else {
                color = color + PALETA.charAt(i);
            }
            i++;
        }
        colores.insertarAlFinal(color);
        System.setProperty("org.graphstream.ui", "swing");
        graph = new SingleGraph("UrbanRoute");
        graph.setAttribute("ui.stylesheet", ESTILO);
        graph.setAttribute("ui.antialias");
    }

    /**
     * Reconstruye la vista a partir del Grafo propio.
     *
     * @param grafo red a dibujar
     */
    public void dibujar(Grafo grafo) {
        graph.clear();
        graph.setAttribute("ui.stylesheet", ESTILO);
        graph.setAttribute("ui.antialias");
        Nodo<Estacion> ne = grafo.getVertices().getCabeza();
        while (ne != null) {
            Estacion e = ne.getDato();
            Node n = graph.addNode(e.getIdEstacion());
            n.setAttribute("ui.label", e.getNombre());
            ne = ne.getSiguiente();
        }
        Nodo<Arista> na = grafo.obtenerAristasUnicas().getCabeza();
        while (na != null) {
            Arista a = na.getDato();
            String o = a.getOrigen().getIdEstacion();
            String d = a.getDestino().getIdEstacion();
            Edge ed = graph.addEdge(idArista(o, d), o, d);
            ed.setAttribute("ui.label", formatearTiempo(a.getTiempoMinutos()));
            na = na.getSiguiente();
        }
    }

    /**
     * Resalta los nodos y aristas de una ruta (no borra otros resaltados:
     * llamar antes a limpiarResaltado si hace falta).
     *
     * @param ruta ruta a resaltar
     */
    public void resaltarRuta(Ruta ruta) {
        Nodo<Estacion> anterior = null;
        Nodo<Estacion> actual = ruta.getEstaciones().getCabeza();
        while (actual != null) {
            Node n = graph.getNode(actual.getDato().getIdEstacion());
            if (n != null) {
                n.setAttribute("ui.class", "ruta");
            }
            if (anterior != null) {
                Edge e = buscarArista(anterior.getDato().getIdEstacion(), actual.getDato().getIdEstacion());
                if (e != null) {
                    e.setAttribute("ui.class", "ruta");
                }
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
    }

    /**
     * Colorea cada subred con un color distinto, conservando el tamaño por grado.
     *
     * @param subredes componentes conexos
     * @param grafo red de referencia para el tamaño de los nodos
     */
    public void colorearSubredes(ListaEnlazada<ListaEnlazada<Estacion>> subredes, Grafo grafo) {
        int s = 0;
        Nodo<ListaEnlazada<Estacion>> comp = subredes.getCabeza();
        while (comp != null) {
            String color = colores.acceder(s % colores.getSize());
            Nodo<Estacion> e = comp.getDato().getCabeza();
            while (e != null) {
                String id = e.getDato().getIdEstacion();
                Node n = graph.getNode(id);
                if (n != null) {
                    n.setAttribute("ui.style", "fill-color: " + color + "; size: "
                            + tamanoNodo(grafo, id) + "px;");
                }
                e = e.getSiguiente();
            }
            comp = comp.getSiguiente();
            s++;
        }
    }

    /**
     * Escala el tamaño de los nodos según su grado (hubs más grandes).
     *
     * @param grafo red de referencia para los grados
     */
    public void escalarPorGrado(Grafo grafo) {
        Nodo<Estacion> ne = grafo.getVertices().getCabeza();
        while (ne != null) {
            String id = ne.getDato().getIdEstacion();
            Node n = graph.getNode(id);
            if (n != null) {
                n.setAttribute("ui.style", "size: " + tamanoNodo(grafo, id) + "px;");
            }
            ne = ne.getSiguiente();
        }
    }

    /**
     * Marca como cerradas las estaciones indicadas y sus conexiones (simulación).
     *
     * @param cerradas estaciones cerradas
     */
    public void marcarCerradas(ListaEnlazada<Estacion> cerradas) {
        Nodo<Estacion> ne = cerradas.getCabeza();
        while (ne != null) {
            Node n = graph.getNode(ne.getDato().getIdEstacion());
            if (n != null) {
                n.setAttribute("ui.class", "cerrada");
                int i = 0;
                while (i < n.getDegree()) {
                    n.getEdge(i).setAttribute("ui.class", "cerrada");
                    i++;
                }
            }
            ne = ne.getSiguiente();
        }
    }

    /**
     * @param tiempo minutos
     * @return el tiempo sin ".0" si es entero
     */
    public static String formatearTiempo(double tiempo) {
        if (tiempo == (int) tiempo) {
            return "" + (int) tiempo;
        }
        return "" + tiempo;
    }

    /** @return tamaño en píxeles según el grado de la estación */
    private int tamanoNodo(Grafo grafo, String id) {
        int grado = grafo.obtenerGrado(id);
        if (grado < 0) {
            grado = 0;
        }
        return 14 + 4 * grado;
    }

    /** Quita resaltados y estilos individuales. */
    public void limpiarResaltado() {
        int i = 0;
        while (i < graph.getNodeCount()) {
            Node n = graph.getNode(i);
            n.removeAttribute("ui.class");
            n.removeAttribute("ui.style");
            i++;
        }
        i = 0;
        while (i < graph.getEdgeCount()) {
            Edge e = graph.getEdge(i);
            e.removeAttribute("ui.class");
            i++;
        }
    }

    /**
     * @return componente Swing embebible con el visor del grafo
     */
    public Component obtenerComponente() {
        if (viewer == null) {
            viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            viewer.enableAutoLayout();
            vista = (ViewPanel) viewer.addDefaultView(false);
        }
        return vista;
    }

    /** Reajusta la cámara para que toda la red quepa en el visor. */
    public void encajar() {
        if (vista != null) {
            vista.getCamera().resetView();
        }
    }

    /** @return grafo GraphStream subyacente */
    public Graph getGraph() {
        return graph;
    }

    private Edge buscarArista(String a, String b) {
        Edge e = graph.getEdge(idArista(a, b));
        if (e == null) {
            e = graph.getEdge(idArista(b, a));
        }
        return e;
    }

    private String idArista(String o, String d) {
        return o + "-" + d;
    }
}
