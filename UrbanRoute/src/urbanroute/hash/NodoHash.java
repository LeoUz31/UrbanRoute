/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.hash;

import urbanroute.dominio.Estacion;

/**
 *
 * @author leo.uzgil@gmail.com
 */
public class NodoHash {

    private String clave;
    private Estacion valor;
    private NodoHash siguiente;

    /**
     * @param clave clave (ID de estación)
     * @param valor estación asociada
     */
    public NodoHash(String clave, Estacion valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public String getClave() { return clave; }
    public Estacion getValor() { return valor; }
    public void setValor(Estacion valor) { this.valor = valor; }
    public NodoHash getSiguiente() { return siguiente; }
    public void setSiguiente(NodoHash siguiente) { this.siguiente = siguiente; }
}
