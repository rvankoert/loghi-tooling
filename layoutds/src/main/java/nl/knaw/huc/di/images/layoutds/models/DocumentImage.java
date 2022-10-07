package nl.knaw.huc.di.images.layoutds.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.PublicAnnotation;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;


@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "frogNerBestAnalyzed", name = "documentImage_frogNerBestAnalyzed2_hidx"),
        @Index(columnList = "id", name = "documentImage_id_hidx", unique = true),
        @Index(columnList = "uuid", name = "documentImage_uuid_hidx", unique = true),
        @Index(columnList = "remoteuri", name = "documentImage_remoteuri_hidx"), // required by the opensearchharvester
        @Index(columnList = "sentToElasticSearch", name = "documentImage_sentToElasticSearch_hidx"),
        @Index(columnList = "uri", name = "documentImage_uri_hidx"),
        @Index(columnList = "originalfilename", name = "documentImage_originalfilename_hidx"),
        @Index(columnList = "uploaderId", name = "documentImage_uploaderid_hidx")
})


// INSERT INTO documentOCRResult (analyzed, documentImageId, result, version,uuid ,transcriber) (SELECT now(),id, tesseractPage, '0.01',uuid_generate_v4(),4  from documentImage where tesseractPage is not null);
// ALTER TABLE documentImage drop column tesseractPage
// alter table documentimagedataset drop column datasetid

public class DocumentImage implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    //CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
//    //alter table DocumentImage add column uuid uuid not null DEFAULT uuid_generate_v4() ;
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @PublicAnnotation(readOnly = true, uuid = "9efc24f7-594f-4a8a-8b72-50ba1f5b5408", label = "UUID")
    private UUID uuid;

    //unique identifier
    @Column(nullable = false, unique = true)
    @Type(type = "text")
    private String uri;

    @Type(type = "text")
    private String iiifManifestUri;

    // uri where image can be retrieved
    @Column(unique = true)
    @Type(type = "text")
    @PublicAnnotation(readOnly = true, uuid = "e14a5166-c0f9-43df-959e-1040b42393ce", label = "Remote uri")
    private String remoteuri;

    @Type(type = "text")
    private String layoutXML;
    private Date layoutXMLAnalyzed;
    private Date sentToElasticSearch;
    //    private Date frogNerAnalyzed;
    @PublicAnnotation(readOnly = true, uuid = "4916095d-f6f7-47d1-ac9e-62ac2bfd08bf", label = "Height")
    private Integer height;
    @PublicAnnotation(readOnly = true, uuid = "130c77b5-5c86-41aa-88bf-da1d38d37d1a", label = "Width")
    private Integer width;
    @PublicAnnotation(readOnly = true, uuid = "a7e4bdab-d439-4d3e-a764-f53532361b8b", label = "Broken")
    private Boolean broken;
    // pagenumber as used within the book itself, printed pagenumber
    @PublicAnnotation(readOnly = false, uuid = "b1edc2df-3111-4284-809a-ff7e7f88316a", label = "Pagenumber")
    private Integer pagenumber;
    /// actual number of page including appendices, title pages etc
    @PublicAnnotation(readOnly = false, uuid = "0b1b1a19-23ba-4173-a5fb-5175a4fb5212", label = "Pageorder")
    private Integer pageorder;
    private Date pagenumberDetected;
    private Date dateDetectedDate;
    @Type(type = "text")
    private String level0; // usually chapter
    @Type(type = "text")
    private String level1; // usually subchapter

    private String documentImageType;
    private String detectedDocumentImageType;
    private long size;
    private String layoutXMLVersion;
    @PublicAnnotation(readOnly = true, uuid = "1aeac32b-0a01-4360-8f1e-98069e8dde83", label = "Deskew angle")
    private Double deskewAngle;
    private Date dateSectionNumberChecked;
    @PublicAnnotation(readOnly = true, uuid = "2ebaf0bf-864b-4384-9d32-c7ec7d6cd160", label = "Column count")
    private Integer columnCount;
    @PublicAnnotation(readOnly = true, uuid = "04310ac2-aa41-469a-a07d-99643582208e", label = "Line count")
    private Integer linesCount;
    @Type(type = "text")
    @PublicAnnotation(readOnly = true, uuid = "8db8e0d3-abc4-40fa-b07d-340b931a8dd6", label = "Tesseract 4 best hocr text")
    private String tesseract4BestHOCRText;
    private String tesseract4BestHOCRVersion;
    private Date tesseract4BestHOCRAnalyzed;
    private Integer tesseract4BestHOCRConfidence;
    private String languageBest;
    private Date frogNerBestAnalyzed;
    private String documentImageLink;
    private Boolean publish;
    private String eadId;
    private Date placesExtracted;

    private Integer tesseract4BestWords;
    @Type(type = "text")
    private String tesseractAltoText;

    private Date textLinesExtracted;
    @Type(type = "text")
    @PublicAnnotation(readOnly = false, uuid = "396a9578-33e8-4f63-ad9e-193e1db0286e", label = "HTR text")
    private String HTRText;
    @PublicAnnotation(readOnly = true, uuid = "ce5b4ad5-9cca-4dcc-a8b9-0cec4841b53d", label = "Label")
    private String label;

