package it.unibs.ingsw;

/**
 * Foglia: estende la classe {@link Categoria}
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Foglia extends Categoria {

    /**
     * Costruttore.
     */
    public Foglia(String _nome, String _descrizione) {
        super(_nome, _descrizione);
    }

    /**
     * @return stringa descrittiva del contenuto della foglia
     */
    public String toString() {
        return super.toString();
    }
}
