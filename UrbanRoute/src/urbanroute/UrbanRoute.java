package urbanroute;

import urbanroute.controlador.ControladorRed;
import urbanroute.VentanaPrincipal;

/**
 * Punto de entrada de UrbanRoute.
 * Crea el controlador con una red vacía y abre la ventana principal (sin consola).
 */
public class UrbanRoute {

    /**
     * Inicia la aplicación.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        ControladorRed controlador = new ControladorRed();
        VentanaPrincipal ventana = new VentanaPrincipal(controlador);
        ventana.setVisible(true);
    }
}
