package version1;

import java.util.HashMap;

public abstract class Categoria {

    private String nome;
    private String descrizione;
    private HashMap<String, Boolean> campiNativi;

    public String getDescrizione() {
        return descrizione;
    }

    public String getNome() {
        return nome;
    }

    public HashMap<String, Boolean> getCampiNativi() {
        return campiNativi;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCampiNativi(HashMap<String, Boolean> campiNativi) {
        this.campiNativi = campiNativi;
    }
}
