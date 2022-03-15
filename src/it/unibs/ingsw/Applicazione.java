package it.unibs.ingsw;

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

}