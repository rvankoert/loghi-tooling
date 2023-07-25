package nl.knaw.huc.di.images.layoutds.models.Page;

import java.util.ArrayList;
import java.util.List;

public class TextLineCustom {
    private List<String> textStyles;
    private String readingOrder;

    public List<String> getTextStyles() {
        return textStyles;
    }

    public void setTextStyles(List<String> textStyles) {
        this.textStyles = textStyles;
    }

    public String getReadingOrder() {
        return readingOrder;
    }

    public void setReadingOrder(String readingOrder) {
        this.readingOrder = readingOrder;
    }
    @Override
    public String toString() {
        String returnValue = "";
        returnValue += this.readingOrder != null ? this.readingOrder: "" + " ";
        if (textStyles!=null){
            for (String textStyle:textStyles) {
                returnValue += textStyle + " ";
            }
        }
        return returnValue.trim();
    }

    public void addCustomTextStyle(String style, int startPosition, int length) {
        String textStyle = "textStyle {offset:"+ startPosition+"; length:"+ length +";"+style+":true;}";
        if (this.getTextStyles()==null){
            this.setTextStyles(new ArrayList<>());
        }
        this.getTextStyles().add(textStyle);
    }

    public void addCustomTextStyle(List<String> styles, int startPosition, int length) {
        final StringBuilder styleBuilder = new StringBuilder();
        for (String style : styles) {
            styleBuilder.append(style).append(":true;");
        }

        String textStyle = "textStyle {offset:" + startPosition + "; length:" + length + ";" + styleBuilder + "}";
        if (this.getTextStyles() == null) {
            this.setTextStyles(new ArrayList<>());
        }
        this.getTextStyles().add(textStyle);
    }

}
