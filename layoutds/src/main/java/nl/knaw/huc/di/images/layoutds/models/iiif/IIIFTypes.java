package nl.knaw.huc.di.images.layoutds.models.iiif;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IIIFTypes {

    Manifest("sc:Manifest"),
    Canvas("sc:Canvas"),
    ResourceImage("dctypes:Image"),
    ResourceText("dctypes:Text"),
    OtherContextAnnotationList( "sc:AnnotationList"),
    Sequence("sc:Sequence"),
    Annotation("oa:Annotation"),
    SpecificResource("oa:SpecificResource"),
    SvgSelector("oa:SvgSelector"),
    FragmentSelector("oa:FragmentSelector"),
    Tag("oa:Tag"),
    ContentAsText("cnt:ContentAsText"),
    Range("sc:Range"),
    Layer("sc:Layer"),
    Hit("search:hit"),
    Commenting("oa:commenting"),
    Tagging("oa:Tagging"),
    Choice("oa:Choice"),
    Painting("sc:painting"),
    Image("dctypes:Image"),
    AnnotationPage("sc:AnnotationPage"),
//    Service("dctypes:Service"),
    ImageService2("dctypes:ImageService2"),;


    private final String text;

    IIIFTypes(final String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

}
