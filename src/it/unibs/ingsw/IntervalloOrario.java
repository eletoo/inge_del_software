package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.LinkedList;
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
     * Verifica che un intervallo orario sia valido (i.e. se non ha durata negativa)
     *
     * @return true se l'intervalllo orario e' valido
     */
    public boolean isValidRange() {
        if (this.start.getHour() < this.end.getHour())
            return true;
        else if (this.start.getHour() == this.end.getHour() && this.start.getMinutes() <= this.end.getMinutes())
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
     * @return orario di inizio di un intervallo
     */
    public Orario getStart() {
        return start;
    }

    /**
     * Verifica se due intervalli orari si sovrappongono
     *
     * @param b intervallo con cui controllare la sovrapposizione
     * @return true se i due intervalli si sovrappongono
     */
    @Contract(pure = true)
    private boolean overlaps(@NotNull IntervalloOrario b) {
        if (this.start.equals(b.start) || this.end.equals(b.end)) //i due intervalli iniziano o finiscono nello stesso momento
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

    /**
     * @return l'elenco di tutti gli orari (uno ogni mezz'ora) contenuti in un intervallo orario
     */
    public List<Orario> getSingoliOrari() {
        List<Orario> orari = new LinkedList<>();
        orari.add(this.start);
        int h = this.start.getHour();
        int m = this.start.getMinutes();
        Orario o = new Orario(h, m);
        while (this.end.isLaterThan(o)) {
            if (o.getMinutes() == 30) {
                if (o.getHour() <= 23)
                    h = h + 1;
                else
                    h = 0;

                m = 0;
            } else
                m = 30;

            o = new Orario(h, m);
            if (this.end.isLaterThan(o))
                orari.add(o);
        }
        return orari;
    }

}