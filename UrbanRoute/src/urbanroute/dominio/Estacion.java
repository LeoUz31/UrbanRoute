package urbanroute.dominio;

/**
 * Contenedor pasivo de los datos de una estación.
 */
public class Estacion {

    private String idEstacion;
    private String nombre;
    private String linea;
    private String zona;
    private int capacidadPasajerosHora;

    /**
     * @param idEstacion             identificador único
     * @param nombre                 nombre visible
     * @param linea                  línea a la que pertenece
     * @param zona                   zona tarifaria/geográfica
     * @param capacidadPasajerosHora capacidad de pasajeros por hora
     */
    public Estacion(String idEstacion, String nombre, String linea, String zona, int capacidadPasajerosHora) {
        this.idEstacion = idEstacion;
        this.nombre = nombre;
        this.linea = linea;
        this.zona = zona;
        this.capacidadPasajerosHora = capacidadPasajerosHora;
    }

    /**
     * Crea una estación solo con su ID (datos restantes vacíos).
     *
     * @param idEstacion identificador
     */
    public Estacion(String idEstacion) {
        this(idEstacion, idEstacion, "", "", 0);
    }

    public String getIdEstacion() { return idEstacion; }
    public void setIdEstacion(String idEstacion) { this.idEstacion = idEstacion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getLinea() { return linea; }
    public void setLinea(String linea) { this.linea = linea; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public int getCapacidadPasajerosHora() { return capacidadPasajerosHora; }
    public void setCapacidadPasajerosHora(int capacidadPasajerosHora) { this.capacidadPasajerosHora = capacidadPasajerosHora; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Estacion)) {
            return false;
        }
        Estacion otra = (Estacion) o;
        return idEstacion == null ? otra.idEstacion == null : idEstacion.equals(otra.idEstacion);
    }

    @Override
    public int hashCode() {
        return idEstacion == null ? 0 : idEstacion.hashCode();
    }

    @Override
    public String toString() {
        return nombre + " (" + idEstacion + ")";
    }
}
