package version1;

import java.io.Serializable;

public class Gerarchia implements Serializable {
    private Categoria root;

    public Gerarchia(Categoria root) {
        this.root = root;
    }

    public Categoria getRoot() {
        return root;
    }

    public String toString() {
        return "\n\nGerarchia: " + root.getNome();
    }

    public void replaceCategoria(Categoria toReplace, Nodo newCat) {
        if (toReplace instanceof Foglia && toReplace.getNome().equals(newCat.getNome())) {
            toReplace = newCat;
        } else {
            Nodo nodo = (Nodo) toReplace;
            for (int i = 0; i < nodo.getCategorieFiglie().size(); i++) {
                replaceCategoria(nodo.getCategorieFiglie().get(i), newCat);
            }
        }
    }

}
