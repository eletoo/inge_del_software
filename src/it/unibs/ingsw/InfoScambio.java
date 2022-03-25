package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 * InfoScambio: classe che tiene traccia delle informazioni di scambio
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class InfoScambio implements Serializable {
    private String piazza;
    private List<String> luoghi;
    private List<Giorno> giorni;
    private List<IntervalloOrario> intervalliOrari;
    private int scadenza;

    /**
     * Costruttore
     */
    public InfoScambio(@NotNull Applicazione app, View view) {
        luoghi = new ArrayList<>();
        giorni = new ArrayList<>();
        intervalliOrari = new ArrayList<>();

        this.configureExchangeSettings(app, view);
    }

    /**
     * @return stringa contenente la descrizione delle informazioni di scambio
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INFORMAZIONI SCAMBI:");
        sb.append("\nPiazza: ");
        sb.append(piazza);
        sb.append("\nLuoghi: ");
        for (int i = 0; i < luoghi.size(); i++) {
            sb.append("\n -> ");
            sb.append(luoghi.get(i));
        }
        sb.append("\nGiorni: ");
        for (int i = 0; i < giorni.size(); i++) {
            sb.append(giorni.get(i).getGiorno());
            if (i < giorni.size() - 1)
                sb.append(", ");
        }
        sb.append("\nIntervalli Orari: ");
        for (int i = 0; i < intervalliOrari.size(); i++) {
            sb.append(intervalliOrari.get(i).toString());
            if (i < intervalliOrari.size() - 1)
                sb.append(", ");
        }
        sb.append("\nScadenza offerte dopo " + scadenza + " giorni");
        return sb.toString();
    }

    /**
     * @return nome della piazza
     */
    public String getPiazza() {
        return piazza;
    }

    /**
     * Richiama metodi per la configurazione delle informazioni di scambio
     *
     * @param app  applicazione
     * @param view view
     */
    private void configureExchangeSettings(@NotNull Applicazione app, View view) {
        configurePiazza(app, view);

        configurePlaces(view);

        configureDays(view);

        configureTimeRanges(view);

        configureExpiration(view);
    }

    /**
     * Configura la scadenza della validita' di un'offerta di baratto
     *
     * @param view view
     */
    private void configureExpiration(@NotNull View view) {
        while ((this.scadenza = view.askNonNegativeNum("Scadenza dell'offerta dopo un numero di giorni pari a (inserire un numero maggiore di 0): ")) == 0)
            ;
    }

    /**
     * Configura gli intervalli orari (almeno uno) durante cui sono consentiti gli scambi chiedendo ora di inizio e fine dell'intervallo
     *
     * @param view view
     */
    private void configureTimeRanges(View view) {
        while (this.intervalliOrari.isEmpty() || view.yesOrNoQuestion("Inserire un altra fascia oraria per lo scambio? [Y/N]").equalsIgnoreCase("y")) {
            view.interactionMessage(View.InteractionMessage.EXCHANGE_HOURS_EVERY_30_MINS);
            //chiede l'orario di inizio
            Orario start = new Orario();
            start = start.askOrario(Orario.StartOrEnd.START, view);
            //chiede l'orario di fine
            Orario end = new Orario();
            end = end.askOrario(Orario.StartOrEnd.END, view);

            IntervalloOrario intervallo = new IntervalloOrario(start, end);
            if (intervallo.isValidRange() && intervallo.isNewRange(this.intervalliOrari))
                this.intervalliOrari.add(intervallo);
            else
                view.errorMessage(View.ErrorMessage.E_INVALID_TIME_RANGE);
        }
    }

    /**
     * Configura i giorni (almeno uno) in cui si possono effettuare gli scambi
     *
     * @param view view
     */
    private void configureDays(View view) {
        while (this.giorni.isEmpty() || view.yesOrNoQuestion("Inserire un altro giorno per lo scambio? [Y/N]").equalsIgnoreCase("y")) {
            Giorno g = view.askGiorno();
            if (g != null && !this.giorni.contains(g))
                this.giorni.add(g);
        }
    }

    /**
     * Configura i luoghi (almeno uno) in cui si possono effettuare gli scambi
     *
     * @param view view
     */
    private void configurePlaces(View view) {
        while (this.luoghi.isEmpty() || view.yesOrNoQuestion("Inserire un altro luogo per lo scambio? [Y/N]").equalsIgnoreCase("y")) {
            this.luoghi.add(view.askLuogo());
        }
    }

    /**
     * Configura la piazza in cui si possono effettuare gli scambi: se la piazza a' gia' stata impostata in precedenza
     * allora non puo' essere modificata, altrimenti viene chiesto all'utente di impostarla
     *
     * @param app  applicazione
     * @param view view
     */
    private void configurePiazza(@NotNull Applicazione app, View view) {
        if (app.getInformazioni() == null) {
            this.piazza = view.askPiazza();
        } else {
            this.piazza = app.getInformazioni().getPiazza();
        }
    }

}