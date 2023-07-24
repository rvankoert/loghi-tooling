package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.FontDescription;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

public class FontDescriptionDAO extends GenericDAO<FontDescription> {

    public FontDescriptionDAO() {
        super(FontDescription.class);
    }

    public Stream<FontDescription> getNonDeleted(Session session) {
            final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            final CriteriaQuery<FontDescription> query = criteriaBuilder.createQuery(FontDescription.class);
            final Root<FontDescription> root = query.from(FontDescription.class);
            query.where(root.get("deleted").isNull());

            return session.createQuery(query).stream();

    }
}