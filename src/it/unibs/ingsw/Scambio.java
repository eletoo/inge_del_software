package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scambio implements Serializable {

    //TODO: l'autore di una proposta in scambio può ritirarla o una volta che è in scambio non è più ritirabile?

    private Fruitore author;
    private Offerta ownOffer;
    private Offerta selectedOffer;
    private ExchangeMessage message;
    private LocalDateTime dateTime;
    private ExchangeMessage lastAuthorMsg;

    public Scambio(Applicazione app, View view, Fruitore autore) {
        this.author = autore;
        this.createExchange(app, view);
    }

    private void createExchange(@NotNull Applicazione app, @NotNull View view) {
        this.ownOffer = view.choose(app.getOfferte(author)
                .stream()
                .filter(e -> e.isAvailableOffer())
                .collect(Collectors.toList()), null);

        this.selectedOffer = view.choose(app.getOfferte(this.ownOffer.getCategoria())
                .stream()
                .filter(e -> (this.ownOffer.getProprietario() != e.getProprietario()))
                .filter(e -> e.isAvailableOffer())
                .collect(Collectors.toList()), null);

//TODO: non so se cambia il valore dello stato solo nell'oggetto o nell'app intera
        if (this.ownOffer != null && this.selectedOffer != null) {
            app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.ACCOPPIATA);
            //this.ownOffer.setStato(Offerta.StatoOfferta.ACCOPPIATA);
            app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.SELEZIONATA);
            //this.selectedOffer.setStato(Offerta.StatoOfferta.SELEZIONATA);
        }
        this.message = null;
        this.lastAuthorMsg = null;
        this.dateTime = LocalDateTime.now();
    }

    /**
     * @param app applicazione
     * @return true se lo scambio a' ancora valido, cioe' non e' ancora stata superata la scadenza
     */
    public boolean isValidExchange(@NotNull Applicazione app) {
        return LocalDateTime.now().isBefore(this.dateTime.plusDays(app.getInformazioni().getScadenza()));
    }

    /**
     * gestisce il singolo scambio: se non e' ancora presente alcuna informazione chiede all'utente di impostarle,
     * altrimenti chiede se accettare l'appuntamento proposto o proporne uno alternativo
     *
     * @param view view
     * @param app  applicazione
     */
    public void manageExchange(@NotNull View view, @NotNull Applicazione app, Fruitore f) {
        if (this.message == null) {
            suggestMeeting(view, app, f);

            app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.IN_SCAMBIO);
            app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.IN_SCAMBIO);
        } else {
            view.message(this.message.getMessage());
            if (view.yesOrNoQuestion("\nAccettare l'appuntamento? [Y/N]").equalsIgnoreCase("y")) {
                app.getOfferta(this.selectedOffer).setStato(Offerta.StatoOfferta.CHIUSA);
                app.getOfferta(this.ownOffer).setStato(Offerta.StatoOfferta.CHIUSA);
                view.message("\n\nOfferta chiusa\n\n");
                app.removeScambio(this);
            } else {
                suggestMeeting(view, app, author);
            }
        }
    }

    /**
     * chiede all'utente le informazioni per un appuntamento per lo scambio
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
        sb.append("\nOrario: " + view.choose(app.getInformazioni().getIntervalliOrari(), null));
        //TODO: posso selezionare l'intervallo o il singolo orario al suo interno?
        this.message.setMessage(sb.toString());
        this.message.setAuthor(f);

        if (this.author.equals(f))
            this.lastAuthorMsg = this.message;
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
        return author;
    }

    /**
     * @return l'ultimo messaggio inviato dall'autore di uno scambio
     */
    public ExchangeMessage getLastAuthorMsg() {
        return this.lastAuthorMsg;
    }


    /**
     * gestisce le offerte di scambio di cui l'utente e' destinatario
     *
     * @param app  applicazione
     * @param view view
     * @param dest utente destinatario
     */
    public static void manageExchanges(@NotNull Applicazione app, @NotNull View view, Fruitore dest) {
        manage(app,
                view,
                "Non hai nessuna nuova proposta di scambio\n",
                "Le tue nuove proposte di scambio:",
                "Selezionare uno scambio da accettare dalla lista? [Y/N]",
                dest,
                Scambio::getDest);
    }

    /**
     * gestisce le offerte di scambio di cui l'utente e' autore
     *
     * @param app    applicazione
     * @param view   view
     * @param author utente autore
     */
    public static void manageOwnExchanges(@NotNull Applicazione app, @NotNull View view, Fruitore author) {
        manage(app,
                view,
                "\n\nNon hai nessun'altra proposta di scambio arretrata\n",
                "Le tue proposte di scambio arretrate:",
                "Selezionare uno scambio da gestire dalla lista? [Y/N]",
                author,
                Scambio::getAuthor);

    }

    /**
     * gestisce le offerte di scambio di un utente
     *
     * @param app               applicazione
     * @param view              view
     * @param noExchanges       messaggio che comunica che non ci sono offerte di scambio
     * @param existingExchanges messaggio che comunica che ci sono offerte di scambio
     * @param selectExchange    richiesta di selezione di uno scambio da una lista
     * @param f                 utente fruitore
     * @param predicate         predicato da applicare a un oggetto Scambio per selezionare gli scambi con un determinato autore o destinatario
     */
    private static void manage(@NotNull Applicazione app, View view, String noExchanges, String existingExchanges, String selectExchange, Fruitore f, Function<Scambio, Fruitore> predicate) {
        //gestione scambi validi
        var userExchanges = app
                .getValidExchanges(app.getScambi())
                .stream()
                .filter(e -> predicate.apply(e).equals(f))
                .collect(Collectors.toList());

        if (userExchanges.isEmpty())
            view.message(noExchanges);

        while (!userExchanges.isEmpty()) {
            view.message(existingExchanges);
            view.showList(userExchanges.stream().collect(Collectors.toList()));
            if (view.yesOrNoQuestion(selectExchange).equalsIgnoreCase("y")) {
                var toAccept = view.choose(userExchanges.stream().collect(Collectors.toList()), null);
                toAccept.manageExchange(view, app, f);
            } else {
                return;
            }
        }
        //gestione scambi invalidi
        manageInvalidExchanges(app);
    }

    /**
     * gestisce le offerte di scambio invalide, cioe' scadute
     *
     * @param app
     */
    private static void manageInvalidExchanges(@NotNull Applicazione app) {
        app.getScambi()
                .stream()
                .filter(e -> !e.isValidExchange(app))
                .forEach(e -> {
                    app.getOfferta(e.ownOffer).setStato(Offerta.StatoOfferta.APERTA);
                    app.getOfferta(e.selectedOffer).setStato(Offerta.StatoOfferta.APERTA);
                    app.removeScambio(e);
                });
    }

    /**
     * @return stringa contenente una breve descrizione dello scambio da effettuare
     */
    public String toString() {
        return this.selectedOffer.getName() + " <--> " + this.ownOffer.getName();
    }

    /**
     * @return stringa contenente una breve descrizione dello scambio e l'ultimo messaggio ad esso relativo introdotto dall'autore
     * dello scambio stesso
     */
    public String getLastAuthorMsgString() {
        if (this.lastAuthorMsg.getMessage() == null)
            return "--";
        return (this.toString() + ": " + this.lastAuthorMsg.getMessage());
    }

    /**
     * visualizza l'ultimo messaggio da parte dell'autore di uno scambio
     *
     * @param f    autore
     * @param app  applicazione
     * @param view view
     */
    public static void viewLastMessageByAuthor(Fruitore f, @NotNull Applicazione app, @NotNull View view) {
        view.showList(app.getScambi()
                .stream()
                .filter(e -> e.getAuthor().equals(f))
                .collect(Collectors.toList()), Scambio::getLastAuthorMsgString
        );
    }

}
