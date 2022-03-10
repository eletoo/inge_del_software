package version1;

import java.io.Serializable;
import java.util.*;

public abstract class Categoria implements Serializable {

    private String nome;
    private String descrizione;
    private Map<String, CampoNativo> campiNativi;

    public Categoria(String _nome, String _descrizione) {
        this.nome = _nome;
        this.descrizione = _descrizione;
        campiNativi = new HashMap<>();
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getNome() {
        return nome;
    }

    public Map<String, CampoNativo> getCampiNativi() {
        return campiNativi;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCampiNativi(HashMap<String, CampoNativo> campiNativi) {

        this.campiNativi = campiNativi;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nNome: " + this.nome);
        sb.append("\nDescrizione: " + this.descrizione);
        sb.append("\nCampi nativi:");
        for (String n : campiNativi.keySet()) {
            sb.append("\n-> " + n);
            sb.append(campiNativi.get(n).isObbligatorio() ? " (Obbligatorio)" : " (Falcotativo)");
        }
        return sb.toString();
    }
}
