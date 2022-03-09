package version1;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class Controller {

    public static final int STD_USERNAME_LEN = 10;
    public static final int STD_PW_LEN = 10;
    public UserDataStore dataStore;
    private View view = new View();
    private Applicazione app = new Applicazione();

    {
        try {
            dataStore = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void firstAccessAsConfiguratore() throws IOException {
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

    public void secondAccessAsConfiguratore(String username) throws IOException {
        boolean auth = false;


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
                        this.secondAccessAsConfiguratore(username);
                    }break;
                    default: view.illicitChoice();
                }
            }
        }else{
            view.wrongUsernameError();
        }


    }

    private void useAsConfiguratore() throws IOException {
        boolean end = false;
        int choice = 0;
        do{
            var db = new File("./db");
            assert db.exists() || db.mkdir();

            var gf = new File("./db/gerarchie.dat");
            var uf = new File("./db/users.dat");
            if(gf.exists()){
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(gf));
                try {
                    app.gerarchie = (Map<String, Gerarchia>) ois.readObject();
                } catch (ClassNotFoundException | IOException e) {
                    app.gerarchie = new HashMap<>();
                }
            } else
                app.gerarchie = new HashMap<>();

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

                    FileOutputStream fileOutputStream = new FileOutputStream(new File("./db/gerarchie.dat"));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(app.gerarchie);
                    objectOutputStream.close();
                    view.salvataggioEseguito();
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

        //dataStore.save(dataStore.getUserMap().get(username));
    }

    public void modifyConfiguratore(String currentUsername)  {
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
            //dataStore.save(dataStore.getUserMap().get(username));
            dataStore.save();
        } else{
            view.credentialsError();
        }

    }

}
