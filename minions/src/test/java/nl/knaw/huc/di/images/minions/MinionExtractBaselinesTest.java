package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutds.models.Page.*;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class MinionExtractBaselinesTest {

    @Test
    public void mergeTextLinesTest() {
        PcGts page = new PcGts();
        page.setPage(new Page());
        page.getPage().setImageHeight(500);
        page.getPage().setImageWidth(500);
        List<TextLine> newTextLines = new ArrayList<>();
        TextLine textLine = new TextLine();
        Coords coords = new Coords();
        coords.setPoints("1,1 1,100 100,100 100,1");
        textLine.setCoords(coords);
        textLine.setTextEquiv(new TextEquiv(1.0, "test text"));
        newTextLines.add(textLine);
        MinionExtractBaselines.mergeTextLines(page, newTextLines, true,
                "test", true, 0 ,false);
        Assert.assertEquals(page.getPage().getTextRegions().get(0).getTextLines().size(), 1);

    }

    @Test
    public void mergeTextLines2Test() {
        PcGts page = new PcGts();
        page.setPage(new Page());
        page.getPage().setImageHeight(500);
        page.getPage().setImageWidth(500);
        List<TextLine> newTextLines = new ArrayList<>();
        TextLine textLine = new TextLine();
        Coords coords = new Coords();
        coords.setPoints("1,1 1,100 100,100 100,1");
        textLine.setCoords(coords);
        textLine.setTextEquiv(new TextEquiv(1.0, "test text"));
        newTextLines.add(textLine);

        textLine = new TextLine();
        coords = new Coords();
        coords.setPoints("201,201 201,300 300,300 300,201");
        textLine.setCoords(coords);
        textLine.setTextEquiv(new TextEquiv(1.0, "test text2"));
        newTextLines.add(textLine);

        MinionExtractBaselines.mergeTextLines(page, newTextLines,true,
                "test", true, 0 ,false);
        Assert.assertEquals(page.getPage().getTextRegions().get(0).getTextLines().size(), 2);
    }

    @Test
    public void mergeTextLinesNoSingleRegionTest() {
        PcGts page = new PcGts();
        page.setPage(new Page());
        page.getPage().setImageHeight(500);
        page.getPage().setImageWidth(500);
        List<TextLine> newTextLines = new ArrayList<>();
        TextLine textLine = new TextLine();
        Coords coords = new Coords();
        coords.setPoints("1,1 1,100 100,100 100,1");
        textLine.setCoords(coords);
        textLine.setTextEquiv(new TextEquiv(1.0, "test text"));
        newTextLines.add(textLine);

        textLine = new TextLine();
        coords = new Coords();
        coords.setPoints("201,201 201,300 300,300 300,201");
        textLine.setCoords(coords);
        textLine.setTextEquiv(new TextEquiv(1.0, "test text2"));
        newTextLines.add(textLine);

        MinionExtractBaselines.mergeTextLines(page, newTextLines,false,
                "test", true, 0 ,false);
        Assert.assertEquals(page.getPage().getTextRegions().size(), 2);
    }

}