package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Configuration;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ConfigurationDAO extends GenericDAO<Configuration> {
    private int autoCompleteLimit;

    public ConfigurationDAO() {
        super(Configuration.class);
    }

    public Configuration getByKey(String key) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            return getByKey(session, key);
        }
    }

    public Configuration getByKey(Session session, String key) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Configuration> criteriaQuery = criteriaBuilder.createQuery(Configuration.class);
        Root<Configuration> configurationRoot = criteriaQuery.from(Configuration.class);

        criteriaQuery.where(criteriaBuilder.equal(configurationRoot.get("key"), key));
        TypedQuery<Configuration> query = session.createQuery(criteriaQuery);
        List<Configuration> configurations = query.getResultList();

        if (configurations.size() == 1) {
            return configurations.get(0);
        } else {
            return null;
        }
    }

    public int getIntByKey(String key, int defaultValue) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            return this.getIntByKey(session, key, defaultValue);
        }
    }

    public int getIntByKey(Session session, String key, int defaultValue) {
        Configuration configuration = getByKey(session, key);
        if (configuration != null) {
            return Integer.parseInt(configuration.getValue());
        }
        return defaultValue;
    }

    public String getStringByKey(String key, String defaultValue) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            return getStringByKey(session, key, defaultValue);
        }
    }

    public String getStringByKey(Session session, String key, String defaultValue) {
        Configuration configuration = getByKey(session, key);
        if (configuration != null) {
            return configuration.getValue();
        }
        return defaultValue;
    }

    public boolean getBooleanByKey(String booleanKey, boolean defaultValue) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            return getBooleanByKey(session, booleanKey, defaultValue);
        }
    }

    public boolean getBooleanByKey(Session session, String booleanKey, boolean defaultValue) {
        Configuration configuration = getByKey(session, booleanKey);
        if (configuration != null) {
            return Boolean.parseBoolean(configuration.getValue());
        }
        return defaultValue;
    }

    public int getAutoCompleteLimit(Session session) {
        if(this.autoCompleteLimit == 0) {
            this.autoCompleteLimit = getIntByKey(session, "autocompleteLimit", 100);
        }
        return this.autoCompleteLimit;
    }
}
