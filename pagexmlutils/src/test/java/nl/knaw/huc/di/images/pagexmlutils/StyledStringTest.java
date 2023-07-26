package nl.knaw.huc.di.images.pagexmlutils;

import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nl.knaw.huc.di.images.pagexmlutils.StyledString.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class StyledStringTest {

    @Test
    public void checkDetectSuperscriptWithOffset() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("t" + SUPERSCRIPTCHAR + "e");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), contains(stringStyle()
                .withStyle("superscript")
                .withOffset(1)
                .withLength(1)));
    }

    @Test
    public void checkDetectMultipleSuperscriptParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + SUPERSCRIPTCHAR + "i" + SUPERSCRIPTCHAR + "s" + " een " + SUPERSCRIPTCHAR + "t" + SUPERSCRIPTCHAR + "e" + SUPERSCRIPTCHAR + "s" + SUPERSCRIPTCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("superscript")
                        .withOffset(4)
                        .withLength(2),
                stringStyle().withStyle("superscript")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectUnderlined() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("t" + UNDERLINECHAR + "e");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), contains(stringStyle()
                .withStyle("underlined")
                .withOffset(1)
                .withLength(1)));
    }

    @Test
    public void checkDetectMultipleUnderlineParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + UNDERLINECHAR + "i" + UNDERLINECHAR + "s" + " een " + UNDERLINECHAR + "t" + UNDERLINECHAR + "e" + UNDERLINECHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("underlined")
                        .withOffset(4)
                        .withLength(2),
                stringStyle().withStyle("underlined")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectMultipleSubscriptParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + SUBSCRIPTCHAR + "i" + SUBSCRIPTCHAR + "s" + " een " + SUBSCRIPTCHAR + "t" + SUBSCRIPTCHAR + "e" + SUBSCRIPTCHAR + "s" + SUBSCRIPTCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("subscript")
                        .withOffset(4)
                        .withLength(2),
                stringStyle().withStyle("subscript")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectSuperscript() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(SUPERSCRIPTCHAR + "t" + SUPERSCRIPTCHAR + "e");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), contains(stringStyle()
                .withStyle("superscript")
                .withOffset(0)
                .withLength(2)));
    }

    @Test
    public void checkDetectMultipleStrikeThroughParts() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + STRIKETHROUGHCHAR + "i" + STRIKETHROUGHCHAR + "s" + " een " + STRIKETHROUGHCHAR + "t" + STRIKETHROUGHCHAR + "e" + STRIKETHROUGHCHAR + "s" + STRIKETHROUGHCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("strikethrough")
                        .withOffset(4)
                        .withLength(2),
                stringStyle().withStyle("strikethrough")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectMultipleStyles() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(SUPERSCRIPTCHAR + "Dit " + STRIKETHROUGHCHAR + "i" + STRIKETHROUGHCHAR + "s" + " een " + UNDERLINECHAR + "t" + UNDERLINECHAR + "e" + UNDERLINECHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("superscript")
                        .withOffset(0)
                        .withLength(1),
                stringStyle().withStyle("strikethrough")
                        .withOffset(4)
                        .withLength(2),
                stringStyle().withStyle("underlined")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectMultipleStylesConnecting() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters(SUBSCRIPTCHAR + "D" + SUBSCRIPTCHAR + "i" + SUBSCRIPTCHAR + "t " + STRIKETHROUGHCHAR + "i" + STRIKETHROUGHCHAR + "s" + " een " + UNDERLINECHAR + "t" + UNDERLINECHAR + "e" + UNDERLINECHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("subscript")
                        .withOffset(0)
                        .withLength(3),
                stringStyle().withStyle("strikethrough")
                        .withOffset(4)
                        .withLength(2),
                stringStyle().withStyle("underlined")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectMultipleStylesOnSameCharacteres() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + "is" + " een " + UNDERLINECHAR + STRIKETHROUGHCHAR + "t" + UNDERLINECHAR + STRIKETHROUGHCHAR + "e" + UNDERLINECHAR + STRIKETHROUGHCHAR + "s" + UNDERLINECHAR + STRIKETHROUGHCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("strikethrough")
                        .withStyle("underlined")
                        .withOffset(11)
                        .withLength(4)
        ));
    }

    @Test
    public void checkDetectMultipleStylesWithinAStyle() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + "is" + " een " + STRIKETHROUGHCHAR + "t" + UNDERLINECHAR + STRIKETHROUGHCHAR + "e" + UNDERLINECHAR + STRIKETHROUGHCHAR + "s" + STRIKETHROUGHCHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("strikethrough")
                        .withOffset(11)
                        .withLength(1),
                stringStyle().withStyle("underlined")
                        .withStyle("strikethrough")
                        .withOffset(12)
                        .withLength(2),
                stringStyle().withStyle("strikethrough")
                        .withOffset(14)
                        .withLength(1)
        ));
    }

    @Test
    public void checkDetectMultipleStylesPartialOverlap() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit " + "is" + " een " + STRIKETHROUGHCHAR + "t" + UNDERLINECHAR + STRIKETHROUGHCHAR + "e" + UNDERLINECHAR + STRIKETHROUGHCHAR + "s" + UNDERLINECHAR + "t");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), hasItems(
                stringStyle().withStyle("strikethrough")
                        .withOffset(11)
                        .withLength(1),
                stringStyle().withStyle("underlined")
                        .withStyle("strikethrough")
                        .withOffset(12)
                        .withLength(2),
                stringStyle().withStyle("underlined")
                        .withOffset(14)
                        .withLength(1)
        ));
    }

    @Test
    public void checkDetectWithoutStyles() {
        TextLineCustom textLineCustom = new TextLineCustom();

        final StyledString styledString = fromStringWithStyleCharacters("Dit is een test");
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));

        assertThat(styledString.getStyles(), is(empty()));
    }

    StringStyleMatcher stringStyle(){
        return new StringStyleMatcher();
    }

    class StringStyleMatcher extends TypeSafeMatcher<StringStyle> {

        private List<String> styles;
        private int offset;
        private int length;

        public StringStyleMatcher() {
            styles = new ArrayList<>();
        }

        public StringStyleMatcher withStyle(String style) {
            this.styles.add(style);

            return this;
        }

        public StringStyleMatcher withOffset(int offset) {
            this.offset = offset;

            return this;
        }

        public StringStyleMatcher withLength(int length) {
            this.length = length;
            return this;
        }

        @Override
        protected boolean matchesSafely(StringStyle stringStyle) {

            return styles.containsAll(stringStyle.getStyles()) && offset == stringStyle.getOffset()
                    && length == stringStyle.getLength();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("StringStyle with styles ").appendValue(styles)
                    .appendText(" with offset ").appendValue(offset)
                    .appendText(" and length ").appendValue(length);
        }

    }

}