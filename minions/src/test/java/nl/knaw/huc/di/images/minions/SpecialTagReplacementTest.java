package nl.knaw.huc.di.images.minions;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
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
        String testString6 = "␃E␃o␆␄␃s␆␃. ␆␃L␃eukoc.";
        processAndPrint(testString6);
    }

    private static void processAndPrint(String testString) {
        // Print the original text
        System.out.println("Original: ");
        System.out.println(testString);

        // Process the text for underline and strikethrough markers
        String processedText = processMarkers(testString);
        System.out.println("Processed: ");
        System.out.println(processedText);

        System.out.println();

    }

    // Process underline and strikethrough markers in the text
    public static String processMarkers(String text) {

        text = processSpecificMarker(text, "␅", "<u>", "</u>");
        text = processSpecificMarker(text, "␃", "<s>", "</s>");
        text = processSpecificMarker(text, "␄", "<sub>", "</sub>");
        text = processSpecificMarker(text, "␆", "<sup>", "</sup>");

        return text;
    }

    // Helper method to process specific marker types
    private static String processSpecificMarker(String text, String marker, String openTag, String closeTag) {
        // Construct regex for the marker
        String tagStarts = "(<sub>|<s>|<u>|<sup>)?";
        String tagEndings = "(</sub>|</s>|</u>|</sup>)?";
        String regex = "(" + marker + tagStarts + "[^<]" + tagEndings + ")+";

        // Create a string builder to store the processed text
        StringBuilder sb = new StringBuilder();

        // Create a pattern and matcher to find the marker in the text
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while (m.find()) {
            // Remove marker symbols and wrap the content with the specified HTML tags
            String groupContent = m.group(0).replaceAll(Pattern.quote(marker), "");
            m.appendReplacement(sb, Matcher.quoteReplacement(openTag + groupContent + closeTag));
        }
        m.appendTail(sb);

        // Consolidate tags and return the processed text
        String regex2 = "(" + closeTag + ")( ?[␅␃␄␆]*)(" + openTag + ")";
        return sb.toString().replaceAll(regex2, "$2");
    }
}

