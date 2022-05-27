package it.unibs.ingsw;

import java.io.*;
import java.security.NoSuchAlgorithmException;

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
     * segnala che lo username e' inesistente oppure che la password e' errata. In caso di credenziale errata permette di
     * scegliere se ritentare l'accesso con le proprie credenziali oppure creare un nuovo profilo configuratore.
     *
     * @throws IOException eccezione I/O
     */
    public void secondAccessAsConfiguratore(String username) throws IOException {
        boolean auth;

        if (dataStore.isUsernameTaken(username)) {
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
     * Permette di utilizzare l'applicazione come configuratore. Carica i dati salvati in modo permanente nell'utilizzo
     * precedente dell'applicazione; permette di selezionare un'azione da svolgere:
     * - creare una nuova gerarchia di categorie
     * - visualizzare le gerarchie attualmente presenti: per ogni gerarchia richiama il metodo toString() opportunamente
     * sovrascritto nella rispettiva classe
     * - salvare i dati: salva il contenuto delle gerarchie in modo permanente
     * - uscire
     *
     * @throws IOException eccezione I/O
     */
    private void useAsConfiguratore() throws IOException {
        boolean end = false;
        String choice = "0";
        do {
            //carica i dati salvati in precedenza
            app.prepareDirectoryStructure();

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
                    view.interactionMessage(View.InteractionMessage.SAVED_CORRECTLY);
                }
                break;
                case "4": {
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