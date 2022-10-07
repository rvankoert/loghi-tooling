package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(DocumentImageSet.class)
public class DocumentImageSet_ {

    //    public static volatile SingularAttribute<DocumentImageSet, Long> id;
//    public static volatile SingularAttribute<DocumentImageSet, String> name;
//    public static volatile SingularAttribute<DocumentImageSet, String> color;
    public static volatile SetAttribute<DocumentImageSet, DocumentImage> documentImages;
}
