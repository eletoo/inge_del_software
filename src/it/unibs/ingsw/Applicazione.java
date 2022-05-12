package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * Applicazione: gestisce una mappa che associa a ogni nome della categoria radice la propria gerarchia.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Applicazione {

    private Map<String, Gerarchia> hierarchies;

    /**
     * Costruttore.
     */
    public Applicazione() {
        hierarchies = new HashMap<>();
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
     * Salva in modo permanente il contenuto delle gerarchie
     *
     * @throws IOException eccezione I/O
     */
    public void saveData() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/gerarchie.dat"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.getHierarchies());
        objectOutputStream.close();
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
        } else
            this.setHierarchies(new HashMap<>());
    }

    /**
     * Crea una nuova gerarchia: chiede il nome della radice, verifica che non esista una gerarchia con radice omonima,
     * chiede la descrizione e imposta i campi nativi della radice.
     * Se la struttura della categoria creata non e' valida oppure l'utente vuole aggiungere delle sottocategorie
     * viene chiesto all'utente a che categoria aggiungere sottocategorie (almeno due) e nome, descrizione ed eventuali
     * nuovi campi nativi per ognuna di esse.
     *
     * @param view view
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
            } else
                view.errorMessage(View.ErrorMessage.E_EXISTING_NAME_IN_HIERARCHY);
        }

        this.addGerarchia(rootname, new Gerarchia(root));
        if (view.yesOrNoQuestion("Salvare la gerarchia creata? [Y/N]").equalsIgnoreCase("y")) {
            this.saveData();
            view.interactionMessage(View.InteractionMessage.SAVED_CORRECTLY);
            return;
        }

        this.getHierarchies().remove(rootname);

    }
}
