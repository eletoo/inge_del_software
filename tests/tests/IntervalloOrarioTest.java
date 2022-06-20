package tests;

import it.unibs.ingsw.IntervalloOrario;
import it.unibs.ingsw.Orario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntervalloOrarioTest {
    Orario o1 = new Orario(10, 00);
    Orario o2 = new Orario(12, 00);
    Orario o3 = new Orario(10, 30);

    @Test
    void tenToTenThirtyIsValidRange() {
        IntervalloOrario i4 = new IntervalloOrario(o1, o3);
        assertTrue(i4.isValidRange());
    }

    @Test
    void tenToMiddayIsValidRange(){
        IntervalloOrario i1 = new IntervalloOrario(o1, o2);
        assertTrue(i1.isValidRange());
    }

    @Test
    void middayToTenIsInvalidRange(){
        IntervalloOrario i2 = new IntervalloOrario(o2, o1);
        assertFalse(i2.isValidRange());
    }

    @Test
    void tenToTenIsValidRange(){
        IntervalloOrario i3 = new IntervalloOrario(o1, o1);
        assertTrue(i3.isValidRange());
    }


}