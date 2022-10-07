package nl.knaw.huc.di.images.stringtools;

import org.junit.Assert;
import org.junit.Test;

public class WordErrorRate {

    @Test()
    public void noErrors() {
        double result = StringTools.wordErrorRate("Test", "Test");
        Assert.assertEquals(0.0,result,0.0001);
    }

    @Test()
    public void oneErrors() {
        double result = StringTools.wordErrorRate("Test test", "Test");
        Assert.assertEquals(0.5,result,0.0001);
    }
    @Test()
    public void oneErrorThreeWords() {
        double result = StringTools.wordErrorRate("Test test1 test", "Test test1");
        Assert.assertEquals(0.3333333,result,0.0001);
    }

    @Test()
    public void oneErrorFourWords() {
        double result = StringTools.wordErrorRate("Test test1 test test3", "Test test1 test test24");
        Assert.assertEquals(0.25,result,0.0001);
    }

    @Test()
    public void oneErrorFourWordsOneDuplicate() {
        double result = StringTools.wordErrorRate("Test test1 test test", "Test test1 test test24");
        Assert.assertEquals(0.25,result,0.0001);
    }

    @Test()
    public void oneErrorFourWordsTwoDuplicate() {
        double result = StringTools.wordErrorRate("Test test1 test test test", "Test test1 test test test24");
        Assert.assertEquals(0.20,result,0.0001);
    }

    @Test()
    public void fiveWordsNoError() {
        double result = StringTools.wordErrorRate("test test1 test test test", "test test1 test test test");
        Assert.assertEquals(0,result,0.0001);
    }

    @Test()
    public void fiveWordsOneError() {
        double result = StringTools.wordErrorRate("test1 test2 test3 test4 test5", "test2 test3 test4 test5 test6");
        Assert.assertEquals(0.2,result,0.0001);
    }

    @Test()
    public void fiveWordsOneError2() {
        double result = StringTools.wordErrorRate("test2 test3 test4 test5 test6", "test1 test2 test3 test4 test5");
        Assert.assertEquals(0.2,result,0.0001);
    }
}
