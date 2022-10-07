package nl.knaw.huc.di.images.layoutds.models.pim;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"pimuser_id", "pimgroup_id", "role"}))
public class Membership implements IPimObject {
    @ManyToOne
    private PimUser pimUser;

    @ManyToOne
    private PimGroup pimGroup;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;
    private UUID uuid;


    public Membership(PimGroup pimGroup, PimUser pimUser, Role role) {
        this();
        this.pimGroup = pimGroup;
        this.pimUser = pimUser;
        this.role = role;
    }

    public Membership() {
        uuid = UUID.randomUUID();
    }

    public PimUser getPimUser() {
        return pimUser;
    }

    public void setPimUser(PimUser pimUser) {
        this.pimUser = pimUser;
    }

    public PimGroup getPimGroup() {
        return pimGroup;
    }

    public void setPimGroup(PimGroup pimGroup) {
        this.pimGroup = pimGroup;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Membership that = (Membership) o;
        return pimUser.equals(that.pimUser) && pimGroup.equals(that.pimGroup) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pimUser, pimGroup, role);
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

    public boolean isAllowedToSeeRole(Role role) {
        return this.role.compareTo(role) <= 0;
    }
}
