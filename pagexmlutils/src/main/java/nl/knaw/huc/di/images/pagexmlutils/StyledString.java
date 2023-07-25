package nl.knaw.huc.di.images.pagexmlutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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