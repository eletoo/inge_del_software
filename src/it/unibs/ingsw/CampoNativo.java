package it.unibs.ingsw;

import java.io.Serializable;
import java.util.function.Function;

/**
 * CampoNativo: tiene traccia dell'obbligatorietà di compilazione del campo e del tipo.
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class CampoNativo implements Serializable {

    private boolean obbligatorio;
    private Tipo type;

    /**
     * Costruttore.
     *
     * @param obbligatorio true se il campo è a compilazione obbligatoria
     * @param type         tipo del campo (di default è String)
     */
    public CampoNativo(boolean obbligatorio, Tipo type) {
        this.obbligatorio = obbligatorio;
        this.type = type;
    }

    /**
     * Enum dei possibili tipi dei campi.
     */
    public static enum Tipo {
        STRING((s) -> {
            assert s instanceof String;
            return s;
        }, (o) -> o.toString() );

        private Function<String, Object> des;
        private Function<Object, String> ser;

        Tipo(Function<String, Object> des, Function<Object, String> ser) {
            this.des = des;
            this.ser = ser;
        }

        public String serialize(Object o){
            return this.ser.apply(o);
        }

        public Object deserialize(String o){
            return this.des.apply(o);
        }
    }

    /**
     * @return true se il campo a compilazione obbligatoria
     */
    public boolean isObbligatorio() {
        return obbligatorio;
    }

    /**
     * @return tipo del campo
     */
    public Tipo getType() {
        return type;
    }

}
