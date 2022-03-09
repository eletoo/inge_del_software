package version1;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) throws IOException {
        Controller controller = new Controller();

        System.out.println("Seleziona un'opzione: \n1. Accedi\n2. Registrati\n3. Esci");
        int val = (new Scanner(System.in)).nextInt();
        switch (val) {
            case 1: {
                controller.dataStore.load();
                if (controller.dataStore.isEmpty()) {
                    controller.firstAccessAsConfiguratore();
                } else {
                    String username;
                    do {
                        System.out.println("Inserisci il tuo nome utente: ");
                        username = (new Scanner(System.in)).next();
                    } while (!controller.dataStore.isUsernameTaken(username));
                    if (controller.dataStore.getUserMap().get(username) instanceof Configuratore) {
                        controller.secondAccessAsConfiguratore(username);
                    }
                }
            }
            break;
            case 2: {
                System.out.println("Seleziona la modalità con cui vuoi registrarti:\n1.Configuratore");
                switch ((new Scanner(System.in)).nextInt()) {
                    case 1: {
                        controller.firstAccessAsConfiguratore();
                    }
                    break;
                    default:
                        System.err.println("ERRORE: Azione non consentita");
                }
            }
            break;
            case 3:{
                System.out.println("Arrivederci!");
            }
            break;
            default:
                System.err.println("ERRORE: Azione non consentita");
        }


    }

}
