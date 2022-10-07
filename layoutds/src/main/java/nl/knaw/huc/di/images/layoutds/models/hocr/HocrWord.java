package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.google.common.base.Strings;

public class HocrWord {
    @JsonProperty("class")
    @JacksonXmlProperty(isAttribute = true, localName = "class")
    private String classString="ocrx_word";

    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty(isAttribute = true, localName = "title")
    private String title;

    @JacksonXmlText
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBoundingBox(int xOffset, int yOffset) {
        if (Strings.isNullOrEmpty(title)) {
            return null;
        }
        String[] splitted = title.split(";");
        for (String s : splitted) {
            String[] subsplitted = s.trim().split(" ");
            if (subsplitted[0].equals("bbox")) {
                int left = Integer.parseInt(subsplitted[1]) + xOffset;
                int top = Integer.parseInt(subsplitted[2]) + yOffset;
                int right = Integer.parseInt(subsplitted[3]) + xOffset;
                int bottom = Integer.parseInt(subsplitted[4]) + yOffset;
                return left + "," + top + " " + right + "," + top + " " + right + "," + bottom + " " + left + "," + bottom;
            }
        }
        return null;
    }

    public void setBoundingBox(String boundingBox) {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @JsonIgnore
    public Integer getConfidence() {
        if (Strings.isNullOrEmpty(title)) {
            return null;
        }
        String[] splitted = title.split(";");
        for (String s : splitted) {
            String[] subsplitted = s.trim().split(" ");
            if (subsplitted[0].equals("x_wconf")) {
                return Integer.parseInt(subsplitted[1]);
            }
        }
        return null;
    }
}
