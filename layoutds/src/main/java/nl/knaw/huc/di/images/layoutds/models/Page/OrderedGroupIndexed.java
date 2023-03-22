package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class OrderedGroupIndexed {
    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labelsList;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "RegionRefIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<RegionRefIndexed> regionRefIndexedList;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "OrderedGroupIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<OrderedGroupIndexed> orderedGroupIndexedList;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UnorderedGroupIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<UnorderedGroupIndexed> unorderedGroupIndexedList;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String regionRef;

    @JacksonXmlProperty(isAttribute = true)
    private Integer index;

    @JacksonXmlProperty(isAttribute = true)
    private String caption;

    // TODO make enum with values: paragraph, list, list-item, figure, article, div, other
    @JacksonXmlProperty(isAttribute = true)
    private String type;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean continuation;

    @JacksonXmlProperty(isAttribute = true)
    private String custom;

    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    public UserDefined getUserDefined() {
        return userDefined;
    }

    public void setUserDefined(UserDefined userDefined) {
        this.userDefined = userDefined;
    }

    public List<Labels> getLabelsList() {
        return labelsList;
    }

    public void setLabelsList(List<Labels> labelsList) {
        this.labelsList = labelsList;
    }

    public List<RegionRefIndexed> getRegionRefIndexedList() {
        return regionRefIndexedList;
    }

    public void setRegionRefIndexedList(List<RegionRefIndexed> regionRefIndexedList) {
        this.regionRefIndexedList = regionRefIndexedList;
    }

    public List<OrderedGroupIndexed> getOrderedGroupIndexedList() {
        return orderedGroupIndexedList;
    }

    public void setOrderedGroupIndexedList(List<OrderedGroupIndexed> orderedGroupIndexedList) {
        this.orderedGroupIndexedList = orderedGroupIndexedList;
    }

    public List<UnorderedGroupIndexed> getUnorderedGroupIndexedList() {
        return unorderedGroupIndexedList;
    }

    public void setUnorderedGroupIndexedList(List<UnorderedGroupIndexed> unorderedGroupIndexedList) {
        this.unorderedGroupIndexedList = unorderedGroupIndexedList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionRef() {
        return regionRef;
    }

    public void setRegionRef(String regionRef) {
        this.regionRef = regionRef;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getContinuation() {
        return continuation;
    }

    public void setContinuation(Boolean continuation) {
        this.continuation = continuation;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
