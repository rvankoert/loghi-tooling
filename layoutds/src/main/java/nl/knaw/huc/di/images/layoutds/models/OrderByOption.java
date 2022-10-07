package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class OrderByOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderByOrder;
    private String title;
    private String elasticFieldName;
    private int environmentId;
    private Boolean publish;
    private Boolean ascending;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrderByOrder() {
        return orderByOrder;
    }

    public void setOrderByOrder(int orderByOrder) {
        this.orderByOrder = orderByOrder;
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

    public Boolean isAscending() {
        return ascending;
    }

    public void setAscending(Boolean ascending) {
        this.ascending = ascending;
    }
}
