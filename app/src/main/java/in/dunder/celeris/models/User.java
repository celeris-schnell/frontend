package in.dunder.celeris.models;

public class User {
    private int id;
    private String name;
    private int balance;
    private String email;
    private String phoneNumber;

    public User(int id, String name, int balance, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
