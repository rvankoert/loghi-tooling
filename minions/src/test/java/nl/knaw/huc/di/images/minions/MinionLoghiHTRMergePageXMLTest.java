package nl.knaw.huc.di.images.minions;


import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import nl.knaw.huc.di.images.pagexmlutils.StyledString;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class MinionLoghiHTRMergePageXMLTest {

    @Test
    public void checkDetectSuperscript() {
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom(StyledString.SUPERSCRIPTCHAR + "t" + StyledString.SUPERSCRIPTCHAR + "e");

        assertThat(result.getTextStyles(), contains(allOf(
                containsString("superscript:true"),
                containsString("offset:0"),
                containsString("length:2")
        )));
    }

    @Test
    public void checkDetectSuperscriptWithOffset() {
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("t" + StyledString.SUPERSCRIPTCHAR + "e");
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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + StyledString.SUPERSCRIPTCHAR + "i" + StyledString.SUPERSCRIPTCHAR + "s" + " een " + StyledString.SUPERSCRIPTCHAR + "t" + StyledString.SUPERSCRIPTCHAR + "e" + StyledString.SUPERSCRIPTCHAR + "s" + StyledString.SUPERSCRIPTCHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("t" + StyledString.UNDERLINECHAR + "e");

        assertThat(result.getTextStyles(), contains(allOf(
                containsString("underlined:true"),
                containsString("offset:1"),
                containsString("length:1")
        )));
    }

    @Test
    public void checkDetectMultipleUnderlineParts() {
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + StyledString.UNDERLINECHAR + "i" + StyledString.UNDERLINECHAR + "s" + " een " + StyledString.UNDERLINECHAR + "t" + StyledString.UNDERLINECHAR + "e" + StyledString.UNDERLINECHAR + "s" + StyledString.UNDERLINECHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + StyledString.SUBSCRIPTCHAR + "i" + StyledString.SUBSCRIPTCHAR + "s" + " een " + StyledString.SUBSCRIPTCHAR + "t" + StyledString.SUBSCRIPTCHAR + "e" + StyledString.SUBSCRIPTCHAR + "s" + StyledString.SUBSCRIPTCHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + StyledString.STRIKETHROUGHCHAR + "i" + StyledString.STRIKETHROUGHCHAR + "s" + " een " + StyledString.STRIKETHROUGHCHAR + "t" + StyledString.STRIKETHROUGHCHAR + "e" + StyledString.STRIKETHROUGHCHAR + "s" + StyledString.STRIKETHROUGHCHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom(StyledString.SUPERSCRIPTCHAR + "Dit " + StyledString.STRIKETHROUGHCHAR + "i" + StyledString.STRIKETHROUGHCHAR + "s" + " een " + StyledString.UNDERLINECHAR + "t" + StyledString.UNDERLINECHAR + "e" + StyledString.UNDERLINECHAR + "s" + StyledString.UNDERLINECHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom(StyledString.SUBSCRIPTCHAR + "D" + StyledString.SUBSCRIPTCHAR + "i" + StyledString.SUBSCRIPTCHAR + "t " + StyledString.STRIKETHROUGHCHAR + "i" + StyledString.STRIKETHROUGHCHAR + "s" + " een " + StyledString.UNDERLINECHAR + "t" + StyledString.UNDERLINECHAR + "e" + StyledString.UNDERLINECHAR + "s" + StyledString.UNDERLINECHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + "is" + " een " + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "t" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "e" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "s" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + "is" + " een " + StyledString.STRIKETHROUGHCHAR + "t" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "e" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "s" + StyledString.STRIKETHROUGHCHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit " + "is" + " een " + StyledString.STRIKETHROUGHCHAR + "t" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "e" + StyledString.UNDERLINECHAR + StyledString.STRIKETHROUGHCHAR + "s" + StyledString.UNDERLINECHAR + "t");

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
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom("Dit is een test");

        assertThat(result.getTextStyles(), is(nullValue()));
    }

}