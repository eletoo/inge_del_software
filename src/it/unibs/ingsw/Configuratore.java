package it.unibs.ingsw;

import java.io.Serializable;

/**
 * Configuratore: estende la classe {@link User}.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Configuratore extends User implements Serializable {

    /**
     * Costruttore.
     *
     * @param _username username del costruttore
     * @param _password password del costruttore
     */
    public Configuratore(String _username, String _password) {
        super(_username, _password);
    }

}
