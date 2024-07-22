package nl.knaw.huc.di.images.layoutds.models.Page;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

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

    @Test
    public void addReadingOrder() {

        TextLineCustom textLineCustom = new TextLineCustom();
        textLineCustom.addCustomTextStyle(Lists.newArrayList("underlined", "superscript"), 0, 2);
        textLineCustom.setReadingOrder("readingOrder {index:1;}");

        Assert.assertEquals("readingOrder {index:1;} textStyle {offset:0; length:2;underlined:true;superscript:true;}", textLineCustom.toString());

        textLineCustom = new TextLineCustom();
        textLineCustom.setReadingOrder("readingOrder {index:1;} ");

        Assert.assertEquals("readingOrder {index:1;}", textLineCustom.toString());
    }
}