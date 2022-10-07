package nl.knaw.huc.di.images.stringtools;

import org.junit.Assert;
import org.junit.Test;

public class LevenshteinDistanceTest {

    @Test()
    public void distanceIsOne() {
        int result = StringTools.editDistance("a","b");
        Assert.assertEquals(1,result);

        result = StringTools.editDistance("a","aa");
        Assert.assertEquals(1,result);

        result = StringTools.editDistance("a","");
        Assert.assertEquals(1,result);

        result = StringTools.editDistance("ab","bab");
        Assert.assertEquals(1,result);

        result = StringTools.editDistance("ab","ba");
        Assert.assertEquals(1,result);
    }
}
