package it.unibs.ingsw;

public class ExchangeMessage {

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
