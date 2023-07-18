package nl.knaw.huc.di.images.pagexmlutils;

import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.Word;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GroundTruthTextLineFormatterTest {

    @Test
    public void getFormattedTextLineStringRepresentationReturnsTheUnicodeTextEquivOfTheTextLineWhenAvailable() {
        final TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Dit is plain text", "Dit is een test"));
        List<Word> wordList = new ArrayList<>();
        wordList.add(wordWithText("Dit"));
        wordList.add(wordWithText("is"));
        wordList.add(wordWithText("iets"));
        wordList.add(wordWithText("anders"));
        textLine.setWords(wordList);

        final String textRepresentation = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);

        assertEquals("Dit is een test", textRepresentation);
    }

    @Test
    public void getFormattedTextLineStringRepresentationReturnsThePlaintTextEquivOfTheTextLineWhenUnicodeIsUnavailable() {
        final TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Dit is plain text", null));
        List<Word> wordList = new ArrayList<>();
        wordList.add(wordWithText("Dit"));
        wordList.add(wordWithText("is"));
        wordList.add(wordWithText("iets"));
        wordList.add(wordWithText("anders"));
        textLine.setWords(wordList);

        final String textRepresentation = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);

        assertEquals("Dit is plain text", textRepresentation);
    }

    private Word wordWithText(String text) {
        final Word word = new Word();
        word.setTextEquiv(new TextEquiv(null, text));
        return word;
    }

    @Test
    public void  getFormattedTextLineStringRepresentationReturnsTheTextEquivOfTheWordsWhenAvailable() {
        final TextLine textLine = new TextLine();
        List<Word> wordList = new ArrayList<>();
        wordList.add(wordWithText("Dit"));
        wordList.add(wordWithText("is"));
        wordList.add(wordWithText("iets"));
        wordList.add(wordWithText("anders"));
        textLine.setWords(wordList);

        final String textRepresentation = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);

        assertEquals("Dit is iets anders", textRepresentation);
    }

    @Test
    public void getFormattedTextLineStringRepresentationAddAckSymbolBeforeSuperscript() {
        final TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Dit is een test"));
        textLine.setCustom("readingOrder {index:2;} textStyle {offset:7; length:3;superscript:true;}");

        final String textRepresentation = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);

        assertEquals("Dit is ␆e␆e␆n test", textRepresentation);
    }

    @Test
    public void getFormattedTextLineStringRepresentationAddAckSymbolBeforeSuperscript0() {
        final TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Dit is een test"));
        textLine.setCustom("readingOrder {index:2;} textStyle {offset:0; length:3;superscript:true;}");

        final String textRepresentation = GroundTruthTextLineFormatter.getFormattedTextLineStringRepresentation(textLine, true);

        assertEquals("␆D␆i␆t is een test", textRepresentation);
    }

}