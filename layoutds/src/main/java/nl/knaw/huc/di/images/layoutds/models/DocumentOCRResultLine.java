package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "documentocrresultline", indexes = {@Index(columnList = "lineId")}, uniqueConstraints = {@UniqueConstraint(columnNames = {"parent_id", "lineid"})})
public class DocumentOCRResultLine implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private UUID uuid;
    @ManyToOne(targetEntity = DocumentOCRResult.class, fetch = FetchType.EAGER, optional = false)
    private DocumentOCRResult parent;
    private String lineId;

    public DocumentOCRResultLine() {
    }

    public DocumentOCRResultLine(DocumentOCRResult documentOCRResult, String lineId) {
        this.parent = documentOCRResult;
        this.lineId = lineId;
        this.uuid = UUID.randomUUID();

    }

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

    public DocumentOCRResult getParent() {
        return parent;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
