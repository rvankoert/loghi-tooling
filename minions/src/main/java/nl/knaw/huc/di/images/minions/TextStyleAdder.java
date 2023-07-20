package nl.knaw.huc.di.images.minions;

import com.google.common.collect.Lists;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import nl.knaw.huc.di.images.pagexmlutils.GroundTruthTextLineFormatter;

import java.util.List;


public class TextStyleAdder {
    private static final List<String> SPECIAL_CHARACTERS = Lists.newArrayList(GroundTruthTextLineFormatter.SUPERSCRIPTCHAR, GroundTruthTextLineFormatter.UNDERLINECHAR, GroundTruthTextLineFormatter.SUBSCRIPTCHAR, GroundTruthTextLineFormatter.STRIKETHROUGHCHAR);

    public static void addTextStyleToTextLineCustom(TextLineCustom textLineCustom, String rawText, String styleCharacter, String styleName) {
        int stylePosition = 0;
        String cleanText = "";
        boolean stylePreviousFound = false;
        int styleLength = 0;
        int nonSpecialCharacterCount = 0;
        for (char character : rawText.toCharArray()) {
            final String stringOfCharacter = String.valueOf(character);
            if (styleCharacter.equals(stringOfCharacter)) {
                stylePreviousFound = true;
                styleLength++;
                nonSpecialCharacterCount = 0;
            } else if (!SPECIAL_CHARACTERS.contains(stringOfCharacter)) {
                nonSpecialCharacterCount++;
                if (stylePreviousFound && nonSpecialCharacterCount >= 2) {
                    textLineCustom.addCustomTextStyle(styleName, stylePosition, styleLength);
                    stylePreviousFound = false;
                    stylePosition = cleanText.length() + 1;
                    styleLength = 0;
                    nonSpecialCharacterCount = 0;
                } else if (!stylePreviousFound) {
                    stylePosition++;
                }
                cleanText += stringOfCharacter;
            }
        }
        if (stylePreviousFound) {
            textLineCustom.addCustomTextStyle(styleName, stylePosition, styleLength);
        }
    }

}
