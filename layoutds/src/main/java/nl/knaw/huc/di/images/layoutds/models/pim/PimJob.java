package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "job")
public class PimJob implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;
    private Boolean inProgress;
    private Date startDate;
    private Date done;
    private JobType jobType;
    private Boolean allData;
    private Integer jobOrder;
//    @ManyToOne(targetEntity = DocumentSeries.class, fetch = FetchType.EAGER)
//    @JoinColumn(name = "documentSeriesId")
//    private DocumentSeries documentSeries;

    @ManyToOne(targetEntity = DocumentImageSet.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentImagesetId")
    private DocumentImageSet documentImageset;

    private String uri;
    private String outputPath;
    private String baseOutputFileName;
    @Column(nullable = false, columnDefinition = "int default 50")
    private int priority;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(targetEntity = PimJob.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @JsonIgnore
    private PimJob parent;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = PimJob.class, fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    @OrderBy("jobOrder ASC")
    private Set<PimJob> children;

    @Type(type = "text")
    private String error;

    @ManyToOne(targetEntity = Tesseract4Model.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "tesseract4modelId")
    @JsonIgnore
    private Tesseract4Model tesseract4model;
    private String iiifHost;

    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pimUserId")
    @JsonIgnore
    private PimUser pimUser;

    @ManyToOne(targetEntity = ElasticSearchIndex.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "elasticSearchIndexId")
    private ElasticSearchIndex elasticSearchIndex;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nextJobId", referencedColumnName = "id")
    private PimJob nextJob;

//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nextJob")
//    private PimJob previousJob;

//    @OneToOne(cascade = CascadeType.ALL, targetEntity = PimJob.class, fetch = FetchType.LAZY)
//    private PimJob nextJob;

    private boolean readyToRun;

    private Integer transkribusUploadId;
    @ManyToOne(targetEntity = KrakenModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "krakenModelId")
    private KrakenModel krakenModel;
    private String notificationEmail;
    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentImageId")
    private DocumentImage documentImage;
    @ManyToOne(targetEntity = PimFieldDefinition.class, fetch = FetchType.EAGER)
    private PimFieldDefinition pimField;
    private String downloadUri;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = P2PaLaJob.class, mappedBy = "wrapper", orphanRemoval = true)
//    @JoinColumn(referencedColumnName = "wrapper_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<P2PaLaJob> p2PaLaJob;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PimJob(PimJob pimJob) {
        this.pimUser = pimJob.getPimUser();
        this.uuid = UUID.randomUUID();
        this.created = new Date();
        this.elasticSearchIndex = pimJob.getElasticSearchIndex();
        this.allData = pimJob.getAllData();
        this.pimUser = pimJob.getPimUser();
        this.documentImageset = pimJob.getDocumentImageset();
//        this.documentSeries = pimJob.getDocumentSeries();
        this.priority = pimJob.getPriority();
    }

    public PimJob(PimUser pimUser) {
        this.pimUser = pimUser;
        this.uuid = UUID.randomUUID();
        this.created = new Date();
        this.priority = 50;
    }

    public PimJob() {
        this.uuid = UUID.randomUUID();
        this.created = new Date();
        this.deleted = null;
        this.updated = null;

        this.priority = 50;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDone() {
        return done;
    }

    public void setDone() {
        final Date done = new Date();
        setUpdated(done);
        this.done = done;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public Boolean getAllData() {
        return allData;
    }

    public void setAllData(Boolean allData) {
        this.allData = allData;
    }

    public Integer getJobOrder() {
        return jobOrder;
    }

    public void setJobOrder(Integer jobOrder) {
        this.jobOrder = jobOrder;
    }

//    public DocumentSeries getDocumentSeries() {
//        return documentSeries;
//    }
//
//    public void setDocumentSeries(DocumentSeries documentSeries) {
//        this.documentSeries = documentSeries;
//    }

    public DocumentImageSet getDocumentImageset() {
        return documentImageset;
    }

    public void setDocumentImageset(DocumentImageSet documentImageset) {
        this.documentImageset = documentImageset;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getBaseOutputFileName() {
        return baseOutputFileName;
    }

    public void setBaseOutputFileName(String baseOutputFileName) {
        this.baseOutputFileName = baseOutputFileName;
    }

    public PimJob getParent() {
        return parent;
    }

    public void setParent(PimJob parent) {
        this.parent = parent;
    }

    public Set<PimJob> getChildren() {
        if (children == null) {
            children = new HashSet<>();
        }
        return children;
    }

    public void setChildren(Set<PimJob> children) {
        this.children = children;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setTesseract4model(Tesseract4Model tesseract4model) {
        this.tesseract4model = tesseract4model;
    }

    public Tesseract4Model getTesseract4model() {
        return tesseract4model;
    }

    public String getIiifHost() {
        return iiifHost;
    }

    public void setIiifHost(String iiifHost) {
        this.iiifHost = iiifHost;
    }

    public PimUser getPimUser() {
        return pimUser;
    }

    public void setPimUser(PimUser pimUser) {
        this.pimUser = pimUser;
    }

    public ElasticSearchIndex getElasticSearchIndex() {
        return elasticSearchIndex;
    }

    public void setElasticSearchIndex(ElasticSearchIndex elasticSearchIndex) {
        this.elasticSearchIndex = elasticSearchIndex;
    }

    public PimJob getNextJob() {
        return nextJob;
    }

    public void setNextJob(PimJob nextJob) {
        this.nextJob = nextJob;
    }

    public boolean getReadyToRun() {
        return readyToRun;
    }

    public void setReadyToRun(boolean readyToRun) {
        this.readyToRun = readyToRun;
    }

    public Integer getTranskribusUploadId() {
        return transkribusUploadId;
    }

    public void setTranskribusUploadId(Integer transkribusUploadId) {
        this.transkribusUploadId = transkribusUploadId;
    }

    public KrakenModel getKrakenModel() {
        return krakenModel;
    }

    public void setKrakenModel(KrakenModel krakenModel) {
        this.krakenModel = krakenModel;
    }

    public void setOcrSystem(OCRJob.OcrSystem ocrSystem) {
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDocumentImage(DocumentImage documentImage) {
        this.documentImage = documentImage;
    }

    public DocumentImage getDocumentImage() {
        return documentImage;
    }

    @Deprecated
    public void setPimField(PimFieldDefinition pimField) {
        this.pimField = pimField;
    }

    @Deprecated
    public PimFieldDefinition getPimField() {
        return pimField;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getDownloadUri() {
        return downloadUri;
    }
}
