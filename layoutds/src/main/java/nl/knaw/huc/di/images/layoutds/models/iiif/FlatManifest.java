package nl.knaw.huc.di.images.layoutds.models.iiif;

public class FlatManifest {

    private String uri;
    private String IIIFId;
    private String type;
    private String label;

    public FlatManifest(String uri, String IIIFId, String type, String label){
        this.uri = uri;
        this.IIIFId = IIIFId;
        this.type = type ;
        this.label= label;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setIIIFId(String iiifId) {
        this.IIIFId = iiifId;
    }

    public String getIIIFId() {
        return IIIFId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
