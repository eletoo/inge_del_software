package it.unibs.ingsw;

import java.io.Serializable;

/**
 * Classe che gestisce un messaggio associato a uno scambio
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class ExchangeMessage implements Serializable {

    private String message;
    private Fruitore author;

    /**
     * Costruttore
     */
    public ExchangeMessage(String message, Fruitore fruitore) {
        this.message = message;
        this.author = fruitore;
    }

    /**
     * @return messaggio
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return autore del messaggio
     */
    public Fruitore getAuthor() {
        return this.author;
    }

    /**
     * @param message messaggio da impostare
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param f autore da impostare
     */
    public void setAuthor(Fruitore f) {
        this.author = f;
    }
}
