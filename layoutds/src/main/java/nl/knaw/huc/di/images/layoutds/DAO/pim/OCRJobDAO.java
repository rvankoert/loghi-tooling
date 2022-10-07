package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.OCRJob;
import nl.knaw.huc.di.images.layoutds.models.pim.P2PaLaJob;
import nl.knaw.huc.di.images.layoutds.models.pim.PimJob;
import nl.knaw.huc.di.images.layoutds.models.pim.SiameseNetworkJob;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class OCRJobDAO extends GenericDAO<OCRJob> {

    public OCRJobDAO() {
        super(OCRJob.class);
    }

    public boolean any(Session session, PimJob pimJob) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<OCRJob> criteriaQuery = criteriaBuilder.createQuery(OCRJob.class);
        Root<OCRJob> jobRoot = criteriaQuery.from(OCRJob.class);
        criteriaQuery.select(jobRoot).where(
                criteriaBuilder.equal(jobRoot.get("parent"), pimJob)
        );
        TypedQuery<OCRJob> query = session.createQuery(criteriaQuery);
        List<OCRJob> results = query.setMaxResults(1).getResultList();
        if (results.size() > 0) {
            return true;
        }
        return false;
    }

    public List<OCRJob> getAllByOcrSystem(int numberJobs, OCRJob.OcrSystem ocrSystem) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            final CriteriaQuery<OCRJob> criteriaQuery = criteriaBuilder.createQuery(OCRJob.class);
            final Root<OCRJob> root = criteriaQuery.from(OCRJob.class);

            if (ocrSystem == OCRJob.OcrSystem.TESSERACT4) {
                criteriaQuery.select(root).where(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("ocrSystem"), ocrSystem),
                        criteriaBuilder.isNull(root.get("ocrSystem"))
                ));
            } else {
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("ocrSystem"), ocrSystem));
            }

            final Query<OCRJob> query = session.createQuery(criteriaQuery);
            return query.setMaxResults(numberJobs).getResultList();
        }
    }
}
