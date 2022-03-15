package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.security.*;

/**
 * UserDataStore: Model dell'applicazione, contiene tutti i dati degli utenti
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class UserDataStore implements Serializable {

    private static UserDataStore instance;

    static {
        try {
            instance = new UserDataStore();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Map<String, User> userMap;

    /**
     * Costruttore.
     *
     * @throws NoSuchAlgorithmException eccezione
     */
    public UserDataStore() throws NoSuchAlgorithmException {
        userMap = new HashMap<>();
    }

    /**
     * @return instance
     */
    public static UserDataStore getInstance() {
        return instance;
    }

    /**
     * @param username username da cercare
     * @return true se lo username e' gia' presente nella userMap
     */
    public boolean isUsernameTaken(String username) {
        return userMap.containsKey(username);
    }

    /**
     * Aggiunge un nuovo utente Configuratore alla userMap
     *
     * @param nome nome utente
     * @param pw   password in chiaro
     */
    public void registerNewConfiguratore(String nome, String pw) {
        userMap.put(nome, new Configuratore(nome, pw));
    }

    /**
     * Aggiorna i dati utente
     *
     * @param oldname username vecchio
     * @param newname username nuovo
     * @param newpw   password nuova
     */
    public void updateUser(String oldname, String newname, String newpw) {
        userMap.get(oldname).changeUsername(newname);
        userMap.get(oldname).changePassword(newpw);
        userMap.put(newname, userMap.get(oldname));
        userMap.remove(oldname);
    }

    /**
     * Genera una password randomica
     *
     * @param len lunghezza della password
     * @return password
     */
    public static @NotNull String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                + "jklmnopqrstuvwxyz!@#$%&£";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Genera uno username randomico
     *
     * @param size lunghezza dello username
     * @return username
     */
    public static String generateRandomString(int size) {
        String rand = "";
        String chars = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_-.";

        for (int i = 0; i < size; i++) {
            rand += chars.toCharArray()[new Random().nextInt(chars.length())];
        }

        return rand;
    }

    /**
     * @param username username
     * @param password password in chiaro
     * @return true se username e password sono registrati e sono stati inseriti correttamente
     */
    public boolean isLoginCorrect(String username, String password) {

        //utente non registrato
        if (!userMap.containsKey(username)) {
            return false;
        }

        return userMap.get(username).authenticate(password);
    }

    /**
     * @return true se non ci sono utenti registrati
     */
    public boolean isEmpty() {
        return userMap.isEmpty();
    }

    /**
     * @return mappa degli utenti registrati
     */
    public Map<String, User> getUserMap() {
        return userMap;
    }

    /**
     * @param usermap mappa con cui impostare i valori di userMap
     */
    public void setUserMap(Map<String, User> usermap) {
        this.userMap = usermap;
    }

    /**
     * Salva il contenuto della userMap in modo permanente
     */
    public void save() {
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

    /**
     * Carica il contenuto della userMap salvato all'uso precedente dell'applicazione
     *
     * @throws IOException eccezione I/O
     */
    public void load() throws IOException {
        var uf = new File("./db/users.dat");
        if (uf.exists()) {
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
