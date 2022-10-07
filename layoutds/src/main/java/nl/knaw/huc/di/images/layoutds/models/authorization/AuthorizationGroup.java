//package nl.knaw.huc.di.images.layoutds.models.authorization;
//
//import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
//
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AuthorizationGroup implements IPimObject {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//
//    private AuthorizationGroup owner;
//
//    private List<AuthorizationUser> users;
//    private List< AuthorizationGroup> groups;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public AuthorizationGroup getOwner() {
//        return owner;
//    }
//
//    public void setOwner(AuthorizationGroup owner) {
//        this.owner = owner;
//    }
//
//    public List<AuthorizationUser> getUsers() {
//        return users;
//    }
//
//    public void setUsers(List<AuthorizationUser> users) {
//        this.users = users;
//    }
//
//    public List<AuthorizationGroup> getGroups() {
//        return groups;
//    }
//
//    public void setGroups(List<AuthorizationGroup> groups) {
//        this.groups = groups;
//    }
//
//    public List<AuthorizationACL> getAcls(){
//        return new ArrayList<>();
//    }
//
//
//}
