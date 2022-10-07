package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.PylaiaModel;

public class PylaiaModelDao extends GenericDAO<PylaiaModel> {
    public PylaiaModelDao() {
        super(PylaiaModel.class);
    }
}
