package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Language;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class LanguageDAO extends GenericDAO<Language> {

    public LanguageDAO() {
        super(Language.class);
    }

    private void init() {
        if (this.getAll().size() == 0) {
            Language language = new Language();
            language.setCode("nld");
            language.setName("Nederlands");
            this.save(language);
        }
    }

    public boolean languageExists(Session session, String code) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Language> criteriaQuery = criteriaBuilder.createQuery(Language.class);
        Root<Language> languageRoot = criteriaQuery.from(Language.class);

        criteriaQuery.where(criteriaBuilder.equal(languageRoot.get("code"), code));
        TypedQuery<Language> query = session.createQuery(criteriaQuery);
        List<Language> languages = query.getResultList();

        return !languages.isEmpty();
    }

    public Language getByCode(String code) {
        init();
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Language> criteriaQuery = criteriaBuilder.createQuery(Language.class);
            Root<Language> languageRoot = criteriaQuery.from(Language.class);

            criteriaQuery.where(criteriaBuilder.equal(languageRoot.get("code"), code));
            TypedQuery<Language> query = session.createQuery(criteriaQuery);
            List<Language> languages = query.getResultList();
            Language language;
            if (languages.size() == 0) {
                language = new Language();
                language.setCode(code);
                language.setName(code);
                Transaction transaction = session.beginTransaction();
                this.save(session, language);
                transaction.commit();
            } else {
                language = languages.get(0);
            }
            session.close();

            return language;
        }
    }
}