package nl.knaw.huc.di.images.stringtools;

import org.junit.Assert;
import org.junit.Test;

public class CharacterErrorRate {

    @Test()
    public void oneError() {
        double result = StringTools.characterErrorRate("aa", "a");
        Assert.assertEquals(0.5,result,0.0001);
    }

    @Test()
    public void oneErrorThreeCharacters() {
        double result = StringTools.characterErrorRate("aaa", "a");
        Assert.assertEquals(0.66666666,result,0.0001);
    }
}
