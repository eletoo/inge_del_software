package version1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Applicazione {

    private List<Gerarchia> gerarchie = new ArrayList<>();
    private List<Configuratore> configuratori = new ArrayList<>();

    public static void main(){

    }

    /**
     * permette il login all'utente Configuratore
     */
    private void authenticate(User user){

    }

    private void authExistingConfigurator(Configuratore conf){

    }

    private void authNewConfigurator(){
        //genero uno username casuale verificando che non sia già presente nella lista dei configuratori
        String tempuser;
        do{
            tempuser = "User"+(new Random());
        }while(isConfiguratorName(tempuser));

        System.out.println("Nome utente: "+tempuser);

    }

    /**
     * @param n nome del configuratore
     * @return true se il nome passato come parametro e' presente nella lista dei configuratori
     */
    private Boolean isConfiguratorName(String n){
        for (Configuratore c : configuratori) {
            if(c.getUsername() == n){
                return true;
            }
        }
        return false;
    }

    public void addConfiguratore(Configuratore c){
        configuratori.add(c);
    }

    public void addGerarchia(Gerarchia g){
        gerarchie.add(g);
    }


}
