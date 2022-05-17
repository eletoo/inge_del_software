package it.unibs.ingsw;

import com.google.gson.*;
import it.unibs.ingsw.exceptions.RequiredConstraintFailureException;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Applicazione: gestisce una mappa che associa a ogni nome della categoria radice la propria gerarchia,
 * un oggetto {@link InfoScambio} che contiene le informazioni di scambio, una lista di oggetti {@link Offerta}
 * che contiene le offerte presenti nell'applicazione e una lista di oggetti {@link Scambio} che contiene gli scambi
 * introdotti dagli utenti nell'applicazione.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Applicazione {

    public static final String DB_JSON_FILES = "./db/jsonFiles/";
    public static final String DB_JSON_CONF_FILE = "./db/conf/conf.json";
    public static final String DB_CONF_DIR = "./db/conf/";

    private Map<String, Gerarchia> hierarchies;
    private InfoScambio informazioni;
    private List<Offerta> offerte;
    private List<Scambio> scambi;

    /**
     * Costruttore.
     */
    public Applicazione() {
        hierarchies = new HashMap<>();
        offerte = new LinkedList<>();
        scambi = new LinkedList<>();
    }

    /**
     * Aggiunge una gerarchia alla mappa.
     *
     * @param rootName  nome della radice della gerarchia
     * @param gerarchia gerarchia
     */
    public void addGerarchia(String rootName, Gerarchia gerarchia) {
        hierarchies.put(rootName, gerarchia);
    }

    /**
     * Ritorna true se esiste gia' una gerarchia con categoria radice chiamata name.
     *
     * @param name nome della categoria radice di cui controllare l'esistenza
     * @return true se la mappa contiene già una gerarchia con categoria radice chiamata name
     */
    public boolean isHierarchyNameTaken(String name) {
        return hierarchies.containsKey(name);
    }

    /**
     * @return le gerarchie contenute nell'applicazione
     */
    public Map<String, Gerarchia> getHierarchies() {
        return hierarchies;
    }

    /**
     * Imposta la mappa delle gerarchie con il contenuto della mappa passata come parametro.
     *
     * @param hierarchies mappa delle gerarchie con cui impostare le gerarchie dell'oggetto su cui è chiamato il metodo
     */
    public void setHierarchies(Map<String, Gerarchia> hierarchies) {
        this.hierarchies = hierarchies;
    }

    /**
     * Restituisce la sola gerarchia di nome rootname.
     *
     * @param rootname nome della cetegoria radice della gerarchia da cercare
     * @return la gerarchia la cui categoria radice ha nome rootname
     */
    public Gerarchia getHierarchy(String rootname) {
        return hierarchies.get(rootname);
    }

    /**
     * Salva in modo permanente il contenuto delle gerarchie.
     * Sovrascrive con l'attuale contenuto delle gerarchie il contenuto dei file .json utilizzabili per la
     * configurazione delle gerarchie
     *
     * @throws IOException eccezione I/O
     */
    public void saveData() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/gerarchie.dat"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.getHierarchies());
        objectOutputStream.close();
        for (Gerarchia h : this.getHierarchies().values()) {
            this.printHierarchyOnFile(h);
        }
    }

    /**
     * Carica il contenuto delle gerarchie salvato in modo permanente all'utilizzo precedente dell'applicazione.
     *
     * @throws IOException eccezione I/O
     */
    public void prepareDirectoryStructure() throws IOException {
        var db = new File("./db");
        assert db.exists() || db.mkdir();

        var gf = new File("./db/gerarchie.dat");
        if (gf.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(gf));
            try {
                this.setHierarchies((Map<String, Gerarchia>) ois.readObject());
            } catch (ClassNotFoundException | IOException e) {
                this.setHierarchies(new HashMap<>());

            }
        } else {
            this.setHierarchies(new HashMap<>());
        }

    }

    /**
     * Salva in modo persistente le informazioni relative a luoghi e orari per l'effettuazione degli scambi.
     * Sovrascrive il contenuto del file conf.json con il contenuto delle attuali informazioni di scambio.
     *
     * @throws IOException eccezione I/O
     */
    public void saveInfo() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("./db/info.dat"));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this.getInformazioni());
        oos.close();
        this.printInfoOnFile(this.getInformazioni());
    }

    /**
     * Importa il contenuto di un file di configurazione delle informazioni di scambio, comunicando un errore
     * qualora il file sia inesistente o nel formato errato
     *
     * @throws IOException eccezione
     */
    public void importInfoFromFile(View view) throws IOException {
        if(this.generatePathList(DB_CONF_DIR).size() != 1){
            //se c'è un numero di file di configurazione diverso da 1 segnala un errore
            view.errorMessage(View.ErrorMessage.E_WRONG_DIR_CONTENT);
            return;
        }

        Path p = Paths.get(DB_JSON_CONF_FILE);

        if (!p.toFile().exists()) {
            view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
            return;
        }

        Reader reader = Files.newBufferedReader(p);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        InfoScambio info = gson.fromJson(reader, InfoScambio.class);

        if (info.getPiazza().isEmpty() || info.getPiazza() == null) {
            view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
            return;
        }

        if (info.getIntervalliOrari().isEmpty()) {
            view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
            return;
        }

        if (info.getScadenza() <= 0) {
            view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
            return;
        }

        if (info.getGiorni().isEmpty()) {
            view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
            return;
        }

        if (info.getLuoghi().isEmpty()) {
            view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
            return;
        }

        this.setInfoScambio(info);

        this.saveInfo();
    }

    /**
     * Scrive le informazioni di scambio passate come parametro in un file .json
     *
     * @param info informazioni di scambio da salvare
     */
    private void printInfoOnFile(@NotNull InfoScambio info) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String file = DB_JSON_CONF_FILE;
        try (FileWriter wr = new FileWriter(file)) {
            wr.write(gson.toJson(info));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le informazioni relative a luoghi e orari per l'effettuazione degli scambi salvate in modo permanente
     * all'utilizzo precedente dell'applicazione
     *
     * @throws IOException eccezione I/O
     */
    public void prepareInfoStructure() throws IOException {
        var db = new File("./db");
        assert db.exists() || db.mkdir();

        var info = new File("./db/info.dat");
        if (info.exists()) {
            ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(info));
            try {
                this.setInfoScambio((InfoScambio) ois2.readObject());
            } catch (ClassNotFoundException | IOException e) {
                this.setInfoScambio(null);
            }
        } else {

            this.setInfoScambio(null);
        }
    }

    /**
     * Imposta le informazioni su luoghi e orari per l'effettuazione degli scambi
     *
     * @param info informazioni da impostare
     */
    public void setInfoScambio(InfoScambio info) {
        this.informazioni = info;
    }

    /**
     * @return informazioni su luoghi e orari per l'effettuazione degli scambi
     */
    public InfoScambio getInformazioni() {
        return this.informazioni;
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
    public void createNewHierarchy(@NotNull View view) throws IOException {
        String rootname = view.askCategoryName();
        if (this.isHierarchyNameTaken(rootname)) {
            view.errorMessage(View.ErrorMessage.E_EXISTING_ROOT_CATEGORY);
            return;
        }
        String descr = view.askDescription();
        Categoria root = new Foglia(rootname, descr);
        root.setCampiNativi(root.generaCampiNativi(null, view));

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
                cat.setCampiNativi(cat.generaCampiNativi(padre.getCat(), view));
                ((Nodo) padre.getCat()).addChild(cat);
            } else view.errorMessage(View.ErrorMessage.E_EXISTING_NAME_IN_HIERARCHY);
        }
        Gerarchia h = new Gerarchia(root);
        this.addGerarchia(rootname, h);
        if (view.yesOrNoQuestion("Salvare la gerarchia creata? [Y/N]").equalsIgnoreCase("y")) {
            this.saveData();
            view.interactionMessage(View.InteractionMessage.SAVED_CORRECTLY);
            return;
        }

        this.getHierarchies().remove(rootname);
    }

    /**
     * Scrive il contenuto di una gerarchia passata come parametro in un file .json
     *
     * @param hierarchy gerarchia da salvare
     */
    private void printHierarchyOnFile(@NotNull Gerarchia hierarchy) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(Categoria.class, (JsonSerializer<Categoria>) (categoria, type, jsonSerializationContext) -> {
            JsonObject ret = new JsonObject();
            ret.addProperty("nome", categoria.getNome());
            ret.addProperty("descrizione", categoria.getDescrizione());
            ret.add("campiNativi", jsonSerializationContext.serialize(categoria.getCampiNativi()));
            if (categoria instanceof Nodo)
                ret.add("figlie", jsonSerializationContext.serialize(((Nodo) categoria).getCategorieFiglie()));
            return ret;
        }).create();

        String file = DB_JSON_FILES + hierarchy.getRoot().getNome() + ".json";
        try (FileWriter wr = new FileWriter(file)) {
            wr.write(gson.toJson(hierarchy));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Importa da una lista di file .json il contenuto delle gerarchie da aggiungere all'applicazione, senza sovrascrivere
     * eventuali gerarchie omonime.
     *
     * @param view view
     * @throws IOException eccezione
     */
    public void importHierarchiesFromFile(View view) throws IOException {
        Gerarchia h;
        try {
            List<Path> paths = this.generatePathList(DB_JSON_FILES);
            Reader reader;

            if (paths.isEmpty()) {
                view.errorMessage(View.ErrorMessage.E_INVALID_FILE_CONTENT);
                return;
            }

            for (Path p : paths) {
                reader = Files.newBufferedReader(p);
                GsonBuilder builder = new GsonBuilder();
                JsonDeserializer<Categoria> categoria_deserializer = (json, typeOfT, context) -> {
                    JsonObject jsonObject = json.getAsJsonObject();
                    if (jsonObject.get("figlie") != null && jsonObject.get("figlie").isJsonArray() && jsonObject.get("figlie").getAsJsonArray().size() > 1)
                        return context.deserialize(jsonObject, Nodo.class);
                    return context.deserialize(jsonObject, Foglia.class);
                };

                JsonDeserializer<Nodo> nodo_deserializer = (json, typeOfT, context) -> {
                    JsonObject jsonObject = json.getAsJsonObject();
                    var nodo = new Nodo(jsonObject.get("nome").getAsString(), jsonObject.get("descrizione").getAsString());
                    var cn = new HashMap<String, CampoNativo>();

                    var o = jsonObject.get("campiNativi").getAsJsonObject();
                    o.keySet().forEach(e -> cn.put(e, context.deserialize(o.get(e), CampoNativo.class)));
                    nodo.setCampiNativi(cn);
                    jsonObject.get("figlie").getAsJsonArray().forEach(e -> nodo.addChild(context.deserialize(e, Categoria.class)));
                    return nodo;
                };

                JsonDeserializer<Foglia> foglia_deserializer = (json, typeOfT, context) -> {
                    JsonObject jsonObject = json.getAsJsonObject();
                    var f = new Foglia(jsonObject.get("nome").getAsString(), jsonObject.get("descrizione").getAsString());
                    var cn = new HashMap<String, CampoNativo>();

                    var o = jsonObject.get("campiNativi").getAsJsonObject();
                    o.keySet().forEach(e -> cn.put(e, context.deserialize(o.get(e), CampoNativo.class)));
                    f.setCampiNativi(cn);
                    return f;
                };

                Gson gson = builder
                        .registerTypeAdapter(Categoria.class, categoria_deserializer)
                        .registerTypeAdapter(Nodo.class, nodo_deserializer)
                        .registerTypeAdapter(Foglia.class, foglia_deserializer)
                        .create();

                h = gson.fromJson(reader, Gerarchia.class);

                boolean valid = true;
//todo: non funziona perché isNameTaken ritorna sempre true
                if (h.getRoot() instanceof Nodo)
                    for (Categoria c : ((Nodo) h.getRoot()).getCategorieFiglie()) {
                        if (h.getRoot().isNameTaken(c.getNome())) {
                            valid = false;
                            break;
                        }
                    }

                if (!this.isHierarchyNameTaken(h.getRoot().getNome()) && valid) {//non sovrascrive nell'applicazione gerarchie omonime già esistenti
                    this.hierarchies.put(h.getRoot().getNome(), h);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.saveData();
        return;
    }

    /**
     * Genera una lista di oggetti {@link Path} da cui ricavare i file .json da leggere
     *
     * @param dir directory
     * @return lista di {@link Path}
     */
    @Contract(pure = true)
    private @NotNull List<Path> generatePathList(String dir) {
        List<Path> path_list = new LinkedList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(DB_JSON_FILES))) {

            path_list = walk
                    .filter(Files::isRegularFile)
                    .filter(e -> e.toString().endsWith(".json"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path_list;
    }

    /**
     * Carica le informazioni relative alle offerte di scambio salvate in modo permanente all'utilizzo precedente
     * dell'applicazione
     *
     * @throws IOException eccezione I/O
     */
    public void prepareOffersStructure() throws IOException {
        var db = new File("./db");
        assert db.exists() || db.mkdir();

        var of = new File("./db/offerte.dat");
        if (of.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(of));
            try {
                this.setOfferte((List<Offerta>) ois.readObject());
            } catch (ClassNotFoundException | IOException e) {
                this.setOfferte(new LinkedList<>());
            }
        } else {
            this.setOfferte(new LinkedList<>());
        }
    }


    /**
     * @return offerte
     */
    public List<Offerta> getOfferte() {
        return offerte;
    }


    /**
     * @param offerte lista di offerte con cui impostare il valore del campo offerte
     */
    public void setOfferte(List<Offerta> offerte) {
        this.offerte = offerte;
    }

    /**
     * @param off offerta da restituire
     * @return offerta cercata all'interno dell'applicazione
     */
    public Offerta getOfferta(Offerta off) {
        if (this.offerte.contains(off))
            return this.offerte.get(this.offerte.indexOf(off));
        throw new RuntimeException("ERROR"); //should not happen
    }

    /**
     * Restituisce la lista di offerte associate a una categoria foglia
     *
     * @param categoria categoria di cui si ricercano le offerte
     * @return lista di offerte associate alla categoria passata come parametro
     */
    public List<Offerta> getOfferte(Foglia categoria) {
        return this.offerte.stream().filter(e -> e.getCategoria().equals(categoria)).collect(Collectors.toList());
    }

    /**
     * Restituisce la lista di offerte associate a un utente fruitore
     *
     * @param utente utente di cui si ricercano le offerte
     * @return lista di offerte associate all'utente passato come parametro
     */
    public List<Offerta> getOfferte(Fruitore utente) {
        return this.offerte.stream().filter(e -> e.getProprietario().equals(utente)).collect(Collectors.toList());
    }

    /**
     * Restituisce la lista di offerte associate a un utente fruitore e aventi uno specifico stato
     *
     * @param utente utente di cui si cercano le offerte
     * @param stato  stato delle offerte da restituire
     * @return lista di offerte dell'utente passato come parametro aventi lo stato cercato
     */
    public List<Offerta> getOfferte(Fruitore utente, Offerta.StatoOfferta stato) {
        return this.offerte
                .stream()
                .filter(e -> e.getProprietario().equals(utente))
                .filter(e -> e.getStato().equals(stato))
                .collect(Collectors.toList());
    }

    /**
     * Aggiunge l'offerta passata come parametro alle offerte, assicurandosi che siano presenti tutti i campi obbligatori
     *
     * @param o offerta da aggiungere alle offerte dell'applicazione
     * @throws RequiredConstraintFailureException eccezione: non e' presente un campo che era obbligatorio
     */
    public void addOfferta(@NotNull Offerta o) throws RequiredConstraintFailureException {
        //controllo campi obbligatori
        for (var field : o.getCategoria().getCampiNativi().entrySet()) {
            if (field.getValue().isObbligatorio() && !o.getValoreCampi().containsKey(field.getKey()))
                throw new RequiredConstraintFailureException();
        }

        this.offerte.add(o);
    }

    /**
     * Salva in modo permanente in un file le offerte introdotte nell'applicazione
     *
     * @throws IOException eccezione I/O
     */
    public void saveOfferte() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/offerte.dat"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.getOfferte());
        objectOutputStream.close();
    }

    /**
     * @param scambi scambi salvati nell'applicazione
     * @return scambi validi (non ancora scaduti)
     */
    public List<Scambio> getValidExchanges(@NotNull List<Scambio> scambi) {
        return scambi.stream().filter(e -> e.isValidExchange(this)).collect(Collectors.toList());
    }

    /**
     * @param s scambi con cui inizializzare il campo scambi
     */
    @Contract(mutates = "this")
    private void setScambi(List<Scambio> s) {
        this.scambi = s;
    }

    /**
     * Carica il contenuto del file salvato contenente la lista degli scambi salvati in precedenza
     *
     * @throws IOException eccezione
     */
    public void prepareExchangesStructure() throws IOException {
        var db = new File("./db");
        assert db.exists() || db.mkdir();

        var exchanges = new File("./db/scambi.dat");
        if (exchanges.exists()) {
            ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(exchanges));
            try {
                this.setScambi((List<Scambio>) ois2.readObject());
            } catch (ClassNotFoundException | IOException e) {
                this.setScambi(new LinkedList<>());
            }
        } else {
            this.setScambi(new LinkedList<>());
        }
    }

    /**
     * Salva le proposte di scambio
     *
     * @throws IOException eccezione
     */
    public void saveExchanges() throws IOException {
        this.saveOfferte();
        FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/scambi.dat"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.getScambi());
        objectOutputStream.close();
    }

    /**
     * @return scambi
     */
    public List<Scambio> getScambi() {
        return scambi;
    }

    /**
     * @param s scambio da aggiungere alla lista
     */
    public void addScambio(Scambio s) {
        scambi.add(s);
    }

    /**
     * @param s scambio da rimuovere dalla lista
     */
    public void removeScambio(Scambio s) {
        scambi.remove(s);
    }


}
