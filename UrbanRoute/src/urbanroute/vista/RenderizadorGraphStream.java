package urbanroute.vista;

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
import urbanroute.estructuras.ArregloDinamico;
import urbanroute.grafo.Arista;
import urbanroute.grafo.Grafo;

/**
 * Traduce el Grafo propio a un Graph de GraphStream (solo renderizado; no usa sus algoritmos).
 */
public class RenderizadorGraphStream {

    private static final String[] PALETA = {
        "#2471a3", "#c0392b", "#27ae60", "#8e44ad", "#d68910", "#17a589", "#7f8c8d", "#cb4335"
    };

    private static final String ESTILO =
            "graph { padding: 40px; }"
            + "node { size: 18px; fill-color: #2471a3; text-size: 13px; text-alignment: under; }"
            + "node.ruta { fill-color: #c0392b; size: 24px; }"
            + "edge { fill-color: #95a5a6; size: 2px; text-size: 11px; }"
            + "edge.ruta { fill-color: #c0392b; size: 4px; }";

    private Graph graph;
    private SwingViewer viewer;

    /** Crea el renderizador con un grafo GraphStream vacío. */
    public RenderizadorGraphStream() {
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
        for (int i = 0; i < grafo.numVertices(); i++) {
            Estacion e = grafo.obtenerEstacion(i);
            Node n = graph.addNode(e.getIdEstacion());
            n.setAttribute("ui.label", e.getNombre());
        }
        ArregloDinamico<Arista> aristas = grafo.obtenerAristasUnicas();
        for (int i = 0; i < aristas.tamanio(); i++) {
            Arista a = aristas.obtener(i);
            String o = a.getOrigen().getIdEstacion();
            String d = a.getDestino().getIdEstacion();
            Edge ed = graph.addEdge(idArista(o, d), o, d);
            ed.setAttribute("ui.label", a.getTiempoMinutos() + "");
        }
    }

    /**
     * Resalta los nodos y aristas de una ruta.
     *
     * @param ruta ruta a resaltar
     */
    public void resaltarRuta(Ruta ruta) {
        limpiarResaltado();
        ArregloDinamico<Estacion> est = ruta.getEstaciones();
        for (int i = 0; i < est.tamanio(); i++) {
            Node n = graph.getNode(est.obtener(i).getIdEstacion());
            if (n != null) {
                n.setAttribute("ui.class", "ruta");
            }
            if (i > 0) {
                Edge e = buscarArista(est.obtener(i - 1).getIdEstacion(), est.obtener(i).getIdEstacion());
                if (e != null) {
                    e.setAttribute("ui.class", "ruta");
                }
            }
        }
    }

    /**
     * Colorea cada subred con un color distinto.
     *
     * @param subredes componentes conexos
     */
    public void colorearSubredes(ArregloDinamico<ArregloDinamico<Estacion>> subredes) {
        for (int s = 0; s < subredes.tamanio(); s++) {
            String color = PALETA[s % PALETA.length];
            ArregloDinamico<Estacion> comp = subredes.obtener(s);
            for (int i = 0; i < comp.tamanio(); i++) {
                Node n = graph.getNode(comp.obtener(i).getIdEstacion());
                if (n != null) {
                    n.setAttribute("ui.style", "fill-color: " + color + ";");
                }
            }
        }
    }

    /**
     * Escala el tamaño de los nodos según su grado (hubs más grandes).
     *
     * @param grafo red de referencia para los grados
     */
    public void escalarPorGrado(Grafo grafo) {
        for (int i = 0; i < grafo.numVertices(); i++) {
            Estacion e = grafo.obtenerEstacion(i);
            Node n = graph.getNode(e.getIdEstacion());
            if (n != null) {
                int tam = 14 + 4 * grafo.obtenerAdyacentes(i).tamanio();
                n.setAttribute("ui.style", "size: " + tam + "px;");
            }
        }
    }

    /** Quita resaltados y estilos individuales. */
    public void limpiarResaltado() {
        graph.nodes().forEach(n -> {
            n.removeAttribute("ui.class");
            n.removeAttribute("ui.style");
        });
        graph.edges().forEach(e -> e.removeAttribute("ui.class"));
    }

    /**
     * @return componente Swing embebible con el visor del grafo
     */
    public Component obtenerComponente() {
        if (viewer == null) {
            viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            viewer.enableAutoLayout();
        }
        return (ViewPanel) viewer.addDefaultView(false);
    }

    /** @return grafo GraphStream subyacente */
    public Graph getGraph() {
        return graph;
    }

    private Edge buscarArista(String a, String b) {
        Edge e = graph.getEdge(idArista(a, b));
        return e != null ? e : graph.getEdge(idArista(b, a));
    }

    private String idArista(String o, String d) {
        return o + "-" + d;
    }
}
