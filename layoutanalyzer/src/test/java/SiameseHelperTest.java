import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.SiameseHelper;
import nl.knaw.huc.di.images.layoutds.models.DocumentTextLineSnippet;
import org.junit.Assert;
import org.junit.Test;

public class SiameseHelperTest {
    @Test
    public void calculateDistanceTest() {
        DocumentTextLineSnippet snippet = new DocumentTextLineSnippet();
        snippet.setFeature1(0.5f);
        snippet.setFeature2(0.5f);
        snippet.setFeature3(0.5f);
        snippet.setFeature4(0.5f);
        snippet.setFeature5(0.5f);
        snippet.setFeature6(0.5f);
        snippet.setFeature7(0.5f);
        snippet.setFeature8(0.5f);
        snippet.setFeature9(0.5f);
        snippet.setFeature10(0.5f);
        snippet.setFeature11(0.5f);
        snippet.setFeature12(0.5f);
        snippet.setFeature13(0.5f);
        snippet.setFeature14(0.5f);
        snippet.setFeature15(0.5f);
        snippet.setFeature16(0.5f);
        snippet.setFeature17(0.5f);
        snippet.setFeature18(0.5f);
        snippet.setFeature19(0.5f);
        snippet.setFeature20(0.5f);

        DocumentTextLineSnippet snippet2 = new DocumentTextLineSnippet();
        snippet2.setFeature1(0.5f);
        snippet2.setFeature2(0.5f);
        snippet2.setFeature3(0.5f);
        snippet2.setFeature4(0.5f);
        snippet2.setFeature5(0.5f);
        snippet2.setFeature6(0.5f);
        snippet2.setFeature7(0.5f);
        snippet2.setFeature8(0.5f);
        snippet2.setFeature9(0.5f);
        snippet2.setFeature10(0.5f);
        snippet2.setFeature11(0.5f);
        snippet2.setFeature12(0.5f);
        snippet2.setFeature13(0.5f);
        snippet2.setFeature14(0.5f);
        snippet2.setFeature15(0.5f);
        snippet2.setFeature16(0.5f);
        snippet2.setFeature17(0.5f);
        snippet2.setFeature18(0.5f);
        snippet2.setFeature19(0.5f);
        snippet2.setFeature20(0.5f);

        float result = SiameseHelper.calculateDistance(snippet, snippet2, null);

        Assert.assertEquals(0, result, 0.0000001);
    }

    @Test
    public void cosineSimilarityTest() {
        DocumentTextLineSnippet snippet = new DocumentTextLineSnippet();
        snippet.setFeature1(0.5f);
        snippet.setFeature2(0.5f);
        snippet.setFeature3(0.5f);
        snippet.setFeature4(0.5f);
        snippet.setFeature5(0.5f);
        snippet.setFeature6(0.5f);
        snippet.setFeature7(0.5f);
        snippet.setFeature8(0.5f);
        snippet.setFeature9(0.5f);
        snippet.setFeature10(0.5f);
        snippet.setFeature11(0.5f);
        snippet.setFeature12(0.5f);
        snippet.setFeature13(0.5f);
        snippet.setFeature14(0.5f);
        snippet.setFeature15(0.5f);
        snippet.setFeature16(0.5f);
        snippet.setFeature17(0.5f);
        snippet.setFeature18(0.5f);
        snippet.setFeature19(0.5f);
        snippet.setFeature20(0.5f);

        DocumentTextLineSnippet snippet2 = new DocumentTextLineSnippet();
        snippet2.setFeature1(0.5f);
        snippet2.setFeature2(0.5f);
        snippet2.setFeature3(0.5f);
        snippet2.setFeature4(0.5f);
        snippet2.setFeature5(0.5f);
        snippet2.setFeature6(0.5f);
        snippet2.setFeature7(0.5f);
        snippet2.setFeature8(0.5f);
        snippet2.setFeature9(0.5f);
        snippet2.setFeature10(0.5f);
        snippet2.setFeature11(0.5f);
        snippet2.setFeature12(0.5f);
        snippet2.setFeature13(0.5f);
        snippet2.setFeature14(0.5f);
        snippet2.setFeature15(0.5f);
        snippet2.setFeature16(0.5f);
        snippet2.setFeature17(0.5f);
        snippet2.setFeature18(0.5f);
        snippet2.setFeature19(0.5f);
        snippet2.setFeature20(0.5f);

        double result = SiameseHelper.cosineSimilarity(snippet, snippet2, null);

        Assert.assertEquals(1, result, 0.0000001);
    }

}