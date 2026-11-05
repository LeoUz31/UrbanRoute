/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.estructuras;

/**
 * Nodo para arreglo
 * @author leo.uzgil@gmail.com
 */
public class Nodo<T> {
    private T info;

    public Nodo(T dato){
        this.info=dato;
    }

    public T get(){
        return this.info;
    }

    public void set(T valor){
        this.info=valor;
    }
}

