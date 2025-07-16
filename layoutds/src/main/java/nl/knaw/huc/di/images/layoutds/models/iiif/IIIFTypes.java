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
    ImageService2("dctypes:ImageService2"),
    Dataset("Dataset"),;


    private final String text;

    IIIFTypes(final String text) {
        this.text = text;
    }

    public static IIIFTypes parseType(String string) {
        for (IIIFTypes type : IIIFTypes.values()) {
            if (type.text.equalsIgnoreCase(string)) {
                return type;
            }
            String[] splitted = type.toString().split(":");
            if (splitted.length > 1 && splitted[1].equalsIgnoreCase(string)) {
                return type;
            }
        }
        if ("manifest".equalsIgnoreCase(string)) {
            return IIIFTypes.Manifest;
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

}
