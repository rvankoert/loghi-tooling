//package nl.knaw.huc.di.images.layoutds.models.authorization;
//
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//public class AuthorizationACL {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private AuthorizationRole role;
//    private AuthorizationResource resource;
//
//    private String failMessage;
//
//    private boolean create;
//    private boolean read;
//    private boolean update;
//    private boolean delete;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public AuthorizationRole getRole() {
//        return role;
//    }
//
//    public void setRole(AuthorizationRole role) {
//        this.role = role;
//    }
//
//    public AuthorizationResource getResource() {
//        return resource;
//    }
//
//    public void setResource(AuthorizationResource resource) {
//        this.resource = resource;
//    }
//
//    public String getFailMessage() {
//        return failMessage;
//    }
//
//    public void setFailMessage(String failMessage) {
//        this.failMessage = failMessage;
//    }
//
//    public boolean isCreate() {
//        return create;
//    }
//
//    public void setCreate(boolean create) {
//        this.create = create;
//    }
//
//    public boolean isRead() {
//        return read;
//    }
//
//    public void setRead(boolean read) {
//        this.read = read;
//    }
//
//    public boolean isUpdate() {
//        return update;
//    }
//
//    public void setUpdate(boolean update) {
//        this.update = update;
//    }
//
//    public boolean isDelete() {
//        return delete;
//    }
//
//    public void setDelete(boolean delete) {
//        this.delete = delete;
//    }
//}
