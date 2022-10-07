package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "TranskribusMetadata")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TranskribusMetadata {
    @JacksonXmlProperty(isAttribute = true, localName = "docId")
    private Integer docId;
    @JacksonXmlProperty(isAttribute = true, localName = "pageId")
    private Integer pageId;
    @JacksonXmlProperty(isAttribute = true, localName = "pageNr")
    private Integer pageNr;
    @JacksonXmlProperty(isAttribute = true, localName = "tsid")
    private Integer tsid;
    @JacksonXmlProperty(isAttribute = true, localName = "status")
    private String status;
    @JacksonXmlProperty(isAttribute = true, localName = "userId")
    private Integer userId;
    @JacksonXmlProperty(isAttribute = true, localName = "imgUrl")
    private String imgUrl;
    @JacksonXmlProperty(isAttribute = true, localName = "xmlUrl")
    private String xmlUrl;
    @JacksonXmlProperty(isAttribute = true, localName = "imageId")
    private Integer imageId;

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public Integer getPageNr() {
        return pageNr;
    }

    public void setPageNr(Integer pageNr) {
        this.pageNr = pageNr;
    }

    public Integer getTsid() {
        return tsid;
    }

    public void setTsid(Integer tsid) {
        this.tsid = tsid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
