package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

//@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@JacksonXmlRootElement(localName = "TextEquiv")
public class TextEquiv {

    public TextEquiv() {
    }

    @JacksonXmlProperty(isAttribute = true, localName = "conf")
    private String conf;

    @JacksonXmlProperty(localName = "PlainText", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private String plainText;

    @JacksonXmlProperty(localName = "Unicode", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private String unicode;

    public TextEquiv(Double totalConf, String plainText, String unicode) {
        if (totalConf != null) {
            this.conf = Double.toString(totalConf);
        }
        this.plainText = plainText;
        this.unicode = unicode;

    }

    public TextEquiv(Double totalConf, String text) {
        if (totalConf != null) {
            this.conf = Double.toString(totalConf);
        }
        this.plainText = text;
        this.unicode = text;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }
}
