package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnorderedGroup {
    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labelsList = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "RegionRef", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<RegionRef> regionRefs = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "OrderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<OrderedGroup> orderedGroups = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UnorderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<UnorderedGroup> unorderedGroups = new ArrayList<>();

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String regionRef;

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

    public List<RegionRef> getRegionRefs() {
        return regionRefs;
    }

    public void setRegionRefs(List<RegionRef> regionRefs) {
        this.regionRefs = regionRefs;
    }

    public List<OrderedGroup> getOrderedGroups() {
        return orderedGroups;
    }

    public void setOrderedGroups(List<OrderedGroup> orderedGroups) {
        this.orderedGroups = orderedGroups;
    }

    public List<UnorderedGroup> getUnorderedGroups() {
        return unorderedGroups;
    }

    public void setUnorderedGroups(List<UnorderedGroup> unorderedGroups) {
        this.unorderedGroups = unorderedGroups;
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
        this.regionRefs.add(regionRef);
    }

    public void addLabels(Labels labels) {
        this.labelsList.add(labels);
    }

    public void addOrderedGroup(OrderedGroup orderedGroup) {
        this.orderedGroups.add(orderedGroup);
    }

    public void addUnorderedGroup(UnorderedGroup unorderedGroup) {
        this.unorderedGroups.add(unorderedGroup);
    }
}
