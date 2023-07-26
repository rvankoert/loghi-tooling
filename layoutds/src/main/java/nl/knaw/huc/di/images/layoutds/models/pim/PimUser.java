package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;


//alter table PimUser add column uuid uuid ;
//update pimuser set uuid = uuid_generate_v4() where uuid is null;
//alter table PimUser alter column uuid set not null;
@Entity
@XmlRootElement
public class PimUser implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String avatar;

    private Long googleId;

    @Type(type = "text")
    private String name;
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String persistentId;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "pimuserroles",
            joinColumns = @JoinColumn(name = "userId"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "role"})
    )
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    private Date created;
    private Date updated;
    private Date deleted;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pimUser", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Membership> memberships;

    /*
     * The group most important for a user at a certain time.
     * This group is one of the pim groups of the user
     */
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = PimGroup.class)
    private PimGroup primaryGroup;

    private Boolean minion;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean disabled;

    public PimUser() {
        this.uuid = UUID.randomUUID();
        created = new Date();
        memberships = new HashSet<>();
        roles = new HashSet<>();
        disabled = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoogleId() {
        return googleId;
    }

    public void setGoogleId(Long googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Role> getRoles() {

        List<Role> roles = Lists.newArrayList(this.roles);
        if (!roles.contains(Role.AUTHENTICATED)) {
            roles.add(Role.AUTHENTICATED);
        }
        return roles;
    }

    public void setRoles(List<Role> roles) {

        this.roles = Set.copyOf(roles);
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
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

    @JsonIgnore
    public boolean isAdmin() {
        if (getRoles().contains(Role.ADMIN)) {
            return true;
        }
        return false;
    }

    public void setMinion(boolean minion) {
        this.minion = minion;
    }

    public boolean isMinion() {
        return minion != null && minion;
    }

    public PimGroup getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(PimGroup primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    @JsonIgnore
    public Set<PimGroup> getSuperGroupsInHierarchyPrimaryGroup() {
        return primaryGroup != null ? primaryGroup.getSuperGroupsInHierarchy() : Set.of();
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }

    public void addMembership(PimGroup pimGroup, Role role) {
        this.memberships.add(new Membership(pimGroup, this, role));
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PimUser pimUser = (PimUser) o;
        return Objects.equals(id, pimUser.id) && Objects.equals(uuid, pimUser.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid);
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}