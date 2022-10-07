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
        @Index(columnList = "id", name = "place_id_hidx"),
        @Index(columnList = "name", name = "place_name_hidx"),
        @Index(columnList = "uri", name = "place_uri_hidx")
})

public class Place implements IPimObject {
    public Place() {
        this.uuid = UUID.randomUUID();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "text")
    private String name;
    @Type(type = "text")
    private String uri;
    private Long documentImageId;
//    private String series;
    private String imageSet;
    private Integer pageOrder;
    private Date checked;
    private String metaphone;
    @Type(type = "text")
    private String provenance;
    private Integer confidence;
    private Double latitude;
    private Double longitude;
    @Transient
    private Integer counter;
    private String tag;
    private Integer geonameId;

    @Column(nullable = false, unique = true)
    private UUID uuid;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDocumentImageId(Long documentImageId) {
        this.documentImageId = documentImageId;
    }

    public Long getDocumentImageId() {
        return documentImageId;
    }

//    public void setSeries(String series) {
//        this.series = series;
//    }
//
//    public String getSeries() {
//        return series;
//    }

    public void setImageSet(String imageSet) {
        this.imageSet = imageSet;
    }

    public String getImageSet() {
        return imageSet;
    }

    public void setPageOrder(Integer pageOrder) {
        this.pageOrder = pageOrder;
    }

    public Integer getPageOrder() {
        return pageOrder;
    }

    public Date getChecked() {
        if (this.checked == null) {
            return null;
        } else {
            return (Date) checked.clone();
        }
    }

    public void setChecked(Date checked) {
        if (checked == null) {
            this.checked = null;
        } else {
            this.checked = (Date) checked.clone();
        }
    }

    public String getMetaphone() {
        return metaphone;
    }

    public void setMetaphone(String metaphone) {
        this.metaphone = metaphone;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }


}