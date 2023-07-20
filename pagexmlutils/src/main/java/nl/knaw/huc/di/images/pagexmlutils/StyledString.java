package nl.knaw.huc.di.images.pagexmlutils;

import java.util.ArrayList;
import java.util.List;

public class StyledString {
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

    public void applyStyle(int offset, int length, String styleChar) {
        for (int i = offset; i < (offset + length); i++ ) {
            styledCharList.get(i).applyStyle(styleChar);
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        styledCharList.forEach(stringBuilder::append);

        return stringBuilder.toString();
    }



    private static class StyledChar {
        private final char character;
        private final List<String> styles;

        public StyledChar(char character) {
            this.character = character;
            this.styles = new ArrayList<>();
        }

        public void applyStyle(String styleCharacter){
            styles.add(styleCharacter);
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
        private final String styleCharacter;

        public StringStyle(int offset, int length, String styleCharacter) {
            this.offset = offset;
            this.length = length;
            this.styleCharacter = styleCharacter;
        }

    }
}
