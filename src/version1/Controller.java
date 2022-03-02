package version1;

import java.security.NoSuchAlgorithmException;

//LA CLASSE APPLICAZIONE USA IL CONTROLLER --> DA INSERIRE NELL'UML

public class Controller {

    public static final int STD_USERNAME_LEN = 10;
    public static final int STD_PW_LEN = 10;
    private UserDataStore dataStore;
    private View view = new View();
    private Applicazione app = new Applicazione();

    {
        try {
            dataStore = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void firstAccessAsConfiguratore() {
        this.addNewConfiguratore();
        boolean auth;
        String username;
        do {
            username = view.askUsername();
            auth = dataStore.isLoginCorrect(username, view.askPassword());

            if (auth) {
                this.modifyConfiguratore(username);
                this.useAsConfiguratore();
            } else {
                view.credentialsError();
            }
        }while(!auth);
    }

    public void secondAccessAsConfiguratore(){
        boolean auth = false;
        String username;
        do {
            username = view.askUsername();
            if(dataStore.isUsernameTaken(username)){
                auth = dataStore.isLoginCorrect(username, view.askPassword());

                if (auth) {
                    this.useAsConfiguratore();
                } else {
                    view.wrongPasswordError();

                    int choice = view.printMenuConfiguratore();

                    switch (choice){
                        case 1:{
                            this.firstAccessAsConfiguratore();
                        }break;
                        case 2: {
                            this.secondAccessAsConfiguratore();
                        }break;
                        default: view.illicitChoice();
                    }
                }
            }else{
                view.wrongUsernameError();
            }

        }while(!auth);

    }

    private void useAsConfiguratore() {
        boolean end = false;
        int choice = 0;
        do{
            choice = view.selectConfiguratoreAction();
            switch (choice){
                case 1:{
                    //crea una nuova gerarchia

                }break;
                case 2: {
                    //visualizza gerarchie
                    for (String r: app.gerarchie.keySet()) {
                        System.out.println(app.gerarchie.get(r).toString());
                    }
                }break;
                case 3: {
                    //salva dati
                }break;
                case 4:{
                    //esci
                    end=true;
                    view.arrivederci();
                }break;
                default: view.illicitChoice();
            }
        }while(!end);
    }

    private void addNewConfiguratore(){
        String username;
        do{
            username= dataStore.generateRandomString(STD_USERNAME_LEN);
        }while(dataStore.isUsernameTaken(username));

        String password = dataStore.generateRandomPassword(STD_PW_LEN);

        view.communicateCredentials(username, password);

        dataStore.registerNewConfiguratore(username, password);
    }

    public void modifyConfiguratore(String currentUsername){
        view.modifyCredentials();
        String username;
        do{
            username= view.askNewUsername();
            if(dataStore.isUsernameTaken(username)){
                view.usernameTakenError();
            }
        }while(dataStore.isUsernameTaken(username));

        String password = view.askCustomPassword();

        if(password!=null && username!=null){
            dataStore.updateUser(currentUsername, username, password);
        } else{
            view.credentialsError();
        }
    }



/*
    private Map<String, User> userMap;

    public Controller(){
        userMap=new HashMap<>();
    }

    //NON COMUNICA ALL'UTENTE LA PASSWORD DA USARE AL PRIMO ACCESSO
    //quando si chiama questo metodo bisogna aver generato randomicamente la password all'esterno in modo
    //da comunicare quella all'utente, poi hasharla e salvarla associata all'username. poi l'utente cambia la password
    //reinserendo quella di default e quella nuova.
    public void registerDefaultConfiguratore(String nome, String pw){
        userMap.put(nome, new Configuratore(nome, pw));
    }

    public boolean isLoginCorrect(String username, String pw) {

        for (String name: userMap.keySet()) {
            if(userMap.get(name).authenticate(pw)){
                return true;
            }
        }
        return false;
    }
*/

}
