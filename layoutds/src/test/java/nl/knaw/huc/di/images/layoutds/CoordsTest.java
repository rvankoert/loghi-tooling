package nl.knaw.huc.di.images.layoutds;

import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageDAO;
import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageSetDAO;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.MetaData;
import nl.knaw.huc.di.images.layoutds.models.Page.Coords;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

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

}

