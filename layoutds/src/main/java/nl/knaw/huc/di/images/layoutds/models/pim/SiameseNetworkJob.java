package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiameseNetworkJob implements IPimObject {
    private String notificationEmail;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date created;
    private Date updated;
    private Date deleted;
    private boolean inProgress;
    private Date startDate;
    private Date done;
    private UUID uuid;
    @Type(type = "text")
    private String errors;

    private double dropout;

    private double learningRate;
    private int batchSize;
    private int imageWidth;
    private int imageHeight;
    private boolean training;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean bulk;
    @ManyToOne
    private DocumentImageSet imageSet;

    @ManyToOne(fetch = FetchType.EAGER)
    private SiameseNetworkModel model;
    private boolean ready;
    @OneToOne
    private PimJob wrapper;
    private String networkSpec;
    private String labelField;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int channels;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean binarizeOtsu;
    @ManyToMany(targetEntity = PimFieldDefinition.class, fetch = FetchType.EAGER)
    private List<PimFieldDefinition> pimFields;

    public SiameseNetworkJob(double dropout, double learningRate, int batchSize, int imageWidth, int imageHeight, boolean training, DocumentImageSet imageSet, SiameseNetworkModel model) {
        this();
        this.dropout = dropout;
        this.learningRate = learningRate;
        this.batchSize = batchSize;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.training = training;
        this.imageSet = imageSet;
        this.model = model;
    }

    public SiameseNetworkJob() {
        uuid = UUID.randomUUID();
        created = new Date();
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

    public void setDone(Date done) {
        this.done = done;
    }

    public void setDone() {
        final Date doneDate = new Date();
        this.setUpdated(doneDate);
        this.setDone(doneDate);
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public double getDropout() {
        return dropout;
    }

    public void setDropout(double dropout) {
        this.dropout = dropout;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    @JsonProperty(value = "config")
    public String getConfig() {
        if (training) {
            return "--dropout " + dropout +
                    " --learning_rate " + learningRate +
                    " --batch_size " + batchSize +
                    " --image_width " + imageWidth +
                    " --image_height " + imageHeight;
        }
        return "";
    }

    @JsonIgnore
    public void appendError(String appendix) {
        setErrors(getErrors() + "\n" + appendix);
    }


    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public boolean isTraining() {
        return training;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    public DocumentImageSet getImageSet() {
        return imageSet;
    }

    public void setImageSet(DocumentImageSet imageSet) {
        this.imageSet = imageSet;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public SiameseNetworkModel getModel() {
        return model;
    }

    public void setModel(SiameseNetworkModel model) {
        this.model = model;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public Boolean isReady() {
        return ready;
    }

    public void setWrapper(PimJob wrapper) {
        this.wrapper = wrapper;
    }

    public PimJob getWrapper() {
        return wrapper;
    }

    public void setNetworkSpec(String networkSpec) {
        this.networkSpec = networkSpec;
    }

    public String getNetworkSpec() {
        return networkSpec;
    }

    public void setLabelField(String labelField) {
        this.labelField = labelField;
    }

    public String getLabelField() {
        return labelField;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getChannels() {
        return channels;
    }

    public void setBinarizeOtsu(boolean binarizeOtsu) {
        this.binarizeOtsu = binarizeOtsu;
    }

    public boolean isBinarizeOtsu() {
        return binarizeOtsu;
    }

    public void setPimFields(List<PimFieldDefinition> pimFields) {
        this.pimFields = pimFields;
    }

    public List<PimFieldDefinition> getPimFields() {
        return pimFields;
    }

    public boolean isBulk() {
        return bulk;
    }

    public void setBulk(boolean bulk) {
        this.bulk = bulk;
    }
}
