/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.hash;

import urbanroute.dominio.Estacion;
import urbanroute.estructuras.ListaEnlazada;
import urbanroute.estructuras.Nodo;

/**
 *
 * @author leo.uzgil@gmail.com
 * Tabla hash con encadenamiento para indexar estaciones por ID
 * Pendiente los siguientes metodos: obtenerTodas(), limpiar(), 
 */

public class TablaHash {

    /** Cantidad de cubetas (no limita la cantidad de estaciones). */
    // Número 31 es primo, elegir este o 37.
    public static final int CUBETAS = 31;

    private ListaEnlazada<ListaEnlazada<Estacion>> cubetas;
    private int elementosTotales;
    private CalculadorHash calculador;

    public TablaHash() {
        this.cubetas = new ListaEnlazada<>();
        int i = 0;
        while (i < CUBETAS) {
            cubetas.insertarAlFinal(new ListaEnlazada<Estacion>());
            i++;
        }
        this.calculador = new CalculadorHash();
        this.elementosTotales = 0;
    }

    /** Inserta la estación; si la clave ya existe, reemplaza su valor. */
    public void insertar(String clave, Estacion estacion) {
        ListaEnlazada<Estacion> cubeta = cubetas.acceder(calculador.calcularIndice(clave, CUBETAS));
        Nodo<Estacion> n = cubeta.getCabeza();
        while (n != null) {
            if (n.getDato().getIdEstacion().equals(clave)) {
                n.setDato(estacion);
                return;
            }
            n = n.getSiguiente();
        }
        cubeta.insertarAlFinal(estacion);
        elementosTotales++;
    }

    /** @return estación o null si no existe */
    public Estacion buscar(String clave) {
        ListaEnlazada<Estacion> cubeta = cubetas.acceder(calculador.calcularIndice(clave, CUBETAS));
        Nodo<Estacion> n = cubeta.getCabeza();
        while (n != null) {
            if (n.getDato().getIdEstacion().equals(clave)) {
                return n.getDato();
            }
            n = n.getSiguiente();
        }
        return null;
    }

    public boolean contiene(String clave) {
        return buscar(clave) != null;
    }

    /** @return estación eliminada o null si no existía */
    public Estacion eliminar(String clave) {
        Estacion e = buscar(clave);
        if (e != null) {
            cubetas.acceder(calculador.calcularIndice(clave, CUBETAS)).eliminar(e);
            elementosTotales--;
        }
        return e;
    }

    /** @return lista nueva con todas las estaciones almacenadas */
    public ListaEnlazada<Estacion> obtenerTodas() {
        ListaEnlazada<Estacion> todas = new ListaEnlazada<>();
        Nodo<ListaEnlazada<Estacion>> c = cubetas.getCabeza();
        while (c != null) {
            Nodo<Estacion> n = c.getDato().getCabeza();
            while (n != null) {
                todas.insertarAlFinal(n.getDato());
                n = n.getSiguiente();
            }
            c = c.getSiguiente();
        }
        return todas;
    }

    /** Vacía todas las cubetas. */
    public void limpiar() {
        Nodo<ListaEnlazada<Estacion>> c = cubetas.getCabeza();
        while (c != null) {
            c.getDato().vaciar();
            c = c.getSiguiente();
        }
        elementosTotales = 0;
    }

    public int size() {
        return elementosTotales;
    }
}
