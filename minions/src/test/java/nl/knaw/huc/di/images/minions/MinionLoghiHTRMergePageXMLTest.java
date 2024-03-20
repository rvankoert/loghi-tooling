package nl.knaw.huc.di.images.minions;


import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import nl.knaw.huc.di.images.pagexmlutils.GroundTruthTextLineFormatter;
import nl.knaw.huc.di.images.pagexmlutils.StyledString;
import org.junit.jupiter.api.Test;

import static nl.knaw.huc.di.images.pagexmlutils.StyledString.fromStringWithStyleCharacters;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MinionLoghiHTRMergePageXMLTest {


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

    @Test
    public void getOldStyleResultLineTest() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("nl-hanatest.jpg\t0.10\tDit is een testtranscriptie");
        assertThat(result.getFilename(), is("nl-hanatest.jpg"));
    }

//    @Test
//    public void getNewStyleResultLineTest() {
//        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("nl-hanatest.jpg\t{hier was json}\t0.10\tDit is een testtranscriptie");
//        assertThat(result.getFilename(), is("nl-hanatest.jpg"));
//    }
}
