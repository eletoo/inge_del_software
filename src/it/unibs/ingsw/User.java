package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.security.*;
import java.util.*;

/**
 * User: gestisce le informazioni relative a un utente
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public abstract class User implements Serializable {
    private String username;
    private String hashedPw;

    /**
     * @return stringa contenente lo username dell'utente
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * Override del metodo equals()
     * @param o oggetto con cui effettuare il confronto
     * @return true se gli oggetti hanno stesso username
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    /**
     * Override del metodo hashCode()
     * @return hashCode dell'oggetto
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Costruttore: salva la password dopo l'hashing
     *
     * @param _username username
     * @param _password password in chiaro
     */
    public User(String _username, String _password) {
        this.username = _username;
        this.hashedPw = hashPassword(_password);
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password dopo l'hashing
     */
    public String getHashedPw() {
        return hashedPw;
    }

    /**
     * Permette di effettuare l'hashing della password dell'utente in modo da non salvarla in chiaro
     *
     * @param pw password in chiaro
     * @return password dopo l'hashing
     */
    @Contract("_ -> new")
    private static @NotNull String hashPassword(@NotNull String pw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(pw.getBytes());
            return new String(Base64.getEncoder().encode(digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param pw password in chiaro
     * @return true se la password pw dopo l'hashing a' uguale alla password salvata
     */
    public boolean authenticate(String pw) {
        return (this.hashedPw.equals(hashPassword(pw)));
    }

    /**
     * Aggiorna la password dell'utente con quella passata come parametro, previo hashing
     *
     * @param newpw nuova password in chiaro
     */
    public void changePassword(String newpw) {
        this.hashedPw = hashPassword(newpw);
    }

    /**
     * Aggiorna lo username dell'utente con quello passato come parametro
     *
     * @param newUsername nuovo username
     */
    public void changeUsername(String newUsername) {
        this.username = newUsername;
    }
}
