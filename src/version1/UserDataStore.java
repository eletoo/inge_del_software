package version1;

import java.nio.charset.StandardCharsets;
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

    private Map<String, String> userMap = new HashMap<>();

    public UserDataStore() throws NoSuchAlgorithmException {
    }

    public static UserDataStore getInstance(){
        return instance;
    }

    public boolean isUsernameTaken(String username){
        return userMap.containsKey(username);
    }

//QUANDO SI USA QUESTO METODO BISOGNA FARE IL CONTROLLO SUL FATTO CHE LO USERNAME NON SIA GIA' STATO USATO!!!
    public void registerUser(String username, String password){
        byte[] passwordHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(Base64.getDecoder().decode(passwordHash));
        userMap.put(username, decoded);
    }

    public boolean isLoginCorrect(String username, String password) {

        //utente non registrato
        if(!userMap.containsKey(username)){
            return false;
        }

        byte[] passwordHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(Base64.getDecoder().decode(passwordHash));

        String storedPasswordHash = userMap.get(username);

        return decoded == storedPasswordHash;
    }

//PROBLEMA: COME GENERARE STRINGHE CASUALI
    public void addDefaultUser(){
        String username = "User"+(new Random().nextInt());
        String password = "asdfghjkl";
        registerUser(username, password);
    }

    public void changePassword(String username, String oldpw, String newpw){
        byte[] oldPasswordHash = digest.digest(oldpw.getBytes(StandardCharsets.UTF_8));
        String decodedOld = new String(Base64.getDecoder().decode(oldPasswordHash));
        byte[] newPasswordHash = digest.digest(newpw.getBytes(StandardCharsets.UTF_8));
        String decodedNew = new String(Base64.getDecoder().decode(newPasswordHash));
        userMap.replace(username, decodedOld, decodedNew);
    }
}
