package urbanroute.estructuras;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author leo.uzgil@gmail.com
 * Clase de Arreglo dinamico
 * @param <T> tipo de elemento almacenado
 */
public class Arreglo<T> {
    public int Max, cantidad;
    Nodo<T>[] lista;

    public Arreglo(int tam){
        this.Max=tam;
        this.cantidad=0;
        this.lista=new Nodo[this.Max];
    }

    private boolean es_posicion_valida(int P){
        return P>=0 && P<this.cantidad;
    }

    public void agregar(T x){
        if (!this.estaLleno()){
            this.lista[this.cantidad]=new Nodo<T>(x);
            this.cantidad++;
        }else{
            System.out.print("error");
        }
    }

    public T obtener(int P){
        if (this.es_posicion_valida(P)){
            return this.lista[P].get();
        }else{
            System.out.print("error");
            return null;
        }
    }

    public T eliminar(int P){
        int indice;
        T eliminado;
        if (this.es_posicion_valida(P)){
            eliminado=this.lista[P].get();
            indice=P;
            while (indice<this.cantidad-1){
                this.lista[indice]=this.lista[indice+1];
                indice++;
            }
            this.lista[this.cantidad-1]=null;
            this.cantidad--;
            return eliminado;
        }else{
            System.out.print("error");
            return null;
        }
    }

    // La posicion equivalente a NULL es en este caso -1.
    public int indiceDe(T x){
        int indice=0;
        while (indice<this.cantidad){
            if (this.lista[indice].get()==x){
                return indice;
            }
            indice++;
        }
        return -1;
    }

    public int size(){
        return this.cantidad;
    }

    public boolean estaVacio(){
        return this.cantidad==0;
    }

    public boolean estaLleno(){
        return this.cantidad==this.Max;
    }

    // Vacia el arreglo; el maximo no cambia.
    public void limpiar(){
        int indice=0;
        while (indice<this.cantidad){
            this.lista[indice]=null;
            indice++;
        }
        this.cantidad=0;
    }
}
