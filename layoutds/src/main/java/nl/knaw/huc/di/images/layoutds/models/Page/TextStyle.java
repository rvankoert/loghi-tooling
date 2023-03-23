package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "TextStyle")
public class TextStyle {

    @JacksonXmlProperty(isAttribute = true, localName = "fontFamily")
    private String fontFamily;

    @JacksonXmlProperty(isAttribute = true, localName = "serif")
    private Boolean serif;

    @JacksonXmlProperty(isAttribute = true, localName = "monospace")
    private Boolean monospace;

    @JacksonXmlProperty(isAttribute = true, localName = "fontSize")
    private Double fontSize;

    // xheight is a pagexml 2016-07-15 property
    @JacksonXmlProperty(isAttribute = true, localName = "xHeight")
    private Integer xHeight;

    @JacksonXmlProperty(isAttribute = true, localName = "kerning")
    private Integer kerning;

    //           <attribute name="textColour" type="pc:ColourSimpleType"/>
//<attribute name="textColourRgb" type="integer"></attribute>
//<attribute name="bgColour" type="pc:ColourSimpleType"></attribute>
//<attribute name="bgColourRgb" type="integer"></attribute>
//<attribute name="reverseVideo" type="boolean"></attribute>
//<attribute name="bold" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "bold")
    Boolean bold;
    //<attribute name="italic" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "italic")
    Boolean italic;
    //<attribute name="underlined" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "underlined")
    private Boolean underlined;
    // FIXME make enum with values: singleLine, doubleLine, other
    @JacksonXmlProperty(isAttribute = true)
    private String underlineStyle;
    //<attribute name="underlineStyle" type="pc:UnderlineStyleSimpleType" use="optional"></attribute>
//<attribute name="subscript" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "subscript")
    Boolean subscript;
    //<attribute name="superscript" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "superscript")
    Boolean superscript;
    //<attribute name="strikethrough" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "strikethrough")
    Boolean strikethrough;
    //<attribute name="smallCaps" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "smallCaps")
    Boolean smallCaps;
    //<attribute name="letterSpaced" type="boolean"/>
    @JacksonXmlProperty(isAttribute = true, localName = "letterSpaced")
    Boolean letterSpaced;

    // FIXME enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true)
    private String textColour;

    @JacksonXmlProperty(isAttribute = true)
    private int textColourRgb;

    // FIXME enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true)
    private String bgColour;

    @JacksonXmlProperty(isAttribute = true)
    private String bgColourRgb;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean reverseVideo;

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Boolean getSerif() {
        return serif;
    }

    public void setSerif(Boolean serif) {
        this.serif = serif;
    }

    public Boolean getMonospace() {
        return monospace;
    }

    public void setMonospace(Boolean monospace) {
        this.monospace = monospace;
    }

    public Double getFontSize() {
        return fontSize;
    }

    public void setFontSize(Double fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getxHeight() {
        return xHeight;
    }

    public void setxHeight(Integer xHeight) {
        this.xHeight = xHeight;
    }

    public Integer getKerning() {
        return kerning;
    }

    public void setKerning(Integer kerning) {
        this.kerning = kerning;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public Boolean getItalic() {
        return italic;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public Boolean getUnderlined() {
        return underlined;
    }

    public void setUnderlined(Boolean underlined) {
        this.underlined = underlined;
    }

    public Boolean getSubscript() {
        return subscript;
    }

    public void setSubscript(Boolean subscript) {
        this.subscript = subscript;
    }

    public Boolean getSuperscript() {
        return superscript;
    }

    public void setSuperscript(Boolean superscript) {
        this.superscript = superscript;
    }

    public Boolean getStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public Boolean getSmallCaps() {
        return smallCaps;
    }

    public void setSmallCaps(Boolean smallCaps) {
        this.smallCaps = smallCaps;
    }

    public Boolean getLetterSpaced() {
        return letterSpaced;
    }

    public void setLetterSpaced(Boolean letterSpaced) {
        this.letterSpaced = letterSpaced;
    }

    public String getTextColour() {
        return textColour;
    }

    public void setTextColour(String textColour) {
        this.textColour = textColour;
    }

    public int getTextColourRgb() {
        return textColourRgb;
    }

    public void setTextColourRgb(int textColourRgb) {
        this.textColourRgb = textColourRgb;
    }

    public String getBgColour() {
        return bgColour;
    }

    public void setBgColour(String bgColour) {
        this.bgColour = bgColour;
    }

    public String getBgColourRgb() {
        return bgColourRgb;
    }

    public void setBgColourRgb(String bgColourRgb) {
        this.bgColourRgb = bgColourRgb;
    }

    public boolean isReverseVideo() {
        return reverseVideo;
    }

    public void setReverseVideo(boolean reverseVideo) {
        this.reverseVideo = reverseVideo;
    }

    public String getUnderlineStyle() {
        return underlineStyle;
    }

    public void setUnderlineStyle(String underlineStyle) {
        this.underlineStyle = underlineStyle;
    }
}
