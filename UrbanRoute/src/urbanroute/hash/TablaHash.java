/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.hash;

import urbanroute.dominio.Estacion;

/**
 *
 * @author leo.uzgil@gmail.com
 * Tabla hash con encadenamiento para indexar estaciones por ID en O(1) promedio.
 * Pendiente los siguientes metodos: obtenerTodas(), limpiar(), 
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
    
    public Estacion buscar(String clave) {
        int i = calculador.calcularIndice(clave, capacidad);
        for (NodoHash n = cubetas[i]; n != null; n = n.getSiguiente()) {
            if (n.getClave().equals(clave)) {
                return n.getValor();
            }
        }
        return null;
    }
