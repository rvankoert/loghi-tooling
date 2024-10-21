package nl.knaw.huc.di.images.pagexmlutils;

import nl.knaw.huc.di.images.layoutds.models.Page.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.knaw.huc.di.images.pagexmlutils.StyledString.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
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

}