package nl.knaw.huc.di.images.layoutds.models.pim;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Acl implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID uuid;

    @ManyToOne(optional = false)
    private PimGroup group;

    @Enumerated(EnumType.STRING)
    private Permission permission;
    private UUID subjectUuid;
    @Enumerated(EnumType.STRING)
    private Role role;

    public Acl() {

    }

    public Acl(UUID subjectUuid, PimGroup group, Permission permission, Role role) {
        this.subjectUuid = subjectUuid;
        this.role = role;
        this.uuid = UUID.randomUUID();
        this.group = group;
        this.permission = permission;
    }

    public static Acl readPermission(UUID subjectUuid, PimGroup group, Role role) {
        return new Acl(subjectUuid, group, Acl.Permission.READ, role);
    }

    public static Acl createPermission(UUID subjectUuid, PimGroup group, Role role) {
        return new Acl(subjectUuid, group, Acl.Permission.CREATE, role);
    }

    public static Acl updatePermission(UUID subjectUuid, PimGroup group, Role role) {
        return new Acl(subjectUuid, group, Acl.Permission.UPDATE, role);
    }

    public static Acl deletePermission(UUID subjectUuid, PimGroup group, Role role) {
        return new Acl(subjectUuid, group, Permission.DELETE, role);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PimGroup getGroup() {
        return group;
    }

    public Permission getPermission() {
        return permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSubjectUuid() {
        return subjectUuid;
    }

    public void setSubjectUuid(UUID subjectUuid) {
        this.subjectUuid = subjectUuid;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public enum Permission {
        READ,
        UPDATE,
        CREATE,
        DELETE
    }

    @Override
    public String toString() {
        return "Acl{" +
                "group=" + group +
                ", permission=" + permission +
                ", subjectUuid=" + subjectUuid +
                ", role=" + role +
                '}';
    }
}
