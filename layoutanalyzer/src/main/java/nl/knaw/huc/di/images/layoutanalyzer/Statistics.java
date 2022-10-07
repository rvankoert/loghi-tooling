package nl.knaw.huc.di.images.layoutanalyzer;

import java.util.Arrays;
import java.util.List;

public class Statistics
{
    final double[] data;
    final int size;
    final int xStart;
    final int xStop;

    public Statistics(List<Double> data)
    {
        this.size = data.size();
        this.xStart = 0;
        this.xStop= data.size();
        this.data = new double[size];
        for (int i=this.xStart; i< this.xStop;i++){
            this.data[i-xStart] = data.get(i);
        }
    }

    public Statistics(List<Double> data, int xStart, int xStop)
    {
        this.size = xStop - xStart;
        this.xStart = xStart;
        this.xStop = xStop ;
        this.data = new double[size];
        for (int i=this.xStart; i< this.xStop;i++){
            this.data[i-xStart] = data.get(i);
        }
    }

    public double getMean()
    {
        double sum = 0.0;
        for(double a : data)
        {
            sum += a;
        }
        return sum/size;
    }

    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :data) {
            temp += (a - mean) * (a - mean);
        }
        return temp/size;
    }

    public double getStdDev()
    {
        return Math.sqrt(getVariance());
    }

    public double median()
    {
        Arrays.sort(data);

        if (size == 0) {
            return 0;
        }

        if (size % 2 == 0)
        {
            return (data[(size / 2) - 1] + data[size / 2]) / 2.0;
        }
        else
        {
            return data[size / 2];
        }
    }

    Double minimum= null;
    public double getMinimum()
    {
        if (minimum == null){
            for (double aData : data) {
                if (minimum == null || aData < minimum) {
                    minimum = aData;
                }
            }
        }
        return minimum;
    }
    Double maximum= null;
    public double getMaximum()
    {
        if (maximum== null){
            for (double aData : data) {
                if (maximum == null || aData > maximum) {
                    maximum = aData;
                }
            }
        }
        return maximum;
    }

}
