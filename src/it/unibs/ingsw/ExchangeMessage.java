package it.unibs.ingsw;

import java.io.Serializable;

public class ExchangeMessage implements Serializable {

    private String message;
    private Fruitore author;

    public ExchangeMessage(String message, Fruitore fruitore){
        this.message = message;
        this.author = fruitore;
    }

    public String getMessage(){
        return this.message;
    }

    public Fruitore getAuthor(){
        return this.author;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setAuthor(Fruitore f){
        this.author = f;
    }
}
