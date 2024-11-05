import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.management.ConstructorParameters;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ATMTest {

    private ATM ATM;
    private Bank realBank;
    private BankInterface mockBankInterface;
    private ATM mockAtm;
    private Bank mockBank;
    private User mockUser;
    User user = new User("1234", "9999", 500.0);

    @BeforeEach
    void setUp() {
        mockBankInterface = mock(BankInterface.class);
        mockAtm = mock(ATM.class);
        mockBank = mock(Bank.class);
        mockUser = mock(User.class);
        ATM = new ATM();

    }

    //INSERTCARD testa ID, assert true med id
    @Test
    @DisplayName("Test valid ID")
    void testValidID() {
        when(mockAtm.insertCard("1234")).thenReturn(true);
        ATM.setUser(user);
        boolean result = ATM.insertCard(user.getId());
        assertTrue(result);
    }

    //INSERTCARD testa ID, assert false med id
    @Test
    @DisplayName("Test invalid ID")
    void testInvalidID() {
        when(mockAtm.insertCard("1235")).thenReturn(false);
        ATM.setUser(user);
        String result = String.valueOf(ATM.insertCard(user.getId()));
        assertNotEquals("1325", result);
    }

    //INSERTCARD testa ID, så att ID inte är null
    @Test
    @DisplayName("Test ID null")
    void testIdNull() {
        when(mockAtm.insertCard(null)).thenReturn(true);
        assertFalse(ATM.insertCard(null));
    }

    //INSERTCARD kort om det är valid och inte låst
    @Test
    @DisplayName("Mock Test invalid card")
    void mockTestInvalidCard() {
        when(mockBankInterface.isCardLocked(user.getId())).thenReturn(true);
        boolean cardLocked = mockBankInterface.isCardLocked(user.getId());
        assertTrue(cardLocked, "Kortet ska vara låst enligt mockad funktion");
    }

    @Test
    @DisplayName("Mock Test valid card")
    void mockTestValidCard() {
        when(mockBankInterface.isCardLocked(user.getId())).thenReturn(false);
        boolean cardNotLocked = mockBankInterface.isCardLocked(user.getId());
        assertFalse(cardNotLocked, "Kortet ska inte vara låst enligt mockad funktion");
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
        assertTrue(spyUser.isLocked(), "Kortet ska vara låst enligt spy på User");
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
        boolean result = ATM.enterPin(user.getPin());
        assertNotEquals("9998", result);
    }


    //ENTERPIN testa köra pinkod med rätt antal siffror tre gånger men med tre olika kombinationer
    @ParameterizedTest
    @ValueSource (strings = {"5555", "9999", "7777"})
    @DisplayName("Trying three different pins")
    void testThreeDifferentPins(String input) {
        ATM.setUser(user);
        boolean result = ATM.enterPin(input);
        if (Objects.equals(user.getPin(), input)) {
            assertTrue(result);
            when(mockAtm.enterPin(input)).thenReturn(true);
        } else {
            assertFalse(result);
            when(mockAtm.enterPin(input)).thenReturn(false);
        }
    }

    //ENTERPIN testa köra pinkod med olika antal siffror och tecken tre gånger
    @ParameterizedTest
    @ValueSource (strings = {"555", "9999", "777g"})
    @DisplayName("Trying three different pins with different length and chars")
    void testThreeDifferentPinsDifferentLength(String input) {
        ATM.setUser(user);
        try {
            boolean result = ATM.enterPin(input);
            if (Objects.equals(user.getPin(), input)) {
                assertTrue(result);
                when(mockAtm.enterPin(input)).thenReturn(true);
            } else {
                assertFalse(result);
                when(mockAtm.enterPin(input)).thenReturn(false);
            }
        }catch(IllegalArgumentException e){
            System.out.println("Caught expected exception for invalid input: " + input);
            assertTrue(e.getMessage().contains("pinkod måste var fyra siffror lång"));
        }
    }


    //CHECKDEPOSIT mocka user med x antal pengar, att det faktiskt finns
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

    //behövs denna verkligen? locked card ska kontrolleras långt innan man kommer in ens
/*    //CHECKDEPOSIT låst kort
    @Test
    @DisplayName("check balance with locked card")*/

    //DEPOSIT assertequal på att det som sätts in är det som faktist kommer in
    //DEPOSIT testa att det plussas ihop rätt

    //WITHDRAW testa att det som finns faktiskt finns assertTrue
    //WITHDRAW testa att det som finns inte stämmer assertFalse
    //WITHDRAW testa att det är rätt format kanske?
    //WITHDRAW testa subtraktion på kontot mot vad smo dras


@Test
    void testATM() {}

}