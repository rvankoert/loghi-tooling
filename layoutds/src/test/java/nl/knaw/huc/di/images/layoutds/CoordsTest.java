package nl.knaw.huc.di.images.layoutds;

import nl.knaw.huc.di.images.layoutds.models.Page.Coords;
import org.junit.Test;

public class CoordsTest {
    @Test(expected = IllegalArgumentException.class)
    public void setPointsIllegalWords() {
        Coords coords = new Coords();
        coords.setPoints("aste asdsad");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPointsValidStartIllegalWords() {
        Coords coords = new Coords();
        coords.setPoints("12,13 asdsad");
    }

    @Test()
    public void setPointsValid() {
        Coords coords = new Coords();
        coords.setPoints("12,13 24,54");
    }

    @Test()
    public void setPointsValid2() {
        Coords coords = new Coords();
        coords.setPoints("12,13 24,54 55,100");
    }

}

