package tests;

import it.unibs.ingsw.Configuratore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    Configuratore c = new Configuratore("nome", "password");

    @Test
    void canAuthenticateUser() {
        assertTrue(c.authenticate("password"));
    }

    @Test
    void afterChangedPasswordCanAuthenticateWithNewPassword() {
        c.changePassword("newpassword");
        assertTrue(c.authenticate("newpassword"));
    }

    @Test
    void afterChangedPasswordCannotAuthenticateWithOldPassword() {
        c.changePassword("newpassword");
        assertFalse(c.authenticate("password"));
    }

    @Test
    void canChangeUsername() {
        c.changeUsername("newnome");
        assertTrue(c.getUsername().equals("newnome"));
    }
}