package nl.knaw.huc.di.images.minions;


import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import nl.knaw.huc.di.images.pagexmlutils.GroundTruthTextLineFormatter;
import nl.knaw.huc.di.images.pagexmlutils.StyledString;
import org.junit.jupiter.api.Test;

import static nl.knaw.huc.di.images.pagexmlutils.StyledString.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class MinionLoghiHTRMergePageXMLTest {

    @Test
    public void checkDetectSuperscript() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(SUPERSCRIPTCHAR + "t" + SUPERSCRIPTCHAR + "e");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), contains(allOf(
                containsString("superscript:true"),
                containsString("offset:0"),
                containsString("length:2")
        )));
    }

    @Test
    public void checkDetectSuperscriptWithOffset() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("t" + SUPERSCRIPTCHAR + "e");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;
        String custom = result.getTextStyles().get(0);
        final String textStyle = custom.substring(custom.indexOf("textStyle"));

        assertThat(textStyle, allOf(
                containsString("superscript:true"),
                containsString("offset:1"),
                containsString("length:1")
        ));
    }

    @Test
    public void checkDetectMultipleSuperscriptParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + SUPERSCRIPTCHAR + "i" + SUPERSCRIPTCHAR + "s" + " een " + SUPERSCRIPTCHAR + "t" + SUPERSCRIPTCHAR + "e" + SUPERSCRIPTCHAR + "s" + SUPERSCRIPTCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("superscript:true"),
                        containsString("offset:4"),
                        containsString("length:2")
                ),
                allOf(containsString("superscript:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectUnderlined() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("t" + UNDERLINECHAR + "e");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), contains(allOf(
                containsString("underlined:true"),
                containsString("offset:1"),
                containsString("length:1")
        )));
    }

    @Test
    public void checkDetectMultipleUnderlineParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + UNDERLINECHAR + "i" + UNDERLINECHAR + "s" + " een " + UNDERLINECHAR + "t" + UNDERLINECHAR + "e" + UNDERLINECHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("underlined:true"),
                        containsString("offset:4"),
                        containsString("length:2")
                ),
                allOf(containsString("underlined:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectMultipleSubscriptParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + SUBSCRIPTCHAR + "i" + SUBSCRIPTCHAR + "s" + " een " + SUBSCRIPTCHAR + "t" + SUBSCRIPTCHAR + "e" + SUBSCRIPTCHAR + "s" + SUBSCRIPTCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("subscript:true"),
                        containsString("offset:4"),
                        containsString("length:2")
                ),
                allOf(containsString("subscript:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectMultipleStrikeThroughParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + STRIKETHROUGHCHAR + "i" + STRIKETHROUGHCHAR + "s" + " een " + STRIKETHROUGHCHAR + "t" + STRIKETHROUGHCHAR + "e" + STRIKETHROUGHCHAR + "s" + STRIKETHROUGHCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("strikethrough:true"),
                        containsString("offset:4"),
                        containsString("length:2")
                ),
                allOf(containsString("strikethrough:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectMultipleStyles() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(SUPERSCRIPTCHAR + "Dit " + STRIKETHROUGHCHAR + "i" + STRIKETHROUGHCHAR + "s" + " een " + UNDERLINECHAR + "t" + UNDERLINECHAR + "e" + UNDERLINECHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("superscript:true"),
                        containsString("offset:0"),
                        containsString("length:1")
                ),
                allOf(containsString("strikethrough:true"),
                        containsString("offset:4"),
                        containsString("length:2")
                ),
                allOf(containsString("underlined:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectMultipleStylesConnecting() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(SUBSCRIPTCHAR + "D" + SUBSCRIPTCHAR + "i" + SUBSCRIPTCHAR + "t " + STRIKETHROUGHCHAR + "i" + STRIKETHROUGHCHAR + "s" + " een " + UNDERLINECHAR + "t" + UNDERLINECHAR + "e" + UNDERLINECHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("subscript:true"),
                        containsString("offset:0"),
                        containsString("length:3")
                ),
                allOf(containsString("strikethrough:true"),
                        containsString("offset:4"),
                        containsString("length:2")
                ),
                allOf(containsString("underlined:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectMultipleStylesOnSameCharacteres() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + "is" + " een " + UNDERLINECHAR + STRIKETHROUGHCHAR + "t" + UNDERLINECHAR + STRIKETHROUGHCHAR + "e" + UNDERLINECHAR + STRIKETHROUGHCHAR + "s" + UNDERLINECHAR + STRIKETHROUGHCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("strikethrough:true"),
                        containsString("underlined:true"),
                        containsString("offset:11"),
                        containsString("length:4")
                )
        ));
    }

    @Test
    public void checkDetectMultipleStylesWithinAStyle() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + "is" + " een " + STRIKETHROUGHCHAR + "t" + UNDERLINECHAR + STRIKETHROUGHCHAR + "e" + UNDERLINECHAR + STRIKETHROUGHCHAR + "s" + STRIKETHROUGHCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("strikethrough:true"),
                        containsString("offset:11"),
                        containsString("length:1")
                ),
                allOf(containsString("underlined:true"),
                        containsString("strikethrough:true"),
                        containsString("offset:12"),
                        containsString("length:2")
                ),
                allOf(containsString("strikethrough:true"),
                        containsString("offset:14"),
                        containsString("length:1")
                )
        ));
    }

    @Test
    public void checkDetectMultipleStylesPartialOverlap() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + "is" + " een " + STRIKETHROUGHCHAR + "t" + UNDERLINECHAR + STRIKETHROUGHCHAR + "e" + UNDERLINECHAR + STRIKETHROUGHCHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), hasItems(
                allOf(containsString("strikethrough:true"),
                        containsString("offset:11"),
                        containsString("length:1")
                ),
                allOf(containsString("underlined:true"),
                        containsString("strikethrough:true"),
                        containsString("offset:12"),
                        containsString("length:2")
                ),
                allOf(containsString("underlined:true"),
                        containsString("offset:14"),
                        containsString("length:1")
                )
        ));
    }

    @Test
    public void checkDetectWithoutStyles() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit is een test");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        TextLineCustom result = textLineCustom;

        assertThat(result.getTextStyles(), is(nullValue()));
    }

    @Test
    public void checkDetectMultipleStylesPartialOverlapFullCircleTest() {
        final TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Dit is een test"));
        final String custom = "textStyle {offset:7; length:5;strikethrough:true;} textStyle {offset:4; length:5;underlined:true;}";
        textLine.setCustom(custom);
        final String textRepresentation = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);

        TextLineCustom textLineCustom1 = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(textRepresentation);
        styledString.getStyles().forEach(style -> textLineCustom1.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        final TextLineCustom textLineCustom = textLineCustom1;

        final TextLine textLine2 = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Dit is een test"));
        textLine2.setCustom(textLineCustom.toString());

        final String textRepresentation2 = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);


        assertThat(textRepresentation, is(textRepresentation2));
    }

}