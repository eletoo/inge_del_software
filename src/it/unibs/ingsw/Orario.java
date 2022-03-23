package it.unibs.ingsw;

import java.io.Serializable;

/**
 * Orario: classe che gestisce le informazioni relative a un orario
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Orario implements Serializable {
    private int hour;
    private int minutes;

    /**
     * Costruttore
     *
     * @param hour    ora
     * @param minutes minuti
     */
    public Orario(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    /**
     * @return stringa contenente la descrizione dell'orario nel formato hh:mm
     */
    public String toString() {
        String hour = this.hour == 0 ? "00" : String.valueOf(this.hour);
        String mins = this.minutes == 0 ? "00" : String.valueOf(this.minutes);
        return hour + ":" + mins;
    }

    /**
     * Verifica la validita' di un orario (i.e. se l'ora e' compresa tra 0 e 23 e se i minuti sono 00 o 30)
     *
     * @param h ora
     * @param m minuti
     * @return true se l'orario e' nel formato richiesto
     */
    public boolean isValid(int h, int m) {
        if (h >= 0 && h <= 23)
            if (m == 0 || m == 30)
                return true;

        return false;
    }

    /**
     * @return minuti
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * @return ora
     */
    public int getHour() {
        return hour;
    }
}
