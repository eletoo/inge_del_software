package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

/**
 * Classe di utilità per assocuiare un nome "completo" (in modo che includa il percorso dalla root) a una categoria e al suo padre.
 */
public class CategoriaEntry {

    private Categoria cat;
    private final Nodo father;
    private final String displayName;

    public CategoriaEntry(Categoria cat, Nodo father, String displayName) {
        this.cat = cat;
        this.father = father;
        this.displayName = displayName;
    }

    public Categoria getCat() {
        return cat;
    }

    public Nodo getFather() {
        return father;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * trasforma una categoria foglia in nodo copiandone i valori.
     * @param c
     * @return
     */
    public static @NotNull Nodo catAsNode(Categoria c) {
        if (c instanceof Nodo) return (Nodo) c;

        Nodo newcat = new Nodo(c.getNome(), c.getDescrizione());
        newcat.setCampiNativi(c.getCampiNativi());
        return newcat;
    }

    /**
     * rimuove la categoria cat dal padre (se possibile) e lo riaggiunge (se possibile) sotto forma di categoria nodo.
     * @return
     */
    public Nodo asNode(){
        if(this.father != null)
            this.father.removeChild(this.cat);
        this.cat = catAsNode(this.cat);
        if(this.father != null)
           this.father.addChild(this.cat);
        return (Nodo) this.cat;
    }
}
