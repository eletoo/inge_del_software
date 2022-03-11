package version1;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class View {

    public View() {
    }

    public void modifyCredentials() {
        System.out.println("ACCESSO EFFETTUATO\nPersonalizza le tue credenziali:");
    }

    public String askUsername() {
        System.out.print("Inserisci il tuo username: ");
        return (new Scanner(System.in).next());
    }

    public String askNewUsername() {
        System.out.println("Inserisci il nuovo username: ");
        return (new Scanner(System.in).nextLine());
    }

    public String askPassword() {
        System.out.print("Inserisci la tua password: ");
        return (new Scanner(System.in).next());
    }

    public String askCustomPassword() {
        System.out.println("Inserisci la nuova password: ");
        String pw = (new Scanner(System.in).next());
        String pw2;
        do {
            System.out.println("Conferma la nuova password: ");
            pw2 = (new Scanner(System.in).next());
            if (!pw.contentEquals(pw2)) {
                System.err.println("Le password non coincidono");
            }
        } while (!pw.contentEquals(pw));
        return pw;
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

    public int selectConfiguratoreAction() {
        System.out.println("Inserisci il numero corrispondente all'azione che vuoi eseguire:" +
                "\n1. Crea una nuova gerarchia" +
                "\n2. Visualizza il contenuto delle gerarchie attualmente presenti nel sistema" +
                "\n3. Salva" +
                "\n4. Esci");
        return (new Scanner(System.in)).nextInt();
    }

    public void arrivederci() {
        System.out.println("Grazie di aver usato l'applicazione.\nArrivederci!");
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
}
