package nl.knaw.huc.di.images.layoutds.models.Page;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class TextLineCustomTest {
    @Test
    public void addCustomTextStyleAddsATextStyle() {
        final TextLineCustom textLineCustom = new TextLineCustom();
        textLineCustom.addCustomTextStyle("underlined", 0, 2);

        assertThat(textLineCustom.getTextStyles(), contains("textStyle {offset:0; length:2;underlined:true;}"));
    }

    @Test
    public void addCustomTextStyleAddsMultipleTextStyle() {

        final TextLineCustom textLineCustom = new TextLineCustom();
        textLineCustom.addCustomTextStyle(Lists.newArrayList("underlined", "superscript"), 0, 2);

        assertThat(textLineCustom.getTextStyles(), contains("textStyle {offset:0; length:2;underlined:true;superscript:true;}"));
    }
}