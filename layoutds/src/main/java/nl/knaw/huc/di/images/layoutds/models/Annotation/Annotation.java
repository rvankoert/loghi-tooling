package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.UUID;


/// These are IIIF annotations
@XmlRootElement
@Entity
@JsonPropertyOrder({"@id", "@context", "@type", "motivation", "on"})
public class Annotation implements IPimObject {
    public Annotation() {
        this.uuid = UUID.randomUUID();
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("@id")
    @Transient
    private String annotationId;

    @JsonProperty("@context")
    private String context;

    @JsonProperty("@type")
    private String type;

    private String[] motivation;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Resource.class, fetch = FetchType.LAZY)
    private List<Resource> resource;


    @JsonProperty("on")
    @OneToMany(cascade = CascadeType.ALL, targetEntity = On.class, fetch = FetchType.EAGER)
    private List<On> annotationOn;
    private Long userId;


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

    private void setId(Long id) {
        this.id = id;
    }

    public String getAnnotationId() {
        return this.getOn().get(0).getAnnotationFull() + "/annotation/" + getId();
    }

    public void setAnnotationId(String value) {
        setId(Long.parseLong(value.split("/annotation/")[value.split("/annotation/").length - 1]));
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getMotivation() {
        return motivation;
    }

    public void setMotivation(String[] motivation) {
        this.motivation = motivation;
    }

    public List<Resource> getResource() {
        return resource;
    }

    public void setResource(List<Resource> resource) {
        this.resource = resource;
    }

    private List<On> getOn() {
        return annotationOn;
    }

    public void setOn(List<On> on) {
        this.annotationOn = on;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}