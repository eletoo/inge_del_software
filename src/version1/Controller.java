package version1;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Controller: mette in comunicazione Model (ossia la classe UserDataStore) e View per rispettare il modello MVC
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Controller {

    public static final int STD_USERNAME_LEN = 10;
    public static final int STD_PW_LEN = 10;
    public UserDataStore dataStore;
    private View view = new View();
    private Applicazione app = new Applicazione();

    {
        try {
            dataStore = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il primo accesso come configuratore: richiama il metodo che aggiunge un nuovo profilo configuratore,
     * richiede l'autenticazione tramite username e password predefiniti, chiama il metodo che permette di modificare
     * le credenziali dell'utente e il metodo che permette al configuratore di accedere alle funzionalità dell'applicazione.
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
                this.modifyConfiguratore(username);
                this.useAsConfiguratore();
            } else {
                view.credentialsError();
            }
        } while (!auth);
    }

    /**
     * Permette a un utente configuratore di effettuare il login senza creare un nuovo profilo.
     * Se lo username e la password sono corretti permette di accedere alle funzionalità dell'applicazione, altrimenti
     * segnala che lo username è inesistente oppure che la password è errata. In caso di password errata permette di
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
                view.wrongPasswordError();
                riproponiAccesso();
            }
        } else {
            view.wrongUsernameError();
            riproponiAccesso();
        }


    }

    public void riproponiAccesso() throws IOException {
        int choice = view.printMenuConfiguratore();
        switch (choice) {
            case 1: {
                this.firstAccessAsConfiguratore();
            }
            break;
            case 2: {
                this.secondAccessAsConfiguratore(view.askUsername());
            }
            break;
            default:
                view.illicitChoice();
        }
    }

    /**
     * Permette di utilizzare l'applicazione come configuratore. Carica i dati salvati in modo permanente nell'utilizzo
     * precedente dell'applicazione; permette di selezionare un'azione da svolgere:
     * - creare una nuova gerarchia di categorie: richiede il nome della radice (verificando sia unico nell'applicazione),
     * la descrizione della categoria e richiama i metodi che permettono di inizializzarne i campi nativi e creare
     * sottocategorie
     * - visualizzare le gerarchie attualmente presenti: per ogni gerarchia richiama il metodo toString() opportunamente
     * sovrascritto nella rispettiva classe
     * - salvare i dati: salva il contenuto delle gerarchie in modo permanente
     * - uscire
     *
     * @throws IOException eccezione I/O
     */
    private void useAsConfiguratore() throws IOException {
        boolean end = false;
        int choice = 0;
        do {
            var db = new File("./db");
            assert db.exists() || db.mkdir();

            var gf = new File("./db/gerarchie.dat");

            if (gf.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(gf));
                try {
                    app.gerarchie = (Map<String, Gerarchia>) ois.readObject();
                } catch (ClassNotFoundException | IOException e) {
                    app.gerarchie = new HashMap<>();
                }
            } else
                app.gerarchie = new HashMap<>();

            choice = view.selectConfiguratoreAction();
            switch (choice) {
                case 1: {
                    //crea una nuova gerarchia

                    String rootname = view.askCategoryName();
                    if (app.gerarchie.containsKey(rootname)) {
                        view.categoriaGiaEsistente();
                    } else {
                        String descr = view.askDescription();
                        Foglia root = new Foglia(rootname, descr);
                        app.gerarchie.put(rootname, new Gerarchia(root));
                        generaCampiNativiRadice(root);
                        generaSottocategorie(root, root);
                        if (view.yesOrNoQuestion("Salvare la gerarchia creata?").equalsIgnoreCase("y")) {
                            salvaDati();
                        }
                    }

                }
                break;
                case 2: {
                    //visualizza gerarchie

                    for (String r : app.gerarchie.keySet()) {
                        System.out.println(app.gerarchie.get(r).toString());
                        System.out.println(app.gerarchie.get(r).getRoot().toString());

                    }
                }
                break;
                case 3: {
                    //salva dati

                    salvaDati();
                }
                break;
                case 4: {
                    //esci

                    end = true;
                    view.arrivederci();
                }
                break;
                default:
                    view.illicitChoice();
            }
        } while (!end);
    }

    private void salvaDati() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/gerarchie.dat"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(app.gerarchie);
        objectOutputStream.close();
        view.salvataggioEseguito();
    }

    /**
     * Aggiunge alla categoria radice i campi nativi "Stato conservazione" e "Descrizione libera" dotandoli di opportuni
     * campi che ne indicano la compilazione obbligatoria o meno. Richiama il metodo che permette di aggiungere altri
     * campi nativi e di ereditare quelli della categoria parent (in questo caso null perché è la radice della gerarchia)
     *
     * @param root categoria radice della gerarchia
     */
    private void generaCampiNativiRadice(Categoria root) {
        CampoNativo statoConservazione = new CampoNativo(true, CampoNativo.Tipo.STRING);
        CampoNativo descrizioneLibera = new CampoNativo(false, CampoNativo.Tipo.STRING);
        HashMap<String, CampoNativo> campi = new HashMap<>();
        campi.put("Stato Conservazione", statoConservazione);
        campi.put("Descrizione Libera", descrizioneLibera);
        root.setCampiNativi(campi);
        generaCampiNativi(root, null);

    }

    /**
     * Genera i campi nativi (chiedendone nome e obbligatorietà) da aggiungere alla categoria c e aggiunge quelli che
     * essa eredita dalla categoria parent
     *
     * @param c      categoria a cui aggiungere i campi nativi ed ereditati
     * @param parent categoria parent da cui ereditare i campi
     */
    private void generaCampiNativi(@NotNull Categoria c, Categoria parent) {
        HashMap<String, CampoNativo> campi = new HashMap<>();

        String ans;
        do {
            ans = view.yesOrNoQuestion("Inserire un altro campo alla categoria " + c.getNome() + "? (Y/N)");

            if (ans.equalsIgnoreCase("y")) {
                String nome = view.inserisciNomeCampo();
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

        if (parent != null) {
            campi.putAll(parent.getCampiNativi());
        }

        c.setCampiNativi(campi);
    }

    /**
     * verifica se il nome della categoria da aggiungere è già presente all'interno della gerarchia di appartenenza della
     * nuova categoria
     *
     * @param root       nome della categoria parent
     * @param searchname nome della categoria da aggiungere alla gerarchia
     * @return true se il nome della nuova categoria è già usato all'interno della gerarchia
     */
    private boolean isNameTaken(@NotNull Categoria root, String searchname) {
        if (root.getNome().equalsIgnoreCase(searchname)) {
            return true;
        }
        if (root instanceof Nodo) {
            for (Categoria child : ((Nodo) root).getCategorieFiglie()) {
                if (isNameTaken(child, searchname)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Se l'utente lo richiede, aggiunge almeno due sottocategorie a quella passata come parametro, assicurandosi che
     * non ci siano omonime nella stessa gerarchia. Per ognuna delle categorie figlie così generate invoca ricorsivamente
     * se stesso per eventualmente generare sue sottocategorie.
     *
     * @param root     categoria di cui si vogliono generare le sottocategorie
     * @param treeRoot categoria radice della gerarchia di appartenenza di root
     */
    private void generaSottocategorie(@NotNull Categoria root, Categoria treeRoot) {
        String ans = view.yesOrNoQuestion("Vuoi inserire (almeno 2) sottocategorie alla categoria " + root.getNome() + "? (Y/N)");

        if (ans.equalsIgnoreCase("y")) {

            //la categoria madre diventa un nodo
            Nodo nodo = new Nodo(root.getNome(), root.getDescrizione());
            //app.gerarchie.put(root.getNome(), new Gerarchia(nodo));
            nodo.setCampiNativi(root.getCampiNativi());
            //i campi nativi vengono cancellati perché creiamo un nuovo oggetto nodo con campi nativi vuoti
            app.gerarchie.get(treeRoot.getNome()).replaceCategoria(root, nodo);

            ArrayList<Categoria> figlie = new ArrayList<>();
            figlie = addCategoriaWithoutDoubles(nodo, figlie);
            figlie = addCategoriaWithoutDoubles(nodo, figlie);

            //chiede se inserire altre categorie figlie
            while (view.yesOrNoQuestion("Inserire altre categorie figlie di " + nodo.getNome() + "? (Y/N)").equalsIgnoreCase("y")) {
                figlie = addCategoriaWithoutDoubles(nodo, figlie);
            }

            nodo.addCategorieFiglie(figlie);

            //per ognuna delle categorie figlie genera eventuali sottocategorie
            for (int i = 0; i < figlie.size(); i++) {
                generaSottocategorie(figlie.get(i), treeRoot);
            }
        }
    }

    /**
     * aggiunge una nuova categoria Foglia alle categorie figlie della categoria denominata rootname assicurandosi che
     * non ci siano altre categorie nella stessa gerarchia aventi lo stesso nome. Richiede che vengano anche impostati
     * i campi nativi della nuova categoria aggiunta.
     *
     * @param root   nome della categoria radice della gerarchia a cui apparterrà la nuova categoria e su cui fare il
     *               controllo di unicità del nome
     * @param figlie lista delle categorie figlie a cui aggiungere la nuova categoria
     */
    private ArrayList<Categoria> addCategoriaWithoutDoubles(Categoria root, ArrayList<Categoria> figlie) {
        String name1;
        do {
            name1 = view.askCategoryName();
            if (!isNameTaken(root, name1)) {
                Foglia f = new Foglia(name1, view.askDescription());
                figlie.add(f);
                generaCampiNativi(f, root);
            } else {
                view.nomeGiaPresenteNellaGerarchia();
            }
        } while (isNameTaken(root, name1));
        return figlie;
    }

    /**
     * Genera una stringa casuale da comunicare all'utente come username assicurandosi che non ci sia un utente omonimo
     * già registrato. Genera una stringa casuale da usare come password. Comunica le credenziali all'utente e invoca
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
     * il nuovo username custom non sia già usato da altri utenti.
     *
     * @param currentUsername username corrente dell'utente configuratore
     */
    public void modifyConfiguratore(String currentUsername) {
        view.modifyCredentials();
        String username;
        do {
            username = view.askNewUsername();
            if (dataStore.isUsernameTaken(username)) {
                view.usernameTakenError();
            }
        } while (dataStore.isUsernameTaken(username));

        String password = view.askCustomPassword();

        if (password != null && username != null) {
            dataStore.updateUser(currentUsername, username, password);
            dataStore.save();
        } else {
            view.credentialsError();
        }

    }

}
