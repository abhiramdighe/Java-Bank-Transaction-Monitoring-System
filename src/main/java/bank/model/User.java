package bank.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String role;
    private String status;

    public User(int id, String username, String passwordHash, String email, String phone, String role, String status) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    
    // Setters
    public void setStatus(String status) { this.status = status; }
}
