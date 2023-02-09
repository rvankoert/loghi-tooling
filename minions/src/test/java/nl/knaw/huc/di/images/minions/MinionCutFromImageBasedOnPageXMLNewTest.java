package nl.knaw.huc.di.images.minions;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

class MinionCutFromImageBasedOnPageXMLNewTest {

    @Test
    public void hasImageExtensionTest() {
        boolean result = MinionCutFromImageBasedOnPageXMLNew.hasImageExtension("test.jpg");
        Assert.assertEquals(true, result);
        result = MinionCutFromImageBasedOnPageXMLNew.hasImageExtension("/test/test.xml");
        Assert.assertEquals(false, result);
    }
}