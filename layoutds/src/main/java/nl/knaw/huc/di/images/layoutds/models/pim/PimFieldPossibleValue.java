package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class PimFieldPossibleValue implements IPimObject{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID uuid;
    private Date created;

    @JsonIgnore
    @ManyToOne(targetEntity = PimFieldDefinition.class, optional = false)
    private PimFieldDefinition pimFieldDefinition;
    private String value;

    public PimFieldPossibleValue() {
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public PimFieldDefinition getPimFieldDefinition() {
        return pimFieldDefinition;
    }

    public void setPimFieldDefinition(PimFieldDefinition pimFieldDefinition) {
        this.pimFieldDefinition = pimFieldDefinition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PimFieldPossibleValue that = (PimFieldPossibleValue) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "PimFieldPossibleValue{" +
                "uuid=" + uuid +
                ", value='" + value + '\'' +
                '}';
    }
}
