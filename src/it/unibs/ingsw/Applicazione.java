package it.unibs.ingsw;

import java.util.*;

/**
 * Applicazione: gestisce una mappa che associa a ogni nome della categoria radice la propria gerarchia
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Applicazione {

    public Map<String, Gerarchia> gerarchie;

    /**
     * costruttore
     */
    public Applicazione() {
        gerarchie = new HashMap<>();
    }

    /**
     * Aggiunge una gerarchia alla mappa
     *
     * @param rootName  nome della radice della gerarchia
     * @param gerarchia gerarchia
     */
    public void addGerarchia(String rootName, Gerarchia gerarchia) {
        gerarchie.put(rootName, gerarchia);
    }

    /**
     * Ritorna true se esiste già una gerarchia con categoria radice chiamata name
     *
     * @param name nome della categoria radice di cui controllare l'esistenza
     * @return true se la mappa contiene già una gerarchia con categoria radice chiamata name
     */
    public boolean isHierarchyNameTaken(String name) {
        return gerarchie.containsKey(name);
    }

}
