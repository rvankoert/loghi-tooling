package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name= "AnnotationDefault")
public class Default
{
    @JsonProperty("@id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("@type")
    private String type;

    private String value;

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}