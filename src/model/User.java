package model;

public class User {
    private String username;
    private String password; // In deployed application, this will store a hash
    private String role;     // "admin" or "standard"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role;     }
}
