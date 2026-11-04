package urbanroute.hash;

/**
 * Transforma una clave de texto en un índice de cubeta (hash polinomial base 31).
 */
public class CalculadorHash {

    private static final int BASE = 31;

    /**
     * @param clave     texto a transformar
     * @param capacidad número de cubetas
     * @return índice en [0, capacidad)
     */
    public int calcularIndice(String clave, int capacidad) {
        long h = 0;
        for (int i = 0; i < clave.length(); i++) {
            h = (h * BASE + clave.charAt(i)) % capacidad;
        }
        return (int) Math.abs(h % capacidad);
    }
}
