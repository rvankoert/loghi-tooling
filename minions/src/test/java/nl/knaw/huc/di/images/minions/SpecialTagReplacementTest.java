package nl.knaw.huc.di.images.minions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialTagReplacementTest {
    public static void main(String[] args) {
        String testString = "Make sure to ␅u␅n␅d␅e␅r␅l␅i␅n␅e ␅t␅h␅i␅s ␅i␅m␅p␅o␅r␅t␅a␅n␅t ␅p␅o␅i␅n␅t␅.";
        processAndPrint(testString);
        String testString2 = "Make sure to ␅u␅n␅d␅e␅r␅l␅i␅n␅e this ␃i␃m␃p␃o␃r␃t␃a␃n␃t ␅p␅o␅i␅n␅t␅.";
        processAndPrint(testString2);
        String testString3 = "The ␃i␃n␃c␃o␃r␃r␃e␃c␃t formula is E=mc␆3, but make sure to ␅e␅m␅p␅h␅a␅s␅i␅z␅e the correct one: E=mc␆2 and water is H␄2␄O.";
        processAndPrint(testString3);
        String testString4 = "The ␃i␃n␃c␃o␃r␃rect formula is E=mc␆3, but make sure to ␅e␅m␅pha␅s␅i␅z␅e the correct one: E=mc␆2 and water is H␄2␄O.";
        processAndPrint(testString4);
        String testString5 = "The ␃i␃n␃c␃␅o␃r␃rect formula is E=mc␆3, but make sure to ␅e␅m␅pha␅s␅i␅z␅e the correct one: E=mc␆2 and water is H␄2␄O.";
        processAndPrint(testString5);

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
        System.out.println();

    }

    // Process underline and strikethrough markers in the text
    public static String processMarkers(String text) {
        // Process underline markers
        text = processSpecificMarker(text, "␅", "<u>", "</u>");
        // Process strikethrough markers
        text = processSpecificMarker(text, "␃", "<s>", "</s>");
        // Process subscript markers
        text = processSpecificMarker(text, "␄", "<sub>", "</sub>");
        // Process superscript markers
        text = processSpecificMarker(text, "␆", "<sup>", "</sup>");

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
        // Consolidate adjacent subscript tags
        text = consolidateSpecificTag(text, "<sub>", "</sub>");
        // Consolidate adjacent superscript tags
        text = consolidateSpecificTag(text, "<sup>", "</sup>");
        return text;
    }

    // Helper method for consolidating tags
    private static String consolidateSpecificTag(String text, String openTag, String closeTag) {
        // Construct regex to match adjacent tags of the same type
        String regex = "(" + Pattern.quote(openTag) + "[^<]*?" + Pattern.quote(closeTag) + "\\s?)+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            // Remove the opening and closing tags, then consolidate the content
            String matchedSequence = matcher.group(0);
            // Removing all occurrences of the open and close tags within the matched sequence
            // And also collapsing consecutive spaces into a single space
            String consolidatedText = matchedSequence
                    .replaceAll(Pattern.quote(openTag), "")
                    .replaceAll(Pattern.quote(closeTag), "")
                    .trim();

            // Determine if a space should be added after the consolidated group
            int endOfMatch = matcher.end();
            boolean shouldAddSpace = endOfMatch < text.length()
                    && text.charAt(endOfMatch) != ','
                    && text.charAt(endOfMatch) != '.';

            // Append the consolidated text with open and close tags, and conditionally add a space
            String replacement = openTag + consolidatedText + closeTag + (shouldAddSpace ? " " : "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}

