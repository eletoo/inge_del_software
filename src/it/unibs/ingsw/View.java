package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * View: gestisce l'interazione con l'utente
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class View {

    public View() {
    }

    /**
     * Richiama il metodo showList() specificando una funzione da eseguire sulla lista
     *
     * @param list lista
     * @param <T>
     */
    public <T> void showList(@NotNull List<T> list) {
        this.showList(list, null);
    }

    /**
     * Mostra la lista passata come parametro applicando la funzione function su ogni suo elemento
     *
     * @param list     lista
     * @param function funzione
     * @param <T>      tipo della lista
     */
    public <T> void showList(@NotNull List<T> list, Function<T, String> function) {
        list.forEach(e -> this.message(function == null ? e.toString() : function.apply(e)));
    }

    /**
     * Permette di selezionare un elemento da un elenco
     *
     * @param selections lista da cui selezionare l'elemento
     * @param function   funzione da applicare agli elementi della lista
     * @param <T>        tipo della lista
     * @return elemento selezionato
     */
    public <T> T choose(@NotNull List<T> selections, Function<T, String> function) {
        assert selections.size() > 0;
        this.message("\nSelezionare un indice.");
        int i = 0;
        for (var o : selections)
            this.message(i++ + ") " + (function == null ? o.toString() : function.apply(o)));

        int input = -1;
        while ((input = this.inInteger()) < 0 || input >= selections.size()) ;
        return selections.get(input);
    }

    /**
     * @return numero intero inserito dall'utente
     */
    private int inInteger() {
        while (true) {
            try {
                return new Scanner(System.in).nextInt();
            } catch (NumberFormatException | InputMismatchException e) {
                this.errorMessage(ErrorMessage.E_INVALID_INPUT);
            }
        }
    }

    /**
     * Enum per la gestione dei messaggi di interazione con l'utente
     */
    public static enum InteractionMessage {
        CUSTOMIZE_CREDENTIALS("\nACCESSO EFFETTUATO\nPersonalizza le tue credenziali:"),
        EXIT_MESSAGE("\nArrivederci"),
        SAVED_CORRECTLY("\nSalvataggio eseguito"),
        AT_LEAST_TWO_CHILDREN("\nN.B. Ogni categoria nodo deve avere almeno due sotto-categorie"),
        CHOOSE_CATEGORY("\nScegliere una categoria"),
        EXCHANGE_HOURS_EVERY_30_MINS("\nGli scambi potranno avvenire allo scoccare dell'ora o della mezz'ora all'interno della fascia oraria specificata"),
        CURRENT_INFO("\nAttualmente sono presenti le seguenti informazioni di scambio: "),
        CHOOSE_OFFER("\nSeleziona una tua offerta da scambiare: "),
        CHOOSE_OTHER_OFFER("\nSeleziona un'offerta con cui scambiare il tuo prodotto: ");

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
     * Richiede la piazza all'utente
     *
     * @return piazza inserita dall'utente
     */
    public String askPiazza() {
        return inLine("Piazza (N.B. Non modificabile in futuro): ");
    }

    /**
     * Richiede il luogo in cui puo' avvenire uno scambio
     *
     * @return luogo inserito dall'utente
     */
    public String askLuogo() {
        return inLine("Luogo: ");
    }

    /**
     * Richiede il giorno in cui puo' avvenire uno scambio
     *
     * @return giorno inserito dall'utente
     */
    public Giorno askGiorno() {
        return this.choose(Arrays.stream(Giorno.values()).collect(Collectors.toList()), Giorno::getGiorno);
    }

    /**
     * Richiede l'inserimento di un numero non negativo, stampando un messaggio di richiesta
     *
     * @param msg messaggio
     * @return valore inserito dall'utente
     */
    public int askNonNegativeNum(String msg) {
        while (true) {
            int num = -1;
            try {
                System.out.println(msg);
                num = new Scanner(System.in).nextInt();
                if (num >= 0)
                    return num;
            } catch (NumberFormatException | IllegalFormatException | InputMismatchException e) {
                errorMessage(ErrorMessage.E_WRONG_FORMAT);
            }
        }
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
        E_USERNAME_TAKEN("Username gi?? in uso da un altro utente"),
        E_UNREGISTERED_USER("Utente non registrato: username inesistente"),
        E_WRONG_PASSWORD("Password errata"),
        E_ILLICIT_CHOICE("Opzione non consentita"),
        E_EXISTING_ROOT_CATEGORY("ERRORE: Il nome ?? gi?? stato assegnato a un'altra categoria radice"),
        E_EXISTING_NAME_IN_HIERARCHY("ERRORE: Nome gi?? presente nella gerarchia"),
        E_UNAUTHORIZED_CHOICE("ERRORE: Azione non consentita"),
        E_WRONG_FORMAT("ERRORE: formato errato, inserire un numero"),
        E_INVALID_TIME("ERRORE: uno degli orari inseriti non ?? valido"),
        E_INVALID_TIME_RANGE("ERRORE: intervallo orario invalido"),
        E_INVALID_INPUT("ERRORE: Input invalido"),
        E_NO_CATEGORIES("Non ci sono categorie selezionabili"),
        E_NO_OFFERS("Non ci sono offerte selezionabili"),
        E_NO_INFO("Non sono ancora state impostate le informazioni di scambio -- Contattare un amministratore"),
        NO_INFO_YET("Non sono ancora presenti informazioni relative agli scambi"),
        NO_HIERARCHIES_YET("Non sono ancora presenti gerarchie all'interno dell'applicazione"),
        E_INVALID_FILE_CONTENT("File di configurazione inesistente o corrotto"),
        E_WRONG_DIR_CONTENT("E' stato trovato pi?? di un file di configurazione -- Numero file consentiti: 1"),
        E_NO_CONF_FILE("Impossibile configurare le impostazioni di scambio da file -- configurare manualmente");

        private String message;

        @Contract(pure = true)
        ErrorMessage(String message) {
            this.message = message;
        }

        @Contract(pure = true)
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
        String s = null;
        while (s == null || s.equalsIgnoreCase("")) {
            System.out.println(prompt);
            s = new Scanner(System.in).next();
        }
        return s;
    }

    /**
     * Legge una riga dall'input utente
     *
     * @param prompt richiesta da fornire all'utente
     * @return riga inserita dall'utente
     */
    public String inLine(String prompt) {
        String s = null;
        while (s == null || s.equalsIgnoreCase("")) {
            System.out.println(prompt);
            s = new Scanner(System.in).nextLine();
        }
        return s;
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
        System.out.println("\nPuoi accedere al tuo profilo usando le seguenti credenziali:\nUsername: " + username + "\nPassword: " + password);
    }

    /**
     * Chiede che azione vuole compiere il configuratore
     *
     * @return scelta dell'utente
     */
    public String selectConfiguratoreAction() {
        System.out.println("\nInserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Crea una nuova gerarchia" +
                "\n2. Visualizza il contenuto delle gerarchie attualmente presenti nel sistema" +
                "\n3. Salva" +
                "\n4. Configura informazioni di scambio" +
                "\n5. Visualizza offerte" +
                "\n6. Visualizza offerte in scambio" +
                "\n7. Visualizza offerte chiuse" +
                "\n8. Configura gerarchie da file" +
                "\n9. Configura informazioni di scambio da file" +
                "\n10. Logout");
        return (new Scanner(System.in)).next();
    }

    /**
     * Chiede che azione vuole compiere il fruitore
     *
     * @return scelta dell'utente
     */
    public String selectFruitoreAction() {
        System.out.println("\nInserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Visualizza il contenuto dell'applicazione e le informazioni di scambio" +
                "\n2. Visualizza offerte personali" +
                "\n3. Visualizza offerte per categoria" +
                "\n4. Crea offerta" +
                "\n5. Ritira offerta" +
                "\n6. Crea proposta di scambio" +
                "\n7. Visualizza ultimi messaggi relativi ai tuoi scambi" +
                "\n8. Logout");
        return (new Scanner(System.in)).next();
    }

    /**
     * @return descrizione della categoria inserita dall'utente
     */
    public String askDescription() {
        System.out.println("Inserire la descrizione della categoria: ");
        return new Scanner(System.in).nextLine();
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
    public boolean yesOrNoQuestion(String prompt) {
        String ans;
        do {
            ans = in(prompt);
        } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
        return ans.equalsIgnoreCase("y") ? true : false;
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
     * @param root    categoria radice
     * @param padre   categoria padre
     * @param choices lista di categorie
     * @param prefix  prefisso
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