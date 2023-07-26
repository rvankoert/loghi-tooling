package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.*;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.exceptions.ValidationException;
import nl.knaw.huc.di.images.layoutds.models.Configuration;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huc.di.images.layoutds.models.pim.Acl.Permission.*;
import static nl.knaw.huc.di.images.layoutds.services.AclMatcher.acl;
import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.userWithMembershipAndPrimaryGroup;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PimRecordServiceTest {

    private final PimGroupDAO pimGroupDAO;
    private final String parentUri;
    private PimRecordService instance;
    private PimRecordDAO pimRecordDAO;
    private ConfigurationDAO configurationDAO;
    private PimUserDao pimUserDao;
    private PimFieldDefinitionDAO pimFieldDefinitionDAO;
    private UUID uriFieldUuid;
    private UUID datasetUriFieldUuid;
    private UUID checkboxDefUuid;

    public PimRecordServiceTest() {
        pimGroupDAO = new PimGroupDAO();
        parentUri = "parent";
    }

    @Before
    public void setUp() throws Exception {
        instance = new PimRecordService();
        pimRecordDAO = new PimRecordDAO();

        configurationDAO = new ConfigurationDAO();
        configurationDAO.save(new Configuration("useGroups", "true"));
        pimUserDao = new PimUserDao();

        pimFieldDefinitionDAO = new PimFieldDefinitionDAO();
        uriFieldUuid = pimFieldDefinitionDAO.save(new PimFieldDefinition("uri", PimFieldDefinition.FieldType.text)).getUuid();
        datasetUriFieldUuid = pimFieldDefinitionDAO.save(new PimFieldDefinition("datasetUri", PimFieldDefinition.FieldType.text)).getUuid();
        checkboxDefUuid = pimFieldDefinitionDAO.save(new PimFieldDefinition("checkboxDef", PimFieldDefinition.FieldType.checkbox)).getUuid();


    }

    @After
    public void tearDown() throws Exception {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void saveIsAllowedForAssistants() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        successfulSave(pimGroup, pimUser);
    }

    private void successfulSave(PimGroup pimGroup, PimUser pimUser) throws PimSecurityException, ValidationException, DuplicateDataException {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);

            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));
            final Transaction transaction = session.beginTransaction();
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        assertThat(pimRecordDAO.getByUUID(pimRecord.getUuid()), is(notNullValue()));
    }

    @Test
    public void saveIsAllowedForResearchers() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        successfulSave(pimGroup, pimUser);
    }

    @Test
    public void saveIsAllowedForPIs() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        successfulSave(pimGroup, pimUser);
    }

    @Test
    public void saveIsAllowedForAdmins() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ADMIN);
        successfulSave(pimGroup, pimUser);
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAnPimSecurityExceptionForAuthenticatedUsers() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.AUTHENTICATED);
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);

            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }
    }

    @Test(expected = ValidationException.class)
    public void saveThrowsAValidationExceptionWhenAFieldContainsANonValidValue() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, checkboxDefUuid), "value"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }
    }

    @Test(expected = ValidationException.class)
    public void saveThrowsAValidationExceptionWhenThereIsNoFieldUri() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        final PimFieldDefinition checkboxDef = new PimFieldDefinition("checkboxDef", PimFieldDefinition.FieldType.checkbox);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimFieldDefinitionDAO.save(session, checkboxDef);
            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }
    }

    @Test(expected = ValidationException.class)
    public void saveThrowsAValidationExceptionWhenThereIsNoFieldDatasetUri() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        final PimFieldDefinition checkboxDef = new PimFieldDefinition("checkboxDef", PimFieldDefinition.FieldType.checkbox);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimFieldDefinitionDAO.save(session, checkboxDef);
            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }
    }

    private PimRecord newPimRecord() {
        final PimRecord pimRecord = new PimRecord();
        pimRecord.setParent(parentUri);
        return pimRecord;
    }

    @Test
    public void saveAddsAcls() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final UUID primaryGroupUuid = pimGroup.getUuid();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);

            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));
            final Transaction transaction = session.beginTransaction();
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final List<Acl> acls = new AclDao().getBySubjectUuid(session, pimRecord.getUuid()).collect(Collectors.toList());
            assertThat(acls, containsInAnyOrder(
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(READ),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(CREATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(UPDATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(DELETE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.RESEARCHER).withPermission(READ),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.RESEARCHER).withPermission(CREATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.RESEARCHER).withPermission(UPDATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.ASSISTANT).withPermission(READ),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.ASSISTANT).withPermission(UPDATE)
            ));
        }
    }

    @Test
    public void saveAddsTheCreator() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));
            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord record = pimRecordDAO.getByUUID(session, pimRecord.getUuid());
            assertThat(record, is(notNullValue()));
            assertThat(record.getCreator(), hasProperty("uuid", equalTo(pimUser.getUuid())));
        }
    }

    @Test
    public void saveStoresTheFields() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord record = pimRecordDAO.getByUUID(session, pimRecord.getUuid());
            assertThat(record, is(notNullValue()));
            assertThat(record.getFieldValues(), is(not(empty())));
        }
    }

    private PimFieldDefinition getFieldDef(Session session, UUID fieldDefinitionUuid) {
        return pimFieldDefinitionDAO.getByUUID(session, fieldDefinitionUuid);
    }

    private PimFieldDefinition getFieldDef(UUID fieldDefinitionUuid) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            return this.getFieldDef(session, fieldDefinitionUuid);
        }
    }

    private PimFieldValue fieldValue(PimFieldDefinition pimFieldDefinition1, String value) {
        final PimFieldValue pimFieldValue = new PimFieldValue();
        pimFieldValue.setField(pimFieldDefinition1);
        pimFieldValue.setValue(value);
        return pimFieldValue;
    }

    @Test
    public void saveAddsNewFieldsToAnExistingRecord() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord byUUID = pimRecordDAO.getByUUID(pimRecord.getUuid());
            byUUID.addFieldValue(fieldValue(getFieldDef(checkboxDefUuid), "true"));
            final Transaction transaction = session.beginTransaction();
            instance.save(session, byUUID, pimUser);
            transaction.commit();
        }


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord record = pimRecordDAO.getByUUID(session, pimRecord.getUuid());
            assertThat(record, is(notNullValue()));
            assertThat(record.getFieldValues(), Matchers.hasSize(3));
        }
    }

    @Test
    public void saveRemovesFieldsFromAnExistingRecord() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, checkboxDefUuid), "true"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord byUUID = pimRecordDAO.getByUUID(pimRecord.getUuid());
            byUUID.getFieldValues().remove(2);
            final Transaction transaction = session.beginTransaction();
            instance.save(session, byUUID, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord record = pimRecordDAO.getByUUID(session, pimRecord.getUuid());
            assertThat(record, is(notNullValue()));
            assertThat(record.getFieldValues(), hasSize(2));
        }
    }

    @Test
    public void saveUpdatesExistingFields() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }


        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord byUUID = pimRecordDAO.getByUUID(pimRecord.getUuid());

            final PimFieldValue pimFieldValue = byUUID.getFieldValues().get(0);
            pimFieldValue.setValue("updatedValue");

            final Transaction transaction = session.beginTransaction();
            instance.save(session, byUUID, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord record = pimRecordDAO.getByUUID(session, pimRecord.getUuid());
            assertThat(record, is(notNullValue()));
            assertThat(record.getFieldValues(), hasItem(
                    hasProperty("value", equalTo("updatedValue"))
            ));
        }
    }

    @Test
    public void saveUpdatesExistingRecordWithSameUrl() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }


        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimRecord byUUID = pimRecordDAO.getByUUID(pimRecord.getUuid());
            byUUID.setId(null);
            byUUID.setUuid(UUID.randomUUID());

            final Transaction transaction = session.beginTransaction();
            instance.save(session, byUUID, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimRecordDAO.getAll(session, 10), hasSize(1));

        }
    }

    @Test
    public void getReturnsTheRecord() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }
        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<PimRecord> record = instance.get(session, parentUri, pimUser);
            assertThat(record, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getReturnsPimRecordForOtherUserOfGroup() throws DuplicateDataException, PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            pimUserDao.save(session, otherUser);
            transaction.commit();
        }
        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, byUUID);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, otherUser.getUuid());
            final Optional<PimRecord> record = instance.get(session, parentUri, byUUID);
            assertThat(record, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getReturnsAnEmptyOptionalWhenItIsNotFound() throws DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<PimRecord> record = instance.get(session, parentUri, pimUser);
            assertThat(record, hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getIgnoresTheDeletedAcls() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            pimUserDao.save(session, otherUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, byUUID);
            transaction.commit();
        }

        final AclDao aclDao = new AclDao();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<Acl> pimRecordAcls = aclDao.getBySubjectUuid(session, pimRecord.getUuid());
            final Transaction transaction = session.beginTransaction();
            for (final Iterator<Acl> iterator = pimRecordAcls.iterator(); iterator.hasNext();) {
                final Acl acl = iterator.next();
                acl.setDeleted(new Date());
                aclDao.save(session, acl);
            }
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<PimRecord> records = instance.get(session, parentUri, otherUser);
            assertThat(records.isEmpty(), is(true));
        }
    }

    @Test
    public void getRecordsByDatasetReturnsTheRecordsOfTheDatasetOfTheUser() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<PimRecord> records = instance.getRecordsByDataset(session, "value2", false, pimUser);
            assertThat(records.iterator().hasNext(), is(true));
        }
    }

    @Test
    public void getRecordsByDatasetReturnsTheRecordsOfTheDatasetAndTheGroupsOfTheUser() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            pimUserDao.save(session, otherUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<PimRecord> records = instance.getRecordsByDataset(session, "value2", false, otherUser);
            assertThat(records.iterator().hasNext(), is(true));
        }
    }

    @Test
    public void getRecordsByDatasetReturnsTheRecordsOfTheDatasetWhenNotUsingGroups() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimUser pimUserWithSameGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimGroup otherGroup = new PimGroup();
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(otherGroup, Role.RESEARCHER);

        doNotUseGroups();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimGroupDAO.save(session, otherGroup);
            pimUserDao.save(session, pimUser);
            pimUserDao.save(session, pimUserWithSameGroup);
            pimUserDao.save(session, otherUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<PimRecord> recordsOfSameGroup = instance.getRecordsByDataset(session, "value2", false, pimUserWithSameGroup);
            assertThat(recordsOfSameGroup.iterator().hasNext(), is(true));
            final Stream<PimRecord> recordsOfUserWithOtherGroup = instance.getRecordsByDataset(session, "value2", false, pimUserWithSameGroup);
            assertThat(recordsOfUserWithOtherGroup.iterator().hasNext(), is(true));
        }
    }

    @Test
    public void getRecordsByDatasetIgnoresTheDeletedAcls() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            pimUserDao.save(session, otherUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, byUUID);
            transaction.commit();
        }

        final AclDao aclDao = new AclDao();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<Acl> pimRecordAcls = aclDao.getBySubjectUuid(session, pimRecord.getUuid());
            final Transaction transaction = session.beginTransaction();
            for (final Iterator<Acl> iterator = pimRecordAcls.iterator(); iterator.hasNext();) {
                final Acl acl = iterator.next();
                acl.setDeleted(new Date());
                aclDao.save(session, acl);
            }
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<PimRecord> records = instance.getRecordsByDataset(session, "value2", false, otherUser);
            assertThat(records.findAny().isEmpty(), is(true));
        }
    }

    private void doNotUseGroups() {
        final Configuration useGroups = configurationDAO.getByKey("useGroups");
        useGroups.setValue("false");
        configurationDAO.save(useGroups);
    }

    @Test
    public void getRecordsByDatasetReturnsAnEmptyStreamWhenNoRecordsAreFound() throws PimSecurityException, ValidationException, DuplicateDataException {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            pimGroupDAO.save(session, pimGroup);
            pimUserDao.save(session, pimUser);
            pimUserDao.save(session, otherUser);
            transaction.commit();
        }

        final PimRecord pimRecord = newPimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, uriFieldUuid), "value"));
            pimRecord.addFieldValue(fieldValue(getFieldDef(session, datasetUriFieldUuid), "value2"));

            final Transaction transaction = session.beginTransaction();
            pimUserDao.getByUUID(session, pimUser.getUuid());
            instance.save(session, pimRecord, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Stream<PimRecord> records = instance.getRecordsByDataset(session, "otherset", false, otherUser);
            assertThat(records.iterator().hasNext(), is(false));
        }
    }
}