import nl.knaw.huc.di.images.layoutanalyzer.Statistics;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StatisticsTest {
    @Test
    public void meanTest() {
        List<Double> data = new ArrayList<>();
        Statistics statistics = new Statistics(data, 0,0);
        Assert.assertEquals(Double.NaN, statistics.getMean(),0.0001);
    }

    @Test
    public void meanTest1() {
        List<Double> data = new ArrayList<>();
        data.add(1d);
        Statistics statistics = new Statistics(data);
        Assert.assertEquals(1d, statistics.getMean(),0.0001);
    }

    @Test
    public void meanTest2() {
        List<Double> data = new ArrayList<>();
        data.add(1d);
        data.add(1d);
        Statistics statistics = new Statistics(data);
        Assert.assertEquals(1d, statistics.getMean(),0.0001);
    }

    @Test
    public void meanTest3() {
        List<Double> data = new ArrayList<>();
        data.add(1d);
        data.add(1d);
        data.add(4d);
        Statistics statistics = new Statistics(data);
        Assert.assertEquals(2d, statistics.getMean(),0.0001);
    }

    @Test
    public void medianTest() {
        List<Double> data = new ArrayList<>();
        data.add(1d);
        data.add(1d);
        data.add(3d);
        data.add(5d);
        data.add(5d);
        Statistics statistics = new Statistics(data);
        Assert.assertEquals(3d, statistics.median(),0.0001);
    }
}