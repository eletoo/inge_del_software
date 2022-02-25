package version1;

public class Configuratore extends User{

    public Configuratore(String _username, String _password) {
        super(_username, _password);
    }

    private Gerarchia createGerarchia(){
        return new Gerarchia();
    }


}
