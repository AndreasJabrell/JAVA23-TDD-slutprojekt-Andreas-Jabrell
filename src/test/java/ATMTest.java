import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ATMTest {

    private ATM ATM;
    private ATM mockAtm;
    private Bank mockBank;
    private User mockUser;
    User user = new User("1234", "9999", 500.0);

    @BeforeEach
    void setUp() {

        mockAtm = mock(ATM.class);
        mockBank = mock(Bank.class);
        mockUser = mock(User.class);
        ATM = new ATM();
    }

    @Test
    @DisplayName("Test card insertion with different IDs")
    void testInsertCardWithDifferentIDs() {
        assertAll("Insert card scenarios",
                () -> {
                    when(mockAtm.insertCard("1234")).thenReturn(true);
                    ATM.setUser(mockUser);
                    assertTrue(ATM.insertCard(mockUser.getId()));
                },
                () -> {
                    when(mockAtm.insertCard("1235")).thenReturn(false);
                    ATM.setUser(mockUser);
                    assertFalse(ATM.insertCard("1235"));
                },
                () -> {
                    when(mockAtm.insertCard(null)).thenReturn(false);
                    assertFalse(ATM.insertCard(null));
                }
        );
    }
    @Test
    @DisplayName("Test card lock status")
    void testCardLockStatus() {
        assertAll("Card lock status scenarios",
                () -> {
                    when(mockBank.isCardLocked(mockUser.getId())).thenReturn(true);
                    assertTrue(mockBank.isCardLocked(mockUser.getId()), "Kortet ska vara låst");
                },
                () -> {
                    when(mockBank.isCardLocked(mockUser.getId())).thenReturn(false);
                    assertFalse(mockBank.isCardLocked(mockUser.getId()), "Kortet ska inte vara låst");
                }
        );
    }

    @Test
    @DisplayName("Test valid card")
    void spyTestValidCard() {
        User spyUser = spy(user);
        doReturn(false).when(spyUser).isLocked();
        assertFalse(spyUser.isLocked());
    }

    //INSERTCARD invalid kort
    @Test
    @DisplayName("Test invalid card")
    void spyTestInvalidCard() {
        User spyUser = spy(user);
        doReturn(true).when(spyUser).isLocked();
        assertTrue(spyUser.isLocked(), "Kortet ska vara låst");
    }

    //ENTERPIN testa pinkod tre siffror assertFalse
    @Test
    @DisplayName("test validate pin format")
    void testPinLength() {
        assertAll("Validering av inmatning",
                () -> assertTrue(ATM.validateInput("1234"), "Test korrekt antal siffror"),
                () -> assertThrows(IllegalArgumentException.class, () -> ATM.validateInput("123"), "Talet är mindre än 4, inte ok"),
                () -> assertThrows(IllegalArgumentException.class, () -> ATM.validateInput("12345"), "Talet är större än 4, inte ok"),
                () -> assertThrows(IllegalArgumentException.class, () -> ATM.validateInput(null), "Inmatning saknas"),
                () -> assertThrows(IllegalArgumentException.class, () -> ATM.validateInput("abcd"), "Inmatning måste vara siffror")
        );
    }

    //ENTERPIN testa att det är rätt pin kod
    @Test
    @DisplayName("Test valid pin code")
    void testPinCode() {
        when(mockAtm.enterPin("9999")).thenReturn(true);
        ATM.setUser(user);
        boolean result = ATM.enterPin("9999");
        assertTrue(result);
    }

    //ENTERPIN testa pinkod fel (fyra fel siffror) assertFalse
    @Test
    @DisplayName("test invalid pin code")
    void testInvalidPinCode() {
        when(mockAtm.enterPin("9998")).thenReturn(false);
        ATM.setUser(user);
        String result = String.valueOf(ATM.enterPin(user.getPin()));
        assertNotEquals("9998", result);
    }


    //ENTERPIN testa köra pinkod med rätt antal siffror tre gånger men med tre olika kombinationer
    @ParameterizedTest
    @ValueSource(strings = {"5555", "9999", "7777"})
    @DisplayName("Trying three different pins")
    void testThreeDifferentPins(String input) {
        ATM.setUser(mockUser);
        boolean result = ATM.enterPin(input);
        if (Objects.equals(mockUser.getPin(), input)) {
            assertTrue(result);
            when(mockAtm.enterPin(input)).thenReturn(true);
        } else {
            assertFalse(result);
            when(mockAtm.enterPin(input)).thenReturn(false);
        }
    }

    //ENTERPIN testa köra pinkod med olika antal siffror och tecken tre gånger
    @ParameterizedTest
    @ValueSource(strings = {"555", "9999", "777g"})
    @DisplayName("Trying three different pins with different length and chars")
    void testThreeDifferentPinsDifferentLength(String input) {
        ATM.setUser(mockUser);
        try {
            boolean result = ATM.enterPin(input);
            if (Objects.equals(mockUser.getPin(), input)) {
                assertTrue(result);
                when(mockAtm.enterPin(input)).thenReturn(true);
            } else {
                assertFalse(result);
                when(mockAtm.enterPin(input)).thenReturn(false);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception for invalid input: " + input);
            assertTrue(e.getMessage().contains("pinkod måste var fyra siffror lång"));
        }
    }

    @Test
    @DisplayName("Test check balance in account")
    void testCheckBalanceInAccount() {
        assertAll("Check balance positive or not",
                () -> assertTrue(ATM.isBalancePositive(1)),
                () -> assertFalse(ATM.isBalancePositive(-1))
        );
    }

    //CHECKDEPOSIT mockad user att id och pin stämmer
    @Test
    @DisplayName("Test mock user check balance")
    void testMockUserCheckBalance() {
        when(mockAtm.checkBalance(user.getId())).thenReturn(500.0);
        ATM.setUser(user);
        double result = ATM.checkBalance(user.getId());
        assertEquals(500.0, result);
    }


    @Test
    @DisplayName("Test deposit functionality")
    void testDeposit() {
        ATM.setUser(user);
        double initialBalance = user.getBalance();
        ATM.deposit(100.0);
        assertEquals(initialBalance + 100.0, user.getBalance());
    }

    @Test
    @DisplayName("Test withdraw functionality with sufficient balance")
    void testWithdrawWithSufficientBalance() {
        ATM.setUser(user);
        double initialBalance = user.getBalance();
        boolean result = ATM.withdraw(100.0);
        assertTrue(result, "Withdrawal should succeed with sufficient balance");
        assertEquals(initialBalance - 100.0, user.getBalance());
    }

    @Test
    @DisplayName("Test withdraw functionality with insufficient balance")
    void testWithdrawWithInsufficientBalance() {
        ATM.setUser(user);
        boolean result = ATM.withdraw(1000.0); // higher than user's balance
        assertFalse(result, "Withdrawal should fail with insufficient balance");
        assertEquals(500.0, user.getBalance()); // initial balance should remain unchanged
    }

    @Test
    @DisplayName("Test failed PIN attempts lock card after 3 tries")
    void testFailedPinAttemptsLockCard() {
        ATM.setUser(user);

        // Mock behavior for failed PIN attempts
        for (int i = 0; i < 3; i++) {
            boolean result = ATM.enterPin("0000");
            assertFalse(result);
            user.incrementFailedAttempts();
        }

        assertEquals(3, user.getFailedAttempts());
        user.lockCard();
        assertTrue(user.isLocked(), "User's card should be locked after 3 failed attempts");
    }

    @Test
    @DisplayName("Test static method getBankName")
    void testStaticMethodGetBankName() {
        try (MockedStatic<Bank> mockedBank = mockStatic(Bank.class)) {
            mockedBank.when(Bank::getBankName).thenReturn("MockBank");

            String bankName = Bank.getBankName();
            assertEquals("MockBank", bankName);
            mockedBank.verify(Bank::getBankName, times(1));
        }
    }
}