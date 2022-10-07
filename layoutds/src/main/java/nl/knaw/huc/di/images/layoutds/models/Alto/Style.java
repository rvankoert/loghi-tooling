package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.Entity;

@Entity
public class Style {
    @JacksonXmlProperty(isAttribute = true, localName = "ID", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String id;
    @JacksonXmlProperty(isAttribute = true, localName = "FONTFAMILY", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String FontFamily;
    @JacksonXmlProperty(isAttribute = true, localName = "FONTSIZE", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String FontSize;

    @JacksonXmlProperty(isAttribute = true, localName = "FONTSTYLE", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String fontStyle;

    @JacksonXmlProperty(isAttribute = true, localName = "ALIGN", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String align;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFontFamily() {
        return FontFamily;
    }

    public void setFontFamily(String fontFamily) {
        FontFamily = fontFamily;
    }

    public String getFontSize() {
        return FontSize;
    }

    public void setFontSize(String fontSize) {
        FontSize = fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }
}
