//package nl.knaw.huc.di.images.layoutds.models;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
//import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
//import org.hibernate.annotations.Type;
//
//import javax.persistence.*;
//import javax.xml.bind.annotation.XmlRootElement;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//@Entity
//@XmlRootElement
//@Table(indexes = {
//        @Index(columnList = "id", name = "documentSeries_id_hidx"),
//        @Index(columnList = "uuid", name = "documentSeries_uuid_hidx", unique = true),
//        @Index(columnList = "series", name = "documentSeries_series_hidx"),
//        @Index(columnList = "prettyName", name = "documentSeries_prettyName_hidx")
//})
//
//public class DocumentSeries implements IPimObject {
//
//    private Date created;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID uuid;
//
//    @Type(type = "text")
//    private String series;
//    @Type(type = "text")
//    private String prettyName;
//
//    @Type(type = "text")
//    private String seriesDescription;
//
//    @Type(type = "text")
//    private String license;
//    @Type(type = "text")
//    private String attribution;
//    @Type(type = "text")
//    private String logo;
//
//    @Type(type = "text")
//    @Column(unique = true)
//    private String uri;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ownerId")
//    @JsonIgnore
//    private PimUser owner;
//
//    public DocumentSeries() {
//        this.uuid = UUID.randomUUID();
//        this.created = new Date();
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public UUID getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(UUID uuid) {
//        this.uuid = uuid;
//    }
//
//    public String getSeries() {
//        return series;
//    }
//
//    public void setSeries(String series) {
//        this.series = series;
//    }
//
//    public String getPrettyName() {
//        return prettyName;
//    }
//
//    public void setPrettyName(String prettyName) {
//        this.prettyName = prettyName;
//    }
//
//    public String getSeriesDescription() {
//        return seriesDescription;
//    }
//
//    public void setSeriesDescription(String seriesDescription) {
//        this.seriesDescription = seriesDescription;
//    }
//
//    @OneToMany(targetEntity = DocumentImageSet.class, mappedBy = "documentSeries", fetch = FetchType.LAZY)
//    @ElementCollection(targetClass = DocumentImageSet.class)
//    @JsonIgnore
//    private Set<DocumentImageSet> documentImageSets;
//
//    public Set<DocumentImageSet> getDocumentImageSets() {
//        return documentImageSets;
//    }
//
//    public void setDocumentImageSets(Set<DocumentImageSet> documentImageSets) {
//        this.documentImageSets = documentImageSets;
//    }
//
//    public String getLicense() {
//        return license;
//    }
//
//    public void setLicense(String license) {
//        this.license = license;
//    }
//
//    public String getAttribution() {
//        return attribution;
//    }
//
//    public void setAttribution(String attribution) {
//        this.attribution = attribution;
//    }
//
//    public String getLogo() {
//        return logo;
//    }
//
//    public void setLogo(String logo) {
//        this.logo = logo;
//    }
//
//    public String getUri() {
//        return uri;
//    }
//
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
//
//    public void setOwner(PimUser owner) {
//        this.owner = owner;
//    }
//
//    public PimUser getOwner() {
//        return owner;
//    }
//
//    public void setCreated(Date created) {
//        this.created = created;
//    }
//
//    public Date getCreated() {
//        return created;
//    }
//}