package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Person;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class PersonDAO extends GenericDAO<Person> {

    public PersonDAO() {
        super(Person.class);
    }

    public Person getByName(String personString) throws DuplicateDataException {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
            Root<Person> personRoot = criteriaQuery.from(Person.class);

            criteriaQuery.where(criteriaBuilder.equal(personRoot.get("name"), personString));
            TypedQuery<Person> query = session.createQuery(criteriaQuery);
//        query.setParameter(p, uri);
            List<Person> persons = query.getResultList();

            session.close();
            if (persons.size() == 1) {
                return persons.get(0);
            } else if (persons.size() > 1) {
                throw new DuplicateDataException("duplicate data");
            } else {
                return null;
            }
        }
    }
}
