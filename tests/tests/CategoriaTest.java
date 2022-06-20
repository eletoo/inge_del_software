package tests;

import it.unibs.ingsw.Foglia;
import it.unibs.ingsw.Nodo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    Nodo n = new Nodo("Nodo", "categoria nodo");
    Foglia f = new Foglia("Foglia", "categoria foglia");
    Foglia f1 = new Foglia("Foglia", "categoria foglia");
    Foglia f2 = new Foglia("Foglia", "categoria foglia");


    @Test
    void isNameTaken() {
        n.addChild(f);
        assertTrue(n.isNameTaken("Foglia"));
    }

    @Test
    void nodeStructureIsInvalidIfHasNoChildren() {
        assertFalse(n.isStructureValid());
    }

    @Test
    void nodeStructureIsInvalidIfHasOneChild(){
        n.addChild(f);
        assertFalse(n.isStructureValid());
    }

    @Test
    void nodeStructureIsValidIfHasTwoChildren(){
        n.addChild(f);
        n.addChild(f1);
        assertTrue(n.isStructureValid());
    }

    @Test
    void nodeStructureIsValidIfHasMoreThanTwoChildren(){
        n.addChild(f);
        n.addChild(f1);
        n.addChild(f2);
        assertTrue(n.isStructureValid());
    }

    @Test
    void leafStructureIsValid(){
        assertTrue(f.isStructureValid());
    }

}