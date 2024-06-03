package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.MembershipDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimGroupDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimUserDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class SecurityServiceTest {

    private SecurityService securityService;
    private PimUserDAO pimUserDao;
    private PimGroupDAO pimGroupDAO;
    private MembershipDAO membershipDao;

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Before
    public void setUp() throws Exception {
        securityService = new SecurityService();
        pimUserDao = new PimUserDAO();
        pimGroupDAO = new PimGroupDAO();
        membershipDao = new MembershipDAO();
    }

    @Test
    public void getRolesUserIsAllowedToGiveForAMembershipReturnsRolesLowerOrSameOfTheUser() {
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final Role role = Role.PI;
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());

            final Stream<Role> roles = securityService.getRolesUserIsAllowedToGiveForAMembership(session, group, user);

            assertThat(roles.collect(Collectors.toSet()), containsInAnyOrder(Role.PI, Role.RESEARCHER, Role.ASSISTANT));
        }
    }

    @Test
    public void getRolesUserIsAllowedToGiveForAMembershipReturnsAnEmptySetWhenTheUserHasNoMembershipWithTheGroup() {
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());

            final Stream<Role> roles = securityService.getRolesUserIsAllowedToGiveForAMembership(session, group, user);

            assertThat(roles.collect(Collectors.toSet()), is(empty()));
        }
    }

    @Test
    public void getRolesUserIsAllowedToGiveForAMembershipReturnsTheRolesWhenTheUserHasAMembershipWithAParentGroup() {
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup parentParentGroup = new PimGroup();
        parentParentGroup.setName("ParentParent");
        pimGroupDAO.save(parentParentGroup);
        final Role role = Role.PI;
        final Membership membership = new Membership(parentParentGroup, pimUser, role);
        membershipDao.save(membership);
        final PimGroup parentGroup = new PimGroup();
        parentGroup.setName("Parent");
        parentGroup.addSupergroup(parentParentGroup);
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSupergroup(parentGroup);
        pimGroup.setName("Pim");
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());

            final Stream<Role> roles = securityService.getRolesUserIsAllowedToGiveForAMembership(session, group, user);

            assertThat(roles.collect(Collectors.toSet()), containsInAnyOrder(Role.PI, Role.RESEARCHER, Role.ASSISTANT));
        }
    }

    @Test
    public void getRolesUserIsAllowedToGiveForAMembershipWillReturnAllRolesForAnAdminThatIsNotAMemberOfTheGroup(){
        final PimUser pimUser = new PimUser();
        pimUser.setRoles(List.of(Role.ADMIN));
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());

            final Stream<Role> roles = securityService.getRolesUserIsAllowedToGiveForAMembership(session, group, user);

            assertThat(roles.collect(Collectors.toSet()), containsInAnyOrder(
                    Role.PI,
                    Role.RESEARCHER,
                    Role.ASSISTANT,
                    Role.JOBRUNNER_MINION,
                    Role.SIAMESENETWORK_MINION,
                    Role.OCR_MINION
            ));
        }
    }

    @Test
    public void getRolesUserIsAllowedToGiveForAMembershipReturnsAnEmptyListForADisabledUser() {
        final PimUser pimUser = new PimUser();
        pimUser.setRoles(List.of(Role.ADMIN));
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());

            final Stream<Role> roles = securityService.getRolesUserIsAllowedToGiveForAMembership(session, group, user);

            assertThat(roles.collect(Collectors.toSet()), is(empty()));
        }
    }
}