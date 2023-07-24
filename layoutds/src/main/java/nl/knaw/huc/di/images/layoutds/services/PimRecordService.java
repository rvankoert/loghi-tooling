package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.DuplicateDataException;
import nl.knaw.huc.di.images.layoutds.DAO.PimFieldValueDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimRecordDAO;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.exceptions.ValidationException;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldValue;
import nl.knaw.huc.di.images.layoutds.models.pim.PimRecord;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import nl.knaw.huc.di.images.layoutds.security.PermissionHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PimRecordService {
    public static final List<Role> ROLES_ALLOWED_TO_CREATE = List.of(Role.PI, Role.RESEARCHER, Role.ASSISTANT);
    private final PermissionHandler permissionHandler;
    private final PimRecordDAO pimRecordDAO;
    private final PimFieldValueDAO pimFieldValueDAO;

    public PimRecordService() {
        permissionHandler = new PermissionHandler(ROLES_ALLOWED_TO_CREATE);
        pimRecordDAO = new PimRecordDAO();
        pimFieldValueDAO = new PimFieldValueDAO();
    }

    public void save(Session session, PimRecord pimRecord, PimUser pimUser) throws PimSecurityException, ValidationException, DuplicateDataException {
        if (!permissionHandler.isAllowedToCreate(session, pimUser)) {
            throw new PimSecurityException("User does not have enough permissions to create entity");
        }


        final PimRecord savedRecord = pimRecordDAO.get(session, pimRecord.getParent(), pimUser);

        if (savedRecord == null) {
            final List<PimFieldValue> fieldValues = pimRecord.getFieldValues();
            pimRecord.setFieldValues(new ArrayList<>());

            pimRecord.setCreator(pimUser);
            final PimRecord save = pimRecordDAO.save(session, pimRecord);


            boolean containsUriField = false;
            boolean containsDatasetUriField = false;
            final Set<String> errors = new HashSet<>();
            for (PimFieldValue fieldValue : fieldValues) {
                if (fieldValue.getField().getName().equals("uri")) {
                    containsUriField = true;
                } else if (fieldValue.getField().getName().equals("datasetUri")) {
                    containsDatasetUriField = true;
                }

                PimFieldDefinitionValidator.validatePimFieldValue(errors, fieldValue);
                final PimFieldValue newValue = new PimFieldValue();
                newValue.setValue(fieldValue.getValue());
                newValue.setPimRecord(save);
                newValue.setCreator(pimUser);
                newValue.setField(fieldValue.getField());
                newValue.setCreated(new Date());

                pimFieldValueDAO.save(session, newValue);
                save.addFieldValue(newValue);
            }

            if (!containsUriField) {
                errors.add("uri is blank");
            }

            if (!containsDatasetUriField) {
                errors.add("Dataset uri is blank");
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            permissionHandler.addAcls(session, pimRecord.getUuid(), pimUser);
        } else {
            update(session, pimRecord, pimUser);
        }
    }

    public void update(Session session, PimRecord pimRecord, PimUser pimUser) throws PimSecurityException, DuplicateDataException {
        final PimRecord dbRecord = pimRecordDAO.get(session, pimRecord.getParent(), pimUser);
        if (!permissionHandler.isAllowedToUpdate(session, pimUser, dbRecord.getUuid())) {
            throw new PimSecurityException("User is not allowed to update");
        }
        final List<PimFieldValue> dbValues = dbRecord.getFieldValues();
        final List<PimFieldValue> newValues = pimRecord.getFieldValues();

        final Iterator<PimFieldValue> valuesToAdd = newValues.stream()
                .filter(value -> !dbValues.contains(value)).iterator();

        while (valuesToAdd.hasNext()) {
            final PimFieldValue pimFieldValue = valuesToAdd.next();
            pimFieldValue.setPimRecord(dbRecord);
            pimFieldValueDAO.save(session, pimFieldValue);
            dbRecord.addFieldValue(pimFieldValue);
        }

        final List<PimFieldValue> valuesToRemove = dbValues.stream()
                .filter(value -> !newValues.contains(value)).collect(Collectors.toList());

        for (PimFieldValue pimFieldValue : valuesToRemove) {
            dbRecord.getFieldValues().remove(pimFieldValue);
        }

        final Iterator<Pair<PimFieldValue, PimFieldValue>> valuesToUpdate = newValues.stream()
                .map(value -> Pair.of(value, dbValues.indexOf(value)))
                .filter(pair -> pair.getValue() >= 0)
                .map(pair -> Pair.of(pair.getKey(), dbValues.get(pair.getValue())))
                .iterator();

        while (valuesToUpdate.hasNext()) {
            final Pair<PimFieldValue, PimFieldValue> next = valuesToUpdate.next();
            final PimFieldValue storedValue = next.getValue();
            final PimFieldValue newValue = next.getKey();
            storedValue.setValue(newValue.getValue());

            pimFieldValueDAO.save(session, storedValue);
        }


        pimRecordDAO.save(session, dbRecord);


    }

    public Optional<PimRecord> get(Session session, String parent, PimUser pimUser) throws DuplicateDataException {
        if (!permissionHandler.useGroups()) {
            return Optional.ofNullable(pimRecordDAO.get(session, parent, pimUser));
        }

        return pimRecordDAO.getByParentUriOfPrimaryGroup(session, parent, pimUser);
    }

    public Stream<PimRecord> getRecordsByDataset(Session session, String decodedUri, boolean onlyOwnData, PimUser pimUser) {
        if (!permissionHandler.useGroups()) {
            return pimRecordDAO.getRecordsByDataset(session, decodedUri, onlyOwnData, pimUser);
        }
        return pimRecordDAO.getRecordsByDatasetAndPrimaryGroup(session, decodedUri, onlyOwnData, pimUser);
    }
}
