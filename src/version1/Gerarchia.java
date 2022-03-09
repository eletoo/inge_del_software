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

    public String toString(){
        return "Gerarchia: "+root.getNome()+root.toString();
    }

}
