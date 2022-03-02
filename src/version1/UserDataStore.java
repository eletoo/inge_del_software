package version1;

import java.util.*;
import java.security.*;


public class UserDataStore {

    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    private static UserDataStore instance;

    static {
        try {
            instance = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Map<String, User> userMap = new HashMap<>();

    public UserDataStore() throws NoSuchAlgorithmException {
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
    }

    public static String generateRandomPassword(int len) {
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

}