//    @Type(type = "text")
//    private String tesseractPage;


    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "documentimagedataset",
            joinColumns = {@JoinColumn(name = "documentImageId")},
            inverseJoinColumns = {@JoinColumn(name = "documentImageSetId")}
    )
    private Set<DocumentImageSet> documentImageSets = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaderId")
    @JsonIgnore
    private PimUser uploader;

    private String sha512;
    @Type(type = "text")
    @PublicAnnotation(readOnly = true, uuid = "6b2b544e-10d0-4bf9-af47-2cf0fcc78da5", label = "Original filename")
    private String originalFileName;
    private Date created;
    @Column(unique = true)
    private Long transkribusPageId;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, targetEntity = MetaData.class, fetch = FetchType.LAZY, mappedBy = "parent")
    private Set<MetaData> metaData;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean sentForSiamese;

    @ManyToMany(targetEntity = VectorModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "documentimagevectormodel",
            joinColumns = {@JoinColumn(name = "documentImageId")},
            inverseJoinColumns = {@JoinColumn(name = "vectorModelId")}
    )
    private List<VectorModel> analyzedByVectorModel;


    public DocumentImage() {
        documentOCRResults = new ArrayList<>();
        created = new Date();
        uuid = UUID.randomUUID();
        analyzedByVectorModel = new ArrayList<>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Boolean getBroken() {
        return broken;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }

    public String getLayoutXML() {
        return layoutXML;
    }

    public void setLayoutXML(String layoutXML) {
        this.layoutXML = layoutXML;
    }

    public Date getLayoutXMLAnalyzed() {
        return layoutXMLAnalyzed;
    }

    public void setLayoutXMLAnalyzed(Date layoutXMLAnalyzed) {
        this.layoutXMLAnalyzed = layoutXMLAnalyzed;
    }

    public String getIiifManifestUri() {
        return iiifManifestUri;
    }

    public void setIiifManifestUri(String iiifManifestUri) {
        this.iiifManifestUri = iiifManifestUri;
    }

    public String getRemoteuri() {
        return remoteuri;
    }

    public void setRemoteuri(String remoteuri) {
        this.remoteuri = remoteuri;
    }

    public String getLayoutXMLVersion() {
        return layoutXMLVersion;
    }

    public void setLayoutXMLVersion(String layoutXMLVersion) {
        this.layoutXMLVersion = layoutXMLVersion;
    }

    public Integer getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(Integer pagenumber) {
        this.pagenumber = pagenumber;
    }

    public Date getPagenumberDetected() {
        return pagenumberDetected;
    }

    public void setPagenumberDetected(Date pagenumberDetected) {
        if (pagenumberDetected == null) {
            this.pagenumberDetected = null;
        } else {
            this.pagenumberDetected = (Date) pagenumberDetected.clone();
        }
    }

    public Date getDateDetectedDate() {
        return dateDetectedDate;
    }

    public void setDateDetectedDate(Date dateDetectedDate) {
        this.dateDetectedDate = dateDetectedDate;
    }

    public Integer getPageorder() {
        return pageorder;
    }

    public void setPageorder(Integer pageorder) {
        this.pageorder = pageorder;
    }

    public String getLevel0() {
        return level0;
    }

    public void setLevel0(String level0) {
        this.level0 = level0;
    }

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public Date getSentToElasticSearch() {
        if (sentToElasticSearch == null) {
            return null;
        }
        return (Date) sentToElasticSearch.clone();
    }

    public void setSentToElasticSearch(Date sentToElasticSearch) {
        if (sentToElasticSearch == null) {
            this.sentToElasticSearch = null;
        } else {
            this.sentToElasticSearch = (Date) sentToElasticSearch.clone();
        }
    }

    public String getDocumentImageType() {
        return documentImageType;
    }

    public void setDocumentImageType(String documentImageType) {
        this.documentImageType = documentImageType;
    }

    public String getDetectedDocumentImageType() {
        return detectedDocumentImageType;
    }

    public void setDetectedDocumentImageType(String detectedDocumentImageType) {
        this.detectedDocumentImageType = detectedDocumentImageType;
    }

    public void setDeskewAngle(Double deskewAngle) {
        this.deskewAngle = deskewAngle;
    }

    public Double getDeskewAngle() {
        return deskewAngle;
    }

    public void setDateSectionNumberChecked(Date dateSectionNumberChecked) {
        if (dateSectionNumberChecked == null) {
            this.dateSectionNumberChecked = null;
        } else {
            this.dateSectionNumberChecked = (Date) dateSectionNumberChecked.clone();
        }
    }

    public Date getDateSectionNumberChecked() {
        return dateSectionNumberChecked;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setLinesCount(Integer linesCount) {
        this.linesCount = linesCount;
    }

    public Integer getLinesCount() {
        return linesCount;
    }

    public void setTesseract4BestHOCRText(String tesseract4BestHOCRText) {
        this.tesseract4BestHOCRText = tesseract4BestHOCRText;
    }

    public String getTesseract4BestHOCRText() {
        return tesseract4BestHOCRText;
    }

    public void setTesseract4BestHOCRVersion(String tesseract4BestHOCRVersion) {
        this.tesseract4BestHOCRVersion = tesseract4BestHOCRVersion;
    }

    public String getTesseract4BestHOCRVersion() {
        return tesseract4BestHOCRVersion;
    }

    public void setTesseract4BestHOCRAnalyzed(Date tesseract4BestHOCRAnalyzed) {
        this.sentToElasticSearch = null;
        this.tesseract4BestHOCRConfidence = null;
        this.frogNerBestAnalyzed = null;
        this.dateDetectedDate = null;
        this.tesseract4BestWords = null;
        this.languageBest = null;
        if (tesseract4BestHOCRAnalyzed == null) {
            this.tesseract4BestHOCRAnalyzed = null;
        } else {
            this.tesseract4BestHOCRAnalyzed = (Date) tesseract4BestHOCRAnalyzed.clone();
        }
    }

    public Date getTesseract4BestHOCRAnalyzed() {
        return tesseract4BestHOCRAnalyzed;
    }

    public void setTesseract4BestHOCRConfidence(Integer tesseract4BestHOCRConfidence) {
        this.tesseract4BestHOCRConfidence = tesseract4BestHOCRConfidence;
    }

    public Integer getTesseract4BestHOCRConfidence() {
        return tesseract4BestHOCRConfidence;
    }

    public void setLanguageBest(String languageBest) {
        this.languageBest = languageBest;
    }

    public String getLanguageBest() {
        return languageBest;
    }

    public void setFrogNerBestAnalyzed(Date frogNerBestAnalyzed) {
        this.sentToElasticSearch = null;
        this.frogNerBestAnalyzed = frogNerBestAnalyzed;
    }

    public Date getFrogNerBestAnalyzed() {
        return frogNerBestAnalyzed;
    }

    @ElementCollection(targetClass = DocumentOCRResult.class)
    @Column
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentImageId")
    private List<DocumentOCRResult> documentOCRResults;

    @JsonIgnore
    @XmlTransient
    public List<DocumentOCRResult> getDocumentOCRResults() {
        return documentOCRResults;
    }

    @JsonIgnore
    public void setDocumentOCRResults(List<DocumentOCRResult> documentOCRResults) {
        this.documentOCRResults = documentOCRResults;
    }

    @ElementCollection(targetClass = DocumentGroundTruth.class)
    @Column
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentImageId")
    private List<DocumentGroundTruth> documentGroundTruth;

    @JsonIgnore
    @XmlTransient
    public List<DocumentGroundTruth> getDocumentGroundTruth() {
        return documentGroundTruth;
    }

    @JsonIgnore
    public void setDocumentGroundTruth(List<DocumentGroundTruth> documentGroundTruth) {
        this.documentGroundTruth = documentGroundTruth;
    }

    public void setDocumentImageLink(String documentImageLink) {
        this.documentImageLink = documentImageLink;
    }

    public String getDocumentImageLink() {
        return documentImageLink;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public void setEadId(String eadId) {
        this.eadId = eadId;
    }

    public String getEadId() {
        return eadId;
    }

    public void setPlacesExtracted(Date placesExtracted) {
        if (placesExtracted == null) {
            this.placesExtracted = null;
        } else {
            this.placesExtracted = (Date) placesExtracted.clone();
        }
    }

    public Date getPlacesExtracted() {
        return placesExtracted;
    }

    public void setTesseract4BestWords(Integer tesseract4BestWords) {
        this.tesseract4BestWords = tesseract4BestWords;
    }

    public Integer getTesseract4BestWords() {
        return tesseract4BestWords;
    }

    public void setTesseractAltoText(String tesseractAltoText) {
        this.tesseractAltoText = tesseractAltoText;
    }

    public String getTesseractAltoText() {
        return tesseractAltoText;
    }

    public Date getTextLinesExtracted() {
        return textLinesExtracted;
    }

    public void setTextLinesExtracted(Date textLinesExtracted) {
        this.textLinesExtracted = textLinesExtracted;
    }

    @JsonIgnore
    public String getName() {
        String fileName = getFileName();

        return fileName.substring(0, fileName.lastIndexOf('.'));

    }

    @JsonIgnore
    public String getFileName() {
        return uri.substring(uri.lastIndexOf('/') + 1).replace("?", "_").replace("=", "_").replace(":", "_");
    }

    public String getHTRText() {
        return HTRText;
    }

    public void setHTRText(String htrText) {
        this.HTRText = htrText;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public void addDocumentImageSet(DocumentImageSet documentImageSet) {
        this.documentImageSets.add(documentImageSet);
    }

    @JsonIgnore
    public Set<DocumentImageSet> getDocumentImageSets() {
        return documentImageSets;
    }

    public void setDocumentImageSets(Set<DocumentImageSet> documentImageSets) {
        this.documentImageSets = documentImageSets;
    }

    public void setUploader(PimUser uploader) {
        this.uploader = uploader;
    }

    public PimUser getUploader() {
        return uploader;
    }

    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getTranskribusPageId() {
        return transkribusPageId;
    }

    public void setTranskribusPageId(Long transkribusPageId) {
        this.transkribusPageId = transkribusPageId;
    }

    public Set<MetaData> getMetaData() {
        return metaData;
    }

    public void setMetaData(Set<MetaData> metaData) {
        this.metaData = metaData;
    }

    public void setSentForSiamese(boolean sentForSiamese) {
        this.sentForSiamese = sentForSiamese;
    }

    public boolean getSentForSiamese() {
        return sentForSiamese;
    }

    public List<VectorModel> getAnalyzedByVectorModel() {
        return analyzedByVectorModel;
    }

    public void setAnalyzedByVectorModel(List<VectorModel> analyzedByVectorModel) {
        this.analyzedByVectorModel = analyzedByVectorModel;
    }
}