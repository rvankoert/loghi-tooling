package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(DocumentImage.class)
public abstract class DocumentImage_ {
    //    public static volatile SingularAttribute<DocumentImageSet, Long> id;
//    public static volatile SingularAttribute<DocumentImageSet, String> name;
    public static volatile SingularAttribute<DocumentImageSet, String> tesseract4BestHOCRAnalyzed;

    public static volatile SetAttribute<DocumentImage, DocumentImageSet> documentImageSets;
}
