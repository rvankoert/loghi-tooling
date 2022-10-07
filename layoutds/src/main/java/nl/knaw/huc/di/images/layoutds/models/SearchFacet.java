package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@Entity
@XmlRootElement
public class SearchFacet implements IPimObject {
    public SearchFacet() {
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int facetOrder;
    private String title;
    private SearchFacetType searchFacetType;
    private String elasticFieldName;
    private int min;
    private int max;
    private int environmentId;
    private Boolean publish;


    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFacetOrder() {
        return facetOrder;
    }

    public void setFacetOrder(int facetOrder) {
        this.facetOrder= facetOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SearchFacetType getSearchFacetType() {
        return searchFacetType;
    }

    public void setSearchFacetType(SearchFacetType searchFacetType) {
        this.searchFacetType = searchFacetType;
    }

    public String getElasticFieldName() {
        return elasticFieldName;
    }

    public void setElasticFieldName(String elasticFieldName) {
        this.elasticFieldName = elasticFieldName;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Boolean showHistogram() {
        return true;
    }

    public int getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(int environmentId) {
        this.environmentId = environmentId;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }
}
