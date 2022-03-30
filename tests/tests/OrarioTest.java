package tests;

import it.unibs.ingsw.Orario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrarioTest {

    @Test
    void isValid() {
        Orario o1 = new Orario(10, 00);
        Orario o2 = new Orario(10, 10);
        Orario o3 = new Orario(10, 30);
        Orario o4 = new Orario(24, 00);
        Orario o5 = new Orario(00, 00);

        assertTrue(o1.isValid(o1.getHour(), o1.getMinutes()));
        assertFalse(o2.isValid(o2.getHour(), o2.getMinutes()));
        assertTrue(o3.isValid(o3.getHour(), o3.getMinutes()));
        assertTrue(o4.isValid(o4.getHour(), o4.getMinutes()));
        assertTrue(o5.isValid(o5.getHour(), o5.getMinutes()));
    }
}