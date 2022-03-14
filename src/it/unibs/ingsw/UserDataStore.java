package it.unibs.ingsw;

import java.io.*;
import java.util.*;
import java.security.*;


public class UserDataStore implements Serializable{

    private static UserDataStore instance;

    static {
        try {
            instance = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Map<String, User> userMap;

    public UserDataStore() throws NoSuchAlgorithmException {
        userMap = new HashMap<>();
    }

    public static UserDataStore getInstance(){
        return instance;
    }

    public boolean isUsernameTaken(String username){
        return userMap.containsKey(username);
    }

    public void registerNewConfiguratore(String nome, String pw){
        userMap.put(nome, new Configuratore(nome, pw));
    }

    public void updateUser(String oldname, String newname, String newpw){
        userMap.get(oldname).changeUsername(newname);
        userMap.get(oldname).changePassword(newpw);
        userMap.put(newname, userMap.get(oldname));
        userMap.remove(oldname);
    }

    public static @org.jetbrains.annotations.NotNull
    String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                   +"jklmnopqrstuvwxyz!@#$%&£";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++){
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String generateRandomString(int size) {
        String rand = "";
        String chars = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_-.";

        for (int i = 0; i < size; i++) {
            rand += chars.toCharArray()[new Random().nextInt(chars.length())];
        }

        return rand;
    }

    public boolean isLoginCorrect(String username, String password) {

        //utente non registrato
        if(!userMap.containsKey(username)){
            return false;
        }

        return userMap.get(username).authenticate(password);
    }

    public boolean isEmpty(){
        return userMap.isEmpty();
    }

    public Map<String, User> getUserMap(){
        return userMap;
    }

    public void setUserMap(Map<String, User> usermap){ this.userMap=usermap; }

    public void save(){
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File("./db/users.dat"));
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(userMap);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() throws IOException{
        var uf = new File("./db/users.dat");
        if(uf.exists()){
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(uf));
            try {
                this.setUserMap((Map<String, User>) ois.readObject());
            } catch (ClassNotFoundException | IOException e) {
                this.setUserMap(new HashMap<>());
            }
        } else
            this.setUserMap(new HashMap<>());
    }
}
