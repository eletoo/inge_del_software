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
    public InfoScambio(Applicazione app, View view) {
        piazza = null;
        luoghi = new ArrayList<>();
        giorni = new ArrayList<>();
        intervalliOrari = new ArrayList<>();
        scadenza = 0;
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
            sb.append("\n");
            sb.append(luoghi.get(i));
        }
        sb.append("\nGiorni: ");
        for (int i = 0; i < giorni.size(); i++) {
            sb.append("\n");
            sb.append(giorni.get(i));
        }
        sb.append("\nIntervalli Orari: ");
        for (int i = 0; i < intervalliOrari.size(); i++) {
            sb.append("\n");
            sb.append(intervalliOrari.get(i).toString());
        }
        sb.append("\nScadenza:");
        sb.append(scadenza);
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
            Orario start;
            int startHour;
            int startMin;
            do {
                startHour = view.askNonNegativeNum("Ora di inizio [hh] [00-23]: ");
                startMin = view.askNonNegativeNum("Minuti di inizio [mm] [00 o 30]: ");
                start = new Orario(startHour, startMin);
                if (!start.isValid(startHour, startMin))
                    view.errorMessage(View.ErrorMessage.E_INVALID_TIME);
            } while (!start.isValid(startHour, startMin));
            //chiede l'orario di fine
            Orario end;
            int endHour;
            int endMin;
            do {
                endHour = view.askNonNegativeNum("Ora di fine [hh] [00-23]: ");
                endMin = view.askNonNegativeNum("Minuti di fine [mm] [00 o 30]: ");
                end = new Orario(endHour, endMin);
                if (!end.isValid(endHour, endMin))
                    view.errorMessage(View.ErrorMessage.E_INVALID_TIME);
            } while (!end.isValid(endHour, endMin));

            IntervalloOrario intervallo = new IntervalloOrario(start, end);
            if (intervallo.isValidRange(start, end))
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
            this.giorni.add(view.askGiorno());
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
