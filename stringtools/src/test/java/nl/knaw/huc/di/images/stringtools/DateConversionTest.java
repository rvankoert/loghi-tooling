package nl.knaw.huc.di.images.stringtools;

import org.junit.Assert;
import org.junit.Test;

public class DateConversionTest {

    @Test()
    public void toRoman() {
        String result= StringTools.toRoman(24);
        Assert.assertEquals("XXIV",result);
    }

}
