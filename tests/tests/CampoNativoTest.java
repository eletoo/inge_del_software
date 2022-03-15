package tests;

import it.unibs.ingsw.CampoNativo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampoNativoTest {

    CampoNativo cn = new CampoNativo(true, CampoNativo.Tipo.STRING);

    @Test
    void isObbligatorio() {
        assertTrue(cn.isObbligatorio());
    }

    @Test
    void getType(){
        assertEquals(CampoNativo.Tipo.STRING, cn.getType());
    }
}