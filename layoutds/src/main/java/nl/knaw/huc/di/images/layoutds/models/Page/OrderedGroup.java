package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderedGroup {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty(isAttribute = true, localName = "caption")
    private String caption;
    @JacksonXmlProperty(isAttribute = true)
    private String regionRef;
    // TODO make enum with values: paragraph, list, list-item, figure, article, div, other
    @JacksonXmlProperty(isAttribute = true)
    private String type;
    @JacksonXmlProperty(isAttribute = true)
    private Boolean continuation;
    @JacksonXmlProperty(isAttribute = true)
    private String custom;
    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    @JacksonXmlProperty(localName = "RegionRefIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RegionRefIndexed> regionRefIndexedList = new ArrayList<>();

    @JacksonXmlProperty(localName = "OrderedGroupIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<OrderedGroupIndexed> orderedGroupIndexed = new ArrayList<>();

    @JacksonXmlProperty(localName = "UnorderedGroupIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<UnorderedGroupIndexed> unorderedGroupIndexed = new ArrayList<>();

    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Labels> labelsList = new ArrayList<>();

    public OrderedGroup() {
        this.setId(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (Strings.isNullOrEmpty(id)) {
            this.id = "orderedgroup_" + UUID.randomUUID().toString();
        } else if (Character.isDigit(id.charAt(0))) {
            this.id = "orderedgroup_" + id;
        } else {
            this.id = id;
        }
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<RegionRefIndexed> getRegionRefIndexedList() {
        if (regionRefIndexedList == null) {
            regionRefIndexedList = new ArrayList<>();
        }
        return regionRefIndexedList;
    }

    public void setRegionRefIndexedList(List<RegionRefIndexed> regionRefIndexedList) {
        this.regionRefIndexedList = regionRefIndexedList;
    }

    public void addRegionRefIndexed(RegionRefIndexed regionRefIndexed) {
        this.regionRefIndexedList.add(regionRefIndexed);
    }

    public List<OrderedGroupIndexed> getOrderedGroupIndexed() {
        return orderedGroupIndexed;
    }

    public void setOrderedGroupIndexed(List<OrderedGroupIndexed> orderedGroupIndexed) {
        this.orderedGroupIndexed = orderedGroupIndexed;
    }

    public List<UnorderedGroupIndexed> getUnorderedGroupIndexed() {
        return unorderedGroupIndexed;
    }

    public void setUnorderedGroupIndexed(List<UnorderedGroupIndexed> unorderedGroupIndexed) {
        this.unorderedGroupIndexed = unorderedGroupIndexed;
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

    public String getRegionRef() {
        return regionRef;
    }

    public void setRegionRef(String regionRef) {
        this.regionRef = regionRef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void addOrderedGroupIndexed(OrderedGroupIndexed orderedGroupIndexed) {
        this.orderedGroupIndexed.add(orderedGroupIndexed);
    }

    public void addUnorderedGroupIndexed(UnorderedGroupIndexed unorderedGroupIndexed) {
        this.unorderedGroupIndexed.add(unorderedGroupIndexed);
    }

    public void setUserDefined(UserDefined userDefined) {
        this.userDefined = userDefined;
    }

    public UserDefined getUserDefined() {
        return userDefined;
    }

    public List<Labels> getLabelsList() {
        return labelsList;
    }

    public void setLabelsList(List<Labels> labelsList) {
        this.labelsList = labelsList;
    }

    public void addLabels(Labels labels) {
        this.labelsList.add(labels);
    }
}
