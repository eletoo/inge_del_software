package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

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
     * Verifica che un intervallo orario sia valido (i.e. se non ha durata negativa)
     *
     * @param start orario di inizio dell'intervallo
     * @param end   orario di fine dell'intervallo
     * @return true se l'intervalllo orario e' valido
     */
    public boolean isValidRange(@NotNull Orario start, @NotNull Orario end) {
        if (start.getHour() <= end.getHour())
            if (start.getMinutes() <= end.getMinutes())
                return true;
        return false;
    }

    /**
     * @return stringa contenente la descrizione dell'intervallo orario
     */
    public String toString() {
        return start.toString() + "-" + end.toString();
    }

    /**
     * @return orario di inizio
     */
    public Orario getStart() {
        return start;
    }

    /**
     * @return orario di fine
     */
    public Orario getEnd() {
        return end;
    }
}
