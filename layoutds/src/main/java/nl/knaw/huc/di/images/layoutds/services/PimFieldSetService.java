package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.PimFieldDefinitionDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimFieldPossibleValueDao;
import nl.knaw.huc.di.images.layoutds.DAO.PimFieldSetDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimRecordDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import nl.knaw.huc.di.images.layoutds.security.PermissionHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PimFieldSetService {

    private final PimFieldSetDAO pimFieldSetDAO;
    private final PimFieldDefinitionDAO pimFieldDefinitionDAO;
    private final PimFieldPossibleValueDao pimFieldPossibleValueDao;
    private final PermissionHandler permissionHandler;

    public PimFieldSetService() {
        pimFieldSetDAO = new PimFieldSetDAO();
        pimFieldDefinitionDAO = new PimFieldDefinitionDAO();
        pimFieldPossibleValueDao = new PimFieldPossibleValueDao();
        permissionHandler = new PermissionHandler();
    }

    public PimFieldSet update(Session session, PimFieldSet pimFieldSet, PimUser pimUser) throws PimSecurityException {
        UUID uuid = pimFieldSet.getUuid();
        PimFieldSet oldPimFieldSet = pimFieldSetDAO.getByUUID(session, uuid);

        if (oldPimFieldSet == null) {
            throw new IllegalArgumentException("Use save for creating a new PimFieldSet");
        }

        if (!userIsAllowedToEdit(session, oldPimFieldSet, pimUser)) {
            throw new PimSecurityException("User does not have enough permissions to update entity");
        }

        final Transaction transaction = session.beginTransaction();
        oldPimFieldSet.setName(pimFieldSet.getName());
        oldPimFieldSet.setPublicPimFieldSet(pimFieldSet.isPublicPimFieldSet());

        final List<PimFieldDefinition> oldFields = oldPimFieldSet.getFields();
        final List<PimFieldDefinition> fields = pimFieldSet.getFields();

        // This order is important
        addFields(oldPimFieldSet, oldFields, fields);
        updateFields(session, oldPimFieldSet, oldFields, fields);
        deleteFields(session, oldFields, fields);

        transaction.commit();

        final List<PimFieldDefinition> nonDeletedFields = oldPimFieldSet.getFields().stream().filter(field -> field.getDeleted() == null).collect(Collectors.toList());
        oldPimFieldSet.setFields(nonDeletedFields);
        return oldPimFieldSet;
    }

    private void addFields(PimFieldSet oldPimFieldSet, List<PimFieldDefinition> oldFields, List<PimFieldDefinition> fields) {
        final Iterator<PimFieldDefinition> fieldsToAdd = fields.stream().filter(field -> !oldFields.contains(field))
                .iterator();
        while (fieldsToAdd.hasNext()) {
            final PimFieldDefinition pimFieldDefinition = fieldsToAdd.next();
            pimFieldDefinition.setPimFieldSet(oldPimFieldSet);
            pimFieldDefinitionDAO.save(pimFieldDefinition);
            oldPimFieldSet.addField(pimFieldDefinition);
        }
    }

    private void deleteFields(Session session, List<PimFieldDefinition> oldFields, List<PimFieldDefinition> fields) {
        final List<PimFieldDefinition> fieldsToDelete = oldFields.stream()
                .filter(field -> !fields.contains(field))
                .collect(Collectors.toList());

        for (PimFieldDefinition pimFieldDefinition : fieldsToDelete) {
            pimFieldDefinition.setDeleted(new Date());
            pimFieldDefinitionDAO.save(session, pimFieldDefinition);
        }
    }

    private void updateFields(Session session, PimFieldSet oldPimFieldSet, List<PimFieldDefinition> oldFields, List<PimFieldDefinition> fields) {
        final Iterator<Pair<PimFieldDefinition, PimFieldDefinition>> fieldsToUpdate = fields.stream()
                .map(field -> Pair.of(field, oldFields.indexOf(field)))
                .filter(pair -> pair.getValue() >= 0)
                .map(pair -> Pair.of(pair.getKey(), oldFields.get(pair.getValue())))
                .iterator();

        while (fieldsToUpdate.hasNext()) {
            final Pair<PimFieldDefinition, PimFieldDefinition> fieldToUpdate = fieldsToUpdate.next();
            final PimFieldDefinition fieldDefinition = fieldToUpdate.getKey();
            final PimFieldDefinition storedFieldDefinition = fieldToUpdate.getValue();

            storedFieldDefinition.setLabel(fieldDefinition.getLabel());
            storedFieldDefinition.setType(fieldDefinition.getType());
            storedFieldDefinition.setName(fieldDefinition.getName());
            storedFieldDefinition.setPimFieldSet(oldPimFieldSet);
            storedFieldDefinition.setDescription(fieldDefinition.getDescription());
            storedFieldDefinition.setCopyValue(fieldDefinition.shouldCopyValue());

            pimFieldDefinitionDAO.save(session, storedFieldDefinition);


            final Iterator<PimFieldPossibleValue> valuesToAdd = fieldDefinition.getPossibleValues().stream()
                    .filter(val -> !storedFieldDefinition.getPossibleValues().contains(val))
                    .iterator();
            while (valuesToAdd.hasNext()) {
                final PimFieldPossibleValue pimFieldPossibleValue = valuesToAdd.next();
                pimFieldPossibleValue.setPimFieldDefinition(storedFieldDefinition);
                pimFieldPossibleValueDao.save(session, pimFieldPossibleValue);
                storedFieldDefinition.addPossibleValue(pimFieldPossibleValue);
            }

            final Set<PimFieldPossibleValue> valuesToRemove = storedFieldDefinition.getPossibleValues().stream()
                    .filter(val -> !fieldDefinition.getPossibleValues().contains(val))
                    .collect(Collectors.toSet());
            storedFieldDefinition.getPossibleValues().removeAll(valuesToRemove);

            final Iterator<PimFieldPossibleValue> valuesToUpdate = fieldDefinition.getPossibleValues().stream()
                    .filter(val -> storedFieldDefinition.getPossibleValues().contains(val))
                    .iterator();
            while (valuesToUpdate.hasNext()) {
                final PimFieldPossibleValue next = valuesToUpdate.next();
                session.merge(next);
            }
        }
    }


    private boolean userIsAllowedToEdit(Session session, PimFieldSet pimFieldSet, PimUser pimUser) {
        final boolean allowedToUpdate = permissionHandler.isAllowedToUpdate(session, pimUser, pimFieldSet.getUuid());
        if (permissionHandler.useGroups()) {
            return allowedToUpdate;
        }
        return pimUser.isAdmin() || (pimFieldSet.getOwner().equals(pimUser)) && allowedToUpdate;
    }

    public PimFieldSet save(PimFieldSet pimFieldSet, PimUser pimUser) throws PimSecurityException {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            if (!permissionHandler.isAllowedToCreate(session, pimUser)) {
                throw new PimSecurityException("User does not have enough permissions to create entity");
            }
            final UUID pimFieldSetUuid = pimFieldSet.getUuid();
            if (pimFieldSetDAO.getByUUID(pimFieldSetUuid) != null) {
                throw new IllegalArgumentException("Use update to update PimFieldSet");
            }

            final List<PimFieldDefinition> fields = pimFieldSet.getFields();
            pimFieldSet.setFields(new ArrayList<>());

            final Transaction transaction = session.beginTransaction();
            pimFieldSet.setOwner(pimUser);
            pimFieldSetDAO.save(session, pimFieldSet);

            for (PimFieldDefinition fieldDefinition : fields) {
                final Set<PimFieldPossibleValue> possibleValues = fieldDefinition.getPossibleValues();

                fieldDefinition.setPossibleValues(new HashSet<>());

                fieldDefinition.setLabel(fieldDefinition.getLabel());
                fieldDefinition.setType(fieldDefinition.getType());
                fieldDefinition.setName(fieldDefinition.getName());
                fieldDefinition.setPimFieldSet(pimFieldSet);
                fieldDefinition.setDescription(fieldDefinition.getDescription());
                fieldDefinition.setCopyValue(fieldDefinition.shouldCopyValue());
                pimFieldDefinitionDAO.save(session, fieldDefinition);
                pimFieldSet.addField(fieldDefinition);

                for (PimFieldPossibleValue possibleValue : possibleValues) {
                    possibleValue.setPimFieldDefinition(fieldDefinition);
                    fieldDefinition.addPossibleValue(possibleValue);
                    pimFieldPossibleValueDao.save(session, possibleValue);
                }

            }

            permissionHandler.addAcls(session, pimFieldSet.getUuid(), pimUser);

            transaction.commit();
        }
        return pimFieldSetDAO.getByUUID(pimFieldSet.getUuid());
    }

    public Stream<PimFieldSet> streamAllForUser(Session session, PimUser pimUser, boolean onlyOwnData) {
        if (pimUser.isAdmin() || !permissionHandler.useGroups()) {
            return pimFieldSetDAO.getAllStreaming(session, pimUser, onlyOwnData);
        } else {
            return pimFieldSetDAO.streamAllForPrimaryGroupOfUser(session, pimUser, onlyOwnData);
        }
    }

    public Optional<PimFieldSet> getByUUID(Session session, UUID uuid, PimUser pimUser) throws PimSecurityException {

        try {
            if (pimUser != null && pimUser.isAdmin()) {
                return Optional.ofNullable(pimFieldSetDAO.getByUUID(session, uuid));
            }
            return pimFieldSetDAO.getByUUID(session, uuid, pimUser, permissionHandler.useGroups());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    public Optional<Pair<PimFieldSet, Stream<PimRecord>>> getPimFieldSetRecordsPair(Session session, UUID uuid, PimUser pimUser, boolean onlyOwnData, Set<String> imageUrls) throws PimSecurityException {
        final Optional<PimFieldSet> fieldSetOpt = getByUUID(session, uuid, pimUser);
        if (fieldSetOpt.isEmpty()) {
            return Optional.empty();
        }

        final PimFieldSet pimFieldSet = fieldSetOpt.get();

        PimRecordDAO pimRecordDAO = new PimRecordDAO();

        final Stream<PimRecord> records = pimRecordDAO.getRecordsByFieldSet(session, pimFieldSet, pimUser, onlyOwnData, imageUrls);
        return Optional.of(Pair.of(pimFieldSet, records));
    }

    public Stream<PimFieldSet> getAutocomplete(Session session, PimUser pimUser, boolean onlyOwnData, String filter, int limit, int skip) {
        if (pimUser.isAdmin() || !permissionHandler.useGroups()) {
            return pimFieldSetDAO.getAutocomplete(session, pimUser, onlyOwnData, filter, limit, skip);
        } else {
            return pimFieldSetDAO.getAutocompleteForPrimaryGroupOfUser(session, pimUser, onlyOwnData, filter, limit, skip);
        }
    }
}
