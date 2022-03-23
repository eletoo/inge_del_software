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

        assertTrue(i1.isValidRange(i1.getStart(), i1.getEnd()));
        assertFalse(i2.isValidRange(i2.getStart(), i2.getEnd()));
        assertTrue(i3.isValidRange(i3.getStart(), i3.getEnd()));
        assertTrue(i4.isValidRange(i4.getStart(), i4.getEnd()));
    }
}