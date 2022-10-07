package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


/// This contains both an image and its pageXML ground truth (documentOCRResult)
@Entity
public class PageImagePair implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;
    private UUID uuid;
    private UUID documentOCRResultUUID;
    private UUID documentImageUUID;
    private String tag;
    private boolean done;
    private Boolean readyForBaselineExtraction;

    public PageImagePair() {
        this.uuid = UUID.randomUUID();
        this.created = new Date();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @ManyToOne(targetEntity = P2PaLaJob.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "p2palaparentId")
    @JsonIgnore
    private P2PaLaJob p2palaparent;

    public UUID getDocumentOCRResultUUID() {
        return documentOCRResultUUID;
    }

    public void setDocumentOCRResultUUID(UUID documentOCRResultUUID) {
        this.documentOCRResultUUID = documentOCRResultUUID;
    }

    public UUID getDocumentImageUUID() {
        return documentImageUUID;
    }

    public void setDocumentImageUUID(UUID documentImageUUID) {
        this.documentImageUUID = documentImageUUID;
    }

    public P2PaLaJob getP2palaparent() {
        return p2palaparent;
    }

    public void setP2palaparent(P2PaLaJob p2palaparent) {
        this.p2palaparent = p2palaparent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.updated = new Date();
        this.done = done;
    }

    public Boolean isReadyForBaselineExtraction() {
        return readyForBaselineExtraction;
    }

    public void setReadyForBaselineExtraction(Boolean readyForBaselineExtraction) {
        this.readyForBaselineExtraction = readyForBaselineExtraction;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
