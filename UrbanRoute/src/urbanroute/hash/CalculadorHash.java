/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package urbanroute.hash;

/**
 *
 * @author leo.uzgil@gmail.com
 * calculador Bucket Sort
 */
public class CalculadorHash {

    public int calcularIndice(String clave, int capacidad) {
        int suma = 0;
        int i = 0;
        while (i < clave.length()) {
            suma = suma + clave.charAt(i);
            i++;
        }
        return suma % capacidad;
    }
}
