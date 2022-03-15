package tests;

import it.unibs.ingsw.Configuratore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    Configuratore c = new Configuratore("nome", "password");

    @Test
    void authenticate() {
        assertTrue(c.authenticate("password"));
    }

    @Test
    void changePassword() {
        c.changePassword("newpassword");
        assertTrue(c.authenticate("newpassword"));
        assertFalse(c.authenticate("password"));
    }

    @Test
    void changeUsername() {
        c.changeUsername("newnome");
        assertTrue(c.getUsername().equals("newnome"));
        assertFalse(c.getUsername().equals("nome"));
    }
}