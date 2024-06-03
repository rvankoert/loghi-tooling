package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.*;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.exceptions.ValidationException;
import nl.knaw.huc.di.images.layoutds.models.Configuration;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huc.di.images.layoutds.models.pim.Acl.Permission.*;
import static nl.knaw.huc.di.images.layoutds.services.AclMatcher.acl;
import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class PimFieldSetServiceTest {

    private PimFieldSetDAO pimFieldSetDAO;
    private PimFieldSetService pimFieldSetService;
    private PimGroup pimGroup;
    private PimGroupDAO pimGroupDAO;
    private PimUser pimUserPI;
    private PimUserDAO pimUserDao;
    private ConfigurationDAO configurationDAO;

    @Before
    public void setUp() throws Exception {
        pimFieldSetDAO = new PimFieldSetDAO();
        pimFieldSetService = new PimFieldSetService();
        pimGroup = new PimGroup();
        pimGroupDAO = new PimGroupDAO();
        pimGroupDAO.save(pimGroup);
        this.pimUserDao = new PimUserDAO();
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUser.setName("test");
        pimUserDao.save(pimUser);
        pimUserPI = pimUserDao.getByUUID(pimUser.getUuid());

        configurationDAO = new ConfigurationDAO();
        configurationDAO.save(new Configuration("useGroups", "true"));
    }

    @After
    public void tearDown() throws Exception {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void saveCreatesANewPimFieldSet() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        assertThat(pimFieldSetDAO.getByUUID(save.getUuid()), is(not(nullValue())));
    }

    @Test
    public void saveAddsOwnerToNewPimFieldSet() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, this.pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet byUUID = pimFieldSetDAO.getByUUID(session, save.getUuid());
            assertThat(byUUID, is(not(nullValue())));
            assertThat(byUUID.getOwner(), hasProperty("name", is("test")));
        }
    }

    @Test
    public void saveStoresThePimFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet.getFields(), contains(
                allOf(
                        hasProperty("name", is("field1")),
                        hasProperty("label", is("field1")),
                        hasProperty("type", is(PimFieldDefinition.FieldType.text))
                )
        ));
    }

    @Test
    public void saveStoresPossibleValuesOfFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addSelectFieldDefinition(pimFieldSet, "field1", "value1", "value2", "value3");

        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet.getFields().get(0).getPossibleValues(), containsInAnyOrder(
                hasProperty("value", is("value1")),
                hasProperty("value", is("value2")),
                hasProperty("value", is("value3"))

        ));
    }

    @Test
    public void saveAddsAclsForTheUsersPrimaryGroup() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        final UUID primaryGroupUuid = pimGroup.getUuid();
        final PimUser user = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(user);

        pimFieldSetService.save(pimFieldSet, user);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final AclDAO aclDao = new AclDAO();
            final Set<Acl> acls = aclDao.getBySubjectUuid(session, pimFieldSet.getUuid()).collect(Collectors.toSet());

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

    private void doNotUseGroups() {
        final Configuration useGroups = configurationDAO.getByKey("useGroups");
        useGroups.setValue("false");
        configurationDAO.save(useGroups);
    }

    @Test
    public void saveDoesNotAddAclsWhenGroupsAreNotUsed() throws Exception {
        doNotUseGroups();
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        final PimUser user = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(user);

        pimFieldSetService.save(pimFieldSet, user);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final AclDAO aclDao = new AclDAO();
            assertThat(aclDao.getAll(session), is(empty()));
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void saveThrowsAnExceptionWhenTheFieldSetAlreadyExists() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        pimFieldSetService.save(pimFieldSet, this.pimUserPI);

        pimFieldSetService.save(pimFieldSet, this.pimUserPI);
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfUserOnlyHasTheRoleAuthenticatedWithinTheGroup() throws PimSecurityException, ValidationException {
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.AUTHENTICATED);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        pimFieldSetService.save(pimFieldSet, pimUser);
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfUserIsDisabled() throws PimSecurityException, ValidationException {
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        pimFieldSetService.save(pimFieldSet, pimUser);
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfUserOnlyHasTheRoleAssistantWithinTheGroup() throws PimSecurityException, ValidationException {
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        pimFieldSetService.save(pimFieldSet, pimUser);
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfTheUserDoesNotHaveAPrimaryGroup() throws PimSecurityException, ValidationException {
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        pimFieldSetService.save(pimFieldSet, pimUser);
    }

    @Test
    public void updateAddsNewFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            addTextFieldDefinition(savedSet, "field2");
            PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getFields(), containsInAnyOrder(
                allOf(
                        hasProperty("name", is("field1")),
                        hasProperty("label", is("field1")),
                        hasProperty("type", is(PimFieldDefinition.FieldType.text))
                ),
                allOf(
                        hasProperty("name", is("field2")),
                        hasProperty("label", is("field2")),
                        hasProperty("type", is(PimFieldDefinition.FieldType.text))
                )
        ));
    }

    @Test
    public void updateAddsNewFieldsAndRemovesOldOnes() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            savedSet.getAllFields().clear(); // remove existing fields
            addTextFieldDefinition(savedSet, "field2");
            PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getFields(), contains(
                allOf(
                        hasProperty("name", is("field2")),
                        hasProperty("label", is("field2")),
                        hasProperty("type", is(PimFieldDefinition.FieldType.text))
                )
        ));
    }

    @Test
    public void updateRemoveFieldsFromPimFieldSetButNotTheFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        addTextFieldDefinition(pimFieldSet, "field2");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        final ArrayList<Long> fieldIds = new ArrayList<>();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            savedSet.getFields().forEach(field -> fieldIds.add(field.getId()));

            assertThat(savedSet.getFields(), containsInAnyOrder(
                    allOf(
                            hasProperty("name", is("field1")),
                            hasProperty("label", is("field1")),
                            hasProperty("type", is(PimFieldDefinition.FieldType.text))
                    ),
                    allOf(
                            hasProperty("name", is("field2")),
                            hasProperty("label", is("field2")),
                            hasProperty("type", is(PimFieldDefinition.FieldType.text))
                    )
            ));

            // removeFirstField
            savedSet.getAllFields().remove(0);
            final PimUser pimUserPI = pimUserDao.getByUUID(session, this.pimUserPI.getUuid());
            pimFieldSetService.update(session, savedSet, pimUserPI);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getFields(), contains(
                allOf(
                        hasProperty("name", is("field2")),
                        hasProperty("label", is("field2")),
                        hasProperty("type", is(PimFieldDefinition.FieldType.text))
                )
        ));

        final PimFieldDefinitionDAO pimFieldDefinitionDAO = new PimFieldDefinitionDAO();
        final List<PimFieldDefinition> fields = fieldIds.stream().map(pimFieldDefinitionDAO::get).filter(Objects::nonNull).collect(Collectors.toList());
        assertThat(fields, hasSize(2));
    }

    @Test
    public void updateAddsPossibleValuesToFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addSelectFieldDefinition(pimFieldSet, "field1", "value1", "value2");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            save.getFields().get(0).addPossibleValue(newPossibleValue("value3"));
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, save, pimUser);
        }
        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getFields().get(0).getPossibleValues(), containsInAnyOrder(
                hasProperty("value", is("value1")),
                hasProperty("value", is("value2")),
                hasProperty("value", is("value3"))
        ));
    }

    @Test
    public void updateUpdatesPossibleValuesOfFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addSelectFieldDefinition(pimFieldSet, "field1", "value1", "value2");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldDefinition pimFieldDefinition = save.getFields().get(0);
            final Optional<PimFieldPossibleValue> value2Opt = pimFieldDefinition.getPossibleValues().stream().filter(val -> val.getValue().equals("value2")).findAny();
            final PimFieldPossibleValue pimFieldPossibleValue = value2Opt.get();
            pimFieldPossibleValue.setValue("value3");

            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, save, pimUser);
        }
        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getFields().get(0).getPossibleValues(), containsInAnyOrder(
                hasProperty("value", is("value1")),
                hasProperty("value", is("value3"))
        ));
    }

    @Test
    public void updateRemovesPossibleValuesOfFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addSelectFieldDefinition(pimFieldSet, "field1", "value1", "value2", "value3");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());

            final Set<PimFieldPossibleValue> newValues = savedSet.getFields().get(0).getPossibleValues().stream().filter(pos -> !pos.getValue().equals("value3")).collect(Collectors.toSet());
            savedSet.getFields().get(0).setPossibleValues(newValues);
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getFields().get(0).getPossibleValues(), containsInAnyOrder(
                hasProperty("value", is("value1")),
                hasProperty("value", is("value2"))
        ));
    }

    @Test
    public void updateUpdatesCopyValueToFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            assertThat(savedSet.getFields().get(0).shouldCopyValue(), is(false));

            savedSet.getFields().get(0).setCopyValue(true);
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(session, save.getUuid());
            assertThat(savedSet2.getFields().get(0).shouldCopyValue(), is(true));
        }
    }

    @Test
    public void updateUpdatesAndRemovesFields() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        addTextFieldDefinition(pimFieldSet, "field2");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            assertThat(savedSet.getFields(), hasSize(2));

            final List<PimFieldDefinition> newFields = savedSet.getFields().stream().filter(field -> field.getName().equals("field1")).collect(Collectors.toList());
            newFields.get(0).setLabel("newField1");
            savedSet.setFields(newFields);

            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(session, save.getUuid());
            assertThat(savedSet2.getFields(), hasSize(1));
            assertThat(savedSet2.getFields().get(0).getLabel(), is("newField1"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateThrowsIllegalArgumentExceptionWhenPimFieldSetDoesNotExist() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            pimFieldSetService.update(session, pimFieldSet, pimUser);
        }
    }

    @Test
    public void updateIsAllowedForAssistantOrHigher() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());

            savedSet.setName("New name");
            pimUser = pimUserDao.getByUUID(session, pimUser.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getName(), is("New name"));
    }

    @Test
    public void updateReturnsTheLastVersion() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());

            savedSet.setName("New name");
            pimUser = pimUserDao.getByUUID(session, pimUser.getUuid());
            final PimFieldSet update = pimFieldSetService.update(session, savedSet, pimUser);
            assertThat(update.getName(), is("New name"));
        }
    }

    @Test(expected = PimSecurityException.class)
    public void updateIsNotAllowedForPublicDataWhenUserIsOnlyAuthenticated() throws PimSecurityException, ValidationException {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.AUTHENTICATED);
        pimUserDao.save(pimUser);
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            addTextFieldDefinition(savedSet, "field2");
            pimUser = pimUserDao.getByUUID(session, pimUser.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }
    }

    @Test(expected = PimSecurityException.class)
    public void updateIsNotAllowedForPublicDataWhenUserIsDisabled() throws PimSecurityException, ValidationException {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());
            addTextFieldDefinition(savedSet, "field2");
            pimUser = pimUserDao.getByUUID(session, pimUser.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }
    }

    @Test
    public void updateIsAllowedForAdminsWhenNotUsingGroups() throws Exception {
        doNotUseGroups();
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());

            savedSet.setName("New name");
            pimUser = pimUserDao.getByUUID(session, pimUser.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getName(), is("New name"));
    }

    @Test
    public void updateIsAllowedForTheOwnerWhenNotUsingGroups() throws Exception {
        doNotUseGroups();
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());

            savedSet.setName("New name");
            pimFieldSetService.update(session, savedSet, pimUserPI);
        }

        final PimFieldSet savedSet2 = pimFieldSetDAO.getByUUID(save.getUuid());
        assertThat(savedSet2.getName(), is("New name"));
    }

    @Test(expected = PimSecurityException.class)
    public void updateIsNotAllowedForAnotherPIWhenNotUsingGroups() throws Exception {
        doNotUseGroups();
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");
        PimUser pimUser = userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);
        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final PimFieldSet savedSet = pimFieldSetDAO.getByUUID(save.getUuid());

            savedSet.setName("New name");
            pimUser = pimUserDao.getByUUID(session, pimUser.getUuid());
            pimFieldSetService.update(session, savedSet, pimUser);
        }

    }

    @Test
    public void streamAllForUserReturnsTheSetsOfTheUsersPrimaryGroup() throws Exception {
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        pimFieldSet.setName("Set");
        pimFieldSetService.save(pimFieldSet, pimUserPI);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser userOfOtherGroup = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(userOfOtherGroup);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, userOfOtherGroup);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Set<PimFieldSet> sets = pimFieldSetService.streamAllForUser(session, pimUser, false).collect(Collectors.toSet());

            assertThat(sets, contains(hasProperty("name", equalTo("Set"))));
        }
    }

    @Test
    public void streamAllForUserIgnoresDeletedAcls() throws Exception {
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        pimFieldSet.setName("Set");
        pimFieldSetService.save(pimFieldSet, pimUserPI);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUserOfGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(otherUserOfGroup);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUserOfGroup);

        final AclDAO aclDao = new AclDAO();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final Stream<Acl> document2Acls = aclDao.getBySubjectUuid(session, otherSet.getUuid());

            final Transaction transaction = session.beginTransaction();
            final PimFieldSet byUUID = pimFieldSetDAO.getByUUID(session, otherSet.getUuid());
            byUUID.setPublicPimFieldSet(false);
            pimFieldSetDAO.save(session, byUUID);

            for (final Iterator<Acl> iterator = document2Acls.iterator(); iterator.hasNext();) {
                final Acl acl = iterator.next();
                acl.setDeleted(new Date());
                aclDao.save(session, acl);
            }
            transaction.commit();
        }


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Set<PimFieldSet> sets = pimFieldSetService.streamAllForUser(session, pimUser, false).collect(Collectors.toSet());

            assertThat(sets, contains(hasProperty("name", equalTo("Set"))));
        }
    }

    @Test
    public void streamAllForUserReturnsAllDataForUserWithAdminRole() throws Exception {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();

        pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Set<PimFieldSet> sets = pimFieldSetService.streamAllForUser(session, pimUser, false).collect(Collectors.toSet());

            assertThat(sets, hasSize(1));
        }
    }

    @Test
    public void streamAllForUserReturnsEmptyStreamForDisabledUser() throws Exception {
        final PimUser pimUser = adminUser();
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();

        pimFieldSetService.save(pimFieldSet, pimUserPI);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Set<PimFieldSet> sets = pimFieldSetService.streamAllForUser(session, pimUser, false).collect(Collectors.toSet());

            assertThat(sets, hasSize(0));
        }
    }

    @Test
    public void streamAllForUserReturnsPublicAndUsersSets() throws Exception {
        doNotUseGroups();
        final PimFieldSet pimFieldSet = new PimFieldSet();
        pimFieldSet.setName("Set");
        pimFieldSetService.save(pimFieldSet, pimUserPI);
        final PimUser otherUser = userWithRoles(List.of(Role.PI));
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        otherSet.setPublicPimFieldSet(true);
        pimFieldSetService.save(otherSet, otherUser);
        final PimFieldSet otherSet2 = new PimFieldSet();
        otherSet2.setName("Other set2");
        otherSet2.setPublicPimFieldSet(false);
        pimFieldSetService.save(otherSet2, otherUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            final Set<PimFieldSet> sets = pimFieldSetService.streamAllForUser(session, pimUser, false).collect(Collectors.toSet());

            assertThat(sets, containsInAnyOrder(
                    hasProperty("name", equalTo("Set")),
                    hasProperty("name", equalTo("Other set"))
            ));
        }
    }

    @Test
    public void streamAllForUserReturnsOnlyUsersSetsWhenOnlyOwnDataIsTrue() throws Exception {
        doNotUseGroups();
        final PimFieldSet pimFieldSet = new PimFieldSet();
        pimFieldSet.setName("Set");
        pimFieldSetService.save(pimFieldSet, pimUserPI);
        final PimUser otherUser = userWithRoles(List.of(Role.PI));
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            final Set<PimFieldSet> sets = pimFieldSetService.streamAllForUser(session, pimUser, true).collect(Collectors.toSet());

            assertThat(sets, contains(hasProperty("name", equalTo("Set"))));
        }
    }

    @Test
    public void getByUuidReturnsThePimFieldSetWhenNotUsingGroups() throws Exception {
        doNotUseGroups();
        final PimUser otherUser = userWithRoles(List.of(Role.PI));
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, this.pimUserPI.getUuid());
            final Optional<PimFieldSet> pimFieldSet = pimFieldSetService.getByUUID(session, otherSet.getUuid(), pimUser);

            assertThat(pimFieldSet.get(), hasProperty("name", equalTo("Other set")));
        }
    }

    @Test
    public void getByUuidReturnsThePimFieldForAdmin() throws Exception {
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, pimUserPI);
        final PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, adminUser.getUuid());
            final Optional<PimFieldSet> pimFieldSet = pimFieldSetService.getByUUID(session, otherSet.getUuid(), pimUser);

            assertThat(pimFieldSet.get(), hasProperty("name", equalTo("Other set")));
        }
    }

    @Test
    public void getByUuidReturnsEmptyOptionalWhenTheUserIsDisabled() throws Exception {
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, pimUserPI);
        final PimUser adminUser = adminUser();
        adminUser.setDisabled(true);
        pimUserDao.save(adminUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, adminUser.getUuid());
            final Optional<PimFieldSet> pimFieldSet = pimFieldSetService.getByUUID(session, otherSet.getUuid(), pimUser);

            assertThat(pimFieldSet.get(), hasProperty("name", equalTo("Other set")));
        }
    }

    @Test
    public void getByUuidReturnsEmptyOptionalWhenThePimFieldSetDoesNotExist() throws Exception {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, this.pimUserPI.getUuid());
            final Optional<PimFieldSet> pimFieldSet = pimFieldSetService.getByUUID(session, UUID.randomUUID(), pimUser);

            assertThat(pimFieldSet.isEmpty(), is(true));
        }
    }

    @Test
    public void getByUuidReturnsEmptyOptionalWhenUserIsNotAllowedToSeeSet() throws Exception {
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, this.pimUserPI.getUuid());
            final Optional<PimFieldSet> fieldSet = pimFieldSetService.getByUUID(session, otherSet.getUuid(), pimUser);

            assertThat(fieldSet.isEmpty(), is(true));
        }
    }

    @Test
    public void getByUuidReturnsThePimFieldWhenTheUserIsAllowedToSee() throws Exception {
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, otherUser.getUuid());
            final Optional<PimFieldSet> pimFieldSet = pimFieldSetService.getByUUID(session, otherSet.getUuid(), pimUser);

            assertThat(pimFieldSet.get(), hasProperty("name", equalTo("Other set")));
        }
    }

    @Test
    public void getByUuidReturnsIgnoresDeletedAcls() throws Exception {
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUser);


        final AclDAO aclDao = new AclDAO();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final Stream<Acl> document2Acls = aclDao.getBySubjectUuid(session, otherSet.getUuid());

            final Transaction transaction = session.beginTransaction();
            final PimFieldSet byUUID = pimFieldSetDAO.getByUUID(session, otherSet.getUuid());
            byUUID.setPublicPimFieldSet(false);
            pimFieldSetDAO.save(session, byUUID);

            for (final Iterator<Acl> iterator = document2Acls.iterator(); iterator.hasNext();) {
                final Acl acl = iterator.next();
                acl.setDeleted(new Date());
                aclDao.save(session, acl);
            }
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, otherUser.getUuid());
            final Optional<PimFieldSet> pimFieldSet = pimFieldSetService.getByUUID(session, otherSet.getUuid(), pimUser);

            assertThat(pimFieldSet, hasProperty("empty", is(true)));
        }
    }

    @Test
    public void getPimFieldSetRecordsPairReturnsTheFieldSetAndItsRecords() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, pimUserPI);

        final PimRecord pimRecord = new PimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final List<PimFieldValue> fieldValues = new ArrayList<>();
            final PimFieldValueDAO pimFieldValueDAO = new PimFieldValueDAO();
            for (PimFieldDefinition field : save.getFields()) {
                final PimFieldValue pimFieldValue = new PimFieldValue();
                pimFieldValue.setField(field);
                pimFieldValue.setValue("value");
                pimFieldValue.setPimRecord(pimRecord);
                fieldValues.add(pimFieldValue);
            }

            pimRecord.setFieldValues(fieldValues);
            new PimRecordDAO().save(session, pimRecord);

            for (PimFieldValue fieldValue : fieldValues) {
                pimFieldValueDAO.save(session, fieldValue);
            }


            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            final Optional<Pair<PimFieldSet, Stream<PimRecord>>> pairOpt = pimFieldSetService.getPimFieldSetRecordsPair(session, save.getUuid(), pimUser, false, null);

            assertThat(pairOpt.isEmpty(), is(false));
            final Pair<PimFieldSet, Stream<PimRecord>> pair = pairOpt.get();
            assertThat(pair.getKey(), is(notNullValue()));
            assertThat(pair.getValue().collect(Collectors.toSet()), contains(hasProperty("uuid", equalTo(pimRecord.getUuid()))));

        }
    }

    @Test
    public void getPimFieldSetRecordsPairReturnsAnEmptyOptionalForADisabledUser() throws Exception {
        final PimFieldSet pimFieldSet = new PimFieldSet();
        addTextFieldDefinition(pimFieldSet, "field1");

        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        final PimFieldSet save = pimFieldSetService.save(pimFieldSet, this.pimUserPI);

        final PimRecord pimRecord = new PimRecord();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final List<PimFieldValue> fieldValues = new ArrayList<>();
            final PimFieldValueDAO pimFieldValueDAO = new PimFieldValueDAO();
            for (PimFieldDefinition field : save.getFields()) {
                final PimFieldValue pimFieldValue = new PimFieldValue();
                pimFieldValue.setField(field);
                pimFieldValue.setValue("value");
                pimFieldValue.setPimRecord(pimRecord);
                fieldValues.add(pimFieldValue);
            }

            pimRecord.setFieldValues(fieldValues);
            new PimRecordDAO().save(session, pimRecord);

            for (PimFieldValue fieldValue : fieldValues) {
                pimFieldValueDAO.save(session, fieldValue);
            }


            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());
            final Optional<Pair<PimFieldSet, Stream<PimRecord>>> pairOpt = pimFieldSetService.getPimFieldSetRecordsPair(session, save.getUuid(), byUUID, false, null);

            assertThat(pairOpt.isEmpty(), is(true));

        }
    }

    @Test
    public void getPimFieldSetRecordsPairReturnsEmptyOptionalWhenTheUserIsNotAllowedToSee() throws Exception {
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(otherUser);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, this.pimUserPI.getUuid());
            final Optional<Pair<PimFieldSet, Stream<PimRecord>>> pairOpt = pimFieldSetService.getPimFieldSetRecordsPair(session, otherSet.getUuid(), pimUser, false, null);

            assertThat(pairOpt.isEmpty(), is(true));
        }
    }

    @Test
    public void getPimFieldSetRecordsPairReturnsAnEmptyOptionalWhenTheFieldSetDoesNotExist() throws Exception {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserPI.getUuid());
            final Optional<Pair<PimFieldSet, Stream<PimRecord>>> pimFieldSet = pimFieldSetService.getPimFieldSetRecordsPair(session, UUID.randomUUID(), pimUser, false, null);

            assertThat(pimFieldSet.isEmpty(), is(true));
        }
    }

    @Test
    public void getAutoCompleteIgnoresDeletedAcls() throws Exception {
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);
        final PimFieldSet pimFieldSet = new PimFieldSet();
        pimFieldSet.setName("Set");
        pimFieldSetService.save(pimFieldSet, pimUserPI);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUserOfGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(otherUserOfGroup);
        final PimFieldSet otherSet = new PimFieldSet();
        otherSet.setName("Other set");
        pimFieldSetService.save(otherSet, otherUserOfGroup);

        final AclDAO aclDao = new AclDAO();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final Stream<Acl> document2Acls = aclDao.getBySubjectUuid(session, otherSet.getUuid());

            final Transaction transaction = session.beginTransaction();
            final PimFieldSet byUUID = pimFieldSetDAO.getByUUID(session, otherSet.getUuid());
            byUUID.setPublicPimFieldSet(false);
            pimFieldSetDAO.save(session, byUUID);

            for (final Iterator<Acl> iterator = document2Acls.iterator(); iterator.hasNext();) {
                final Acl acl = iterator.next();
                acl.setDeleted(new Date());
                aclDao.save(session, acl);
            }
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Set<PimFieldSet> sets = pimFieldSetService.getAutocomplete(session, pimUser, false, "", 100, 0).collect(Collectors.toSet());

            assertThat(sets, contains(hasProperty("name", equalTo("Set"))));
        }
    }

    private void addSelectFieldDefinition(PimFieldSet pimFieldSet, String name, String... possibleValues) {
        final PimFieldDefinition pimFieldDefinition = new PimFieldDefinition();
        pimFieldDefinition.setType(PimFieldDefinition.FieldType.select);
        pimFieldDefinition.setName(name);
        pimFieldDefinition.setLabel(name);
        pimFieldDefinition.setPossibleValues(Arrays.stream(possibleValues).map(this::newPossibleValue).collect(Collectors.toSet()));
        pimFieldSet.addField(pimFieldDefinition);
    }

    private PimFieldPossibleValue newPossibleValue(String val) {
        final PimFieldPossibleValue pimFieldPossibleValue = new PimFieldPossibleValue();
        pimFieldPossibleValue.setValue(val);
        return pimFieldPossibleValue;
    }


    private void addTextFieldDefinition(PimFieldSet pimFieldSet, String name) {
        final PimFieldDefinition pimFieldDefinition = new PimFieldDefinition();
        pimFieldDefinition.setType(PimFieldDefinition.FieldType.text);
        pimFieldDefinition.setName(name);
        pimFieldDefinition.setLabel(name);
        pimFieldSet.addField(pimFieldDefinition);
    }

}
