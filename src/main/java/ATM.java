import java.util.Objects;

public class ATM {
    private Bank bank;
    private User currentUser;


    public void setUser(User user) {
        this.currentUser = user;
    }

    public boolean insertCard(String userId) {
        return currentUser != null && Objects.equals(currentUser.getId(), userId);
    }

    public boolean enterPin(String pin) {
        /*if (currentUser.getFailedAttempts() < 3) {*/
            if (!validateInput(pin)) {
                currentUser.incrementFailedAttempts();
                System.out.println("Invalid Pin");
                System.out.println(currentUser.getFailedAttempts());
                return false;
            }
        //}
        //returnerar true om pin inte är null och stämmer överens med pin från användaren
        return currentUser.getPin() != null && Objects.equals(currentUser.getPin(), pin);
    }

    public double checkBalance() {
        return 0;
    }

    public void deposit(double amount) {

    }

    public boolean withdraw(double amount) {
        return true;
    }

    public static boolean validateInput(String input) throws IllegalArgumentException {
        if (input == null || input.length() != 4 || !input.matches("\\d{4}")) {
            throw new IllegalArgumentException("pinkod måste var fyra siffror lång");
        } else {
            return true;
        }
    }

}
