package version1;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Applicazione {

//    private List<Gerarchia> gerarchie = new ArrayList<>();
    private UserDataStore configStore;
    {
        try {
            configStore = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {

        }
    }

    private List<Configuratore> configuratori = new ArrayList<>();

    //per ogni coppia (username, password) bisogna creare un corrispondente configuratore da aggiungere all'arraylist



    /**
     * permette il login all'utente Configuratore
     */
    private void authenticate(){
        System.out.print("\nUsername: ");
        String username = (new Scanner(System.in)).next();
        System.out.println("\nPassword: ");
        String password = (new Scanner(System.in)).next();
        if (this.configStore.isLoginCorrect(username, password)){
            System.out.println("Login eseguito correttamente");

        }

    }

    public void addConfiguratore(Configuratore c){

    }
/*
    public void addGerarchia(Gerarchia g){
        gerarchie.add(g);
    }
*/

}
