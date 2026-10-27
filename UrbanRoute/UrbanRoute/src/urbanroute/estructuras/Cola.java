package urbanroute.estructuras;

/**
 * TDA Cola FIFO con nodos enlazados (usada por BFS).
 *
 * @param <T> tipo de elemento
 */
public class Cola<T> {

    /** Nodo interno de la cola. */
    private static class NodoCola<T> {
        private final T dato;
        private NodoCola<T> siguiente;

        NodoCola(T dato) {
            this.dato = dato;
        }
    }

    private NodoCola<T> frente;
    private NodoCola<T> fin;
    private int tamanio;

    /**
     * Agrega un elemento al final.
     *
     * @param dato elemento
     */
    public void encolar(T dato) {
        NodoCola<T> nuevo = new NodoCola<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    /**
     * Extrae el elemento del frente.
     *
     * @return elemento extraído
     * @throws IllegalStateException si la cola está vacía
     */
    public T desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /**
     * @return elemento del frente sin extraerlo
     * @throws IllegalStateException si la cola está vacía
     */
    public T verFrente() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return frente.dato;
    }

    /** @return true si no hay elementos */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /** @return cantidad de elementos */
    public int tamanio() {
        return tamanio;
    }
}
