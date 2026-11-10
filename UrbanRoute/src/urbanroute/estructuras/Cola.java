package urbanroute.estructuras;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author leo.uzgil@gmail.com
 * @param <T> tipo de elemento
 * TDA Cola FIFO con nodos enlazados (usada por BFS).
 */
public class Cola<T> {
    private ListaEnlazada<T> items;

    public Cola() {
        this.items = new ListaEnlazada<>();
    }

    public boolean esta_vacia() {
        return items.estaVacia();
    }

    public void encolar(T item) {
        items.insertarAlFinal(item);
    }

    /** @return primer elemento, o null si la cola está vacía */
    public T desencolar() {
        return items.eliminarPrimero();
    }
}
