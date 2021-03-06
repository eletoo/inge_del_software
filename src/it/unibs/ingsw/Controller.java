package it.unibs.ingsw;

import java.io.*;

/**
 * Controller: gestisce l'accesso degli utenti e l'interazione con l'applicazione mettendo in comunicazione {@link UserDataStore},
 * {@link View} e {@link Applicazione}
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Controller {

    public UserDataStore dataStore;
    private View view = new View();
    private Applicazione app = new Applicazione();


    /**
     * Costruttore.
     */
    public Controller() {
        dataStore = new UserDataStore();
    }

    /**
     * Gestisce il primo accesso come configuratore: richiama il metodo che aggiunge un nuovo profilo configuratore,
     * richiede l'autenticazione tramite username e password predefiniti, chiama il metodo che permette di modificare
     * le credenziali dell'utente e il metodo che permette al configuratore di accedere alle funzionalita' dell'applicazione.
     *
     * @throws IOException eccezione I/O
     */
    public void firstAccessAsConfiguratore() throws IOException {
        dataStore.addNewConfiguratore(view);
        boolean auth;
        String username;
        do {
            username = view.askUsername();
            auth = dataStore.isLoginCorrect(username, view.askPassword());

            if (auth) {
                dataStore.customizeConfiguratore(username, view);
                this.useAsConfiguratore();
            } else {
                view.errorMessage(View.ErrorMessage.E_CREDENTIALS_ERROR);
            }
        } while (!auth);
    }

    /**
     * Permette a un utente configuratore di effettuare il login senza creare un nuovo profilo.
     * Se lo username e la password sono corretti permette di accedere alle funzionalita' dell'applicazione, altrimenti
     * segnala che lo username e' inesistente oppure che la password e' errata.
     *
     * @throws IOException eccezione I/O
     */
    public void secondAccessAsConfiguratore(String username) throws IOException {
        boolean auth;

        if (dataStore.isUsernameTaken(username) && dataStore.getUserMap().get(username) instanceof Configuratore) {
            auth = dataStore.isLoginCorrect(username, view.askPassword());

            if (auth) {
                this.useAsConfiguratore();
            } else {
                view.errorMessage(View.ErrorMessage.E_WRONG_PASSWORD);
            }
        } else {
            view.errorMessage(View.ErrorMessage.E_UNREGISTERED_USER);
        }
    }

    /**
     * Gestisce il primo accesso come fruitore: richiede all'utente username e password personalizzati,
     * registra il nuovo profilo utente e permette l'utilizzo dell'applicazione.
     *
     * @throws IOException eccezione I/O
     */
    public void firstAccessAsFruitore() throws IOException {
        var user = dataStore.addedNewFruitore(view);
        if (user != null)
            this.useAsFruitore(user);
    }

    /**
     * Permette a un utente fruitore di effettuare il login senza creare un nuovo profilo.
     * Se lo username e la password sono corretti permette di accedere alle funzionalita' dell'applicazione, altrimenti
     * segnala che lo username e' inesistente oppure che la password e' errata.
     *
     * @throws IOException eccezione I/O
     */
    public void secondAccessAsFruitore(String username) throws IOException {
        boolean auth;

        if (dataStore.isUsernameTaken(username)) {
            auth = dataStore.isLoginCorrect(username, view.askPassword());

            if (auth && dataStore.getUserMap().get(username) instanceof Fruitore) {
                this.useAsFruitore((Fruitore) dataStore.getUserMap().get(username));
            } else {
                view.errorMessage(View.ErrorMessage.E_WRONG_PASSWORD);
            }
        } else {
            view.errorMessage(View.ErrorMessage.E_UNREGISTERED_USER);
        }
    }

    /**
     * Permette di utilizzare l'applicazione come fruitore. Carica i dati salvati in modo permanente nell'utilizzo
     * precedente dell'applicazione; permette di selezionare un'azione da svolgere:
     * - visualizzare nome e descrizione delle gerarchie presenti nel sistema e le informazioni relative a luoghi
     * e orari per l'effettuazione degli scambi
     * - visualizzare le offerte personali
     * - visualizzare le offerte per categoria
     * - creare un'offerta
     * - ritirare un'offerta
     * - creare una proposta di scambio
     * - visualizzare l'ultimo messaggio inviato dalla controparte in uno scambio
     * - uscire
     *
     * @param fruitore utente fruitore
     * @throws IOException eccezione I/O
     */
    private void useAsFruitore(Fruitore fruitore) throws IOException {
        boolean end = false;
        String choice = "0";

        //carica i dati salvati in precedenza
        this.prepareStructures();

        //gestisce le offerte di scambio prima ancora di selezionare altre azioni
        Scambio.manageExchanges(app, view, fruitore);
        Scambio.manageOwnExchanges(app, view, fruitore);

        do {
            //seleziona un'azione da compiere
            choice = view.selectFruitoreAction();
            switch (choice) {
                case "1": {
                    //visualizza contenuto gerarchie e informazioni applicazione

                    if (app.getHierarchies().isEmpty()) {
                        view.errorMessage(View.ErrorMessage.NO_HIERARCHIES_YET);
                    } else {
                        System.out.println("GERARCHIE:");
                    }
                    for (String r : app.getHierarchies().keySet()) {
                        System.out.println(app.getHierarchy(r).toString());
                    }
                    System.out.println();
                    if (app.getInformazioni() != null)
                        System.out.println(app.getInformazioni().toString());
                    else
                        view.errorMessage(View.ErrorMessage.NO_INFO_YET);
                }
                break;
                case "2": {
                    // visualizza offerte personali
                    Offerta.viewPersonalOffers(fruitore, app, view);
                }
                break;
                case "3": {
                    // visualizza offerte per categoria
                    Offerta.viewOffersByCategory(app, view);
                }
                break;
                case "4": {
                    // crea offerta
                    Offerta.createOffer(app, view, fruitore);
                }
                break;
                case "5": {
                    // ritira offerta
                    Offerta.undoOffer(app, view, fruitore);
                }
                break;
                case "6": {
                    //crea proposta di scambio
                    if (app.getHierarchies().isEmpty()) {
                        view.errorMessage(View.ErrorMessage.E_NO_CATEGORIES);
                        break;
                    }

                    if (app.getInformazioni() == null) {
                        view.errorMessage(View.ErrorMessage.E_NO_INFO);
                        break;
                    }

                    var sc = Scambio.createExchange(app, view, fruitore);
                    if (sc != null) {
                        app.addScambio(sc);
                        app.saveExchanges();
                    }
                }
                break;
                case "7": {
                    //visualizza l'ultimo messaggio inviato dall'autore dello scambio
                    Scambio.viewLastMessageByCounterpart(fruitore, app, view);
                }
                break;
                case "8": {
                    //esci
                    end = true;
                    view.interactionMessage(View.InteractionMessage.EXIT_MESSAGE);
                }
                break;
                default:
                    view.errorMessage(View.ErrorMessage.E_ILLICIT_CHOICE);
            }
        } while (!end);
    }

    /**
     * Prepara le strutture dati caricando eventuali dati gia' salvati
     *
     * @throws IOException eccezione
     */
    private void prepareStructures() throws IOException {
        app.prepareDirectoryStructure();
        app.prepareInfoStructure();
        app.prepareOffersStructure();
        app.prepareExchangesStructure();
    }

    /**
     * Permette di utilizzare l'applicazione come configuratore. Carica i dati salvati in modo permanente nell'utilizzo
     * precedente dell'applicazione; permette di selezionare un'azione da svolgere:
     * - creare una nuova gerarchia di categorie
     * - visualizzare le gerarchie attualmente presenti: per ogni gerarchia richiama il metodo toString() opportunamente
     * sovrascritto nella rispettiva classe
     * - salvare i dati: salva il contenuto delle gerarchie in modo permanente
     * - configurare le informazioni di scambio
     * - visualizzare le offerte per categoria
     * - visualizzare le offerte in scambio di una categoria foglia
     * - visualizzare le offerte chiuse di una categoria foglia
     * - configurare le gerarchie da file
     * - configurare le impostazioni di scambio da file
     * - uscire
     *
     * @throws IOException eccezione I/O
     */
    private void useAsConfiguratore() throws IOException {
        boolean end = false;
        String choice = "0";
        do {
            //carica i dati salvati in precedenza
            this.prepareStructures();

            choice = view.selectConfiguratoreAction();
            switch (choice) {
                case "1": {
                    //crea una nuova gerarchia
                    app.createNewHierarchy(view);
                }
                break;
                case "2": {
                    //visualizza gerarchie
                    for (String r : app.getHierarchies().keySet()) {
                        System.out.println(app.getHierarchy(r).toString());
                        System.out.println(app.getHierarchy(r).getRoot().toString());
                    }
                }
                break;
                case "3": {
                    //salva dati
                    app.saveData();
                    app.saveInfo();
                    app.saveOfferte();
                    app.saveExchanges();
                    view.interactionMessage(View.InteractionMessage.SAVED_CORRECTLY);
                }
                break;
                case "4": {
                    //configura informazioni di scambio
                    if (app.getInformazioni() == null) {
                        app.setInfoScambio(new InfoScambio(app, view));
                    } else {
                        view.interactionMessage(View.InteractionMessage.CURRENT_INFO);
                        System.out.println(app.getInformazioni().toString());
                        if (view.yesOrNoQuestion("\nSovrascrivere le informazioni di scambio presenti (N.B. La piazza non ?? modificabile)? [Y/N]")) {
                            app.setInfoScambio(new InfoScambio(app, view));
                        }
                    }
                    app.saveInfo();//se non modifico le informazioni di scambio e conf.json ?? corrotto/incompleto qui viene
                    //sovrascritto con le informazioni correnti complete e non modificate
                    view.interactionMessage(View.InteractionMessage.SAVED_CORRECTLY);
                }
                break;
                case "5": {
                    //mostra offerte per categoria
                    Offerta.viewOffersByCategory(app, view);
                }
                break;
                case "6": {
                    //visualizza offerte in scambio di una categoria foglia
                    Offerta.viewOffers(app, view, Offerta.StatoOfferta.IN_SCAMBIO);
                }
                break;
                case "7": {
                    //visualizza offerte chiuse di una categoria foglia
                    Offerta.viewOffers(app, view, Offerta.StatoOfferta.CHIUSA);
                }
                break;
                case "8": {
                    //configura le gerarchie da file
                    app.importHierarchiesFromFile(this.view);
                }
                break;
                case "9": {
                    //configura le impostazioni di scambio da file
                    app.importInfoFromFile(this.view);
                }
                break;
                case "10": {
                    //esci
                    end = true;
                    view.interactionMessage(View.InteractionMessage.EXIT_MESSAGE);
                }
                break;
                default:
                    view.errorMessage(View.ErrorMessage.E_ILLICIT_CHOICE);
            }
        } while (!end);
    }

}