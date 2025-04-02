package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Coords {
    @JacksonXmlProperty(isAttribute = true, localName = "points")
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        setPoints(points, false);
    }

    public void setPoints(String points, boolean fix) {
        String[] splitted = points.split(" ");
        ArrayList<String> pointsList = new ArrayList<>();
        for (String point : splitted) {
            String[] splittedPoint = point.split(",");
            int x = Integer.parseInt(splittedPoint[0]);
            int y = Integer.parseInt(splittedPoint[1]);
            if (x < 0 || y < 0) {
                if (fix) {
                    x = Math.max(0, x);
                    y = Math.max(0, y);
                    point = x + "," + y;
                } else {
                    throw new IllegalArgumentException("Points string is not valid(negative values) :\"" + points + "\"");
                }
            }
            pointsList.add(point);
        }
        if (splitted.length < 2) {
            if (!fix) {
                throw new IllegalArgumentException("Points string is not valid (splitted.length<2) Only one point found:\"" + points + "\"");
            }
        }

        this.points = String.join(" ", pointsList);
    }


}
