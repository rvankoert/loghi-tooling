package nl.knaw.huc.di.images.minions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialTagReplacementTest {
    public static void main(String[] args) {
        String testString = "Make sure to ␅u␅n␅d␅e␅r␅l␅i␅n␅e ␅t␅h␅i␅s ␅i␅m␅p␅o␅r␅t␅a␅n␅t ␅p␅o␅i␅n␅t␅.";
        processAndPrint(testString);
        String testString2 = "Make sure to ␅u␅n␅d␅e␅r␅l␅i␅n␅e this ␃i␃m␃p␃o␃r␃t␃a␃n␃t ␅p␅o␅i␅n␅t␅.";
        processAndPrint(testString2);
    }

    private static void processAndPrint(String testString) {
        // Print the original text
        System.out.println("Original: ");
        System.out.println(testString);

        // Process the text for underline and strikethrough markers
        String processedText = processMarkers(testString);
        System.out.println("Processed: ");
        System.out.println(processedText);

        // Consolidate adjacent underline and strikethrough tags
        String consolidatedText = consolidateTags(processedText);
        System.out.println("Consolidated: ");
        System.out.println(consolidatedText);
    }

    // Process underline and strikethrough markers in the text
    public static String processMarkers(String text) {
        // Process underline markers
        text = processSpecificMarker(text, "␅", "<u>", "</u>");
        // Process strikethrough markers
        text = processSpecificMarker(text, "␃", "<s>", "</s>");
        return text;
    }

    // Helper method to process specific marker types
    private static String processSpecificMarker(String text, String marker, String openTag, String closeTag) {
        // Construct regex for the marker
        String regex = marker + "(.?)";
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while (m.find()) {
            // Remove marker symbols and wrap the content with the specified HTML tags
            String groupContent = m.group(1).replaceAll(Pattern.quote(marker), "");
            m.appendReplacement(sb, Matcher.quoteReplacement(openTag + groupContent + closeTag));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    // Consolidate adjacent tags of the same type
    public static String consolidateTags(String text) {
        // Consolidate adjacent underline tags
        text = consolidateSpecificTag(text, "<u>", "</u>");
        // Consolidate adjacent strikethrough tags
        text = consolidateSpecificTag(text, "<s>", "</s>");
        return text;
    }

    // Helper method for consolidating tags
    private static String consolidateSpecificTag(String text, String openTag, String closeTag) {
        // Construct regex to match adjacent tags of the same type
        String regex = "(" + Pattern.quote(openTag) + "[^<]+" + Pattern.quote(closeTag) + "\\s*)+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            // Remove the opening and closing tags, then consolidate the content
            String matchedGroup = matcher.group(0);
            String consolidatedText = matchedGroup.replaceAll(Pattern.quote(openTag) + "|" + Pattern.quote(closeTag), "").trim();
            // Replace the original sequence with a single tag pair wrapping the consolidated text
            matcher.appendReplacement(sb, Matcher.quoteReplacement(openTag + consolidatedText + closeTag + " "));
        }
        matcher.appendTail(sb);

        // Trim to remove trailing spaces after replacements
        return sb.toString().trim();
    }
}

