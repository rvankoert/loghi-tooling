package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(columnList = "imageLocation", name = "documentTextLineSnippet_imageLocation_hidx")
})

public class DocumentTextLineSnippet implements Comparable, IPimObject {
    public DocumentTextLineSnippet() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentImageId")
    @JsonIgnore
    private DocumentImage parent;

    @Column(unique = true)
    private String imageLocation;
    private int startX;
    private int startY;
    private int height;
    private int width;
    private int majorVersion;
    private int minorVersion;
    private Date created;

    private float feature1;
    private float feature2;
    private float feature3;
    private float feature4;
    private float feature5;
    private float feature6;
    private float feature7;
    private float feature8;
    private float feature9;
    private float feature10;
    private float feature11;
    private float feature12;
    private float feature13;
    private float feature14;
    private float feature15;
    private float feature16;
    private float feature17;
    private float feature18;
    private float feature19;
    private float feature20;
    private long century;
    private String tag;
    private boolean isValidation;
    @Type(type = "text")
    private String features;

    @Column(name = "documentImageId", insertable = false, updatable = false)
    private Integer documentImageId;

    @Transient
    private double distanceFromTarget;
    @Transient
    private double cosineFromTarget;
    private String model;


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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DocumentImage getParent() {
        return parent;
    }

    public void setParent(DocumentImage parent) {
        this.parent = parent;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public float getFeature1() {
        return feature1;
    }

    public void setFeature1(float feature1) {
        this.feature1 = feature1;
    }

    public float getFeature2() {
        return feature2;
    }

    public void setFeature2(float feature2) {
        this.feature2 = feature2;
    }

    public float getFeature3() {
        return feature3;
    }

    public void setFeature3(float feature3) {
        this.feature3 = feature3;
    }

    public float getFeature4() {
        return feature4;
    }

    public void setFeature4(float feature4) {
        this.feature4 = feature4;
    }

    public float getFeature5() {
        return feature5;
    }

    public void setFeature5(float feature5) {
        this.feature5 = feature5;
    }

    public float getFeature6() {
        return feature6;
    }

    public void setFeature6(float feature6) {
        this.feature6 = feature6;
    }

    public float getFeature7() {
        return feature7;
    }

    public void setFeature7(float feature7) {
        this.feature7 = feature7;
    }

    public float getFeature8() {
        return feature8;
    }

    public void setFeature8(float feature8) {
        this.feature8 = feature8;
    }

    public float getFeature9() {
        return feature9;
    }

    public void setFeature9(float feature9) {
        this.feature9 = feature9;
    }

    public float getFeature10() {
        return feature10;
    }

    public void setFeature10(float feature10) {
        this.feature10 = feature10;
    }

    public float getFeature11() {
        return feature11;
    }

    public void setFeature11(float feature11) {
        this.feature11 = feature11;
    }

    public float getFeature12() {
        return feature12;
    }

    public void setFeature12(float feature12) {
        this.feature12 = feature12;
    }

    public float getFeature13() {
        return feature13;
    }

    public void setFeature13(float feature13) {
        this.feature13 = feature13;
    }

    public float getFeature14() {
        return feature14;
    }

    public void setFeature14(float feature14) {
        this.feature14 = feature14;
    }

    public float getFeature15() {
        return feature15;
    }

    public void setFeature15(float feature15) {
        this.feature15 = feature15;
    }

    public float getFeature16() {
        return feature16;
    }

    public void setFeature16(float feature16) {
        this.feature16 = feature16;
    }

    public float getFeature17() {
        return feature17;
    }

    public void setFeature17(float feature17) {
        this.feature17 = feature17;
    }

    public float getFeature18() {
        return feature18;
    }

    public void setFeature18(float feature18) {
        this.feature18 = feature18;
    }

    public float getFeature19() {
        return feature19;
    }

    public void setFeature19(float feature19) {
        this.feature19 = feature19;
    }

    public float getFeature20() {
        return feature20;
    }

    public void setFeature20(float feature20) {
        this.feature20 = feature20;
    }

    public long getCentury() {
        return century;
    }

    public void setCentury(long century) {
        this.century = century;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getDocumentImageId() {
        return documentImageId;
    }

    public void setDocumentImageId(Integer documentImageId) {
        this.documentImageId = documentImageId;
    }

    public boolean isValidation() {
        return isValidation;
    }

    public void setValidation(boolean validation) {
        isValidation = validation;
    }

    public double getDistanceFromTarget() {
        return distanceFromTarget;
    }

    public void setDistanceFromTarget(double distanceFromTarget) {
        this.distanceFromTarget = distanceFromTarget;
    }

    @Override
    public int compareTo(Object o) {
        if ( ((DocumentTextLineSnippet) o).getDistanceFromTarget()>getDistanceFromTarget()){
            return 1;
        }else{
            return -1;
        }

    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public void setCosineFromTarget(double cosineFromTarget) {
        this.cosineFromTarget = cosineFromTarget;
    }

    public double getCosineFromTarget() {
        return cosineFromTarget;
    }
}
