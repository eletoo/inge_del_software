package version1;

import java.util.*;

public class Applicazione {

    public Map<String, Gerarchia> gerarchie;

    public Applicazione(){
        gerarchie = new HashMap<>();
    }

    public void addGerarchia(String rootName, Gerarchia gerarchia){
        gerarchie.put(rootName, gerarchia);
    }

    public boolean isHierarchyNameTaken(String name){
        return gerarchie.containsKey(name);
    }
}
