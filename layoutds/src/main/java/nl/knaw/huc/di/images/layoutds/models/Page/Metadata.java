package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Date;

@JacksonXmlRootElement(localName = "Metadata")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Metadata {
    @JacksonXmlProperty(localName = "Creator", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String creator;
    @JacksonXmlProperty(localName = "Created", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JacksonXmlProperty(localName = "LastChange", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Date lastChange;
    @JacksonXmlProperty(localName = "TranskribusMetadata", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TranskribusMetadata transkribusMetadata;

    @JacksonXmlProperty(localName = "Comments", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String comments;

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

    public TranskribusMetadata getTranskribusMetadata() {
        return transkribusMetadata;
    }

    public void setTranskribusMetadata(TranskribusMetadata transkribusMetadata) {
        this.transkribusMetadata = transkribusMetadata;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
