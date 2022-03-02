package version1;

public class Gerarchia {
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
