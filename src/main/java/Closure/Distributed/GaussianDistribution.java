package Closure.Distributed;

import java.util.List;
import java.util.Random;

public class GaussianDistribution implements Distribution{
    private Random random = new Random();
    private double mean;
    private double sd;

    public GaussianDistribution()
    {

    }

    public GaussianDistribution(double mean, double sd)
    {
        this.mean = mean;
        this.sd = sd;
    }

    public GaussianDistribution(double mean, List<Integer> data)
    {
        this.mean = mean;
        double sum = 0;

        for(int integer : data)
        {
            sum+= Math.pow(integer-mean,2);
        }

        this.sd = Math.sqrt((sum)/(data.size()-1));

    }

    public void setMean(double mean)
    {
        this.mean = mean;
    }

    public void setSd(double sd)
    {
        this.sd = sd;
    }

    @Override
    public int getRandom()
    {
        return (int) random.nextGaussian(mean,sd);
    }
}
