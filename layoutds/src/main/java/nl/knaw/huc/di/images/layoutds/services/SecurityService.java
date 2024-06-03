package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.MembershipDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hibernate.Session;

import java.util.Optional;
import java.util.stream.Stream;

public class SecurityService {
    public Stream<Role> getRolesUserIsAllowedToGiveForAMembership(Session session, PimGroup group, PimUser user) {

        if(user.getDisabled()) {
            return Stream.empty();
        }

        final Stream<Role> roles = Stream.of(Role.values())
                .filter(role -> role != Role.AUTHENTICATED && role != Role.ADMIN);
        if (user.isAdmin()) {
            return roles;
        }

        final MembershipDAO membershipDao = new MembershipDAO();
        Optional<Membership> membershipOpt = getMembership(session, group, user, membershipDao);

        return membershipOpt.map(membership -> roles
                .filter(membership::isAllowedToSeeRole)).orElse(Stream.empty());
    }

    private Optional<Membership> getMembership(Session session, PimGroup group, PimUser user, MembershipDAO membershipDao) {
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
