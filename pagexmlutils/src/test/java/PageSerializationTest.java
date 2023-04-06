import com.fasterxml.jackson.core.JsonProcessingException;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PageSerializationTest {
    @Test
    public void metadata() throws JsonProcessingException {
        String creator = "creator";
        Date created = new Date();
        Date lastChanged = new Date();
        String filename = "filename.jpg";
        int height = 100;
        int width = 200;
        PcGts page = new PcGts(creator, created, lastChanged, filename, height, width);
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
        String creator = "creator";
        Date created = new Date();
        Date lastChanged = new Date();
        String filename = "filename.jpg";
        int height = 100;
        int width = 200;
        PcGts page = new PcGts(creator, created, lastChanged, filename, height, width);
        addAlternativeImage(page, "comments", 0.6d, "/test/example.jpg");
        addAlternativeImage(page, "comments alternative", 0.65d, "/test/example.png");

        String contents = PageUtils.convertPcGtsToString(page);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized);

        assertThat(reserialized, is(contents));
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

    private void addLabel(Labels labels, String label2Type, String label2Value, String label2Comments) {
        final Label label2 = new Label();
        label2.setType(label2Type);
        label2.setValue(label2Value);
        label2.setComments(label2Comments);
        labels.addLabel(label2);
    }
}
