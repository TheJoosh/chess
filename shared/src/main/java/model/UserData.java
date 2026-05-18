package model;

public class UserData {
    
    private final String username;
    private final String password;
    private final String email;

    public UserData(String username, String password, String email) {

        this.username = username;
        this.password = password;
        this.email = email;
    }

    //get the username
    public String getUsername() {
        return username;
    }

    //get the password
    public String getPassword() {
        return password;
    }

    //get the email
    public String getEmail() {
        return email;
    }
}
