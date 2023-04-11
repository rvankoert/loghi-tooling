package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class UnorderedGroupIndexed {
    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labelsList;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "RegionRef", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<RegionRef> regionRefList = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "OrderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<OrderedGroup> orderedGroupList;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UnorderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<UnorderedGroup> unorderedGroupList;

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

    public List<RegionRef> getRegionRefList() {
        return regionRefList;
    }

    public void setRegionRefList(List<RegionRef> regionRefList) {
        this.regionRefList = regionRefList;
    }

    public List<OrderedGroup> getOrderedGroupList() {
        return orderedGroupList;
    }

    public void setOrderedGroupList(List<OrderedGroup> orderedGroupList) {
        this.orderedGroupList = orderedGroupList;
    }

    public List<UnorderedGroup> getUnorderedGroupList() {
        return unorderedGroupList;
    }

    public void setUnorderedGroupList(List<UnorderedGroup> unorderedGroupList) {
        this.unorderedGroupList = unorderedGroupList;
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


    public void addRegionRef(RegionRef regionRef) {
        this.regionRefList.add(regionRef);
    }
}
