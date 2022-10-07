package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import nl.knaw.huc.di.images.layoutds.models.pim.PimJob;
import nl.knaw.huc.di.images.layoutds.models.pim.Tesseract4Model;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class OCRJob implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentImageId")
    private DocumentImage documentImage;
    private String languages;
    private String tessdataPath;
    @ManyToOne(targetEntity = Tesseract4Model.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "tesseract4ModelId")
    private Tesseract4Model tesseract4Model;
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(targetEntity = PimJob.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @JsonIgnore
    private PimJob parent;
    @ManyToOne(targetEntity = KrakenModel.class, fetch = FetchType.EAGER)
    private KrakenModel krakenModel;
    @Enumerated(EnumType.STRING)
    private OcrSystem ocrSystem;

    public OCRJob() {
        this.uuid = UUID.randomUUID();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentImage getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(DocumentImage documentImage) {
        this.documentImage = documentImage;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getTessdataPath() {
        return tessdataPath;
    }

    public void setTessdataPath(String tessdataPath) {
        this.tessdataPath = tessdataPath;
    }


    public Tesseract4Model getTesseract4Model() {
        return tesseract4Model;
    }

    public void setTesseract4Model(Tesseract4Model tesseract4Model) {
        this.tesseract4Model = tesseract4Model;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setParent(PimJob parent) {
        this.parent = parent;
    }

    public PimJob getParent() {
        return parent;
    }

    public KrakenModel getKrakenModel() {
        return krakenModel;
    }

    public void setKrakenModel(KrakenModel krakenModel) {
        this.krakenModel = krakenModel;
    }

    public OcrSystem getOcrSystem() {
        return ocrSystem;
    }

    public void setOcrSystem(OcrSystem ocrSystem) {
        this.ocrSystem = ocrSystem;
    }

    public enum OcrSystem {
        TESSERACT4,
        KRAKEN
    }
}
