public class Credential {
    private String email;
    private String password;

    public Credential(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Credential() {
    }

    public static Credential from (User user) {
        return new Credential(user.getEmail(), user.getPassword());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
