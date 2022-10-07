package nl.knaw.huc.di.images.layoutds.models.pim;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class ApiKey implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private UUID uuid;

    @OneToOne(fetch = FetchType.EAGER)
    private PimUser pimUser;

    @Column(columnDefinition = "boolean default false")
    private boolean disabled;

    public ApiKey() {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PimUser getPimUser() {
        return pimUser;
    }

    public void setPimUser(PimUser pimUser) {
        this.pimUser = pimUser;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
