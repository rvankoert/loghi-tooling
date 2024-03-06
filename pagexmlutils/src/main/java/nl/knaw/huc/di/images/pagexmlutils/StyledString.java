package nl.knaw.huc.di.images.pagexmlutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StyledString {
    public static String STRIKETHROUGHCHAR = "␃"; //Unicode Character “␃” (U+2403)
    public static String UNDERLINECHAR = "␅"; //Unicode Character “␅” (U+2405)
    public static String SUBSCRIPTCHAR = "␄"; // Unicode Character “␄” (U+2404)
    public static String SUPERSCRIPTCHAR = "␆"; // Unicode Character “␆” (U+2406)
    private static final Map<String, String> CHARACTER_STYLE_MAP = Map.of(
            SUPERSCRIPTCHAR, "superscript",
            UNDERLINECHAR, "underlined",
            SUBSCRIPTCHAR, "subscript",
            STRIKETHROUGHCHAR, "strikethrough"
    );

    private static final Map<String, String> STYLE_CHARACTER_MAP = Map.of(
            "superscript", SUPERSCRIPTCHAR,
            "underlined", UNDERLINECHAR,
            "subscript", SUBSCRIPTCHAR,
            "strikethrough", STRIKETHROUGHCHAR
    );

    private final List<StyledChar> styledCharList;

    private StyledString(List<StyledChar> styledCharList) {

        this.styledCharList = styledCharList;
    }

    public static StyledString fromString(String from) {
        List<StyledChar> styledCharList = new ArrayList<>();
        for (char character : from.toCharArray()) {
            styledCharList.add(new StyledChar(character));
        }

        return new StyledString(styledCharList);
    }

    public static StyledString fromStringWithStyleCharacters(String stringWithStyles) {
        final List<StyledChar> styledCharList = new ArrayList<>(); // TODO make sortedset
        List<String> styles = new ArrayList<>();
        for (char character : stringWithStyles.toCharArray()) {
            final String stringOfCharacter = String.valueOf(character);
            if (CHARACTER_STYLE_MAP.containsKey(stringOfCharacter)) {
                styles.add(stringOfCharacter);
            } else {
                styledCharList.add(new StyledChar(character, styles));
                styles = new ArrayList<>();
            }
        }
        return new StyledString(styledCharList);
    }

    public static boolean isAllowedStyle(String style) {
        return STYLE_CHARACTER_MAP.containsKey(style);
    }

    public static String applyHtmlTagging(String text) {
        // Print the original text
        System.out.println("Original: ");
        System.out.println(text);

        // Process the text for underline and strikethrough markers
        String processedText = processMarkers(text);
        System.out.println("Processed: ");
        System.out.println(processedText);

        // Consolidate adjacent underline and strikethrough tags
        String consolidatedText = consolidateTags(processedText);
        System.out.println("Consolidated: ");
        System.out.println(consolidatedText);

        return consolidatedText;
    }

    // Process underline and strikethrough markers in the text
    public static String processMarkers(String text) {
        // Process underline markers
        text = processSpecificMarker(text, "␅", "<u>", "</u>");
        // Process strikethrough markers
        text = processSpecificMarker(text, "␃", "<s>", "</s>");
        // Process subscript
        // TODO text = processSpecificMarker(text, "␄", "<sub>", "</sub>");
        // Process superscript
        // TODO text = processSpecificMarker(text, "␆", "<sup>", "</sup>");

        // TODO HANDLE STRIKETHROUGH + UNDERLINE COMBINATIONS

        return text;
    }

    public static String applyHtmlTaggingWithRegex(String text) {
        // Define the mappings from style characters to HTML tags
        HashMap<String, String[]> tagMap = new HashMap<>();
        tagMap.put("␃", new String[]{"<s>", "</s>"});
        tagMap.put("␅", new String[]{"<u>", "</u>"});
        tagMap.put("␄", new String[]{"<sub>", "</sub>"});
        tagMap.put("␆", new String[]{"<sup>", "</sup>"});

        // Process each style
        for (Map.Entry<String, String[]> entry : tagMap.entrySet()) {
            String styleChar = entry.getKey();
            String[] tags = entry.getValue();

            // Construct a regex pattern that matches sequences of the style character followed by any character
            String pattern = styleChar + "(.)";

            // Use regex to find matches and replace them with the corresponding HTML tag wrapped around the characters
            text = text.replaceAll(pattern + "+", matchResult -> {
                // Extract the text that was styled
                String styledText = matchResult.group().replaceAll(styleChar, "");
                // Wrap the styled text with the appropriate HTML tags
                return tags[0] + styledText + tags[1];
            });
        }

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

    public List<StringStyle> getStyles() {
        List<StringStyle>  stringStyles = new ArrayList<>();
        List<String> currentStyle = null;
        int styleLength = 0;
        int styleOffset = 0;
        for (int i = 0; i < styledCharList.size(); i++) {
            final StyledChar styledChar = styledCharList.get(i);
            final List<String> charStyle = styledChar.styles;
            if (currentStyle == null && !charStyle.isEmpty()) {
                currentStyle = charStyle;
                styleOffset = i;
            }

            if (currentStyle != null) {
                if (currentStyle.equals(charStyle)) {
                    styleLength++;
                } else  {
                    final List<String> styleNames = currentStyle.stream().map(CHARACTER_STYLE_MAP::get).collect(Collectors.toList());
                    stringStyles.add(new StringStyle(styleOffset, styleLength, styleNames));
                    currentStyle = charStyle;
                    styleLength = 1;
                    styleOffset = i;
                }
            }
        }

        if (currentStyle != null && !currentStyle.isEmpty()) {
            final List<String> styleNames = currentStyle.stream().map(CHARACTER_STYLE_MAP::get).collect(Collectors.toList());
            stringStyles.add(new StringStyle(styleOffset, styleLength, styleNames));
        }

        return stringStyles;
    }

    public void applyStyles(int offset, int length, List<String> styles) {
        for (int i = offset; i < (offset + length); i++) {
            final List<String> styleChars = styles.stream().map(STYLE_CHARACTER_MAP::get).collect(Collectors.toList());
            styledCharList.get(i).applyStyles(styleChars);
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        styledCharList.forEach(stringBuilder::append);

        return stringBuilder.toString();
    }

    public String getCleanText() {
        final StringBuilder stringBuilder = new StringBuilder();
        styledCharList.forEach(character -> stringBuilder.append(character.character));
        return stringBuilder.toString();
    }

    public static class StyledChar {
        private final char character;
        private final List<String> styles; // TODO make sortedset

        public StyledChar(char character) {
            this.character = character;
            this.styles = new ArrayList<>();
        }

        public StyledChar(char character, List<String> styles) {
            this.character = character;
            this.styles = styles;
        }

        public void applyStyles(List<String> styleCharacter) {
            styles.addAll(styleCharacter);
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder();
            styles.forEach(stringBuilder::append);
            stringBuilder.append(character);

            return stringBuilder.toString();
        }
    }

    public static class StringStyle {
        private final int offset;
        private final int length;
        private final List<String> styles; // stylenames

        public StringStyle(int offset, int length, List<String> styles) {
            this.offset = offset;
            this.length = length;
            this.styles = styles;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public List<String> getStyles() {
            return styles;
        }

        @Override
        public String toString() {
            return "StringStyle{" +
                    "offset=" + offset +
                    ", length=" + length +
                    ", styles=" + styles +
                    '}';
        }
    }
}
