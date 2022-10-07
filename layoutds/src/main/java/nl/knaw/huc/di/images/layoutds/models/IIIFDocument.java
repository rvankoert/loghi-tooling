package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "id", name = "iiifDocument_id_hidx"),
        @Index(columnList = "uri", name = "iiifDocument_uri_hidx")
})

public class IIIFDocument implements IPimObject {
    public IIIFDocument() {
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type="text")
    @Column(unique = true, nullable = false)
    private String uri;

    @Type(type="text")
    private String content;
    private String context;
    private String IIIFId;
    private String type;
    @Type(type="text")
    private String label;
    @Type(type = "text")
    private String seeAlso;
    @Type(type = "text")
    private String related;

    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimUserId")
    @JsonIgnore
    private PimUser owner;


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
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setIIIFId(String iiifId) {
        this.IIIFId = iiifId;
    }

    public String getIIIFId() {
        return IIIFId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setSeeAlso(String seeAlso) {
        this.seeAlso = seeAlso;
    }

    public String getSeeAlso() {
        return seeAlso;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public String getRelated() {
        return related;
    }

    public PimUser getOwner() {
        return owner;
    }

    public void setOwner(PimUser owner) {
        this.owner = owner;
    }
}
