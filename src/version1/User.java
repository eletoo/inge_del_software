package version1;

import java.io.Serializable;
import java.security.*;
import java.util.*;

public abstract class User implements Serializable {
    private String username;
    private String hashedPw;

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
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(pw.getBytes());
            return new String(Base64.getEncoder().encode(digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean authenticate(String pw){
        return (this.hashedPw.equals(hashPassword(pw)));
    }

    public void changePassword(String newpw){
        this.hashedPw = hashPassword(newpw);
    }

    public void changeUsername(String newUsername){
        this.username = newUsername;
    }
}
