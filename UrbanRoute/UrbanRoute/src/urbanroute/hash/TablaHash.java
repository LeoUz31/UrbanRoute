package urbanroute.hash;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ArregloDinamico;

/**
 * Tabla hash con encadenamiento para indexar estaciones por ID en O(1) promedio.
 */
public class TablaHash {

    private static final int CAPACIDAD_INICIAL = 31;

    private NodoHash[] cubetas;
    private int capacidad;
    private int elementosTotales;
    private final double factorCargaLimite;
    private final CalculadorHash calculador;

    /** Crea una tabla con capacidad prima inicial y factor de carga 0.75. */
    public TablaHash() {
        this(CAPACIDAD_INICIAL, 0.75);
    }

    /**
     * @param capacidadInicial  capacidad inicial (se ajusta al primo siguiente)
     * @param factorCargaLimite límite que dispara el rehash
     */
    public TablaHash(int capacidadInicial, double factorCargaLimite) {
        this.capacidad = siguientePrimo(Math.max(2, capacidadInicial));
        this.cubetas = new NodoHash[capacidad];
        this.factorCargaLimite = factorCargaLimite;
        this.calculador = new CalculadorHash();
        this.elementosTotales = 0;
    }

    /**
     * Inserta o reemplaza la estación asociada a la clave.
     *
     * @param clave    ID de la estación
     * @param estacion estación
     */
    public void insertar(String clave, Estacion estacion) {
        int i = calculador.calcularIndice(clave, capacidad);
        for (NodoHash n = cubetas[i]; n != null; n = n.getSiguiente()) {
            if (n.getClave().equals(clave)) {
                n.setValor(estacion);
                return;
            }
        }
        NodoHash nuevo = new NodoHash(clave, estacion);
        nuevo.setSiguiente(cubetas[i]);
        cubetas[i] = nuevo;
        elementosTotales++;
        if ((double) elementosTotales / capacidad > factorCargaLimite) {
            rehash();
        }
    }

    /**
     * @param clave ID de la estación
     * @return estación o null si no existe
     */
    public Estacion buscar(String clave) {
        int i = calculador.calcularIndice(clave, capacidad);
        for (NodoHash n = cubetas[i]; n != null; n = n.getSiguiente()) {
            if (n.getClave().equals(clave)) {
                return n.getValor();
            }
        }
        return null;
    }

    /**
     * @param clave ID de la estación
     * @return true si existe
     */
    public boolean contiene(String clave) {
        return buscar(clave) != null;
    }

    /**
     * @param clave ID de la estación
     * @return estación eliminada o null si no existía
     */
    public Estacion eliminar(String clave) {
        int i = calculador.calcularIndice(clave, capacidad);
        NodoHash anterior = null;
        for (NodoHash n = cubetas[i]; n != null; n = n.getSiguiente()) {
            if (n.getClave().equals(clave)) {
                if (anterior == null) {
                    cubetas[i] = n.getSiguiente();
                } else {
                    anterior.setSiguiente(n.getSiguiente());
                }
                elementosTotales--;
                return n.getValor();
            }
            anterior = n;
        }
        return null;
    }

    /** @return todas las estaciones almacenadas */
    public ArregloDinamico<Estacion> obtenerTodas() {
        ArregloDinamico<Estacion> todas = new ArregloDinamico<>(Math.max(1, elementosTotales));
        for (int i = 0; i < capacidad; i++) {
            for (NodoHash n = cubetas[i]; n != null; n = n.getSiguiente()) {
                todas.agregar(n.getValor());
            }
        }
        return todas;
    }

    /** Vacía la tabla. */
    public void limpiar() {
        cubetas = new NodoHash[capacidad];
        elementosTotales = 0;
    }

    /** @return número de elementos */
    public int tamanio() {
        return elementosTotales;
    }

    /** @return número de cubetas */
    public int getCapacidad() {
        return capacidad;
    }

    /** Duplica (al primo siguiente) la capacidad y redistribuye los nodos. */
    private void rehash() {
        NodoHash[] viejas = cubetas;
        capacidad = siguientePrimo(capacidad * 2);
        cubetas = new NodoHash[capacidad];
        for (NodoHash cabeza : viejas) {
            NodoHash n = cabeza;
            while (n != null) {
                NodoHash sig = n.getSiguiente();
                int i = calculador.calcularIndice(n.getClave(), capacidad);
                n.setSiguiente(cubetas[i]);
                cubetas[i] = n;
                n = sig;
            }
        }
    }

    private static int siguientePrimo(int n) {
        while (!esPrimo(n)) {
            n++;
        }
        return n;
    }

    private static boolean esPrimo(int n) {
        if (n < 2) {
            return false;
        }
        for (int d = 2; (long) d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }
}
