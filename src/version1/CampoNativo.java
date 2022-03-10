package version1;

public class CampoNativo {

    private boolean obbligatorio;
    private Tipo type;

    public CampoNativo(boolean obbligatorio, Tipo type) {
        this.obbligatorio = obbligatorio;
        this.type = type;
    }

    public static enum Tipo {
        STRING
    }

    public boolean isObbligatorio() {
        return obbligatorio;
    }

    public Tipo getType() {
        return type;
    }

}
