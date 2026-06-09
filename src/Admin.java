public class Admin {

    private final String USERNAME = "admin";
    private final String PASSWORD = "12345";

    public boolean login(String username,
                         String password) {

        return USERNAME.equals(username)
                && PASSWORD.equals(password);
    }
}