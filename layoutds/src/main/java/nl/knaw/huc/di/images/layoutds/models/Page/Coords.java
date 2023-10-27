package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Coords {
    private static Pattern pointsPattern = Pattern.compile("([0-9]+,[0-9]+ )+([0-9]+,[0-9]+)");

    @JacksonXmlProperty(isAttribute = true, localName = "points")
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        // regex to check if points is a valid string
        // https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
        Matcher matcher = pointsPattern.matcher(points);
        if (!matcher.matches()){
            throw new IllegalArgumentException("Points string is not valid");
        }

        this.points = points;
    }


}
