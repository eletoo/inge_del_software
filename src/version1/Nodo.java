package version1;

import java.util.ArrayList;
import java.util.List;

public class Nodo extends Categoria{
    private List<Categoria> categorieFiglie;

    public Nodo(String _nome, String _descrizione) {
        super(_nome, _descrizione);
        categorieFiglie = new ArrayList<>();
    }

    public void addCategorieFiglie(ArrayList<Categoria> figlie){
        categorieFiglie = figlie;
    }

    public List<Categoria> getCategorieFiglie(){
        return categorieFiglie;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Categoria c: categorieFiglie) {
            sb.append(c.toString());
        }
        return sb.toString();
    }
}
