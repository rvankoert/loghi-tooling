package nl.knaw.huc.di.images.stringtools;

import org.junit.Assert;
import org.junit.Test;

public class StringToolsTest {

    @Test()
    public void isRomanNumeralTest1(){
        boolean result = StringTools.isRomanNumeral("MMXX");
        Assert.assertEquals(true, result);
    }

    @Test()
    public void isRomanNumeralTest2(){
        boolean result = StringTools.isRomanNumeral("XC");
        Assert.assertEquals(true, result);
    }

    @Test()
    public void isRomanNumeralTest3() {
        boolean result = StringTools.isRomanNumeral("XXXX");
        Assert.assertEquals(false, result);
    }

    @Test()
    public void isNumericTestFalse() {
        boolean result = StringTools.isNumeric("asd123");
        Assert.assertEquals(false, result);
    }

    @Test()
    public void isNumericTestTrue() {
        boolean result = StringTools.isNumeric("123");
        Assert.assertEquals(true, result);
    }

    @Test()
    public void getDutchMonthNumberTest() {
        int result = StringTools.getDutchMonthNumber("Oktober");
        Assert.assertEquals(10, result);
    }

    @Test()
    public void cleanUriTest1() {
        String input = "http://test.com/";
        String output = "http://test.com/";

        String result = StringTools.cleanUri(input);
        Assert.assertEquals(output, result);
    }

    @Test()
    public void cleanUriTest2() {
        String input = "http:// test.com/";
        String output= "http://_test.com/";

        String result = StringTools.cleanUri(input);
        Assert.assertEquals(output,result);
    }

}
