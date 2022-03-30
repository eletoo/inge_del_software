package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

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
     * Costruttore
     */
    public Orario() {
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
        if (h == 24 && m == 0)
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

    /**
     * StartOrEnd: enum che tiene traccia del fatto che un orario sia di inizio o di fine di un intervallo
     */
    public static enum StartOrEnd {
        START("inizio"), END("fine");

        private String startOrEnd;

        StartOrEnd(String startOrEnd) {
            this.startOrEnd = startOrEnd;
        }

        public String getStartOrEnd() {
            return this.startOrEnd;
        }
    }

    /**
     * Chiede di inserire un orario nel formato hh:mm, verificandone la validita'.
     *
     * @param startOrEnd posizione dell'orario all'inizio o alla fine dell'intervallo
     * @param view       view
     * @return orario (di inizio o di fine dell'intervallo a seconda di quanto specificato nei parametri) inserito dall'utente
     */
    public Orario askOrario(@NotNull StartOrEnd startOrEnd, @NotNull View view) {
        Orario hour;
        int h;
        int m;
        do {
            h = view.askNonNegativeNum("Ora di " + startOrEnd.getStartOrEnd() + " [hh] [00-24]: ");
            m = view.askNonNegativeNum("Minuti di " + startOrEnd.getStartOrEnd() + " [mm] [00 o 30]: ");
            hour = new Orario(h, m);
            if (!hour.isValid(h, m))
                view.errorMessage(View.ErrorMessage.E_INVALID_TIME);
        } while (!hour.isValid(h, m));
        return hour;
    }

    /**
     * Verifica se l'orario e' successivo a quello passato come parametro. Se gli orari sono uguali ritorna true.
     *
     * @param o orario da confrontare
     * @return true se l'orario su cui e' chiamato il metodo e' uguale o successivo all'orario passato come parametro
     */
    public boolean isLaterThan(@NotNull Orario o) {
        if (this.getHour() > o.getHour())
            return true;
        if (this.getHour() == o.getHour() && this.getMinutes() >= o.getMinutes())
            return true;
        return false;
    }

    /**
     * Verifica se due orari sono lo stesso orario
     *
     * @param o orario da confrontare
     * @return true se gli orari sono gli stessi
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orario orario = (Orario) o;
        return hour == orario.hour && minutes == orario.minutes;
    }

    /**
     * Override del metodo hashCode() della classe Object
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(hour, minutes);
    }
}