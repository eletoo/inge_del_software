package it.unibs.ingsw;

import java.io.Serializable;

/**
 * Giorno: enum che tiene traccia dei giorni della settimana
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public enum Giorno implements Serializable {
    LUNEDI("Lunedì"),
    MARTEDI("Martedì"),
    MERCOLEDI("Mercoledì"),
    GIOVEDI("Giovedì"),
    VENERDI("Venerdì"),
    SABATO("Sabato"),
    DOMENICA("Domenica");

    private String giorno;

    /**
     * Costruttore
     */
    Giorno(String giorno) {
        this.giorno = giorno;
    }

    /**
     * @return nome del giorno della settimana
     */
    public String getGiorno() {
        return this.giorno;
    }
}
