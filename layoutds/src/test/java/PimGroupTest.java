import nl.knaw.huc.di.images.layoutds.DAO.PimGroupDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class PimGroupTest {


    private final PimGroupDAO pimGroupDAO;

    public PimGroupTest() {
        pimGroupDAO = new PimGroupDAO();
    }

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void addSubgroupIsAllowed() {
        final PimGroup pimGroup = new PimGroup();
        final PimGroup subgroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);

        pimGroup.addSubgroup(subgroup);
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup retrievedGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(retrievedGroup.getSubgroups(), Matchers.hasItem(subgroup));
        }
    }

    @Test
    public void addSubgroupWithSubgroupIsAllowed() {
        final PimGroup pimGroup = new PimGroup();
        final PimGroup subgroup = new PimGroup();
        subgroup.addSubgroup(new PimGroup());
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);

        pimGroup.addSubgroup(subgroup);
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup retrievedGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(retrievedGroup.getSubgroups(), Matchers.hasItem(subgroup));
        }
    }

    @Test
    public void addSubGroupAllowsImageSetWhenTheGroupToAddToAlreadyGotSubGroupsWithSubGroups() {
        final PimGroup pimGroup = new PimGroup();
        final PimGroup subGroup = new PimGroup();
        final PimGroup subSubGroup = new PimGroup();
        subGroup.addSubgroup(subSubGroup);
        pimGroup.addSubgroup(subGroup);
        pimGroupDAO.save(subSubGroup);
        pimGroupDAO.save(subGroup);
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup retrievedGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimGroup subGroupToAdd = new PimGroup();
            pimGroupDAO.save(session, subGroupToAdd);
            retrievedGroup.addSubgroup(subGroupToAdd);
            pimGroupDAO.save(session, retrievedGroup);
            assertThat(retrievedGroup.getSubgroups(), Matchers.hasItem(subGroupToAdd));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubgroupDoesNotAllowSubgroupWith3LevelsDepth() {
        final PimGroup pimGroup = new PimGroup();
        final PimGroup subgroup = new PimGroup();
        final PimGroup subSubgroup = new PimGroup();
        final PimGroup subSubSubgroup = new PimGroup();
        subSubgroup.addSubgroup(subSubSubgroup);
        subgroup.addSubgroup(subSubgroup);
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);


        pimGroup.addSubgroup(subgroup);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubGroupDoesNotAllowSubgroupsToBeAddedAtThe3rdLevel() {
        final PimGroup supergroup = new PimGroup();
        final PimGroup superSupergroup = new PimGroup();
        superSupergroup.addSubgroup(supergroup);
        final PimGroup pimGroup = new PimGroup();
        supergroup.addSubgroup(pimGroup);
        final PimGroup subgroup = new PimGroup();
        pimGroupDAO.save(supergroup);
        pimGroupDAO.save(superSupergroup);
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup dbPimGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());

            dbPimGroup.addSubgroup(subgroup);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubgroupDoesNotAllowSubgroupsWhenTheTotalDepthWillGreaterThan3() {
        final PimGroup supergroup = new PimGroup();
        final PimGroup pimGroup = new PimGroup();
        supergroup.addSubgroup(pimGroup);
        final PimGroup subgroup = new PimGroup();
        final PimGroup subSubgroup = new PimGroup();
        subgroup.addSubgroup(subSubgroup);
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);
        pimGroupDAO.save(supergroup);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup dbPimGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());

            dbPimGroup.addSubgroup(subgroup);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubgroupDoesNotAllowASubgroupToBeAddedToOneOfItsSubgroups() {
        final PimGroup pimGroup = new PimGroup();
        final PimGroup subgroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        subgroup.addSubgroup(pimGroup);
        pimGroupDAO.save(subgroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup dbPimGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimGroup dbSubGroup = pimGroupDAO.getByUUID(session, subgroup.getUuid());
            dbPimGroup.addSubgroup(dbSubGroup);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubgroupIsNotAllowedWhenGroupIsAlreadyPartOfABranch() {
        final PimGroup pimGroup = new PimGroup();
        final PimGroup subgroup = new PimGroup();
        final PimGroup subSubgroup = new PimGroup();
        subgroup.addSubgroup(subSubgroup);
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);
        pimGroup.addSubgroup(subgroup);
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup dbSubSubgroup = pimGroupDAO.getByUUID(session, subSubgroup.getUuid());
            final PimGroup dbPimGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            dbPimGroup.addSubgroup(dbSubSubgroup);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubgroupIsNotAllowedWhenGroupSetIsAlreadyPartOfAnotherBranchOfASupergroup() {
        final PimGroup supergroup = new PimGroup();
        final PimGroup pimGroup = new PimGroup();
        supergroup.addSubgroup(pimGroup);
        final PimGroup subgroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        pimGroupDAO.save(subgroup);
        supergroup.addSubgroup(subgroup);
        pimGroupDAO.save(supergroup);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup dbPimGroup = pimGroupDAO.getByUUID(session, pimGroup.getUuid());

            dbPimGroup.addSubgroup(subgroup);
        }
    }

}

