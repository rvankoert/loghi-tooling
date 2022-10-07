package nl.knaw.huc.di.images.layoutds.models.pim;

import nl.knaw.huc.di.images.layoutds.models.DocumentImage;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Entity
public class Swipe implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;

    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimUserId")
    private PimUser user;
    private Boolean result;

    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "documentImageId")
    private DocumentImage documentImage;

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

    public Swipe() {
        this.uuid = UUID.randomUUID();
        this.setCreated(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public PimUser getUser() {
        return user;
    }

    public void setUser(PimUser user) {
        this.user = user;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public DocumentImage getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(DocumentImage documentImage) {
        this.documentImage = documentImage;
    }
}
