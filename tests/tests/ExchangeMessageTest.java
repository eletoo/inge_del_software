package tests;

import it.unibs.ingsw.ExchangeMessage;
import it.unibs.ingsw.Fruitore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ExchangeMessageTest {
    ExchangeMessage em = new ExchangeMessage("Messaggio", new Fruitore("User", "userpw"));

    @Test
    void canReadMessageField(){
        assertEquals("Messaggio", em.getMessage());
    }

    @Test
    void canReadNotNullAuthor(){
        assertNotNull(em.getAuthor());
    }
}
