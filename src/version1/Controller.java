package version1;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


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

    public void secondAccessAsConfiguratore(String username) throws IOException {
        boolean auth = false;

        if (dataStore.isUsernameTaken(username)) {
            auth = dataStore.isLoginCorrect(username, view.askPassword());

            if (auth) {
                this.useAsConfiguratore();
            } else {
                view.wrongPasswordError();

                int choice = view.printMenuConfiguratore();

                switch (choice) {
                    case 1: {
                        this.firstAccessAsConfiguratore();
                    }
                    break;
                    case 2: {
                        this.secondAccessAsConfiguratore(username);
                    }
                    break;
                    default:
                        view.illicitChoice();
                }
            }
        } else {
            view.wrongUsernameError();
        }


    }

    private void useAsConfiguratore() throws IOException {
        boolean end = false;
        int choice = 0;
        do {
            var db = new File("./db");
            assert db.exists() || db.mkdir();

            var gf = new File("./db/gerarchie.dat");
            var uf = new File("./db/users.dat");
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
                        System.out.println("ERRORE: Il nome è già stato assegnato a un'altra categoria radice");
                    } else {
                        String descr = view.askDescription();
                        Foglia root = new Foglia(rootname, descr);
                        app.gerarchie.put(rootname, new Gerarchia(root));
                        generaCampiNativiRadice(root);
                        generaSottocategorie(root);
                    }

                }
                break;
                case 2: {
                    //visualizza gerarchie

                    for (String r : app.gerarchie.keySet()) {
                        System.out.println(app.gerarchie.get(r).toString());
                    }
                }
                break;
                case 3: {
                    //salva dati

                    FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/gerarchie.dat"));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(app.gerarchie);
                    objectOutputStream.close();
                    view.salvataggioEseguito();
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

    private void generaCampiNativiRadice(Categoria root) {
        CampoNativo statoConservazione = new CampoNativo(true, CampoNativo.Tipo.STRING);
        CampoNativo descrizioneLibera = new CampoNativo(false, CampoNativo.Tipo.STRING);
        HashMap<String, CampoNativo> campi = new HashMap<>();
        campi.put("Stato Conservazione", statoConservazione);
        campi.put("Descrizione Libera", descrizioneLibera);
        generaCampiNativi(root, null);
    }

    private void generaCampiNativi(@NotNull Categoria c, Categoria parent) {
        HashMap<String, CampoNativo> campi = new HashMap<>();

        String ans;
        do {
            do {
                System.out.println("Vuoi inserire un altro campo alla categoria " + c.getNome() + "? (Y/N)");
                ans = (new Scanner(System.in)).next();
            } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
            if (ans.equalsIgnoreCase("y")) {
                System.out.println("Nome campo: ");
                String nome = (new Scanner(System.in)).next();
                boolean obbligatorio;
                String ans2;
                do {
                    System.out.println("Campo a compilazione obbligatoria? (Y/N)");
                    ans2 = (new Scanner(System.in)).next();
                } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
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
    private boolean isNameTaken(Categoria root, String searchname) {
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

    private void generaSottocategorie(Categoria root) {
        String ans;
        do {
            System.out.println("Vuoi inserire (almeno 2) sottocategorie alla categoria " + root.getNome() + "? (Y/N)");
            ans = (new Scanner(System.in)).next();
        } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));

        if (ans.equalsIgnoreCase("y")) {
            ArrayList<Categoria> figlie = new ArrayList<>();
            addCategoriaWithoutDoubles(root, figlie);
            addCategoriaWithoutDoubles(root, figlie);

            //la categoria madre diventa un nodo
            Nodo nodo = new Nodo(root.getNome(), root.getDescrizione());
            app.gerarchie.put(root.getNome(), new Gerarchia(nodo));

            //chiede se inserire altre categorie figlie
            String ans2;
            do {

                do {
                    System.out.println("Inserire altre categorie figlie di " + root.getNome() + "? (Y/N)");
                    ans2 = (new Scanner(System.in)).next();
                } while (!ans2.equalsIgnoreCase("y") && !ans2.equalsIgnoreCase("n"));

                if (ans.equalsIgnoreCase("y")) {
                    addCategoriaWithoutDoubles(root, figlie);
                }
            } while (ans2.equalsIgnoreCase("y"));

            nodo.addCategorieFiglie(figlie);

            //per ognuna delle categorie figlie genera eventuali sottocategorie
            for (int i = 0; i < figlie.size(); i++) {
                generaSottocategorie(figlie.get(i));
            }
        }
    }

    /**
     * aggiunge una nuova categoria Foglia alle categorie figlie della categoria denominata rootname assicurandosi che
     * non ci siano altre categorie nella stessa gerarchia aventi lo stesso nome
     *
     * @param root   nome della categoria radice della gerarchia a cui apparterrà la nuova categoria e su cui fare il
     *               controllo di unicità del nome
     * @param figlie lista delle categorie figlie a cui aggiungere la nuova categoria
     */
    private void addCategoriaWithoutDoubles(Categoria root, ArrayList<Categoria> figlie) {
        String name1;
        do {
            name1 = view.askCategoryName();
            if (!isNameTaken(root, name1)) {
                Foglia f = new Foglia(name1, view.askDescription());
                figlie.add(f);
                generaCampiNativi(f, root);
            } else {
                System.out.println("Nome già presente nella gerarchia");
            }
        } while (isNameTaken(root, name1));
    }

    private void addNewConfiguratore() {
        String username;
        do {
            username = dataStore.generateRandomString(STD_USERNAME_LEN);
        } while (dataStore.isUsernameTaken(username));

        String password = dataStore.generateRandomPassword(STD_PW_LEN);

        view.communicateCredentials(username, password);

        dataStore.registerNewConfiguratore(username, password);
    }

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
            //dataStore.save(dataStore.getUserMap().get(username));
            dataStore.save();
        } else {
            view.credentialsError();
        }

    }

}
