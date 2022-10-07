package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huc.di.images.layoutds.models.iiif.IIIFTypes;

import javax.persistence.*;

@Entity
@Table(name= "AnnotationWithin")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Within
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private IIIFTypes type;
    private Integer total;

    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IIIFTypes getType() {
        return type;
    }

    public void setType(IIIFTypes type) {
        this.type = type;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }
}

