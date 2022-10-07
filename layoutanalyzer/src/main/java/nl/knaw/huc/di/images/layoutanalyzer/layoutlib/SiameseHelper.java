package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import nl.knaw.huc.di.images.layoutds.models.DocumentTextLineSnippet;

import java.util.List;

public class SiameseHelper {


    static double[] getVector(DocumentTextLineSnippet snippet) {
        double[] vector = new double[20];
        vector[0] = snippet.getFeature1();
        vector[1] = snippet.getFeature2();
        vector[2] = snippet.getFeature3();
        vector[3] = snippet.getFeature4();
        vector[4] = snippet.getFeature5();
        vector[5] = snippet.getFeature6();
        vector[6] = snippet.getFeature7();
        vector[7] = snippet.getFeature8();
        vector[8] = snippet.getFeature9();
        vector[9] = snippet.getFeature10();
        vector[10] = snippet.getFeature11();
        vector[11] = snippet.getFeature12();
        vector[12] = snippet.getFeature13();
        vector[13] = snippet.getFeature14();
        vector[14] = snippet.getFeature15();
        vector[15] = snippet.getFeature16();
        vector[16] = snippet.getFeature17();
        vector[17] = snippet.getFeature18();
        vector[18] = snippet.getFeature19();
        vector[19] = snippet.getFeature20();
        return vector;
    }

    public static double cosineSimilarity(DocumentTextLineSnippet snippet, DocumentTextLineSnippet targetSnippet, List<Integer> featuresToInclude) {
        double[] vectorA = getVector(snippet);
        double[] vectorB = getVector(targetSnippet);
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            if (featuresToInclude == null || featuresToInclude.contains(i + 1)) {
                dotProduct += vectorA[i] * vectorB[i];
                normA += Math.pow(vectorA[i], 2);
                normB += Math.pow(vectorB[i], 2);
            }
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static float calculateDistance(DocumentTextLineSnippet snippet, DocumentTextLineSnippet targetSnippet, List<Integer> featuresToInclude) {
        float total = 0;
        double[] vectorA = getVector(snippet);
        double[] vectorB = getVector(targetSnippet);
        for (int i = 0; i < vectorA.length; i++) {
            if (featuresToInclude == null || featuresToInclude.contains(i + 1)) {
                total += append(vectorA[i], vectorB[i]);
            }
        }
        return total;
    }

    private static double append(double feature1, double feature11) {
        return Math.pow(feature1 - feature11, 2);
    }

}
