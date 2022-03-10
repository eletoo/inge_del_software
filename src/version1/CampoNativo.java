package version1;

/**
 * CampoNativo: tiene traccia dell'obbligatorietà di compilazione del campo e del tipo
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class CampoNativo {

    private boolean obbligatorio;
    private Tipo type;

    /**
     * Costruttore
     *
     * @param obbligatorio true se il campo è a compilazione obbligatoria
     * @param type         tipo del campo (di default è String)
     */
    public CampoNativo(boolean obbligatorio, Tipo type) {
        this.obbligatorio = obbligatorio;
        this.type = type;
    }

    /**
     * Enum dei possibili tipi dei campi
     */
    public static enum Tipo {
        STRING
    }

    /**
     * Verifica se un campo è a compilazione obbligatoria
     *
     * @return true se il campo a compilazione obbligatoria
     */
    public boolean isObbligatorio() {
        return obbligatorio;
    }

    /**
     * Ritorna il tipo del campo
     *
     * @return tipo del campo
     */
    public Tipo getType() {
        return type;
    }

}
