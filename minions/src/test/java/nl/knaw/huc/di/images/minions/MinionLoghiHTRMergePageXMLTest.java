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

    // OLD STYLE TESTS MinionLoghiHTRMergePageXML

    @Test
    public void getOldStyleResultLineTest() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("nl-hanatest.png\t0.10\tDit is een testtranscriptie");
        assertThat(result.getFilename(), is("nl-hanatest"));
        assertThat(result.getMetadata(), is("[]")); // should be [] if not provided
        assertThat(result.getConfidence(), is(0.10)); // confidence is a double
        assertThat(result.getText().toString(), is("Dit is een testtranscriptie"));
    }

    @Test
    public void getOldStyleResultLineTestRealistic() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_28_0791-line_34324324322.png\t0.7871581315994263\tdit is een andere teststring");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_28_0791-line_34324324322"));
        assertThat(result.getMetadata(), is("[]")); // should be [] if not provided
        assertThat(result.getConfidence(), is(0.7871581315994263)); // confidence is a double
        assertThat(result.getText().toString(), is("dit is een andere teststring"));
    }

    @Test
    public void getOldStyleResultLineTestRealisticEmptyPred() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_28_0791-line_34324324322.png\t0.7871581315994263\t");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_28_0791-line_34324324322"));
        assertThat(result.getMetadata(), is("[]")); // should be [] if not provided
        assertThat(result.getConfidence(), is(0.7871581315994263)); // confidence is a double
        assertThat(result.getText().toString(), is(""));
    }

    // NEW STYLE TESTS MinionLoghiHTRMergePageXML

    @Test
    public void getNewStyleResultLineTest() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("nl-hanatest.png\t{hier was json}\t0.10\tDit is een testtranscriptie");
        assertThat(result.getFilename(), is("nl-hanatest"));
        assertThat(result.getMetadata(), is("{hier was json}"));
        assertThat(result.getConfidence(), is(0.10)); // confidence is a double
        assertThat(result.getText().toString(), is("Dit is een testtranscriptie"));
    }

    @Test
    public void getNewStyleResultLineTestRealistic() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\tnog een andere *3 variatie");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(result.getText().toString(), is("nog een andere *3 variatie"));
    }

    @Test
    public void getNewStyleResultLineTestRealisticEmptyPred() {
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(result.getText().toString(), is(""));
    }

}