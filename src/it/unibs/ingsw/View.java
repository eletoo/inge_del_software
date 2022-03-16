package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * View: gestisce l'interazione con l'utente
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class View {

    public View() {
    }

    /**
     * Enum per la gestione dei messaggi di interazione con l'utente
     */
    public static enum InteractionMessage {
        CUSTOMIZE_CREDENTIALS("ACCESSO EFFETTUATO\nPersonalizza le tue credenziali:"),
        EXIT_MESSAGE("Arrivederci!"),
        SAVED_CORRECTLY("Salvataggio eseguito"),
        AT_LEAST_TWO_CHILDREN("N.B. Ogni categoria nodo deve avere almeno due sotto-categorie"),
        CHOOSE_CATEGORY("Scegliere una categoria");

        private String message;

        InteractionMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    /**
     * @param intmess messaggio di interazione da presentare all'utente
     */
    public void interactionMessage(@NotNull InteractionMessage intmess) {
        System.out.println(intmess.getMessage());
    }

    /**
     * Richiede lo username dell'utente
     *
     * @return username inserito dall'utente
     */
    public String askUsername() {
        return in("Inserisci il tuo username: ");
    }

    /**
     * Richiede un nuovo username all'utente
     *
     * @return username inserito dall'utente
     */
    public String askNewUsername() {
        return in("Inserisci il nuovo username: ");
    }

    /**
     * Richiede la password all'utente
     *
     * @return password inserita dall'utente
     */
    public String askPassword() {
        return in("Inserisci la tua password: ");
    }

    /**
     * Presenta all'utente un messaggio
     *
     * @param text testo del messaggio
     */
    public void message(String text) {
        System.out.println(text);
    }

    /**
     * Enum per la gestione dei messaggi di errore
     */
    public static enum ErrorMessage {
        E_CREDENTIALS_ERROR("Errore nell'inserimento delle credenziali"),
        E_DIFFERENT_PASSWORDS("Le password non coincidono"),
        E_USERNAME_TAKEN("Username già in uso da un altro utente"),
        E_UNREGISTERED_USER("Utente non registrato: username inesistente"),
        E_WRONG_PASSWORD("Password errata"),
        E_ILLICIT_CHOICE("Opzione non consentita"),
        E_EXISTING_ROOT_CATEGORY("ERRORE: Il nome è già stato assegnato a un'altra categoria radice"),
        E_EXISTING_NAME_IN_HIERARCHY("Nome già presente nella gerarchia"),
        E_UNAUTHORIZED_CHOICE("ERRORE: Azione non consentita");

        private String message;

        ErrorMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    /**
     * Presenta all'utente un messaggio di errore
     *
     * @param err messaggio di errore
     */
    public void errorMessage(@NotNull ErrorMessage err) {
        System.err.println(err.getMessage());
    }

    /**
     * Legge una stringa dall'input utente
     *
     * @param prompt richiesta da fornire all'utente
     * @return stringa inserita dall'utente
     */
    public String in(String prompt) {
        System.out.println(prompt);
        return new Scanner(System.in).next();
    }

    /**
     * Legge una riga dall'input utente
     *
     * @param prompt richiesta da fornire all'utente
     * @return riga inserita dall'utente
     */
    public String inLine(String prompt) {
        System.out.println(prompt);
        return new Scanner(System.in).nextLine();
    }

    /**
     * Richiede all'utente la nuova password
     *
     * @return nuova password valida
     */
    public String askCustomPassword() {

        do {
            String pw = in("Inserisci la nuova password: ");
            String pw2 = in("Conferma la nuova password: ");
            if (pw.contentEquals(pw2))
                return pw;
            errorMessage(ErrorMessage.E_DIFFERENT_PASSWORDS);
        } while (true);
    }

    /**
     * Comunica le credenziali all'utente
     *
     * @param username username
     * @param password password
     */
    public void communicateCredentials(String username, String password) {
        System.out.println("Puoi accedere al tuo profilo usando le seguenti credenziali:\nUsername: " + username + "\nPassword: " + password);
    }

    /**
     * Chiede se si vuole creare un nuovo configuratore o accedere a un profilo esistente
     *
     * @return scelta dell'utente
     */
    public String chooseFromMenuConfiguratore() {
        System.out.println("Inserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Crea nuovo configuratore" +
                "\n2. Accedi al tuo profilo");
        return (new Scanner(System.in)).next();
    }

    /**
     * Chiede che azione vuole compiere il configuratore
     *
     * @return scelta dell'utente
     */
    public String selectConfiguratoreAction() {
        System.out.println("Inserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Crea una nuova gerarchia" +
                "\n2. Visualizza il contenuto delle gerarchie attualmente presenti nel sistema" +
                "\n3. Salva" +
                "\n4. Esci");
        return (new Scanner(System.in)).next();
    }

    /**
     * @return descrizione della categoria inserita dall'utente
     */
    public String askDescription() {
        return inLine("Inserire la descrizione della categoria: ");
    }

    /**
     * @return nome della categoria inserito dall'utente
     */
    public String askCategoryName() {
        return inLine("Inserisci il nome della categoria: ");
    }

    /**
     * @return nome del campo nativo inserito dall'utente
     */
    public String insertFieldName() {
        return inLine("Nome campo: ");
    }

    /**
     * @param prompt domanda con risposta si'/no da presentare all'utente
     * @return risposta dell'utente
     */
    public String yesOrNoQuestion(String prompt) {
        String ans;
        do {
            ans = in(prompt);
        } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
        return ans;
    }

    /**
     * Permette all'utente di scegliere una categoria e ne ritorna il riferimento.
     *
     * @param root categoria da trovare
     * @return riferimento alla categoria
     */
    public CategoriaEntry findCategory(Categoria root) {
        interactionMessage(InteractionMessage.CHOOSE_CATEGORY);
        List<CategoriaEntry> choices = getCategoriesAsList(root);

        int i = 0;
        for (var choice : choices)
            message((i++) + ") " + choice.getDisplayName());

        int answ = -1;
        do {
            String v = in("Effettuare una scelta");
            try {
                answ = Integer.parseInt(v);
            } catch (NumberFormatException e) {
            }
        } while (answ < 0 || answ >= choices.toArray().length);

        return choices.get(answ);
    }

    /**
     * Ottiene una lista di {@link CategoriaEntry} a partire da una categoria radice.
     *
     * @param root categoria radice
     * @return lista di Categorie
     */
    @Contract("_ -> new")
    private @NotNull List<CategoriaEntry> getCategoriesAsList(Categoria root) {
        return getCategoriesAsList(root, null, new ArrayList<>(), "");
    }

    /**
     * Ottiene una lista di {@link CategoriaEntry} a partire da una categoria radice.
     *
     * @param root categoria radice
     * @param padre categoria padre
     * @param choices lista di categorie
     * @param prefix prefisso
     * @return lista di categorie
     */
    private @NotNull List<CategoriaEntry> getCategoriesAsList(Categoria root, Nodo padre, @NotNull List<CategoriaEntry> choices, String prefix) {
        choices.add(new CategoriaEntry(root, padre, prefix + root.getNome()));
        if (root instanceof Nodo)
            for (Categoria child : ((Nodo) root).getCategorieFiglie())
                getCategoriesAsList(child, (Nodo) root, choices, prefix + root.getNome() + "->");
        return choices;
    }
}
