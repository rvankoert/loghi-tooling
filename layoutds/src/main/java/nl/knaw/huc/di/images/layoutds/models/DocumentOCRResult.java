package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;


//ALTER TABLE documentOCRResult drop column confidence;
//UPDATE documentOCRResult set transcriber = 1 where ocrtype='tesseract';
//ALTER TABLE documentOCRResult drop column ocrtype;

@Entity
@XmlRootElement
public class DocumentOCRResult implements IPimObject {
    private TranscriptionFormat format;
    private String creator;
    @Column(unique = true)
    private String remoteURL;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean emptyPage;

    public DocumentOCRResult(String result, Long documentImageId, Transcriber transcriber) {
        this.result = result;
        this.documentImageId = documentImageId;
        this.transcriber = transcriber;
        this.uuid = UUID.randomUUID();
    }

    public DocumentOCRResult() {
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentImageId;

    @Type(type = "text")
    private String result;
    private Transcriber transcriber;
    private String version;
    private Date analyzed;
    @Type(type = "text")
    private String params;


    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Transient
    private String cleanText;

    private String language;

    private String status;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Transcriber getTranscriber() {
        return transcriber;
    }

    public void setTranscriber(Transcriber transcriber) {
        this.transcriber = transcriber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getAnalyzed() {
        return analyzed;
    }

    public void setAnalyzed(Date analyzed) {
        this.analyzed = analyzed;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setFormat(TranscriptionFormat format) {
        this.format = format;
    }

    public TranscriptionFormat getFormat() {
        return format;
    }

    public String getCleanText() {
        return cleanText;
    }

    public void setCleanText(String cleanText) {
        this.cleanText = cleanText;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setRemoteURL(String remoteURL) {
        this.remoteURL = remoteURL;
    }

    public String getRemoteURL() {
        return remoteURL;
    }

    public void setEmptyPage(boolean emptyPage) {
        this.emptyPage = emptyPage;
    }

    public boolean getEmptyPage() {
        return emptyPage;
    }
}
