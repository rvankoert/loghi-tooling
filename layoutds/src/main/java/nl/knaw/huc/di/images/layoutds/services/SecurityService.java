package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.MembershipDao;
import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hibernate.Session;

import java.util.Optional;
import java.util.stream.Stream;

public class SecurityService {
    public Stream<Role> getRolesUserIsAllowedToGiveForAMembership(Session session, PimGroup group, PimUser user) {

        final Stream<Role> roles = Stream.of(Role.values())
                .filter(role -> role != Role.AUTHENTICATED && role != Role.ADMIN);
        if (user.isAdmin()) {
            return roles;
        }

        final MembershipDao membershipDao = new MembershipDao();
        Optional<Membership> membershipOpt = getMembership(session, group, user, membershipDao);

        return membershipOpt.map(membership -> roles
                .filter(membership::isAllowedToSeeRole)).orElse(Stream.empty());
    }

    private Optional<Membership> getMembership(Session session, PimGroup group, PimUser user, MembershipDao membershipDao) {
        Optional<Membership> membershipOpt = membershipDao.findByGroupAndUser(session, group, user);
        if (membershipOpt.isPresent()) {
            return membershipOpt;
        }

        for (PimGroup supergroup : group.getSupergroups()) {
            membershipOpt = membershipDao.findByGroupAndUser(session, supergroup, user);
            if (membershipOpt.isPresent()) {
                return membershipOpt;
            } else {
                return getMembership(session, supergroup, user, membershipDao);
            }
        }


        return Optional.empty();
    }
}
