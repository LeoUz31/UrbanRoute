package urbanroute.estructuras;

/**
 * Reemplazo propio de ArrayList. Crece duplicando su capacidad.
 *
 * @param <T> tipo de elemento almacenado
 */
public class ArregloDinamico<T> {

    private static final int CAPACIDAD_INICIAL = 10;

    private Object[] datos;
    private int capacidad;
    private int tamanio;

    /** Crea un arreglo vacío con capacidad inicial por defecto. */
    public ArregloDinamico() {
        this(CAPACIDAD_INICIAL);
    }

    /**
     * Crea un arreglo vacío con la capacidad indicada.
     *
     * @param capacidadInicial capacidad inicial (mínimo 1)
     */
    public ArregloDinamico(int capacidadInicial) {
        this.capacidad = Math.max(1, capacidadInicial);
        this.datos = new Object[this.capacidad];
        this.tamanio = 0;
    }

    /**
     * Inserta un elemento al final; duplica capacidad si está lleno.
     *
     * @param elemento elemento a insertar
     */
    public void agregar(T elemento) {
        if (tamanio == capacidad) {
            redimensionar(capacidad * 2);
        }
        datos[tamanio++] = elemento;
    }

    /**
     * Retorna el elemento en la posición indicada.
     *
     * @param indice posición
     * @return elemento
     * @throws IndexOutOfBoundsException si el índice es inválido
     */
    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        validarIndice(indice);
        return (T) datos[indice];
    }

    /**
     * Reemplaza el elemento en la posición indicada.
     *
     * @param indice   posición
     * @param elemento nuevo elemento
     */
    public void establecer(int indice, T elemento) {
        validarIndice(indice);
        datos[indice] = elemento;
    }

    /**
     * Elimina y retorna el elemento en la posición indicada, desplazando el resto.
     *
     * @param indice posición
     * @return elemento eliminado
     * @throws IndexOutOfBoundsException si el índice es inválido
     */
    @SuppressWarnings("unchecked")
    public T eliminar(int indice) {
        validarIndice(indice);
        T eliminado = (T) datos[indice];
        for (int i = indice; i < tamanio - 1; i++) {
            datos[i] = datos[i + 1];
        }
        datos[--tamanio] = null;
        return eliminado;
    }

    /**
     * Elimina la primera ocurrencia del elemento (según equals).
     *
     * @param elemento elemento a eliminar
     * @return true si se eliminó
     */
    public boolean eliminarElemento(T elemento) {
        int i = indiceDe(elemento);
        if (i < 0) {
            return false;
        }
        eliminar(i);
        return true;
    }

    /**
     * Busca la primera posición del elemento (según equals).
     *
     * @param elemento elemento buscado
     * @return índice o -1 si no existe
     */
    public int indiceDe(T elemento) {
        for (int i = 0; i < tamanio; i++) {
            if (elemento == null ? datos[i] == null : elemento.equals(datos[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param elemento elemento buscado
     * @return true si el elemento existe
     */
    public boolean contiene(T elemento) {
        return indiceDe(elemento) >= 0;
    }

    /** @return cantidad de elementos */
    public int tamanio() {
        return tamanio;
    }

    /** @return true si no hay elementos */
    public boolean estaVacio() {
        return tamanio == 0;
    }

    /** Vacía el arreglo conservando la capacidad. */
    public void limpiar() {
        for (int i = 0; i < tamanio; i++) {
            datos[i] = null;
        }
        tamanio = 0;
    }

    private void redimensionar(int nuevaCapacidad) {
        Object[] nuevo = new Object[nuevaCapacidad];
        for (int i = 0; i < tamanio; i++) {
            nuevo[i] = datos[i];
        }
        datos = nuevo;
        capacidad = nuevaCapacidad;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + " fuera de rango [0," + tamanio + ")");
        }
    }
}
