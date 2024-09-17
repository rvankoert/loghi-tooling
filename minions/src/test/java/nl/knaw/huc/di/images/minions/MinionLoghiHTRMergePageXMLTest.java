package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import nl.knaw.huc.di.images.pagexmlutils.GroundTruthTextLineFormatter;
import nl.knaw.huc.di.images.pagexmlutils.StyledString;
import org.json.simple.parser.ParseException;
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

    // Test for converting transcription text containing tag-like characters, only applicable when a model is trained
    // using the -use_tags flag in the MinionCutFromImageBasedOnPageXMLNew

    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputStrikethrough(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t<s>I am a strikethrough text</s> with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("␃I ␃a␃m ␃a ␃s␃t␃r␃i␃k␃e␃t␃h␃r␃o␃u␃g␃h ␃t␃e␃x␃t with some regular text"));
    }
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputUnderlined(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t<u>I am an underlined text</u> with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("␅I ␅a␅m ␅a␅n ␅u␅n␅d␅e␅r␅l␅i␅n␅e␅d ␅t␅e␅x␅t with some regular text"));
    }
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputSuperscript(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\tI am special text with<sup>superscript</sup> and some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("I am special text with␆s␆u␆p␆e␆r␆s␆c␆r␆i␆p␆t and some regular text"));
    }
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputSubscript(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\tI am special text with<sub>subscript</sub> and some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("I am special text with␄s␄u␄b␄s␄c␄r␄i␄p␄t and some regular text"));
    }

    // combined
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputCombined(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t<s>I am a strikethrough text but <u>also underlined</u></s> with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("␃I ␃a␃m ␃a ␃s␃t␃r␃i␃k␃e␃t␃h␃r␃o␃u␃g␃h ␃t␃e␃x␃t ␃b␃u␃t ␅␃a␅␃l␅␃s␅␃o ␅␃u␅␃n␅␃d␅␃e␅␃r␅␃l␅␃i␅␃n␅␃e␅␃d with some regular text"));

    }

    // combined reversed
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputCombinedReversed(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t<s>I am underlined text but <u>also<sup>sup</sup> strikethrough<sub>sub</sub></u></s> with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("␃I ␃a␃m ␃u␃n␃d␃e␃r␃l␃i␃n␃e␃d ␃t␃e␃x␃t ␃b␃u␃t ␅␃a␅␃l␅␃s␅␃o␅␃␆s␅␃␆u␅␃␆p ␅␃s␅␃t␅␃r␅␃i␅␃k␅␃e␅␃t␅␃h␅␃r␅␃o␅␃u␅␃g␅␃h␅␃␄s␅␃␄u␅␃␄b with some regular text"));
    }

    // combined reversed subscript + subscript
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputCombinedReversedSubSup(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t<u>I am underlined text but <s>also<sup>sup</sup> strikethrough<sub>sub</sub></s></u> with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("␅I ␅a␅m ␅u␅n␅d␅e␅r␅l␅i␅n␅e␅d ␅t␅e␅x␅t ␅b␅u␅t ␅␃a␅␃l␅␃s␅␃o␅␃␆s␅␃␆u␅␃␆p ␅␃s␅␃t␅␃r␅␃i␅␃k␅␃e␅␃t␅␃h␅␃r␅␃o␅␃u␅␃g␅␃h␅␃␄s␅␃␄u␅␃␄b with some regular text"));
    }

    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputUnclosedStrikethroughtest(){
        String test = "I am an <s><u>underlined text <u>with some <s>regular text";
        assertThat(StyledString.applyMarkersWithNestedTags(test), is("I am an ␅␃u␅␃n␅␃d␅␃e␅␃r␅␃l␅␃i␅␃n␅␃e␅␃d text ␅w␅i␅t␅h some ␃r␃e␃g␃u␃l␃a␃r text"));
    }

    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputUnclosedAndClosed(){
        String test = "I am an <s><u>underlined text <u>with</u> some <s>regular text</s>";
        assertThat(StyledString.applyMarkersWithNestedTags(test), is("I am an ␅␃u␅␃n␅␃d␅␃e␅␃r␅␃l␅␃i␅␃n␅␃e␅␃d text ␅w␅i␅t␅h some ␃r␃e␃g␃u␃l␃a␃r ␃t␃e␃x␃t"));
    }


    // Unclosed strikethrough
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputUnclosedUnderlined(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\tI am an <u>underlined text with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("I am an ␅u␅n␅d␅e␅r␅l␅i␅n␅e␅d text with some regular text"));
    }

    // Unclosed underlined
    @Test
    public void getNewStyleResultLineHtmlTagHtrOutputUnclosedStrikethrough(){
        MinionLoghiHTRMergePageXML.ResultLine result = MinionLoghiHTRMergePageXML.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\tI am <s>strikethrough text with some regular text");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(StyledString.applyMarkersWithNestedTags(result.getText().toString()), is("I am ␃s␃t␃r␃i␃k␃e␃t␃h␃r␃o␃u␃g␃h text with some regular text"));
    }

    @Test
    public void htmlTestToTextStyle(){
        String test = StyledString.applyMarkersWithNestedTags("This is a <u><s>mistake</s></u> that needs correction.");

        // Init TextLineCustom
        TextLineCustom textLineCustom = new TextLineCustom();
        final StyledString styledString = StyledString.fromStringWithStyleCharacters(test);
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));
        assertThat(textLineCustom.toString(),is("textStyle {offset:10; length:7;underlined:true;strikethrough:true;}"));
    }

    @Test
    public void unicodeTestToTextStyle(){
        String test = StyledString.applyMarkersWithNestedTags("This is a ␅␃m␅␃i␅␃s␅␃t␅␃a␅␃k␅␃e that needs correction.");
        // Init TextLineCustom
        TextLineCustom textLineCustom = new TextLineCustom();
        final StyledString styledString = StyledString.fromStringWithStyleCharacters(test);
        styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));
        assertThat(textLineCustom.toString(),is("textStyle {offset:10; length:7;underlined:true;strikethrough:true;}"));
    }

    @Test
    public void extractTextLineCustomTest() throws ParseException {
        String test = "structure {type:Marginal;}";
        TextLineCustom textLineCustom = MinionLoghiHTRMergePageXML.extractTextLineCustom(test);
        assertThat(textLineCustom.toString(),is(test));
    }

    @Test
    public void extractTextLineCustomTest2() throws ParseException {
        String test = "structure {type:Marginal;}";
        String input = test + " readingOrder {index:0;} textStyle {offset:0; length:19;underlined:true;}";
        TextLineCustom textLineCustom = MinionLoghiHTRMergePageXML.extractTextLineCustom(input);
        textLineCustom.setTextStyles(null);
        textLineCustom.setReadingOrder("");
        assertThat(textLineCustom.toString(),is(test));
    }
}
