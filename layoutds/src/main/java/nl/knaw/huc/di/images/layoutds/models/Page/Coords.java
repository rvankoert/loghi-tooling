package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Coords {
    @JacksonXmlProperty(isAttribute = true, localName = "points")
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
            String[] splitted = points.split(" ");
            for (String point:splitted){
                String[] splittedPoint = point.split(",");
                int x = Integer.parseInt(splittedPoint[0]);
                int y = Integer.parseInt(splittedPoint[1]);
                if (x < 0 || y < 0) {
                    throw new IllegalArgumentException("Points string is not valid :\"" + points + "\"" );
                }
            }
            if (splitted.length<2){
                throw new IllegalArgumentException("Points string is not valid :\"" + points + "\"" );
            }

        this.points = points;
    }


}
