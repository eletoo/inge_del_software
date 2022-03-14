package it.unibs.ingsw;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class View {

    public View() {
    }

    public void modifyCredentials() {
        System.out.println("ACCESSO EFFETTUATO\nPersonalizza le tue credenziali:");
    }

    public String askUsername() {
        return in("Inserisci il tuo username: ");
    }

    public String askNewUsername() {
        return in("Inserisci il nuovo username: ");
    }

    public String askPassword() {
        return in("Inserisci la tua password: ");
    }

    public void message(String text) {
        System.out.println(text);
    }

    public String in(String prompt) {
        System.out.println(prompt);
        return new Scanner(System.in).next();
    }

    public String askCustomPassword() {

        do {
            String pw = in("Inserisci la nuova password: ");
            String pw2 = in("Conferma la nuova password: ");
            if (pw.contentEquals(pw2))
                return pw;
            System.err.println("Le password non coincidono");
        } while (true);
    }

    public void credentialsError() {
        System.err.println("Errore nell'inserimento delle credenziali");
    }

    public void usernameTakenError() {
        System.err.println("Username già in uso da un altro utente");
    }

    public void communicateCredentials(String username, String password) {
        System.out.println("Puoi accedere al tuo profilo usando le seguenti credenziali:\nUsername: " + username + "\nPassword: " + password);
    }

    public int printMenuConfiguratore() {
        System.out.println("Inserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Crea nuovo configuratore" +
                "\n2. Accedi al tuo profilo");
        return (new Scanner(System.in)).nextInt();
    }

    public void wrongUsernameError() {
        System.err.println("Utente non registrato: username inesistente");
    }

    public void wrongPasswordError() {
        System.err.println("Password errata");
    }

    public void illicitChoice() {
        System.err.println("Opzione non consentita");
    }

    public String selectConfiguratoreAction() {
        System.out.println("Inserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Crea una nuova gerarchia" +
                "\n2. Visualizza il contenuto delle gerarchie attualmente presenti nel sistema" +
                "\n3. Salva" +
                "\n4. Esci");
        return (new Scanner(System.in)).next();
    }

    public void arrivederci() {
        System.out.println("Arrivederci!");
    }

    public void salvataggioEseguito() {
        System.out.println("Salvataggio eseguito");
    }

    public String askDescription() {
        System.out.println("Inserire la descrizione della categoria: ");
        return (new Scanner(System.in)).nextLine();
    }

    public String askCategoryName() {
        System.out.println("Inserisci il nome della categoria: ");
        return (new Scanner(System.in)).nextLine();
    }

    public void categoriaGiaEsistente() {
        System.out.println("ERRORE: Il nome è già stato assegnato a un'altra categoria radice");
    }

    public String inserisciNomeCampo() {
        System.out.println("Nome campo: ");
        return (new Scanner(System.in)).nextLine();
    }

    public void nomeGiaPresenteNellaGerarchia() {
        System.out.println("Nome già presente nella gerarchia");
    }

    public String yesOrNoQuestion(String s) {
        String ans;
        do {
            System.out.println(s);
            ans = (new Scanner(System.in)).next();
        } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
        return ans;
    }

    /**
     * Permette all'utente di scegliere una categoria e ne ritorna il riferimento
     *
     * @param root
     * @return
     */
    public CategoriaEntry findCategory(Categoria root) {
        message("Scegliere una categoria");
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
     * ottiene una lista di {@link CategoriaEntry} a partire da una categoria radice
     *
     * @param root
     * @return
     */
    @Contract("_ -> new")
    private @NotNull List<CategoriaEntry> getCategoriesAsList(Categoria root) {
        return getCategoriesAsList(root, null, new ArrayList<>(), "");
    }

    /**
     * ottiene una lista di {@link CategoriaEntry} a partire da una categoria radice
     *
     * @param root
     * @return
     */
    private @NotNull List<CategoriaEntry> getCategoriesAsList(Categoria root, Nodo padre, @NotNull List<CategoriaEntry> choices, String prefix) {
        choices.add(new CategoriaEntry(root, padre, prefix + root.getNome()));
        if (root instanceof Nodo)
            for (Categoria child : ((Nodo) root).getCategorieFiglie())
                getCategoriesAsList(child, (Nodo) root, choices, prefix + root.getNome() + "->");
        return choices;
    }
}
