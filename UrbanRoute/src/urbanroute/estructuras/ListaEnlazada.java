package urbanroute.estructuras;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author leo.uzgil@gmail.com
 * Clase de lista simplemente enlazada con apuntador a cabeza y fin.
 * @param <T> tipo de elemento almacenado
 */
public class ListaEnlazada<T> {
    private Nodo<T> cabeza;
    private Nodo<T> fin;
    private int size;

    public ListaEnlazada() {
        this.cabeza = null;
        this.fin = null;
        this.size = 0;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getSize() {
        return size;
    }

    /** @return primer nodo (para recorrer la lista), o null si está vacía */
    public Nodo<T> getCabeza() {
        return cabeza;
    }

    /** @return dato de la posición indicada, o null si la posición no es válida */
    public T acceder(int indice) {
        if (indice < 0 || indice >= size) {
            return null;
        }
        Nodo<T> actual = cabeza;
        int i = 0;
        while (i < indice) {
            actual = actual.getSiguiente();
            i++;
        }
        return actual.getDato();
    }

    /** Reemplaza el dato de la posición indicada (no hace nada si la posición no es válida). */
    public void modificar(int indice, T dato) {
        if (indice < 0 || indice >= size) {
            return;
        }
        Nodo<T> actual = cabeza;
        int i = 0;
        while (i < indice) {
            actual = actual.getSiguiente();
            i++;
        }
        actual.setDato(dato);
    }

    /** @return posición del dato (comparado por referencia, ==), o -1 si no está */
    public int indiceDe(T dato) {
        Nodo<T> actual = cabeza;
        int i = 0;
        while (actual != null) {
            if (actual.getDato() == dato) {
                return i;
            }
            actual = actual.getSiguiente();
            i++;
        }
        return -1;
    }

    public boolean contiene(T dato) {
        return indiceDe(dato) != -1;
    }

    public void insertarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        if (fin == null) {
            fin = nuevo;
        }
        size++;
    }

    public void insertarAlFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        size++;
    }

    /** Inserta el dato justo antes del nodo referencia (que debe pertenecer a la lista). */
    public void insertarAntesDe(Nodo<T> referencia, T dato) {
        if (referencia == cabeza) {
            insertarAlInicio(dato);
            return;
        }
        Nodo<T> anterior = cabeza;
        while (anterior != null && anterior.getSiguiente() != referencia) {
            anterior = anterior.getSiguiente();
        }
        if (anterior == null) {
            return;
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(referencia);
        anterior.setSiguiente(nuevo);
        size++;
    }

    /** @return dato eliminado del inicio, o null si la lista estaba vacía */
    public T eliminarPrimero() {
        if (cabeza == null) {
            return null;
        }
        Nodo<T> eliminado = cabeza;
        cabeza = eliminado.getSiguiente();
        if (cabeza == null) {
            fin = null;
        }
        eliminado.setSiguiente(null);
        size--;
        return eliminado.getDato();
    }

    /** Elimina la primera aparición del dato (comparado por referencia, ==). */
    public boolean eliminar(T dato) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato() == dato) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == fin) {
                    fin = anterior;
                }
                actual.setSiguiente(null);
                size--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    /** Elimina el dato de la posición indicada. @return dato eliminado o null */
    public T eliminarEn(int indice) {
        T dato = acceder(indice);
        if (dato == null) {
            return null;
        }
        eliminar(dato);
        return dato;
    }

    /** Desenlaza todos los nodos y deja la lista vacía. */
    public void vaciar() {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            Nodo<T> siguiente = actual.getSiguiente();
            actual.setSiguiente(null);
            actual = siguiente;
        }
        cabeza = null;
        fin = null;
        size = 0;
    }
}
