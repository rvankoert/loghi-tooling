package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.SiameseNetworkModel;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

public class SiameseNetworkModelDao extends GenericDAO<SiameseNetworkModel> {
    public SiameseNetworkModelDao() {
        super(SiameseNetworkModel.class);
    }
}
