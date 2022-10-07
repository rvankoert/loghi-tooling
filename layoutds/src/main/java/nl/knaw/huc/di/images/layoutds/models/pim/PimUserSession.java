package nl.knaw.huc.di.images.layoutds.models.pim;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class PimUserSession implements IPimObject {

    public PimUserSession() {
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private PimUser user;

    @Column(unique = true, updatable = false)
    private String sessionId;

    @Column
    private Date lastActive;


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

    public PimUser getUser() {
        return user;
    }

    public void setUser(PimUser user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }
}
