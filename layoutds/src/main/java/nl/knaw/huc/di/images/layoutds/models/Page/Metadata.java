package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Metadata {
    @JacksonXmlProperty(localName = "Creator",namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String creator;
    @JacksonXmlProperty(localName = "Created", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JacksonXmlProperty(localName = "LastChange", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Date lastChange;
    // Not in the official PAGE spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    // But it is used
    @Deprecated
    @JacksonXmlProperty(localName = "TranskribusMetadata", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TranskribusMetadata transkribusMetadata;

    @JacksonXmlProperty(localName = "Comments", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String comments;

    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MetadataItem", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15" )
    List<MetadataItem> metadataItems = new ArrayList<>();

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    // Not in the official PAGE spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    // But it is used
    public TranskribusMetadata getTranskribusMetadata() {
        return transkribusMetadata;
    }

    // Not in the official PAGE spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    // But it is used
    public void setTranskribusMetadata(TranskribusMetadata transkribusMetadata) {
        this.transkribusMetadata = transkribusMetadata;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<MetadataItem> getMetadataItems() {
        return metadataItems;
    }

    public void setMetadataItems(List<MetadataItem> metadataItems) {
        this.metadataItems = metadataItems;
    }

    public UserDefined getUserDefined() {
        return userDefined;
    }

    public void setUserDefined(UserDefined userDefined) {
        this.userDefined = userDefined;
    }

    public void addMetadataItem(MetadataItem metadataItem) {
        metadataItems.add(metadataItem);
    }
}
