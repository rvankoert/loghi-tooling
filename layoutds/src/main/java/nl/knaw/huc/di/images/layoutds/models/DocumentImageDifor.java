package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@Entity
@XmlRootElement
@Table

public class DocumentImageDifor implements IPimObject {

    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentImageId")
    private DocumentImage documentImage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    private String collection;
    private String repository;

    private String shelfmark;

    private String page;

    @Column(unique = true)
    private String uri;
    private String approximateDate;
    private String origin;
    private String writing;
    private String scribe;
    private String layout;
    private String numberOfLines;
    private String iiifManifest;

    @Type(type = "text")
    private String notes;
    private String tag;

    public DocumentImageDifor() {

        this.uuid = UUID.randomUUID();
    }

    public DocumentImage getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(DocumentImage documentImage) {
        this.documentImage = documentImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getApproximateDate() {
        return approximateDate;
    }

    public void setApproximateDate(String approximateDate) {
        this.approximateDate = approximateDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public String getScribe() {
        return scribe;
    }

    public void setScribe(String scribe) {
        this.scribe = scribe;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(String numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    public String getIiifManifest() {
        return iiifManifest;
    }

    public void setIiifManifest(String iiifManifest) {
        this.iiifManifest = iiifManifest;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}