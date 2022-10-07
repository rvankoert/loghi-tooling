package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "id", name = "person_id_hidx"),
        @Index(columnList = "name", name = "person_name_hidx"),
        @Index(columnList = "uri", name = "person_uri_hidx")
})

public class Person implements IPimObject {

    public Person() {
        this.uuid = UUID.randomUUID();

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "text")
    private String name;
    @Type(type = "text")
    private String uri;
    private Long documentImageId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDocumentImageId(Long documentImageId) {
        this.documentImageId = documentImageId;
    }

    public Long getDocumentImageId() {
        return documentImageId;
    }
}