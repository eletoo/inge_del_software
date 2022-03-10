package version1;

import java.io.Serializable;

/**
 * Configuratore: estende la classe User
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Configuratore extends User implements Serializable {

    /**
     * Costruttore
     *
     * @param _username username del costruttore
     * @param _password password del costruttore
     */
    public Configuratore(String _username, String _password) {
        super(_username, _password);
    }

}
