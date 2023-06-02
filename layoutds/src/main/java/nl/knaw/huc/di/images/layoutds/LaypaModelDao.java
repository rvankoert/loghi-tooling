package nl.knaw.huc.di.images.layoutds;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.LaypaModel;

public class LaypaModelDao extends GenericDAO<LaypaModel> {
    public LaypaModelDao() {
        super(LaypaModel.class);
    }
}
