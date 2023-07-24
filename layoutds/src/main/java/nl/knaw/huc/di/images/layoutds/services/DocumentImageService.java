package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageDAO;
import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageSetDAO;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.security.PermissionHandler;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentImageService {

    private final PermissionHandler permissionHandler;
    private final DocumentImageDAO documentImageDAO;
    private final DocumentImageSetDAO documentImageSetDAO;

    public DocumentImageService() {
        this(new PermissionHandler());
    }

    public DocumentImageService(PermissionHandler permissionHandler) {

        this.permissionHandler = permissionHandler;
        documentImageDAO = new DocumentImageDAO();
        documentImageSetDAO = new DocumentImageSetDAO();
    }

    public Optional<DocumentImage> getByUuid(Session session, UUID uuid, PimUser pimUser) {
        final DocumentImageDAO documentImageDAO = new DocumentImageDAO();
        final DocumentImage documentImage = documentImageDAO.getByUUID(session, uuid);

        return getDocumentImageOptional(session, pimUser, documentImage);
    }

    private Optional<DocumentImage> getDocumentImageOptional(Session session, PimUser pimUser, DocumentImage documentImage) {
        if (pimUser.getDisabled()) {
            return Optional.empty();
        }
        if (documentImage != null) {
            if (permissionHandler.useGroups()) {
                for (DocumentImageSet documentImageSet : documentImage.getDocumentImageSets()) {
                    if (documentImageSet.isPublicDocumentImageSet() || permissionHandler.isAllowedToRead(session, documentImageSet.getUuid(), pimUser)) {
                        return Optional.of(documentImage);
                    }
                }
            } else {
                for (DocumentImageSet documentImageSet : documentImage.getDocumentImageSets()) {
                    if (pimUser.isAdmin() || documentImageSet.isPublicDocumentImageSet() || (documentImageSet.getOwner() != null && documentImageSet.getOwner().equals(pimUser))) {
                        return Optional.of(documentImage);
                    }
                }
            }
        }


        return Optional.empty();
    }

    public Optional<DocumentImage> getByRemoteUri(Session session, String remoteUri, PimUser pimUser) {
        final DocumentImage documentImage = documentImageDAO.getByRemoteUri(session, remoteUri);

        return getDocumentImageOptional(session, pimUser, documentImage);
    }

    public Stream<DocumentImage> getAllStreaming(Session session, PimUser pimUser) {
        if (pimUser.getDisabled()) {
            return Stream.empty();
        }

        if (permissionHandler.useGroups()) {
            final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();
            return documentImageSetDAO.streamAllForPrimaryGroupOfUser(session, pimUser, false)
                    .flatMap(set -> set.getDocumentImages().stream());
        }

        return documentImageDAO.getAllStreaming(session);
    }

    public Optional<DocumentImage> get(Session session, long id, PimUser pimUser) {
        if (pimUser.getDisabled()) {
            return Optional.empty();
        }

        final DocumentImage documentImage = documentImageDAO.get(session, id);

        if (documentImage != null) {
            for (DocumentImageSet documentImageSet : documentImage.getDocumentImageSets()) {
                if (permissionHandler.isAllowedToRead(session, documentImageSet.getUuid(), pimUser)) {
                    return Optional.of(documentImage);
                }
            }
        }

        return Optional.empty();
    }

    public Stream<DocumentImage> getAutoComplete(Session session, PimUser pimUser, String filter, int limit, int skip) {
        if (pimUser.getDisabled()) {
            return Stream.empty();
        }

        if (permissionHandler.useGroups()) {
            final List<Long> imageSetIds = documentImageSetDAO.streamAllForPrimaryGroupOfUser(session, pimUser, false)
                    .map(DocumentImageSet::getId)
                    .collect(Collectors.toList());
            return documentImageDAO.getAutocompleteForPrimaryGroupOfUser(session, filter, limit, skip, imageSetIds);
        }

        return documentImageDAO.getAutocomplete(session, filter, limit, skip);
    }
}
