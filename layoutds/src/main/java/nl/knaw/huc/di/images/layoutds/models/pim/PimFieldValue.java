package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


//ALTER TABLE pimFieldValue ALTER COLUMN  field set not null
//create index pimfieldvaluecreatorpimrecordfield on pimfieldvalue (pimUserId, pimrecordId,pimfieldId);
@Table(indexes = {
        @Index(columnList = "pimUserId, pimrecordId,pimfieldId", name = "pimfieldvalue_creatorpimrecordfield"),
        @Index(columnList = "pimrecordId", name = "pimfieldvalue_pimrecord")
})

@Entity
@XmlRootElement
public class PimFieldValue implements IPimObject {
    public PimFieldValue() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;

    @ManyToOne(targetEntity = PimFieldDefinition.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pimFieldId", nullable = false)
    PimFieldDefinition field;


    //alter table pimfieldvalue alter column value type text;
    @Type(type = "text")
    String value;

    @ManyToOne(targetEntity = PimRecord.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pimRecordId")
    PimRecord pimRecord;

    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimUserId")
    @JsonIgnore
    private PimUser creator;


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

    public PimFieldDefinition getField() {
        return field;
    }

    public void setField(PimFieldDefinition field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PimRecord getPimRecord() {
        return pimRecord;
    }

    public void setPimRecord(PimRecord pimRecord) {
        this.pimRecord = pimRecord;
    }

    public void setCreator(PimUser creator) {
        this.creator = creator;
    }

    public PimUser getCreator() {
        return creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PimFieldValue that = (PimFieldValue) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
