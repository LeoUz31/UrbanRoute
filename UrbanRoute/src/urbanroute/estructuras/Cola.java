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
public class Cola {
    public int Max, cantidad;
    int[] items;

    public Cola(int tam){
        this.Max=tam;
        this.cantidad=0;
        this.items=new int[this.Max];
    }

    public boolean esta_vacia(){
        return this.cantidad==0;
    }

    public void encolar(int item){
        if (this.cantidad<this.Max){
            this.items[this.cantidad]=item;
            this.cantidad++;
        }else{
            System.out.print("error");
        }
    }

    // La posicion equivalente a NULL es en este caso -1.
    public int desencolar(){
        int item, indice;
        if (!this.esta_vacia()){
            item=this.items[0];
            indice=0;
            while (indice<this.cantidad-1){
                this.items[indice]=this.items[indice+1];
                indice++;
            }
            this.cantidad--;
            return item;
        }else{
            return -1;
        }
    }
}
