package version1;

import java.util.Scanner;

public class MainClass {

    public static void main(){
        Controller controller = new Controller();

        System.out.println("Seleziona un'opzione: \n1. Accedi\n2. Registrati");
        int val = (new Scanner(System.in)).nextInt();
        switch(val){
            case 1 : {

            }break;
            case 2 : {

            }break;
            default : System.err.println("ERRORE: Azione non consentita");
        }
    }

}
