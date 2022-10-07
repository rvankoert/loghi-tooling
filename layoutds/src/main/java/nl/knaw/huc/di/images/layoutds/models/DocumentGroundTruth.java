package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@Entity
@XmlRootElement
public class DocumentGroundTruth implements IPimObject {
    public DocumentGroundTruth() {
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentImageId;

    @Type(type = "text")
    private String groundTruth;
    private Date made;

    @ElementCollection(targetClass = PimUser.class)
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private PimUser pimUser;

    private DocumentGroundTruthType groundTruthType;
    private DocumentGroundTruthStatus groundTruthStatus;


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

    public Long getDocumentImageId() {
        return documentImageId;
    }

    public void setDocumentImageId(Long documentImageId) {
        this.documentImageId = documentImageId;
    }

    public String getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(String groundTruth) {
        this.groundTruth = groundTruth;
    }

    public Date getMade() {
        return made;
    }

    public void setMade(Date made) {
        this.made = made;
    }

    public void setPimUser(PimUser pimUser) {
        this.pimUser = pimUser;
    }

    public PimUser getPimUser() {
        return pimUser;
    }

    public DocumentGroundTruthType getGroundTruthType() {
        return groundTruthType;
    }

    public void setGroundTruthType(DocumentGroundTruthType groundTruthType) {
        this.groundTruthType = groundTruthType;
    }
}
