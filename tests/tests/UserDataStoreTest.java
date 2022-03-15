package tests;

import it.unibs.ingsw.UserDataStore;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class UserDataStoreTest {

    UserDataStore uds = new UserDataStore();

    UserDataStoreTest() throws NoSuchAlgorithmException {
    }

    @Test
    void isUsernameTaken() {
        uds.registerNewConfiguratore("nome", "password");
        assertTrue(uds.isUsernameTaken("nome"));
    }

    @Test
    void updateUser() {
        uds.registerNewConfiguratore("nome", "password");
        uds.updateUser("nome", "newnome", "newpassword");
        assertTrue(uds.getUserMap().get("newnome").getUsername().equals("newnome"));
    }

    @Test
    void isLoginCorrect() {
        uds.registerNewConfiguratore("nome", "password");
        assertTrue(uds.isLoginCorrect("nome", "password"));
        assertFalse(uds.isLoginCorrect("newnome", "password"));
        assertFalse(uds.isLoginCorrect("nome", "newpassword"));
    }

    @Test
    void isEmpty() {
        assertTrue(uds.isEmpty());
    }
}