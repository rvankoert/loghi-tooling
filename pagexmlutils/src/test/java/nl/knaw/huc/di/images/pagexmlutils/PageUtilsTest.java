package nl.knaw.huc.di.images.pagexmlutils;

import nl.knaw.huc.di.images.layoutds.models.Page.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static nl.knaw.huc.di.images.stringtools.StringTools.convertStringToXMLDocument;
import static org.junit.Assert.assertEquals;

public class PageUtilsTest {

    @Test
    public void getReadingOrderFromCustomTest(){
        TextRegion textRegion = new TextRegion();
        textRegion.setCustom("structure {type:Text;} readingOrder {index:1;}");

        Integer index = PageUtils.getReadingOrderFromCustom(textRegion.getCustom());

        assertEquals(1, index.intValue());

    }

    @Test
    public void testRebuildReadingOrder() {
        // Setup
        PcGts page = new PcGts();
        List<TextRegion> textRegions = new ArrayList<>();
        TextRegion textRegion1 = new TextRegion();
        textRegion1.setId("r1");
        textRegion1.setCustom("readingOrder {index:1;}");
        TextRegion textRegion2 = new TextRegion();
        textRegion2.setId("r2");
        textRegion2.setCustom("readingOrder {index:2;}");
        textRegions.add(textRegion1);
        textRegions.add(textRegion2);
        page.getPage().setTextRegions(textRegions);

        // Call the method
        PageUtils.rebuildReadingOrder(page);

        // Verify the results
        ReadingOrder readingOrder = page.getPage().getReadingOrder();
        List<RegionRefIndexed> regionRefs = readingOrder.getOrderedGroup().getRegionRefIndexedList();
        assertEquals(2, regionRefs.size());
        assertEquals("r1", regionRefs.get(0).getRegionRef());
        assertEquals("r2", regionRefs.get(1).getRegionRef());
    }

    @Test
    public void testRebuildReadingOrder2() {
        // Setup
        PcGts page = new PcGts();
        List<TextRegion> textRegions = new ArrayList<>();
        TextRegion textRegion1 = new TextRegion();
        textRegion1.setId("r1");
        textRegion1.setCustom("readingOrder {index:0;}");
        TextRegion textRegion2 = new TextRegion();
        textRegion2.setId("r2");
        textRegion2.setCustom("");
        textRegions.add(textRegion1);
        textRegions.add(textRegion2);
        page.getPage().setTextRegions(textRegions);

        // Call the method
        PageUtils.rebuildReadingOrder(page);

        // Verify the results
        ReadingOrder readingOrder = page.getPage().getReadingOrder();
        List<RegionRefIndexed> regionRefs = readingOrder.getOrderedGroup().getRegionRefIndexedList();
        assertEquals(2, regionRefs.size());
        assertEquals("r1", regionRefs.get(0).getRegionRef());
        assertEquals("r2", regionRefs.get(1).getRegionRef());
        assertEquals(0, regionRefs.get(0).getIndex());
        assertEquals(1, regionRefs.get(1).getIndex());
    }

    @Test
    public void testRebuildReadingOrder3() {
        // Setup
        PcGts page = new PcGts();
        List<TextRegion> textRegions = new ArrayList<>();
        TextRegion textRegion1 = new TextRegion();
        textRegion1.setId("r1");
        textRegion1.setCustom("readingOrder {index:3;}");
        TextRegion textRegion2 = new TextRegion();
        textRegion2.setId("r2");
        textRegion2.setCustom("");
        textRegions.add(textRegion1);
        textRegions.add(textRegion2);
        page.getPage().setTextRegions(textRegions);

        // Call the method
        PageUtils.rebuildReadingOrder(page);

        // Verify the results
        ReadingOrder readingOrder = page.getPage().getReadingOrder();
        List<RegionRefIndexed> regionRefs = readingOrder.getOrderedGroup().getRegionRefIndexedList();
        assertEquals(2, regionRefs.size());
        assertEquals("r1", regionRefs.get(0).getRegionRef());
        assertEquals("r2", regionRefs.get(1).getRegionRef());
        assertEquals(0, regionRefs.get(0).getIndex());
        assertEquals(1, regionRefs.get(1).getIndex());
    }


    @Test
    public void testAddReadingOrderToTextRegionCustom() {
        // Setup
        PcGts page = new PcGts();
        List<TextRegion> textRegions = new ArrayList<>();
        TextRegion textRegion1 = new TextRegion();
        textRegion1.setId("r1");
        textRegion1.setCustom("structure {type:Text;} readingOrder {index:1;}");
        TextRegion textRegion2 = new TextRegion();
        textRegion2.setId("r2");
        textRegion2.setCustom("structure {type:Text;} readingOrder {index:2;}");
        TextRegion textRegion3 = new TextRegion();
        textRegion3.setId("r3");
        textRegion3.setCustom("structure {type:Text;}");
        textRegions.add(textRegion1);
        textRegions.add(textRegion2);
        page.getPage().setTextRegions(textRegions);

        // Call the method
        PageUtils.addReadingOrderToTextRegionCustom(textRegion1, 2);
        PageUtils.addReadingOrderToTextRegionCustom(textRegion2, 1);
        PageUtils.addReadingOrderToTextRegionCustom(textRegion3, 3);

        assertEquals("structure {type:Text;} readingOrder {index:2;}", textRegion1.getCustom());
        assertEquals("structure {type:Text;} readingOrder {index:1;}", textRegion2.getCustom());
        assertEquals("readingOrder {index:3;} structure {type:Text;}", textRegion3.getCustom());
        // Verify the results
//        ReadingOrder readingOrder = page.getPage().getReadingOrder();
//        List<RegionRefIndexed> regionRefs = readingOrder.getOrderedGroup().getRegionRefIndexedList();
//        assertEquals(2, regionRefs.size());
//        assertEquals("r1", regionRefs.get(0).getRegionRef());
//        assertEquals("r2", regionRefs.get(1).getRegionRef());
    }

    @Test
    public void testGetTextLineXHeight() {
        // Setup
        String xml = "<TextLine id=\"tl1\" xheight=\"12\">" +
                "  <Coords points=\"0,0 10,0 10,10 0,10\"/>" +
                "</TextLine>";
        Document document = convertStringToXMLDocument(xml);
        Node textLineNode = document.getFirstChild();

        // Call the method
        TextLine textLine = PageUtils.getTextLine(textLineNode, false);

        // Verify the results
        assertEquals("tl1", textLine.getId());
        assertEquals(12, textLine.getTextStyle().getxHeight(), 0.001);
    }

    @Test
    public void testMaximumConfidence(){
        // Setup
        PcGts page = new PcGts();
        List<TextRegion> textRegions = new ArrayList<>();
        TextRegion textRegion1 = new TextRegion();
        textRegion1.setId("r1");
        textRegion1.setCustom("structure {type:Text;} readingOrder {index:1;}");
        TextLine textLine1 = new TextLine();
        textLine1.setId("tl1");
        textLine1.setTextEquiv(new TextEquiv(0.5, "text1"));
        textRegion1.getTextLines().add(textLine1);
        textRegions.add(textRegion1);
        page.getPage().setTextRegions(textRegions);
        List<TextLine> lines = PageUtils.getTextLines(page, true, 0.1, 0.2);
        assertEquals(0, lines.size());
        lines = PageUtils.getTextLines(page, true, 0.1, 0.7);
        assertEquals(1, lines.size());
        lines = PageUtils.getTextLines(page, true, null, 0.7);
        assertEquals(1, lines.size());
        lines = PageUtils.getTextLines(page, true, 0.1, null);
        assertEquals(1, lines.size());
    }

}