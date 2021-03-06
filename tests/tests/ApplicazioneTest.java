package tests;

import it.unibs.ingsw.Applicazione;
import it.unibs.ingsw.Foglia;
import it.unibs.ingsw.Gerarchia;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ApplicazioneTest {
    Applicazione app = new Applicazione();

    @Test
    @DisplayName("Root name should be recognised")
    void isHierarchyNameTaken() {
        app.addGerarchia("radice", new Gerarchia(new Foglia("radice", "categoria radice")));
        assertTrue(app.isHierarchyNameTaken("radice"));
    }

    @Test
    void canAddHierarchy(){
        app.addGerarchia("radice", new Gerarchia(new Foglia("radice", "categoria radice")));
        assert app.getHierarchies().keySet().size() == 1;
    }

    @Test
    void canRetrieveHierarchy(){
        app.addGerarchia("radice", new Gerarchia(new Foglia("radice", "categoria radice")));
        assertNotNull(app.getHierarchies().get("radice"));
    }

}