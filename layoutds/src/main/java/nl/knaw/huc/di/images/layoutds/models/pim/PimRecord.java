package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "parent", name = "pimrecord_parent_hidx")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PimRecord implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;
    private String parent;
    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimUserId")
    @JsonIgnore
    private PimUser creator;
    @OneToMany(targetEntity = PimFieldValue.class, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<PimFieldValue> fieldValues;
    @Column(nullable = false, unique = true)
    private UUID uuid;


    public PimRecord() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
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

    public List<PimFieldValue> getFieldValues() {
        if (fieldValues == null) {
            fieldValues = new ArrayList<>();
        }
        return fieldValues;
    }

    public void setFieldValues(List<PimFieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public PimUser getCreator() {
        return creator;
    }

    public void setCreator(PimUser creator) {
        this.creator = creator;
    }

    public void addFieldValue(PimFieldValue value) {
        if (this.fieldValues == null) {
            this.fieldValues = new ArrayList<>();
        }
        this.fieldValues.add(value);
    }
}
