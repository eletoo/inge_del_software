package it.unibs.ingsw;

import org.jetbrains.annotations.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Scambio implements Serializable {

    private Offerta ownOffer;
    private Offerta selectedOffer;
    private ExchangeMessage messageA;
    private ExchangeMessage messageB;
    private LocalDateTime dateTime;

    /**
     * Costruttore
     *
     * @param app      applicazione
     * @param own      offerta accoppiata
     * @param selected offerta selezionata
     */
    private Scambio(@NotNull Applicazione app, @NotNull Offerta own, @NotNull Offerta selected) {
        this.ownOffer = own;
        this.selectedOffer = selected;

        app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.ACCOPPIATA);
        app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.SELEZIONATA);

        this.dateTime = LocalDateTime.now();
        this.messageA = new ExchangeMessage(null, own.getProprietario());
        this.messageB = new ExchangeMessage(null, selected.getProprietario());

    }

    /**
     * Fa selezionare le due offerte da scambiare, gestendo i casi in cui l'elenco di offerte di uno dei due autori sia vuoto
     *
     * @param app    applicazione
     * @param view   view
     * @param author autore della proposta di scambio
     * @return nuovo oggetto scambio
     */
    public static @Nullable Scambio createExchange(@NotNull Applicazione app, @NotNull View view, @NotNull Fruitore author) {
        if (app.getOfferte(author, Offerta.StatoOfferta.APERTA).isEmpty()) {
            view.errorMessage(View.ErrorMessage.E_NO_OFFERS);
            return null;
        }

        view.interactionMessage(View.InteractionMessage.CHOOSE_OFFER);
        var ownOffer = view.choose(app.getOfferte(author)
                .stream()
                .filter(Offerta::isAvailableOffer)
                .collect(Collectors.toList()), null);

        var possible_offers = app.getOfferte(ownOffer.getCategoria())
                .stream()
                .filter(e -> !ownOffer.getProprietario().equals(e.getProprietario()))
                .filter(Offerta::isAvailableOffer)
                .collect(Collectors.toList());

        if (possible_offers.isEmpty()) {
            view.errorMessage(View.ErrorMessage.E_NO_OFFERS);
            return null;
        }

        view.interactionMessage(View.InteractionMessage.CHOOSE_OTHER_OFFER);
        var selectedOffer = view.choose(possible_offers, null);

        return new Scambio(app, ownOffer, selectedOffer);
    }

    /**
     * @param app applicazione
     * @return true se lo scambio a' ancora valido, cioe' non e' ancora stata superata la scadenza
     */
    public boolean isValidExchange(@NotNull Applicazione app) {
        //riga di test per verificare il funzionamento corretto del timer
        //return LocalDateTime.now().isBefore(this.dateTime.plusSeconds(app.getInformazioni().getScadenza()))
          //      && (this.ownOffer.getStato() != Offerta.StatoOfferta.CHIUSA && this.selectedOffer.getStato() != Offerta.StatoOfferta.CHIUSA);

        return LocalDateTime.now().isBefore(this.dateTime.plusDays(app.getInformazioni().getScadenza()))
              && (this.ownOffer.getStato() != Offerta.StatoOfferta.CHIUSA && this.selectedOffer.getStato() != Offerta.StatoOfferta.CHIUSA);
    }

    /**
     * Gestisce il singolo scambio permettendo di selezionare le offerte da scambiare
     *
     * @param view view
     * @param app  applicazione
     */
    public void manageExchange(@NotNull View view, @NotNull Applicazione app, Fruitore f) throws IOException {
        if (this.messageA.getMessage() == null) {
            suggestMeeting(view, app, f);
            app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.IN_SCAMBIO);
            app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.IN_SCAMBIO);
            return;
        }

        view.message(this.messageA.getMessage());
        if (view.yesOrNoQuestion("\nAccettare l'appuntamento? [Y/N]").equalsIgnoreCase("y")) {
            app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.CHIUSA);
            app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.CHIUSA);
            view.message("\n\nOfferta chiusa\n");
            app.removeScambio(this);
            app.saveExchanges();
        } else {
            suggestMeeting(view, app, f);
            app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.IN_SCAMBIO);
            app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.IN_SCAMBIO);
        }
    }

    /**
     * Chiede all'utente le informazioni per un appuntamento per lo scambio
     *
     * @param view view
     * @param app  applicazione
     * @param f    autore della proposta di appuntamento
     */
    private void suggestMeeting(@NotNull View view, @NotNull Applicazione app, Fruitore f) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nInformazioni appuntamento per lo scambio:");
        sb.append("\nLuogo: " + view.choose(app.getInformazioni().getLuoghi(), null));
        sb.append("\nGiorno: " + view.choose(app.getInformazioni().getGiorni(), null));

        List<Orario> orari = new LinkedList<>();
        app.getInformazioni().getIntervalliOrari().stream().forEach(e -> orari.addAll(e.getSingoliOrari()));
        sb.append("\nOrario: " + view.choose(orari, null));

        if (this.ownOffer.getProprietario().equals(f))
            this.messageB.setMessage(sb.toString());
        else
            this.messageA.setMessage(sb.toString());

        this.dateTime = LocalDateTime.now();
    }

    /**
     * @return destinatario di un'offerta di scambio
     */
    public Fruitore getDest() {
        return this.selectedOffer.getProprietario();
    }

    /**
     * @return autore di un'offerta di scambio
     */
    public Fruitore getAuthor() {
        return this.ownOffer.getProprietario();
    }

    /**
     * Gestisce le offerte di scambio di cui l'utente e' destinatario
     *
     * @param app  applicazione
     * @param view view
     * @param dest utente destinatario
     * @throws IOException eccezione
     */
    public static void manageExchanges(@NotNull Applicazione app, @NotNull View view, Fruitore dest) throws IOException {
        manage(app,
                view,
                "\nNon hai nessuna nuova proposta di scambio",
                "\nLe tue nuove proposte di scambio:",
                "\nSelezionare uno scambio da accettare dalla lista? [Y/N]",
                dest,
                Scambio::getDest);
    }

    /**
     * Gestisce le offerte di scambio di cui l'utente e' autore
     *
     * @param app    applicazione
     * @param view   view
     * @param author utente autore
     * @throws IOException eccezione
     */
    public static void manageOwnExchanges(@NotNull Applicazione app, @NotNull View view, Fruitore author) throws IOException {
        manage(app,
                view,
                "\n\nNon hai nessun'altra proposta di scambio arretrata",
                "\nLe tue proposte di scambio arretrate:",
                "\nSelezionare uno scambio da gestire dalla lista? [Y/N]",
                author,
                Scambio::getAuthor);
    }

    /**
     * Gestisce le offerte di scambio di un utente
     *
     * @param app               applicazione
     * @param view              view
     * @param noExchanges       messaggio che comunica che non ci sono offerte di scambio
     * @param existingExchanges messaggio che comunica che ci sono offerte di scambio
     * @param selectExchange    richiesta di selezione di uno scambio da una lista
     * @param f                 utente fruitore
     * @param predicate         predicato da applicare a un oggetto Scambio per selezionare gli scambi con un determinato autore o destinatario
     * @throws IOException eccezione
     */
    private static void manage(@NotNull Applicazione app, View view, String noExchanges, String existingExchanges, String selectExchange, Fruitore f, Function<Scambio, Fruitore> predicate) throws IOException {
        //gestione scambi validi
        List<Scambio> userExchanges;
        if (app.getScambi() == null)
            userExchanges = new ArrayList<>();
        else
            userExchanges = app
                    .getValidExchanges(app.getScambi())
                    .stream()
                    .filter(e -> predicate.apply(e).equals(f))
                    .collect(Collectors.toList());

        if (userExchanges.isEmpty())
            view.message(noExchanges);

        while (!userExchanges.isEmpty()) {
            Scambio toAccept;
            view.message(existingExchanges);
            view.showList(userExchanges);
            if (view.yesOrNoQuestion(selectExchange).equalsIgnoreCase("y")) {
                toAccept = view.choose(userExchanges, null);
                toAccept.manageExchange(view, app, f);
                userExchanges.remove(toAccept);
                app.saveExchanges();
            } else {
                return;
            }
        }
        //gestione scambi invalidi
        manageInvalidExchanges(app);
    }

    /**
     * Gestisce le offerte di scambio invalide, cioe' scadute
     *
     * @param app
     */
    private static void manageInvalidExchanges(@NotNull Applicazione app) throws IOException {
        if (app.getScambi() == null)
            return;

        var scambi = app.getScambi()
                .stream()
                .filter(e -> !e.isValidExchange(app))
                .collect(Collectors.toList());

        for (Scambio s : scambi) {
            app.getOfferta(s.ownOffer).setStato(Offerta.StatoOfferta.APERTA);
            app.getOfferta(s.selectedOffer).setStato(Offerta.StatoOfferta.APERTA);
            app.removeScambio(s);
        }

        app.saveExchanges();
    }

    /**
     * @return stringa contenente una breve descrizione dello scambio da effettuare
     */
    public String toString() {
        return this.selectedOffer.getName() + " <--> " + this.ownOffer.getName();
    }

    /**
     * @return stringa contenente una breve descrizione dello scambio e l'ultimo messaggio a esso relativo introdotto
     * dall'utente controparte nello scambio
     */
    public String getLastMsgString(Fruitore f) {
        if (this.messageB.getAuthor().equals(f))
            return (this.messageB.getMessage() == null) ? "--" : this + ": " + this.messageB.getMessage();

        return (this.messageA.getMessage() == null) ? "--" : this + ": " + this.messageA.getMessage();
    }

    /**
     * Visualizza l'ultimo messaggio da parte della controparte
     *
     * @param f    autore
     * @param app  applicazione
     * @param view view
     */
    public static void viewLastMessageByCounterpart(Fruitore f, @NotNull Applicazione app, @NotNull View view) {
        if (app.getScambi().isEmpty())
            return;

        var exc = view.choose(app.getScambi()
                .stream()
                .filter(e -> e.getAuthor().equals(f) || e.getDest().equals(f))
                .collect(Collectors.toList()), null
        );

        view.message(exc.getLastMsgString(f));
    }
}