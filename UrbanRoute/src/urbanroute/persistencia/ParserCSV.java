/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.persistencia;

import urbanroute.dominio.Estacion;
import urbanroute.grafo.Arista;
/**
 *
 * @author leo.uzgil@gmail.com
 * Convierte líneas CSV en objetos de dominio.
 * Las líneas vacías, de comentario (#) y de encabezado se ignoran.
 * 
 */
public class ParserCSV {

    public static final String SEPARADOR = ",";
    /** Encabezado del archivo diccionario (enunciado). */
    public static final String ENCABEZADO_ESTACIONES = "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora";
    /** Encabezado del archivo de conexiones (enunciado). */
    public static final String ENCABEZADO_CONEXIONES = "Estacion_Origen,Estacion_Destino,Tiempo_Minutos";
    /** Encabezado de conexiones de versiones anteriores (se sigue aceptando al leer). */
    public static final String ENCABEZADO_CONEXIONES_ANTERIOR = "Estacion_Origen,Estacion_Destino,Tiempo_Traslado_Min";

    /** Motivo del último error de parseo. */
    public String error = "";

    /** @return true si la línea es vacía, comentario o encabezado */
    public boolean esIgnorable(String linea) {
        String limpia = linea.trim();
        if (limpia.length() == 0 || limpia.charAt(0) == '#') {
            return true;
        }
        if (limpia.equalsIgnoreCase(ENCABEZADO_ESTACIONES) || limpia.equalsIgnoreCase(ENCABEZADO_CONEXIONES)
                || limpia.equalsIgnoreCase(ENCABEZADO_CONEXIONES_ANTERIOR)) {
            return true;
        }
        return false;
    }

    /** @return true si la línea tiene 5 campos (estación) */
    public boolean esEstacion(String linea) {
        return dividir(linea).length == 5;
    }

    /** @return true si la línea tiene 3 campos (conexión) */
    public boolean esConexion(String linea) {
        return dividir(linea).length == 3;
    }

    /**
     * @param linea ID,Nombre,Linea,Zona,Capacidad
     * @return estación instanciada, o null si la línea es inválida
     */
    public Estacion parsearEstacion(String linea) {
        String[] c = dividir(linea);
        if (c.length != 5) {
            error = "Estación: se esperaban 5 campos -> " + linea;
            return null;
        }
        if (hayVacios(c)) {
            error = "Campo vacío -> " + linea;
            return null;
        }
        int capacidad = aEntero(c[4]);
        if (capacidad == -1) {
            error = "Capacidad no numérica o negativa -> " + linea;
            return null;
        }
        return new Estacion(c[0], c[1], c[2], c[3], capacidad);
    }

    /**
     * Crea una Arista con estaciones provisionales (solo ID); el Grafo resuelve las reales.
     *
     * @param linea Origen,Destino,Tiempo
     * @return arista instanciada, o null si la línea es inválida
     */
    public Arista parsearArista(String linea) {
        String[] c = dividir(linea);
        if (c.length != 3) {
            error = "Conexión: se esperaban 3 campos -> " + linea;
            return null;
        }
        if (hayVacios(c)) {
            error = "Campo vacío -> " + linea;
            return null;
        }
        double tiempo = aDecimal(c[2]);
        if (tiempo == -1) {
            error = "Tiempo no numérico o negativo -> " + linea;
            return null;
        }
        Estacion origen = new Estacion(c[0], c[0], "", "", 0);
        Estacion destino = new Estacion(c[1], c[1], "", "", 0);
        return new Arista(origen, destino, tiempo);
    }

    /** @return línea CSV de la estación */
    public String formatearEstacion(Estacion e) {
        return e.getIdEstacion() + SEPARADOR + e.getNombre() + SEPARADOR + e.getLinea()
                + SEPARADOR + e.getZona() + SEPARADOR + e.getCapacidadPasajerosHora();
    }

    /** @return línea CSV de la conexión (sin ".0" si el tiempo es entero) */
    public String formatearArista(Arista a) {
        double t = a.getTiempoMinutos();
        String tiempo;
        if (t == (int) t) {
            tiempo = "" + (int) t;
        } else {
            tiempo = "" + t;
        }
        return a.getOrigen().getIdEstacion() + SEPARADOR + a.getDestino().getIdEstacion() + SEPARADOR + tiempo;
    }


    /** Separa la línea por comas y quita los espacios de cada campo. */
    private String[] dividir(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        int i = 0;
        while (i < campos.length) {
            campos[i] = campos[i].trim();
            i++;
        }
        return campos;
    }

    private boolean hayVacios(String[] campos) {
        int i = 0;
        while (i < campos.length) {
            if (campos[i].length() == 0) {
                return true;
            }
            i++;
        }
        return false;
    }

    /** @return el entero (>= 0) o -1 si el texto no es un número entero válido */
    public int aEntero(String s) {
        int n = 0;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
            n = n * 10 + (c - '0');
            i++;
        }
        return n;
    }

    /** @return el decimal (>= 0) o -1 si el texto no es un número válido*/
    public double aDecimal(String s) {
        double n = 0;
        double factor = 1;
        boolean hayPunto = false;
        boolean hayDigito = false;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '.') {
                if (hayPunto) {
                    return -1;
                }
                hayPunto = true;
            } else if (c >= '0' && c <= '9') {
                hayDigito = true;
                if (hayPunto) {
                    factor = factor / 10;
                    n = n + (c - '0') * factor;
                } else {
                    n = n * 10 + (c - '0');
                }
            } else {
                return -1;
            }
            i++;
        }
        if (!hayDigito) {
            return -1;
        }
        return n;
    }
}
