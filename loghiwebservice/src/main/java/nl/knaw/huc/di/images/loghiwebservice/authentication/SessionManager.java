package nl.knaw.huc.di.images.loghiwebservice.authentication;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final ConcurrentHashMap<UUID, User> sessionUserMap;

    public SessionManager() {
        sessionUserMap = new ConcurrentHashMap<>();
    }

    public synchronized Optional<User> getUserBySession(UUID sessionId) {
        return Optional.ofNullable(sessionUserMap.get(sessionId));
    }

    public synchronized void register(UUID sessionId, User user) {
        sessionUserMap.put(sessionId, user);
    }
}
