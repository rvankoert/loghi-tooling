package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name= "AnnotationResource")
public class Resource
{

    @JsonProperty("@id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("@type")
    private String type;

    private String format;

    private String chars;


    public String getChars ()
    {
        return chars;
    }

    public void setChars (String chars)
    {
        this.chars = chars;
    }

    public String getFormat ()
    {
        return format;
    }

    public void setFormat (String format)
    {
        this.format = format;
    }

}
