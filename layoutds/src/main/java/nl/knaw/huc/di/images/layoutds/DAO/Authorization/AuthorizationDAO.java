//package nl.knaw.huc.di.images.layoutds.DAO.Authorization;
//
//import nl.knaw.huc.di.images.layoutds.models.authorization.AuthorizationACL;
//import nl.knaw.huc.di.images.layoutds.models.authorization.AuthorizationGroup;
//import nl.knaw.huc.di.images.layoutds.models.authorization.AuthorizationResource;
//import nl.knaw.huc.di.images.layoutds.models.authorization.AuthorizationUser;
//
//import java.util.List;
//
//public class AuthorizationDAO {
//
//
//    public void addPersonToGroup(AuthorizationUser user, AuthorizationGroup group) {
//        if (group.getUsers().contains(user)) {
//            throw new SecurityException("Group already contains user");
//        }
//        group.getUsers().add(user);
////        AuthorizationGroupDAO dao = new AuthorizationGroupDAO();
////        dao.save(group);
//    }
//
//    public void removePersonToGroup(AuthorizationUser user, AuthorizationGroup group) {
//        if (!group.getUsers().contains(user)) {
//            throw new SecurityException("Group does not contain user");
//        }
//        group.getUsers().remove(user);
////        AuthorizationGroupDAO dao = new AuthorizationGroupDAO();
////        dao.save(group);
//    }
//
//    public void viewGroups(AuthorizationUser user) {
//    }
//
//    public void viewPersons(AuthorizationGroup group) {
//
//    }
//
//    public void viewGroups(AuthorizationGroup group) {
//
//    }
//
//    public boolean isPersonInGroup() {
//        return false;
//    }
//
//    private boolean canCreateGroup(AuthorizationUser user) {
////        AuthorizationGroupDAO dao = new AuthorizationGroupDAO();
////        List<AuthorizationGroup> userGroups = dao.getByUser(user);
////        for (AuthorizationGroup group : userGroups) {
////            for (AuthorizationACL acl : group.getAcls()) {
////                if (acl.isCreate() && acl.getResource().getName().equals("create")) {
////                    return true;
////                }
////            }
////        }
//        return false;
//    }
//
//    // this will be facaded later
//    public boolean isPersonAllowedAccess(AuthorizationUser person, AuthorizationResource resource) {
//        return true;
//    }
//
//    public void createGroup(AuthorizationUser creator, String name) {
//        if (!canCreateGroup(creator)) {
//            throw new SecurityException("not enough privileges to create group");
//        }
//        AuthorizationGroup group = new AuthorizationGroup();
//        group.setName(name);
//        group.setOwner(creator.getPrimaryGroup());
//
////        AuthorizationGroupDAO dao = new AuthorizationGroupDAO();
////        dao.save(group);
//    }
//
////    public void createResource(String name, AuthorizationUser owner) {
////        AuthorizationResource resource = new AuthorizationResource();
////        resource.setOwner(owner);
////        resource.setName(name);
////        AuthorizationResourceDAO dao = new AuthorizationResourceDAO();
////        dao.save(resource);
////    }
//
//
//}
