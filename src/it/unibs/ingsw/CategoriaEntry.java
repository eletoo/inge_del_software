package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

/**
 * Classe di utilita' per associare un nome "completo" (in modo che includa il percorso dalla root) a una categoria e al suo padre.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class CategoriaEntry {

    private Categoria cat;
    private final Nodo father;
    private final String displayName;

    /**
     * Costruttore.
     */
    public CategoriaEntry(Categoria cat, Nodo father, String displayName) {
        this.cat = cat;
        this.father = father;
        this.displayName = displayName;
    }

    /**
     * @return la categoria.
     */
    public Categoria getCat() {
        return cat;
    }

    /**
     * @return la categoria padre.
     */
    public Nodo getFather() {
        return father;
    }

    /**
     * @return il nome che contiene il percorso da root alla categoria corrente.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Trasforma una categoria foglia in nodo copiandone i valori.
     *
     * @param c categoria da trasformare in nodo
     * @return categoria trasformata in nodo
     */
    public static @NotNull Nodo catAsNode(Categoria c) {
        if (c instanceof Nodo) return (Nodo) c;

        Nodo newcat = new Nodo(c.getNome(), c.getDescrizione());
        newcat.setCampiNativi(c.getCampiNativi());
        return newcat;
    }

    /**
     * Rimuove la categoria cat dal padre (se possibile) e la riaggiunge (se possibile) sotto forma di categoria nodo.
     *
     * @return categoria sotto forma di Nodo
     */
    public Nodo asNode() {
        if (this.father != null)
            this.father.removeChild(this.cat);
        this.cat = catAsNode(this.cat);
        if (this.father != null)
            this.father.addChild(this.cat);
        return (Nodo) this.cat;
    }
}
