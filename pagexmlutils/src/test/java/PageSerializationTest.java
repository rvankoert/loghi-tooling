import com.fasterxml.jackson.core.JsonProcessingException;
import nl.knaw.huc.di.images.layoutds.models.Page.Metadata;
import nl.knaw.huc.di.images.layoutds.models.Page.MetadataItem;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PageSerializationTest {
    @Test
    public void pageWithMetadataItems() throws JsonProcessingException, TransformerException {
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
        page.getPage().setImageFilename("filename.jpg");
        page.getPage().setImageWidth(100);
        page.getPage().setImageHeight(200);

        String contents = PageUtils.convertPcGtsToString(page, PageUtils.NAMESPACE2019);
        final PcGts deserialized = PageUtils.readPageFromString(contents);
        final String reserialized = PageUtils.convertPcGtsToString(deserialized, PageUtils.NAMESPACE2019);
        System.out.println(contents);
        System.out.println(reserialized);
        assertThat(reserialized, is(contents));
    }
}
