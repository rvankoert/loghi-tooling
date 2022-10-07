package nl.knaw.huc.di.images.layoutds.models.pim;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class SiameseConfusionMatrix implements IPimObject{
    private Date created;
    private UUID uuid;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "text")
    private String report;

    public SiameseConfusionMatrix() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
