package version1;

import java.security.*;
import java.util.*;

public abstract class User {
    private String username;
    private String hashedPw;
    MessageDigest digest;

    {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public User(String _username, String _password){
        this.username = _username;
        this.hashedPw = hashPassword(_password);
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPw(){
        return hashedPw;
    }

    private static String hashPassword(String pw){
        byte[] passwordHash = Base64.getEncoder().encode(pw.getBytes());
        return new String(Base64.getDecoder().decode(passwordHash));
    }

    public boolean authenticate(String pw){
        return (this.hashedPw == hashPassword(pw));
    }

    public void changePassword(String newpw){
        this.hashedPw = hashPassword(newpw);
    }

    public void changeUsername(String newUsername){
        this.username = newUsername;
    }
}
