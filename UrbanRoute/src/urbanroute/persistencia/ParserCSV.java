package urbanroute.persistencia;

import urbanroute.dominio.Estacion;
import urbanroute.grafo.Arista;

/**
 * Convierte líneas CSV en objetos de dominio.
 * <ul>
 *   <li>Estación (5 campos): ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora</li>
 *   <li>Conexión (3 campos): Estacion_Origen,Estacion_Destino,Tiempo_Traslado_Min</li>
 * </ul>
 * Las líneas vacías, de comentario (#) y de encabezado se ignoran.
 */
public class ParserCSV {

    public static final String SEPARADOR = ",";
    public static final String SECCION_ESTACIONES = "#ESTACIONES";
    public static final String SECCION_CONEXIONES = "#CONEXIONES";
    public static final String ENCABEZADO_ESTACIONES = "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora";
    public static final String ENCABEZADO_CONEXIONES = "Estacion_Origen,Estacion_Destino,Tiempo_Traslado_Min";

    /**
     * @param linea línea cruda
     * @return true si es vacía, comentario o encabezado
     */
    public boolean esIgnorable(String linea) {
        String l = linea.trim();
        return l.isEmpty() || l.startsWith("#")
                || l.equalsIgnoreCase(ENCABEZADO_ESTACIONES)
                || l.equalsIgnoreCase(ENCABEZADO_CONEXIONES);
    }

    /**
     * @param linea línea cruda
     * @return true si tiene 5 campos (estación)
     */
    public boolean esEstacion(String linea) {
        return dividir(linea).length == 5;
    }

    /**
     * @param linea línea cruda
     * @return true si tiene 3 campos (conexión)
     */
    public boolean esConexion(String linea) {
        return dividir(linea).length == 3;
    }

    /**
     * @param linea ID,Nombre,Linea,Zona,Capacidad
     * @return estación instanciada
     * @throws IllegalArgumentException si la línea es inválida
     */
    public Estacion parsearEstacion(String linea) {
        String[] c = dividir(linea);
        if (c.length != 5) {
            throw new IllegalArgumentException("Estación: se esperaban 5 campos -> " + linea);
        }
        validarNoVacios(c, linea);
        int capacidad;
        try {
            capacidad = Integer.parseInt(c[4]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Capacidad no numérica -> " + linea);
        }
        if (capacidad < 0) {
            throw new IllegalArgumentException("Capacidad negativa -> " + linea);
        }
        return new Estacion(c[0], c[1], c[2], c[3], capacidad);
    }

    /**
     * Crea una Arista con estaciones provisionales (solo ID); el Grafo resuelve las reales.
     *
     * @param linea Origen,Destino,Tiempo
     * @return arista instanciada
     * @throws IllegalArgumentException si la línea es inválida
     */
    public Arista parsearArista(String linea) {
        String[] c = dividir(linea);
        if (c.length != 3) {
            throw new IllegalArgumentException("Conexión: se esperaban 3 campos -> " + linea);
        }
        validarNoVacios(c, linea);
        double tiempo;
        try {
            tiempo = Double.parseDouble(c[2]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Tiempo no numérico -> " + linea);
        }
        return new Arista(new Estacion(c[0]), new Estacion(c[1]), tiempo);
    }

    /**
     * @param e estación
     * @return línea CSV de la estación
     */
    public String formatearEstacion(Estacion e) {
        return e.getIdEstacion() + SEPARADOR + e.getNombre() + SEPARADOR + e.getLinea()
                + SEPARADOR + e.getZona() + SEPARADOR + e.getCapacidadPasajerosHora();
    }

    /**
     * @param a arista
     * @return línea CSV de la conexión
     */
    public String formatearArista(Arista a) {
        double t = a.getTiempoMinutos();
        String tiempo = (t == Math.floor(t)) ? String.valueOf((long) t) : String.valueOf(t);
        return a.getOrigen().getIdEstacion() + SEPARADOR + a.getDestino().getIdEstacion() + SEPARADOR + tiempo;
    }

    private String[] dividir(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }
        return campos;
    }

    private void validarNoVacios(String[] campos, String linea) {
        for (String c : campos) {
            if (c.isEmpty()) {
                throw new IllegalArgumentException("Campo vacío -> " + linea);
            }
        }
    }
}
