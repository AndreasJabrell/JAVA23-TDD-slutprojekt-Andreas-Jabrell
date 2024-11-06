import java.util.Objects;
import java.util.Scanner;

public class ATM {
    private Bank bank;
    private User currentUser;


    public void setUser(User user) {
        this.currentUser = user;
    }

    public boolean insertCard(String userId) {
        if (currentUser == null || currentUser.isLocked()) return false;
        return currentUser != null && Objects.equals(currentUser.getId(), userId);
    }

    public boolean enterPin(String pin) {
        if (currentUser != null) {
        }
            if (!validateInput(pin)) {
                return false;
            }

        //returnerar true om pin inte är null och stämmer överens med pin från användaren
        return currentUser.getPin() != null && Objects.equals(currentUser.getPin(), pin);
    }

    public double checkBalance(String id) {
        if(currentUser != null) {
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

    public static boolean validateInput(String input) throws IllegalArgumentException {
        if (input == null || input.length() != 4 || !input.matches("\\d{4}")) {
            throw new IllegalArgumentException("pinkod måste var fyra siffror lång");
        } else {
            return true;
        }
    }


    public boolean isBalancePositive(int balance) {
        return balance >= 0;
    }

    public static void main(String[] args){
        User user = new User("1234", "9999", 500.0);
        ATM atm = new ATM();
        atm.setUser(user);
        Scanner scanner = new Scanner(System.in);
        System.out.println("hej, välkommen till banken, sätt in ditt kort(alltså skriv in ditt id");
        String id = scanner.nextLine();
        System.out.println("skriv in din pin kod");
        String pin = scanner.nextLine();

        if(atm.insertCard(id) && atm.enterPin(pin)) {
            System.out.println("välkommen");
        } else {
            System.out.println("oopsie");
        }
        /*atm.insertCard("1234");
        atm.enterPin("9999");
        System.out.println(user.getBalance());
        System.out.println(atm.withdraw(1));
        System.out.println(atm.checkBalance("1234"));*/

    }
}
