package it.unibs.ingsw;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Classe contenente il metodo main
 *
 * @author Elena Tonini, Mattia Pavlovic, Claudia Manfredi
 */
public class Main {

    /**
     * Permette all'utente di scegliere se accedere, registrarsi ex novo o uscire dall'applicazione ed esegue le azioni
     * relative.
     *
     * @param args .
     * @throws IOException eccezione I/O
     */
    public static void main(String args[]) throws IOException {
        Controller controller = new Controller();
        View view = new View();

        Path path = Paths.get(System.getProperty("user.dir") + "/db");

        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }

        String val;
        do {
            val = view.in("Seleziona un'opzione: \n1. Accedi\n2. Registrati\n3. Esci");
            switch (val) {
                case "1": {
                    controller.dataStore.load();
                    if (controller.dataStore.isEmpty()) {
                        controller.firstAccessAsConfiguratore();
                    } else {
                        String username = view.askUsername();
                        if (controller.dataStore.isUsernameTaken(username)) {
                            if (controller.dataStore.getUserMap().get(username) instanceof Configuratore) {
                                controller.secondAccessAsConfiguratore(username);
                            } else if (controller.dataStore.getUserMap().get(username) instanceof Fruitore) {
                                controller.secondAccessAsFruitore(username);
                            }
                        } else {
                            view.errorMessage(View.ErrorMessage.E_UNREGISTERED_USER);
                        }
                    }
                }
                break;
                case "2": {
                    controller.dataStore.load();
                    if (controller.dataStore.isEmpty()) {
                        view.message("Non c'è alcun utente registrato -- crea un primo profilo Configuratore");
                        controller.firstAccessAsConfiguratore();
                    } else {
                        String choice = view.in("Seleziona la modalità con cui vuoi registrarti:\n1. Configuratore\n2. Fruitore");
                        switch (choice) {
                            case "1": {
                                controller.firstAccessAsConfiguratore();
                            }
                            break;
                            case "2": {
                                controller.firstAccessAsFruitore();
                            }
                            break;
                            default:
                                view.errorMessage(View.ErrorMessage.E_UNAUTHORIZED_CHOICE);
                        }
                    }
                }
                break;
                case "3": {
                    view.interactionMessage(View.InteractionMessage.EXIT_MESSAGE);
                }
                break;
                default:
                    view.errorMessage(View.ErrorMessage.E_UNAUTHORIZED_CHOICE);
            }
        } while (!val.contentEquals("3"));
    }
}