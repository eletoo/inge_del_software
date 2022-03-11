package version1;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) throws IOException {
        Controller controller = new Controller();
        View view = new View();

        int val;
        do {
            System.out.println("Seleziona un'opzione: \n1. Accedi\n2. Registrati\n3. Esci");
            val = (new Scanner(System.in)).nextInt();
            switch (val) {
                case 1: {
                    controller.dataStore.load();
                    if (controller.dataStore.isEmpty()) {
                        controller.firstAccessAsConfiguratore();
                    } else {
                        String username = view.askUsername();
                        if (controller.dataStore.isUsernameTaken(username)) {
                            if (controller.dataStore.getUserMap().get(username) instanceof Configuratore) {
                                controller.secondAccessAsConfiguratore(username);
                            }
                        } else {
                            view.wrongUsernameError();
                            controller.riproponiAccesso();
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
                case 3: {
                    System.out.println("Arrivederci!");
                }
                break;
                default:
                    System.err.println("ERRORE: Azione non consentita");

            }

        } while (val != 3);


    }

}
