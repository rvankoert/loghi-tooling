package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "id", name = "documentManifest_id_hidx"),
        @Index(columnList = "imagesetuuid", name = "documentManifest_imagesetuuid_hidx"),
//        @Index(columnList = "series", name = "documentManifest_series_hidx"),
        @Index(columnList = "imageSet", name = "documentManifest_imageSet_hidx")
})

public class DocumentManifest implements IPimObject {
    public DocumentManifest() {
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Type(type = "text")
//    private String series;
    @Type(type = "text")
    private String imageSet;
    private UUID imagesetuuid;

    @Type(type = "text")
    private String manifest;


    @Column(nullable = false, unique = true)
    private UUID uuid;

    private Date created;
    private Date updated;
    private Date deleted;

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

//    public String getSeries() {
//        return series;
//    }
//
//    public void setSeries(String series) {
//        this.series = series;
//    }

    public String getImageSet() {
        return imageSet;
    }

    public void setImageSet(String imageSet) {
        this.imageSet = imageSet;
    }

    public String getManifest() {
        return manifest;
    }

    public void setManifest(String manifest) {
        this.manifest = manifest;
    }

    public UUID getImagesetuuid() {
        return imagesetuuid;
    }

    public void setImagesetuuid(UUID imagesetuuid) {
        this.imagesetuuid = imagesetuuid;
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
}