package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Controller: mette in comunicazione Model (ossia la classe {@link UserDataStore}) e {@link View} per rispettare il modello MVC.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Controller {

    public static final int STD_USERNAME_LEN = 10;
    public static final int STD_PW_LEN = 10;
    public UserDataStore dataStore;
    private View view = new View();
    private Applicazione app = new Applicazione();

    /**
     * Costruttore.
     */
    public Controller() {
        try {
            dataStore = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il primo accesso come configuratore: richiama il metodo che aggiunge un nuovo profilo configuratore,
     * richiede l'autenticazione tramite username e password predefiniti, chiama il metodo che permette di modificare
     * le credenziali dell'utente e il metodo che permette al configuratore di accedere alle funzionalita' dell'applicazione.
     *
     * @throws IOException eccezione I/O
     */
    public void firstAccessAsConfiguratore() throws IOException {
        this.addNewConfiguratore();
        boolean auth;
        String username;
        do {
            username = view.askUsername();
            auth = dataStore.isLoginCorrect(username, view.askPassword());

            if (auth) {
                this.customizeConfiguratore(username);
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
                this.redoAccess();
            }
        } else {
            view.errorMessage(View.ErrorMessage.E_UNREGISTERED_USER);
            this.redoAccess();
        }


    }

    /**
     * Permette all'utente o di creare un nuovo profilo oppure di ritentare l'accesso con le proprie credenziali.
     *
     * @throws IOException eccezione I/O
     */
    public void redoAccess() throws IOException {
        String choice = view.chooseFromMenuConfiguratore();
        switch (choice) {
            case "1": {
                this.firstAccessAsConfiguratore();
            }
            break;
            case "2": {
                this.secondAccessAsConfiguratore(view.askUsername());
            }
            break;
            default:
                view.errorMessage(View.ErrorMessage.E_ILLICIT_CHOICE);
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

                    this.createNewHierarchy();
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

    /**
     * Crea una nuova gerarchia: chiede il nome della radice, verifica che non esista una gerarchia con radice omonima,
     * chiede la descrizione e imposta i campi nativi della radice.
     * Se la struttura della categoria creata non e' valida oppure l'utente vuole aggiungere delle sottocategorie
     * viene chiesto all'utente a che categoria aggiungere sottocategorie (almeno due) e nome, descrizione ed eventuali
     * nuovi campi nativi per ognuna di esse.
     *
     * @throws IOException eccezione I/O
     */
    private void createNewHierarchy() throws IOException {
        String rootname = view.askCategoryName();
        if (app.isHierarchyNameTaken(rootname)) {
            view.errorMessage(View.ErrorMessage.E_EXISTING_ROOT_CATEGORY);
            return;
        }
        String descr = view.askDescription();
        Categoria root = new Foglia(rootname, descr);
        root.setCampiNativi(generaCampiNativi(root, null));

        //Se la struttura non è valida l'utente dovrà proseguire nell'aggiunta al fine di renderla tale (oppure se ha sbagliato ricomincia)
        //altrimenti chiediamo se vuole aggiungere una categoria
        while (!root.isStructureValid() || view.yesOrNoQuestion("Aggiungere una nuova categoria? [Y/N]").equalsIgnoreCase("y")) {
            view.interactionMessage(View.InteractionMessage.AT_LEAST_TWO_CHILDREN);
            CategoriaEntry padre = view.findCategory(root); //Prompt per l'utente in modo che scelga una categoria

            if (padre.getCat() == root) //se la categoria è la root (stessa istanza)
                root = padre.asNode(); //setto la root uguale a se stessa ma come nodo
            else {
                padre.asNode(); //faccio sì che la categoria scelta diventi nodo e venga aggiornato il padre.
            }

            String name = view.askCategoryName();
            if (!root.isNameTaken(name)) {
                String desc = view.askDescription();
                var cat = new Foglia(name, desc);
                cat.setCampiNativi(generaCampiNativi(cat, padre.getCat()));
                ((Nodo) padre.getCat()).addChild(cat);
            } else
                view.errorMessage(View.ErrorMessage.E_EXISTING_NAME_IN_HIERARCHY);
        }

        app.addGerarchia(rootname, new Gerarchia(root));
        if (view.yesOrNoQuestion("Salvare la gerarchia creata?").equalsIgnoreCase("y")) {
            app.saveData();
            view.interactionMessage(View.InteractionMessage.SAVED_CORRECTLY);
        }
    }

    /**
     * Restituisce i campi nativi da assegnare alla categoria radice dotandoli di opportuni valori che ne indicano
     * la compilazione obbligatoria o meno.
     *
     * @return campi da assegnare alla categoria radice
     */
    private @NotNull Map<String, CampoNativo> generaCampiNativiRadice() {
        CampoNativo statoConservazione = new CampoNativo(true, CampoNativo.Tipo.STRING);
        CampoNativo descrizioneLibera = new CampoNativo(false, CampoNativo.Tipo.STRING);
        Map<String, CampoNativo> campi = new HashMap<>();
        campi.put("Stato Conservazione", statoConservazione);
        campi.put("Descrizione Libera", descrizioneLibera);
        return campi;
    }

    /**
     * Genera i campi nativi (chiedendone nome e obbligatorieta') da aggiungere alla categoria c e aggiunge quelli che
     * essa eredita dalla categoria parent
     *
     * @param c      categoria a cui aggiungere i campi nativi ed ereditati
     * @param parent categoria parent da cui ereditare i campi
     * @return campi nativi
     */
    private @NotNull Map<String, CampoNativo> generaCampiNativi(@NotNull Categoria c, Categoria parent) {
        Map<String, CampoNativo> campi = new HashMap<>();

        if (parent == null) {
            campi.putAll(generaCampiNativiRadice());
        } else {
            campi.putAll(parent.getCampiNativi());
        }

        String ans;
        do {
            ans = view.yesOrNoQuestion("Inserire un altro campo descrittivo alla categoria " + c.getNome() + "? (Y/N)");

            if (ans.equalsIgnoreCase("y")) {
                String nome = view.insertFieldName();
                boolean obbligatorio;
                String ans2 = view.yesOrNoQuestion("Campo a compilazione obbligatoria? (Y/N)");

                if (ans2.equalsIgnoreCase("y")) {
                    obbligatorio = true;
                } else {
                    obbligatorio = false;
                }
                CampoNativo nuovo = new CampoNativo(obbligatorio, CampoNativo.Tipo.STRING);
                campi.put(nome, nuovo);
            }
        } while (!ans.equalsIgnoreCase("n"));

        return campi;
    }

    /**
     * Genera una stringa casuale da comunicare all'utente come username assicurandosi che non ci sia un utente omonimo
     * gia' registrato. Genera una stringa casuale da usare come password. Comunica le credenziali all'utente e invoca
     * un metodo per registrarlo nel dataStore
     */
    private void addNewConfiguratore() {
        String username;
        do {
            username = dataStore.generateRandomString(STD_USERNAME_LEN);
        } while (dataStore.isUsernameTaken(username));

        String password = dataStore.generateRandomPassword(STD_PW_LEN);

        view.communicateCredentials(username, password);

        dataStore.registerNewConfiguratore(username, password);
    }

    /**
     * Permette all'utente configuratore di nome currentUsername di modificare le proprie credenziali, assicurandosi che
     * il nuovo username custom non sia gia' usato da altri utenti.
     *
     * @param currentUsername username corrente dell'utente configuratore
     */
    public void customizeConfiguratore(String currentUsername) {
        view.interactionMessage(View.InteractionMessage.CUSTOMIZE_CREDENTIALS);
        String username;
        do {
            username = view.askNewUsername();
            if (dataStore.isUsernameTaken(username)) {
                view.errorMessage(View.ErrorMessage.E_USERNAME_TAKEN);
            }
        } while (dataStore.isUsernameTaken(username));

        String password = view.askCustomPassword();

        if (password != null && username != null) {
            dataStore.updateUser(currentUsername, username, password);
            dataStore.save();
        } else {
            view.errorMessage(View.ErrorMessage.E_CREDENTIALS_ERROR);
        }

    }


}