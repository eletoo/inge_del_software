package version1;

public abstract class User {
    private String username;
    private String password;

    public User(String _username, String _password){
        this.username=_username;
        this.password=_password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }
}
