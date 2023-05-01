import com.fasterxml.jackson.core.JsonProcessingException;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class PageSerializationTest {
    @Test
    public void metadata() throws JsonProcessingException {
        PcGts page = createDefaultPcGts();
        final Metadata metadata = page.getMetadata();
        metadata.setCreator("test");
        metadata.setCreated(new Date());
        metadata.setLastChange(new Date());
        metadata.setExternalRef("externalRef");
        final MetadataItem item = new MetadataItem();
        item.setValue("Test");
        item.setName("Name");
        item.setType("other");
        item.setDate(new Date());
        final Labels labels = new Labels();
        labels.setExternalId("externalId");
        labels.setPrefix("prefix");
        labels.setExternalModel("externalModel");
        labels.setComments("comments");
        addLabel(labels, "labelType", "labelValue", "labelComments");
        addLabel(labels, "label2Type", "label2Value", "label2Comments");
        item.setLabels(labels);
        metadata.getMetadataItems().add(item);
        final MetadataItem metadataItem2 = new MetadataItem();
        metadataItem2.setValue("Test2");
        metadata.addMetadataItem(metadataItem2);

        final UserDefined userDefined = new UserDefined();
        addUserAttribute(userDefined, "value", "name", "description");
        addUserAttribute(userDefined, "value1", "name1", "description1");
        metadata.setUserDefined(userDefined);

        page.getPage().setImageFilename("filename.jpg");
        page.getPage().setImageWidth(100);
        page.getPage().setImageHeight(200);

        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));
    }

    @Test
    public void pageWithAlternativeImages() throws Exception {
        PcGts page = createDefaultPcGts();
        addAlternativeImage(page, "comments", 0.6d, "/test/example.jpg");
        addAlternativeImage(page, "comments alternative", 0.65d, "/test/example.png");

        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));
    }

    @Test
    public void pageWithBorder() throws Exception {
        final PcGts page = createDefaultPcGts();
        final Border border = new Border();
        final Coords coords = new Coords();
        coords.setPoints("3408,1582 3798,1584 3798,1635 3408,1636");
        border.setCoords(coords);
        page.getPage().setBorder(border);

        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));

    }

    @Test
    public void pageWithPrintSpace() throws Exception {
        final PcGts page = createDefaultPcGts();
        final PrintSpace printSpace = new PrintSpace();
        final Coords coords = new Coords();
        coords.setPoints("3408,1582 3798,1584 3798,1635 3408,1636");
        printSpace.setCoords(coords);
        page.getPage().setPrintSpace(printSpace);


        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));
    }

    @Test
    public void pageWithReadingOrderWithOrderedGroup() throws Exception {
        final PcGts page = createDefaultPcGts();
        final ReadingOrder readingOrder = new ReadingOrder();
        readingOrder.setConfidence(0.6);
        final OrderedGroup orderedGroup = createOrderedGroup("GroupId", "caption", "regionRef", "article", false, "custom", "comments");
        final RegionRefIndexed region1 = new RegionRefIndexed(0, "region1");
        orderedGroup.addRegionRefIndexed(region1);
        final RegionRefIndexed region2 = new RegionRefIndexed(1, "region2");
        orderedGroup.addRegionRefIndexed(region2);

        final OrderedGroupIndexed orderedGroupIndexed = createOrderedGroupIndexed("GroupId1", "caption1", "regionRef1", "article1", "custom1", "comments1", 1);
        orderedGroup.addOrderedGroupIndexed(orderedGroupIndexed);
        final Labels labels = new Labels();
        addLabel(labels, "labelType", "labelValue", "labelComments");
        addLabel(labels, "labelType1", "labelValue1", "labelComments1");
        orderedGroup.addLabels(labels);
        final Labels labels1 = new Labels();
        addLabel(labels1, "labelType2", "labelValue2", "labelComments2");
        addLabel(labels1, "labelType3", "labelValue3", "labelComments3");
        orderedGroup.addLabels(labels1);
        final UserDefined userDefined = new UserDefined();
        userDefined.addUserAttribute(new UserAttribute("name", "description", "float", "0.8"));
        orderedGroup.setUserDefined(userDefined);
        readingOrder.setOrderedGroup(orderedGroup);
        page.getPage().setReadingOrder(readingOrder);


        final OrderedGroupIndexed orderedSubGroupIndexed = createOrderedGroupIndexed("GroupId2", "caption2", "regionRef2", "article", "custom2", "comments2", 2);
        orderedSubGroupIndexed.addRegionRefIndexed(new RegionRefIndexed(2, "region3"));
        orderedGroupIndexed.addOrderedGroupIndexed(orderedSubGroupIndexed);
        final Labels labels2 = new Labels();
        addLabel(labels2, "labelType4", "labelValue4", "labelComments4");
        addLabel(labels2, "labelType5", "labelValue5", "labelComments5");
        orderedGroupIndexed.addLabels(labels2);
        final Labels labels3 = new Labels();
        addLabel(labels3, "labelType6", "labelValue6", "labelComments6");
        addLabel(labels3, "labelType7", "labelValue7", "labelComments7");
        orderedGroupIndexed.addLabels(labels3);
        final UserDefined userDefined1 = new UserDefined();
        userDefined1.addUserAttribute(new UserAttribute("name", "description", "float", "0.6"));
        orderedGroupIndexed.setUserDefined(userDefined1);

        final UnorderedGroupIndexed unorderedGroupIndexed = createUnorderedGroupIndexed("GroupId3", "caption3", "regionRef3", "custom3", "comments3", 3, "region4");
        unorderedGroupIndexed.addOrderedGroup(createOrderedGroup("GroupId5", "caption5", "regionRef5", "custom5", false, "comments5", "region5"));
        unorderedGroupIndexed.addUnorderedGroup(createUnorderedGroup("GroupId6", "caption6", "regionRef6", "custom6", false, "comments6", "region6"));
        unorderedGroupIndexed.addRegionRef(new RegionRef("test"));
        final UserDefined userDefined2 = new UserDefined();
        userDefined2.addUserAttribute(new UserAttribute("name", "description", "float", "0.6"));
        unorderedGroupIndexed.setUserDefined(userDefined2);
        orderedGroup.addUnorderedGroupIndexed(unorderedGroupIndexed);

        final UnorderedGroupIndexed unorderedSubGroupIndexed = createUnorderedGroupIndexed("GroupId4", "caption4", "regionRef4", "custom4", "comments4", 4, "region5");
        orderedGroupIndexed.addUnorderedGroupIndexed(unorderedSubGroupIndexed);


        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));

    }

    private OrderedGroup createOrderedGroup(String groupId, String caption, String regionRef, String article, boolean continuation, String custom, String comments) {
        final OrderedGroup orderedGroup = new OrderedGroup();
        orderedGroup.setId(groupId);
        orderedGroup.setCaption(caption);
        orderedGroup.setRegionRef(regionRef);
        orderedGroup.setType(article);
        orderedGroup.setContinuation(continuation);
        orderedGroup.setCustom(custom);
        orderedGroup.setComments(comments);
        return orderedGroup;
    }

    @Test
    public void pageWithReadingOrderWithUnorderedGroup() throws Exception {
        final PcGts page = createDefaultPcGts();
        final ReadingOrder readingOrder = new ReadingOrder();
        readingOrder.setConfidence(0.6);
        page.getPage().setReadingOrder(readingOrder);

        final UnorderedGroup unorderedGroup = createUnorderedGroup("GroupId", "caption", "regionRef", "article", false, "custom", "comments");
        readingOrder.setUnorderedGroup(unorderedGroup);

        final Labels labels = new Labels();
        addLabel(labels, "labelType", "labelValue", "labelComments");
        addLabel(labels, "labelType1", "labelValue1", "labelComments1");
        unorderedGroup.addLabels(labels);
        final Labels labels1 = new Labels();
        addLabel(labels1, "labelType2", "labelValue2", "labelComments2");
        addLabel(labels1, "labelType3", "labelValue3", "labelComments3");
        unorderedGroup.addLabels(labels1);

        unorderedGroup.addRegionRef(new RegionRef("region1"));
        unorderedGroup.addRegionRef(new RegionRef("region2"));

        final OrderedGroup orderedGroup = createOrderedGroup("GroupId1", "caption1", "regionRef1", "article", false, "custom1", "comments1");
        orderedGroup.addRegionRefIndexed(new RegionRefIndexed(1, "ref"));
        unorderedGroup.addOrderedGroup(orderedGroup);
        final OrderedGroup orderedGroup2 = createOrderedGroup("GroupId2", "caption2", "regionRef2", "article", false, "custom2", "comments2");
        orderedGroup2.addRegionRefIndexed(new RegionRefIndexed(2, "ref2"));
        unorderedGroup.addOrderedGroup(orderedGroup2);

        final UnorderedGroup unorderedGroup1 = createUnorderedGroup("GroupId3", "caption3", "regionRef3", "article", false, "custom3", "comments3");
        unorderedGroup1.addRegionRef(new RegionRef("region3"));
        unorderedGroup.addUnorderedGroup(unorderedGroup1);
        final UnorderedGroup unorderedGroup2 = createUnorderedGroup("GroupId4", "caption4", "regionRef4", "article", false, "custom4", "comments4");
        unorderedGroup2.addRegionRef(new RegionRef("region4"));
        unorderedGroup.addUnorderedGroup(unorderedGroup2);


        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));

    }

    private UnorderedGroup createUnorderedGroup(String groupId, String caption, String regionRef, String article, boolean continuation, String custom, String comments) {
        final UnorderedGroup unorderedGroup = new UnorderedGroup();
        unorderedGroup.setId(groupId);
        unorderedGroup.setCaption(caption);
        unorderedGroup.setRegionRef(regionRef);
        unorderedGroup.setType(article);
        unorderedGroup.setContinuation(continuation);
        unorderedGroup.setCustom(custom);
        unorderedGroup.setComments(comments);
        return unorderedGroup;
    }

    @Test
    public void pageWithLayers() throws JsonProcessingException {
        final PcGts page = createDefaultPcGts();
        final Layers layers = new Layers();
        page.getPage().setLayers(layers);
        final Layer layer = new Layer();
        layer.setCaption("caption");
        layer.setId("id");
        layer.setzIndex(1);
        layer.addRegionRef(new RegionRef("region"));
        layer.addRegionRef(new RegionRef("region1"));
        layers.addLayer(layer);

        final Layer layer2 = new Layer();
        layer2.setCaption("caption1");
        layer2.setId("id1");
        layer2.setzIndex(2);
        layer2.addRegionRef(new RegionRef("region2"));
        layer2.addRegionRef(new RegionRef("region3"));
        layers.addLayer(layer2);

        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));
    }

    private UnorderedGroupIndexed createUnorderedGroupIndexed(String groupId3, String caption3, String regionRef3, String custom3, String comments3, int i, String region4) {
        final UnorderedGroupIndexed unorderedGroupIndexed = new UnorderedGroupIndexed();
        unorderedGroupIndexed.setId(groupId3);
        unorderedGroupIndexed.setCaption(caption3);
        unorderedGroupIndexed.setRegionRef(regionRef3);
        unorderedGroupIndexed.setType("article");
        unorderedGroupIndexed.setContinuation(false);
        unorderedGroupIndexed.setCustom(custom3);
        unorderedGroupIndexed.setComments(comments3);
        unorderedGroupIndexed.setIndex(i);
        unorderedGroupIndexed.addRegionRef(new RegionRef(region4));
        return unorderedGroupIndexed;
    }

    private OrderedGroupIndexed createOrderedGroupIndexed(String groupId, String caption, String regionRef, String article, String custom, String comments, int index) {
        final OrderedGroupIndexed orderedGroupIndexed = new OrderedGroupIndexed();
        orderedGroupIndexed.setId(groupId);
        orderedGroupIndexed.setCaption(caption);
        orderedGroupIndexed.setRegionRef(regionRef);
        orderedGroupIndexed.setType(article);
        orderedGroupIndexed.setContinuation(false);
        orderedGroupIndexed.setCustom(custom);
        orderedGroupIndexed.setComments(comments);
        orderedGroupIndexed.setIndex(index);
        return orderedGroupIndexed;
    }

    private PcGts createDefaultPcGts() {
        String creator = "creator";
        Date created = new Date();
        Date lastChanged = new Date();
        String filename = "filename.jpg";
        int height = 100;
        int width = 200;
        return new PcGts(creator, created, lastChanged, filename, height, width);
    }

    private void addAlternativeImage(PcGts page, String comments, double confidence, String fileName) {
        final AlternativeImage alternativeImage = new AlternativeImage();
        alternativeImage.setComments(comments);
        alternativeImage.setConfidence(confidence);
        alternativeImage.setFileName(fileName);
        page.getPage().addAlternativeImage(alternativeImage);
    }

    private void addUserAttribute(UserDefined userDefined, String value, String name, String description) {
        final UserAttribute userAttribute = new UserAttribute();
        userAttribute.setValue(value);
        userAttribute.setType("xsd:string");
        userAttribute.setName(name);
        userAttribute.setDescription(description);
        userDefined.addUserAttribute(userAttribute);
    }

    private void addLabel(Labels labels, String type, String value, String comments) {
        final Label label = new Label();
        label.setType(type);
        label.setValue(value);
        label.setComments(comments);
        labels.addLabel(label);
    }
}
