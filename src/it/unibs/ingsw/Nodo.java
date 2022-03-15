package it.unibs.ingsw;

import java.util.ArrayList;
import java.util.List;

/**
 * Nodo: estende la classe {@link Categoria}.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Nodo extends Categoria {
    private List<Categoria> categorieFiglie;

    /**
     * Costruttore: inizializza la lista delle categorie figlie
     *
     * @param _nome        nome del nodo
     * @param _descrizione descrizione del nodo
     */
    public Nodo(String _nome, String _descrizione) {
        super(_nome, _descrizione);
        categorieFiglie = new ArrayList<>();
    }

    /**
     * @return lista delle categorie figlie
     */
    public List<Categoria> getCategorieFiglie() {
        return categorieFiglie;
    }

    /**
     * Rimuove una categoria dalla lista delle figlie
     *
     * @param child categoria figlia da rimuovere
     */
    public void removeChild(Categoria child) {
        this.categorieFiglie.remove(child);
    }

    /**
     * Aggiunge una categoria alla lista delle figlie
     *
     * @param child categoria figlia da aggiungere
     */
    public void addChild(Categoria child) {
        this.categorieFiglie.add(child);
    }

    /**
     * @return stringa descrittiva della categoria nodo
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (categorieFiglie.size() != 0) {
            sb.append("\n\nCategorie figlie di " + this.getNome() + ":");
            int i = 1;
            for (Categoria c : categorieFiglie) {
                sb.append("\n\n" + (i++) + ")");
                sb.append(c.toString());
            }
        }
        return sb.toString();
    }
}
