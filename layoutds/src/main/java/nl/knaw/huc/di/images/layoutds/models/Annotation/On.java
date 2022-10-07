package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;

@Entity
@Table(name = "AnnotationOn")
@JsonPropertyOrder({"@id", "@type", "full", "selector", "within"})
public class On
{
    @JsonProperty("@id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("full")
    private String annotationFull;

    @OneToOne(cascade = CascadeType.ALL, targetEntity=Selector.class, fetch=FetchType.EAGER)
    private Selector selector;

    @OneToOne(cascade = CascadeType.ALL, targetEntity=Within.class, fetch=FetchType.EAGER)
    private Within within;

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

    public String getAnnotationFull() {
        return annotationFull;
    }

    public void setAnnotationFull(String annotationFull) {
        this.annotationFull = annotationFull;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Within getWithin() {
        return within;
    }

    public void setWithin(Within within) {
        this.within = within;
    }
}

