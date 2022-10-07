package nl.knaw.huc.di.images.layoutds.models.pim;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
@XmlRootElement
public class PimACL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "text")
    private String name;

    private Date created;
    private Date updated;
    private Date deleted;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    private List<PimGroup> groups;
    private List<IPimObject> pimObjects;


    public PimACL() {
        created = new Date();
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}