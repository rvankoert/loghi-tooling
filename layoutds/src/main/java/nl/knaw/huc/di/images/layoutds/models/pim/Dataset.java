//package nl.knaw.huc.di.images.layoutds.models.pim;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
//
//import javax.persistence.*;
//import java.util.Date;
//import java.util.List;
//
//
//@Entity
//public class Dataset {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private Date created;
//    private Date updated;
//    private Date deleted;
//
//    @ManyToMany(mappedBy = "datasets", fetch=FetchType.LAZY)
//    private List<DocumentImage> documentImages;
//
//
//    private String name;
//    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "pimUserId")
//    private PimUser owner;
//
//    private boolean publicDataset;
//
//    public Dataset() {
//        this.created = new Date();
//
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
//    public Date getCreated() {
//        return created;
//    }
//
//    public void setCreated(Date created) {
//        this.created = created;
//    }
//
//    public Date getUpdated() {
//        return updated;
//    }
//
//    public void setUpdated(Date updated) {
//        this.updated = updated;
//    }
//
//    public Date getDeleted() {
//        return deleted;
//    }
//
//    public void setDeleted(Date deleted) {
//        this.deleted = deleted;
//    }
//
//    @JsonProperty("name")
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public PimUser getOwner() {
//        return owner;
//    }
//
//    public void setOwner(PimUser owner) {
//        this.owner = owner;
//    }
//
//    public boolean isPublicDataset() {
//        return publicDataset;
//    }
//
//    public void setPublicDataset(boolean publicDataset) {
//        this.publicDataset = publicDataset;
//    }
//
//    public boolean validate() {
//        return getName() != null && getName().trim().length() >= 3;
//    }
//
//    public void addImage(DocumentImage image) {
//        documentImages.add(image);
//    }
//
//    public List<DocumentImage> getImages() {
//        return documentImages;
//    }
//
//    public void setImages(List<DocumentImage> images) {
//        this.documentImages = images;
//    }
//}
