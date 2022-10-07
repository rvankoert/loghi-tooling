package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@Entity
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "pimfield")
public class PimFieldDefinition implements IPimObject {

    private String name;
    private String dataPath;

    public PimFieldDefinition() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }

    public PimFieldDefinition(String name, FieldType type) {
        this();
        this.name = name;
        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;

    // FetchType.EAGER and without JsonIgnore will cause an infinite loop when serializing to JSON
    @ManyToOne(targetEntity = PimFieldSet.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimFieldSetId")
    @JsonIgnore
    private PimFieldSet pimFieldSet;


    @JsonProperty("label")
    private String label;
    @JsonProperty("type")
    @Enumerated(value = EnumType.STRING)
    private FieldType type;
    @JsonProperty("value")
    private String value;
    @JsonProperty("checked")
    private Boolean checked;
    @JsonProperty("description")
    private String description;
    @JsonProperty("copyValue")
    @Column(columnDefinition = "boolean default false")
    private boolean copyValue;

    @JsonIgnore
    @Transient
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @OneToMany(targetEntity = PimFieldPossibleValue.class, fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PimFieldPossibleValue> possibleValues = new HashSet<>();


    @Column(nullable = false, unique = true)
    private UUID uuid;

    @JsonProperty("readOnly")
    private Boolean readOnly;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("type")
    public FieldType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(FieldType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("checked")
    public Boolean getChecked() {
        return checked;
    }

    @JsonProperty("checked")
    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonAnyGetter
    @Transient
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    @Transient
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PimFieldSet getPimFieldSet() {
        return pimFieldSet;
    }

    public void setPimFieldSet(PimFieldSet pimFieldSet) {
        this.pimFieldSet = pimFieldSet;
    }

    public Set<PimFieldPossibleValue> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(Set<PimFieldPossibleValue> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public void addPossibleValue(PimFieldPossibleValue possibleValue) {
        possibleValues.add(possibleValue);
    }

    public void removePossibleValue(PimFieldPossibleValue possibleValue) {
        possibleValues.remove(possibleValue);
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public boolean shouldCopyValue() {
        return copyValue;
    }

    public void setCopyValue(boolean copyValue) {
        this.copyValue = copyValue;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String toString() {
        return "PimFieldDefinition{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PimFieldDefinition that = (PimFieldDefinition) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }



    public enum FieldType {
        text,
        checkbox,
        date,
        documentimage,
        numeric,
        pimuser,
        script,
        select,
        notes,
        autocomplete
    }
}
