package version1;

import java.io.Serializable;
import java.util.*;

/**
 * Categoria: classe astratta, tiene traccia di nome, descrizione e campi nativi della categoria
 * N.B. Per "campi nativi" si intendono sia i campi nativi della categoria sia i campi che essa poi eventualmente
 * erediterà dalla categoria genitore. Sono stati chiamati tutti direttamente "campi nativi" per semplicità di
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
     * Ritorna la descrizione della categoria
     *
     * @return descrizione della categoria
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Ritorna il nome della categoria
     *
     * @return nome della categoria
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ritorna la mappa contenente i campi nativi
     *
     * @return mappa dei campi nativi
     */
    public Map<String, CampoNativo> getCampiNativi() {
        return campiNativi;
    }

    /**
     * Imposta la descrizione della categoria
     *
     * @param descrizione descrizione della categoria
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Imposta il nome della categoria
     *
     * @param nome nome della categoria
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Imposta i campi della categoria
     *
     * @param campi campi della categoria
     */
    public void setCampiNativi(Map<String, CampoNativo> campi) {

        this.campiNativi = campi;
    }

    /**
     * Override del metodo toString() per consentire la stampa del contenuto della categoria.
     *
     * @return stringa contenente nome, descrizione, elenco dei campi e obbligatorietà di ciascuno di essi
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
}
