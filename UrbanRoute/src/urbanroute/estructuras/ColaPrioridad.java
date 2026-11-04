package urbanroute.estructuras;

/**
 * Min-Heap propio para Dijkstra. Extrae siempre el elemento de menor prioridad.
 *
 * @param <T> tipo de elemento
 */
public class ColaPrioridad<T> {

    private static final int CAPACIDAD_INICIAL = 16;

    private Object[] heap;
    private double[] prioridades;
    private int tamanio;

    /** Crea un heap vacío. */
    public ColaPrioridad() {
        heap = new Object[CAPACIDAD_INICIAL];
        prioridades = new double[CAPACIDAD_INICIAL];
        tamanio = 0;
    }

    /**
     * Inserta un elemento con su prioridad.
     *
     * @param elemento  elemento
     * @param prioridad prioridad (menor = primero)
     */
    public void insertar(T elemento, double prioridad) {
        if (tamanio == heap.length) {
            crecer();
        }
        heap[tamanio] = elemento;
        prioridades[tamanio] = prioridad;
        subir(tamanio);
        tamanio++;
    }

    /**
     * Extrae el elemento de menor prioridad.
     *
     * @return elemento mínimo
     * @throws IllegalStateException si está vacía
     */
    @SuppressWarnings("unchecked")
    public T extraerMinimo() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola de prioridad está vacía");
        }
        T minimo = (T) heap[0];
        tamanio--;
        heap[0] = heap[tamanio];
        prioridades[0] = prioridades[tamanio];
        heap[tamanio] = null;
        if (tamanio > 0) {
            bajar(0);
        }
        return minimo;
    }

    /**
     * @return prioridad del elemento mínimo
     * @throws IllegalStateException si está vacía
     */
    public double verPrioridadMinima() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola de prioridad está vacía");
        }
        return prioridades[0];
    }

    /** @return true si no hay elementos */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /** @return cantidad de elementos */
    public int tamanio() {
        return tamanio;
    }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (prioridades[i] >= prioridades[padre]) {
                break;
            }
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = izq + 1;
            int menor = i;
            if (izq < tamanio && prioridades[izq] < prioridades[menor]) {
                menor = izq;
            }
            if (der < tamanio && prioridades[der] < prioridades[menor]) {
                menor = der;
            }
            if (menor == i) {
                return;
            }
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        Object tmp = heap[a];
        heap[a] = heap[b];
        heap[b] = tmp;
        double tp = prioridades[a];
        prioridades[a] = prioridades[b];
        prioridades[b] = tp;
    }

    private void crecer() {
        int nueva = heap.length * 2;
        Object[] h = new Object[nueva];
        double[] p = new double[nueva];
        for (int i = 0; i < tamanio; i++) {
            h[i] = heap[i];
            p[i] = prioridades[i];
        }
        heap = h;
        prioridades = p;
    }
}
