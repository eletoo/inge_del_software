package it.unibs.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Nodo extends Categoria {
    private List<Categoria> categorieFiglie;

    public Nodo(String _nome, String _descrizione) {
        super(_nome, _descrizione);
        categorieFiglie = new ArrayList<>();
    }

    public void addCategorieFiglie(ArrayList<Categoria> figlie) {
        categorieFiglie = figlie;
    }

    public List<Categoria> getCategorieFiglie() {
        return categorieFiglie;
    }

    public void removeChild(Categoria child) {
        this.categorieFiglie.remove(child);
    }

    public void addChild(Categoria child) {
        this.categorieFiglie.add(child);
    }

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
