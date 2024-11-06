import java.util.Objects;

public class ATM {
    private Bank bank;
    private User currentUser;

    public static boolean validateInput(String input) throws IllegalArgumentException {
        if (input == null || input.length() != 4 || !input.matches("\\d{4}")) {
            throw new IllegalArgumentException("pinkod måste var fyra siffror lång");
        } else {
            return true;
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    //kontrollerar att user inte är null och att kortet inte är låst
    public boolean insertCard(String userId) {
        if (currentUser == null || currentUser.isLocked()) return false;
        return currentUser != null && Objects.equals(currentUser.getId(), userId);
    }

    //kontrollerar pin och att det inte testas för många gånger
    public boolean enterPin(String pin) {
        if (currentUser == null) {
            System.out.println("Ingen användare inloggad");
            return false;
        }

        if (!validateInput(pin)) {
            int remainingAttempts = 3 - currentUser.getFailedAttempts();
            currentUser.incrementFailedAttempts();

            if (remainingAttempts > 1) {
                System.out.println("Fel pinkod, testa igen. Du har nu " + (remainingAttempts - 1) + " försök kvar");
            } else if (remainingAttempts == 1) {
                System.out.println("Fel pinkod, ett försök kvar");
            }

            if (currentUser.getFailedAttempts() >= 3) {
                currentUser.lockCard();
                System.out.println("Ditt kort är nu låst, kontakta " + Bank.getBankName() + " för hjälp");
                return false;
            }

            return false;
        }
        currentUser.resetFailedAttempts();
        return Objects.equals(currentUser.getPin(), pin);
    }


    public double checkBalance(String id) {
        if (currentUser != null && currentUser.getId().equals(id)) {
            return currentUser.getBalance();
        }
        return 0;
    }

    public void deposit(double amount) {
        if (currentUser != null && amount > 0) {
            currentUser.deposit(amount);
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public boolean withdraw(double amount) {
        if (currentUser != null && amount > 0 && currentUser.getBalance() >= amount) {
            currentUser.withdraw(amount);
            System.out.println("Withdrawn: " + amount);
            return true;
        } else {
            System.out.println("Insufficient balance or invalid amount.");
            return false;
        }
    }

    public boolean isBalancePositive(int balance) {
        return balance >= 0;
    }

    public void endSession() {
        this.currentUser = null;
        System.out.println("Uttag avbrutet, vi ses");
    }
}
