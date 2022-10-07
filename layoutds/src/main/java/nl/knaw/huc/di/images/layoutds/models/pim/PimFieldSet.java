package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//CREATE UNIQUE INDEX pimfieldset_uuid_unique ON pimfieldset (uuid)
@Entity
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PimFieldSet implements IPimObject {
    public PimFieldSet() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;

    private String name;

    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimUserId")
    @JsonIgnore
    private PimUser owner;


    @JsonProperty("fields")
    @ElementCollection(targetClass = PimFieldDefinition.class)
    @Column
//    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pimFieldSet", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<PimFieldDefinition> fields;


    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(columnDefinition = "boolean default false")
    private boolean publicPimFieldSet;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<PimFieldDefinition> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields.stream().filter(field -> field.getDeleted() == null).collect(Collectors.toList());
    }

    // Will also return the deleted fields
    @JsonIgnore
    public List<PimFieldDefinition> getAllFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<PimFieldDefinition> fields) {
        this.fields = fields;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(PimFieldDefinition pimFieldDefinition) {
        if (this.fields == null) {
            this.fields = new ArrayList<>();
        }
        this.fields.add(pimFieldDefinition);
    }

    public PimUser getOwner() {
        return owner;
    }

    public void setOwner(PimUser owner) {
        this.owner = owner;
    }

    public boolean isPublicPimFieldSet() {
        return publicPimFieldSet;
    }

    public void setPublicPimFieldSet(boolean publicPimFieldSet) {
        this.publicPimFieldSet = publicPimFieldSet;
    }
}
