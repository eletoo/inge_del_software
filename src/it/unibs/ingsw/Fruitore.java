package it.unibs.ingsw;

import java.io.Serializable;
/**
 * Fruitore: estende la classe {@link User}.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Fruitore extends User implements Serializable {
    /**
     * Costruttore: salva la password dopo l'hashing
     *
     * @param _username username
     * @param _password password in chiaro
     */
    public Fruitore(String _username, String _password) {
        super(_username, _password);
    }


}
