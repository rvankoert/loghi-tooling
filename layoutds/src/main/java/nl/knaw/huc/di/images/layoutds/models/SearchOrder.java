package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class SearchOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int searchOrder;
    private String title;
    private String elasticFieldName;
    private int environmentId;
    private Boolean publish;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSearchOrder() {
        return searchOrder;
    }

    public void setSearchOrder(int searchOrder) {
        this.searchOrder = searchOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getElasticFieldName() {
        return elasticFieldName;
    }

    public void setElasticFieldName(String elasticFieldName) {
        this.elasticFieldName = elasticFieldName;
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
