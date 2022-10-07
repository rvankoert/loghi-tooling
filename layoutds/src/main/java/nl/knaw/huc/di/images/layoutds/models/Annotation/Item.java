package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name= "AnnotationItem")
public class Item
{
    @JsonProperty("@id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("@type")
    private String type;


    @Type(type = "text")
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

