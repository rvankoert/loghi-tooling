package nl.knaw.huc.di.images.layoutds.services;

public class GroupNotFoundException extends Exception {
    public GroupNotFoundException() {
        super("Group not found");
    }
}
