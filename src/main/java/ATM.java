import java.util.Objects;
import java.util.Scanner;

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
                //currentUser.incrementFailedAttempts();
                //System.out.println("Invalid Pin");
                //System.out.println(currentUser.getFailedAttempts());
                return false;
            }
        //}
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
