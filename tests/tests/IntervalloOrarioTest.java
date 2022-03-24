package tests;

import it.unibs.ingsw.IntervalloOrario;
import it.unibs.ingsw.Orario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntervalloOrarioTest {

    @Test
    void isValidRange() {

        Orario o1 = new Orario(10, 00);
        Orario o2 = new Orario(12, 00);
        Orario o3 = new Orario(10, 30);
        IntervalloOrario i1 = new IntervalloOrario(o1, o2);
        IntervalloOrario i2 = new IntervalloOrario(o2, o1);
        IntervalloOrario i3 = new IntervalloOrario(o1, o1);
        IntervalloOrario i4 = new IntervalloOrario(o1, o3);

        assertTrue(i1.isValidRange());
        assertFalse(i2.isValidRange());
        assertTrue(i3.isValidRange());
        assertTrue(i4.isValidRange());
    }
}