package version1;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

//LA CLASSE APPLICAZIONE USA IL CONTROLLER --> DA INSERIRE NELL'UML

public class Controller {
    private Map<String, User> userMap;

    public Controller(){
        userMap=new HashMap<>();
    }

    //NON VA BENE PERCHE' NON COMUNICA ALL'UTENTE LA PASSWORD DA USARE AL PRIMO ACCESSO, LA GENERA E LA SALVA SUBITO HASHATA
    //probabilmente quando si chiama questo metodo bisognerebbe aver generato randomicamente la password all'esterno in modo
    //da comunicare quella all'utente, poi hasharla e salvarla associata all'username. poi l'utente cambia la password
    //reinserendo quella di default e quella nuova.
    public void registerDefaultConfiguratore(){
        String username = "User"+(userMap.keySet().size()+1);
        String password = new BigInteger(130, new SecureRandom()).toString(32);
        userMap.put(username, new Configuratore(username, password));
    }

    public boolean isLoginCorrect(String username, String pw) {

        for (String name: userMap.keySet()) {
            if(userMap.get(name).authenticate(username, pw)){
                return true;
            }
        }
        return false;
    }


}
