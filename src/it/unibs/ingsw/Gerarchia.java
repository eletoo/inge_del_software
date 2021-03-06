package it.unibs.ingsw;

import java.io.Serializable;

/**
 * Gerarchia: classe che tiene traccia della categoria radice di una gerarchia
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Gerarchia implements Serializable {
    private Categoria root;

    /**
     * Costruttore.
     */
    public Gerarchia(Categoria root) {
        this.root = root;
    }

    /**
     * @return categoria radice
     */
    public Categoria getRoot() {
        return root;
    }

    /**
     * @return stringa descrittiva della gerarchia
     */
    public String toString() {
        return "\nGerarchia: " + root.getNome() + "\nDescrizione: " + root.getDescrizione();
    }

}
