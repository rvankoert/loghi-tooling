package nl.knaw.huc.di.images.loghiwebservice.authentication;

import javax.security.auth.Subject;
import java.security.Principal;

public class User implements Principal {

    private final String userName;

    public User(String userName) {
        this.userName = userName;
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}
