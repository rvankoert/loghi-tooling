//package nl.knaw.huc.di.images.layoutds.models.authorization;
//
//import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
//
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//public class AuthorizationUser implements IPimObject {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private String identifier;
//    private String identityProvider;
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
//    public String getIdentifier() {
//        return identifier;
//    }
//
//    public void setIdentifier(String identifier) {
//        this.identifier = identifier;
//    }
//
//    public String getIdentityProvider() {
//        return identityProvider;
//    }
//
//    public void setIdentityProvider(String identityProvider) {
//        this.identityProvider = identityProvider;
//    }
//
//    public AuthorizationGroup getPrimaryGroup(){
//        return new AuthorizationGroup();
//    }
//}
