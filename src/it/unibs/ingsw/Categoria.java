package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 * Categoria: classe astratta, tiene traccia di nome, descrizione e campi nativi della categoria.
 * N.B. Per "campi nativi" si intendono sia i campi nativi della categoria sia i campi che essa poi eventualmente
 * ereditera' dalla categoria genitore. Sono stati chiamati tutti direttamente "campi nativi" per semplicita' di
 * comprensione
 */
public abstract class Categoria implements Serializable {

    private String nome;
    private String descrizione;
    private Map<String, CampoNativo> campiNativi;

    /**
     * Costruttore, inizializza la mappa che tiene traccia dei campi nativi
     *
     * @param _nome        nome della categoria
     * @param _descrizione descrizione della categoria
     */
    public Categoria(String _nome, String _descrizione) {
        this.nome = _nome;
        this.descrizione = _descrizione;
        campiNativi = new HashMap<>();
    }

    /**
     * @return descrizione della categoria
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @return nome della categoria
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return mappa dei campi nativi
     */
    public Map<String, CampoNativo> getCampiNativi() {
        return campiNativi;
    }

    /**
     * @param descrizione descrizione da dare alla categoria
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * @param nome nome da dare alla categoria
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param campi campi da assegnare alla categoria
     */
    public void setCampiNativi(Map<String, CampoNativo> campi) {

        this.campiNativi = campi;
    }

    /**
     * Override del metodo toString() per consentire la stampa del contenuto della categoria.
     *
     * @return stringa contenente nome, descrizione, elenco dei campi e obbligatorieta' di ciascuno di essi
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nNome: " + this.nome);
        sb.append("\nDescrizione: " + this.descrizione);
        sb.append("\nCampi nativi:");
        for (String n : campiNativi.keySet()) {
            sb.append("\n-> " + n);
            sb.append(campiNativi.get(n).isObbligatorio() ? " (Obbligatorio)" : " (Falcotativo)");
        }
        return sb.toString();
    }

    /**
     * Ritorna true se e solo se il nome passato a' presente all'interno della struttura della categoria.
     *
     * @param name nome
     * @return true se il nome è già stato utilizzato
     */
    public boolean isNameTaken(String name) {
        return isNameTaken(this, name);
    }

    /**
     * Funzione ricorsiva che computa per ogni livello se il nome e' stato utilizzato o meno.
     *
     * @param c    categoria in esame
     * @param name nome
     * @return true se name e' il nome della categoria o di uno dei suoi figli
     */
    private static boolean isNameTaken(@NotNull Categoria c, @NotNull String name) {
        if (name.equals(c.nome))
            return true;

        if (c instanceof Nodo)
            for (var child : ((Nodo) c).getCategorieFiglie())
                if (isNameTaken(child, name))
                    return true;
        return false;
    }

    /**
     * @return true se le condizioni di struttura sono rispettate (se ha dei figli devono essercene almeno due)
     */
    public boolean isStructureValid() {
        return isStructureValid(this);
    }

    /**
     * Funzione ricorsiva per la validazione della struttura di una categoria.
     *
     * @param c categoria di cui validare la struttura
     * @return true se e' una foglia o se e' un nodo con almeno due figli con struttura valida
     */
    private static boolean isStructureValid(Categoria c) {
        if (c instanceof Foglia)
            return true;

        if (c instanceof Nodo) {
            if (((Nodo) c).getCategorieFiglie().size() < 2)
                return false;
            for (var child : ((Nodo) c).getCategorieFiglie())
                if (!isStructureValid(child))
                    return false;
        }
        return true;
    }
}
