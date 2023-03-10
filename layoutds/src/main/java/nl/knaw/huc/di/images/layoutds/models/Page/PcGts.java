package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;

import java.util.Date;
import java.util.UUID;

// Use http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 to stay compatible with Transkribus
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "PcGts", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
public class PcGts {
    @JacksonXmlProperty(localName = "Metadata", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Metadata metadata;
    @JacksonXmlProperty(localName = "Page", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Page page;
    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    @JacksonXmlProperty
    private String schemaLocation;

    @JacksonXmlProperty(localName = "pcGtsId", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String pcGtsId;

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    @JacksonXmlProperty
    private String name;

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    @JacksonXmlProperty
    private String date;

    public PcGts() {
    }

    public PcGts(String creator, Date created, Date lastChanged, String filename, int height, int width) {
        this.getMetadata().setCreator(creator);
        this.getMetadata().setCreated(created);
        this.getMetadata().setLastChange(lastChanged);
        this.getPage().setImageFilename(filename);
        this.getPage().setImageHeight(height);
        this.getPage().setImageWidth(width);
    }


//    @JacksonXmlProperty
//    private Long docId;
//
//    @JacksonXmlProperty
//    private String title;
//
//    @JacksonXmlProperty
//    private String uploadTimestamp;
//
//    @JacksonXmlProperty
//    private String uploader;
//
//    @JacksonXmlProperty
//    private String uploaderId;
//
//    @JacksonXmlProperty
//    private String nrOfPages;
//
//    @JacksonXmlProperty
//    private String pageId;
//
//    @JacksonXmlProperty
//    private String url;
//
//    @JacksonXmlProperty
//    private String thumbUrl;
//
//    @JacksonXmlProperty
//    private String status;

    public Metadata getMetadata() {
        if (metadata == null) {
            metadata = new Metadata();
        }
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Page getPage() {
        if (this.page == null) {
            this.page = new Page();
        }
        return this.page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    public String getSchemaLocation() {
        return schemaLocation;
    }

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getPcGtsId() {
        return pcGtsId;
    }

    public void setPcGtsId(String pcGtsId) {
        if (Strings.isNullOrEmpty(pcGtsId)) {
            this.pcGtsId = "page_" + UUID.randomUUID().toString();
        } else if (Character.isDigit(pcGtsId.charAt(0))) {
            this.pcGtsId = "page_" + pcGtsId;
        } else {
            this.pcGtsId = pcGtsId;
        }
    }

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    public String getName() {
        return name;
    }

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    public String getDate() {
        return date;
    }

    // Not part of the PAGE XML spec, but might be used in some of our stored files
    @Deprecated
    public void setDate(String date) {
        this.date = date;
    }
}
