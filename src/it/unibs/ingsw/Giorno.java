package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    /**
     * @return nome del giorno della settimana senza accento
     */
    @Contract(pure = true)
    public @NotNull String getUnaccentedGiorno(){
        return this.giorno.replace("ì", "i");
    }
}
