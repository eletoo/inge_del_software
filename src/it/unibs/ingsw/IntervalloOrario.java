package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * IntervalloOrario: classe che gestisce un intervallo orario
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class IntervalloOrario implements Serializable {
    private Orario start;
    private Orario end;

    /**
     * Costruttore
     *
     * @param start orario di inizio dell'intervallo
     * @param end   orario di fine dell'intervallo
     */
    public IntervalloOrario(Orario start, Orario end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Verifica che un intervallo orario sia valido (i.e. se non ha durata negativa o nulla)
     *
     * @return true se l'intervalllo orario e' valido
     */
    public boolean isValidRange() {
        if (this.start.getHour() < this.end.getHour())
            return true;
        else if (this.start.getHour() == this.end.getHour() && this.start.getMinutes() < this.end.getMinutes())
            return true;
        return false;
    }

    /**
     * Verifica che un intervallo orario non sia gia' stato inserito
     *
     * @param intervals intervalli orari gia' esistenti
     * @return true se l'intervallo orario non e' ancora stato inserito
     */
    public boolean isNewRange(@NotNull List<IntervalloOrario> intervals) {
        for (IntervalloOrario i : intervals) {
            if (this.overlaps(i) || i.overlaps(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se due intervalli orari si sovrappongono
     *
     * @param b intervallo con cui controllare la sovrapposizione
     * @return true se i due intervalli si sovrappongono
     */
    @Contract(pure = true)
    private boolean overlaps(@NotNull IntervalloOrario b) {
        if (this.start == b.start || this.end == b.end) //i due intervalli iniziano o finiscono nello stesso momento
            return true;
        if (b.start.isLaterThan(this.start) && !b.start.isLaterThan(this.end)) //B inizia dopo l'inizio di A ma prima della fine di A

            return true;
        return false;
    }

    /**
     * @return stringa contenente la descrizione dell'intervallo orario
     */
    public String toString() {
        return start.toString() + "-" + end.toString();
    }

}