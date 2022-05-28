package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * UserDataStore: classe che contiene tutti i dati degli utenti
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class UserDataStore implements Serializable {

    public static final int STD_USERNAME_LEN = 10;
    public static final int STD_PW_LEN = 10;

    private Map<String, User> userMap;

    /**
     * Costruttore.
     */
    public UserDataStore() {
        userMap = new HashMap<>();
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
        this.save();
    }

    /**
     * Aggiunge un nuovo utente Fruitore alla userMap
     *
     * @param nome nome utente
     * @param pw   password in chiaro
     */
    public void registerNewFruitore(String nome, String pw) {
        userMap.put(nome, new Fruitore(nome, pw));
        this.save();
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
        this.save();
    }

    /**
     * Genera una password randomica
     *
     * @param len lunghezza della password
     * @return password
     */
    private static @NotNull String generateRandomPassword(int len) {
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
    private static String generateRandomString(int size) {
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
     * Genera una stringa casuale da comunicare all'utente come username assicurandosi che non ci sia un utente omonimo
     * gia' registrato. Genera una stringa casuale da usare come password. Comunica le credenziali all'utente e invoca
     * un metodo per registrarlo nel dataStore
     */
    public void addNewConfiguratore(@NotNull View view) {
        String username;
        do {
            username = this.generateRandomString(STD_USERNAME_LEN);
        } while (this.isUsernameTaken(username));

        String password = this.generateRandomPassword(STD_PW_LEN);

        view.communicateCredentials(username, password);

        this.registerNewConfiguratore(username, password);
    }

    /**
     * Chiede username all'utente fruitore, se esiste gia' un utente omonimo ritorna false, altrimenti chiede la password,
     * registra l'utente Fruitore e ritorna true.
     *
     * @param view view
     * @return true se è stato aggiunto un nuovo profilo fruitore al dataStore
     */
    public boolean addedNewFruitore(@NotNull View view) {
        String username = view.askUsername();
        while (this.isUsernameTaken(username)) {
            view.errorMessage(View.ErrorMessage.E_USERNAME_TAKEN);
            return false;
        }
        String password = view.askCustomPassword();

        this.registerNewFruitore(username, password);
        return true;
    }

    /**
     * Permette all'utente configuratore di nome currentUsername di modificare le proprie credenziali, assicurandosi che
     * il nuovo username custom non sia gia' usato da altri utenti.
     *
     * @param currentUsername username corrente dell'utente configuratore
     */
    public void customizeConfiguratore(String currentUsername, @NotNull View view) {
        view.interactionMessage(View.InteractionMessage.CUSTOMIZE_CREDENTIALS);
        String username;
        do {
            username = view.askNewUsername();
            if (this.isUsernameTaken(username)) {
                view.errorMessage(View.ErrorMessage.E_USERNAME_TAKEN);
            }
        } while (this.isUsernameTaken(username));

        String password = view.askCustomPassword();

        if (password != null && username != null) {
            this.updateUser(currentUsername, username, password);
        } else {
            view.errorMessage(View.ErrorMessage.E_CREDENTIALS_ERROR);
        }
    }


    /**
     * Salva il contenuto della userMap in modo permanente
     */
    private void save() {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(System.getProperty("user.dir") + "/db/users.dat"));
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
        var uf = new File(System.getProperty("user.dir") + "/db/users.dat");
        if (uf.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(uf));
            try {
                setUserMap((Map<String, User>) ois.readObject());
            } catch (ClassNotFoundException | IOException e) {
                setUserMap(new HashMap<>());
            }
        } else
            setUserMap(new HashMap<>());
    }
}
