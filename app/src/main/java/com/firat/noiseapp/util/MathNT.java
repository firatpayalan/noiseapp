package com.firat.noiseapp.util;

import java.util.List;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class MathNT {

    public static final long round(double a) {
        return (long) Math.floor(a + 0.5d);
    }

    public static final double roundTo(double number, int decimals) {
        if (decimals > 0) {
            double r = powerOfTen(decimals);
            return MathNT.round(number * r) / r;
        } else
            throw new IllegalArgumentException("decimals must be > 0");
    }

    public static final double powerOfTen(int power) {
        double r = 1;
        if (power != 0)
            for (int p = 0; p < Math.abs(power); p++)
                r *= 10;
        if (power < 0)
            r = 1 / r;
        return r;
    }


    //never used
//	public static final double acos(double d)
//	{
//		double d1 = asin(d);
//		if(Double.isNaN(d1))
//			return d1;
//		else
//			return 1.5707963267948966D - d1;
//	}

    //never used
//	public static final double asin(double d)
//	{
//		if(d < -1D || d > 1.0D)
//			return Double.NaN;
//		if(d == -1D)
//			return -1.5707963267948966D;
//		if(d == 1.0D)
//			return 1.5707963267948966D;
//		else
//			return atan(d / Math.sqrt(1.0D - d * d));
//	}

    public static final double atan(double d) {
        boolean flag = false;
        boolean flag1 = false;
        int i = 0;
        if (d < 0.0D) {
            d = -d;
            flag = true;
        }
        if (d > 1.0D) {
            d = 1.0D / d;
            flag1 = true;
        }
        double d2;
        for (; d > 0.26179938779914941D; d *= d2) {
            i++;
            d2 = d + 1.7320508075688772D;
            d2 = 1.0D / d2;
            d *= 1.7320508075688772D;
            d--;
        }

        double d1 = d * d;
        double d3 = d1 + 1.4087812D;
        d3 = 0.55913709D / d3;
        d3 += 0.60310578999999997D;
        d3 -= d1 * 0.051604539999999997D;
        d3 *= d;
        for (; i > 0; i--)
            d3 += 0.52359877559829882D;

        if (flag1)
            d3 = 1.5707963267948966D - d3;
        if (flag)
            d3 = -d3;
        return d3;
    }

    //never used
//	public static final double atan2(double d, double d1)
//	{
//		if(d == 0.0D && d1 == 0.0D)
//			return 0.0D;
//		if(d1 > 0.0D)
//			return atan(d / d1);
//		if(d1 < 0.0D)
//			if(d < 0.0D)
//				return -(3.1415926535897931D - atan(d / d1));
//			else
//				return 3.1415926535897931D - atan(-d / d1);
//		return d >= 0.0D ? 1.5707963267948966D : -1.5707963267948966D;
//	}

    private static final double _log(double d) {
        if (d <= 0.0D)
            return (0.0D / 0.0D);
        double d1 = 0.0D;
        int i;
        for (i = 0; d > 0.0D && d <= 1.0D; i++)
            d *= 2D;

        d /= 2D;
        i--;
        double d2 = d - 1.0D;
        double d3 = d + 1.0D;
        double d4 = d2 / d3;
        double d5 = d4;
        d3 = d5 * d4;
        for (long l = 1L; l < 50L; l += 2L) {
            d1 += d5 / l;
            d5 *= d3;
        }

        d1 *= 2D;
        for (int j = 0; j < i; j++)
            d1 += -0.69314718055994529D;

        return d1;
    }

    public static final double log(double d) {
        if (d <= 0.0D)
            return Double.NaN;
        if (d == 1.0D)
            return 0.0D;
        if (d > 1.0D) {
            d = 1.0D / d;
            return -_log(d);
        } else {
            return _log(d);
        }
    }

    public static final double log10(double d) {
        return log(d) / 2.3025850929940459D;
    }

    private static final double exp(double d) {
        if (d == 0.0D)
            return 1.0D;
        double d1 = 1.0D;
        long l = 1L;
        boolean flag = d < 0.0D;
        if (flag)
            d = -d;
        double d2 = d / l;
        for (long l1 = 2L; l1 < 50L; l1++) {
            d1 += d2;
            d2 = (d2 * d) / l1;
        }

        if (flag)
            return 1.0D / d1;
        else
            return d1;
    }

    public static final double pow(double x, double y) {
        if (y == 0.0D)
            return 1.0D;
        if (y == 1.0D)
            return x;
        if (x == 0.0D)
            return 0.0D;
        if (x == 1.0D)
            return 1.0D;
        long l = (long) Math.floor(y);
        boolean flag = y == l;
        if (flag) {
            boolean flag1 = false;
            if (y < 0.0D)
                flag1 = true;
            double d2 = x;
            for (long l1 = 1L; l1 < (flag1 ? -l : l); l1++)
                d2 *= x;
            if (flag1)
                return 1.0D / d2;
            else
                return d2;
        }
        if (x > 0.0D)
            return exp(y * log(x));
        else
            return (0.0D / 0.0D);
    }

    // //code taken from:
    // http://today.java.net/pub/a/today/2007/11/06/creating-java-me-math-pow-method.html
    // public static final double pow(double x, double y)
    // {
    // //Convert the real power to a fractional form
    // int den = 1024; //declare the denominator to be 1024
    //
    // /*Conveniently 2^10=1024, so taking the square root 10
    // times will yield our estimate for n. In our example
    // n^3=8^2 n^1024 = 8^683.*/
    //
    // int num = (int)(y*den); // declare numerator
    //
    // int iterations = 10; /*declare the number of square root
    // iterations associated with our denominator, 1024.*/
    //
    // double n = Double.MAX_VALUE; /* we initialize our
    // estimate, setting it to max*/
    //
    // while( n >= Double.MAX_VALUE && iterations > 1)
    // {
    // /* We try to set our estimate equal to the right
    // hand side of the equation (e.g., 8^2048). If this
    // number is too large, we will have to rescale. */
    //
    // n = x;
    //
    // for( int i=1; i < num; i++ )n*=x;
    //
    // /*here, we handle the condition where our starting
    // point is too large*/
    // if( n >= Double.MAX_VALUE )
    // {
    // iterations--; /*reduce the iterations by one*/
    //
    // den = (int)(den / 2); /*redefine the denominator*/
    //
    // num = (int)(y*den); //redefine the numerator
    // }
    // }
    //
    // /*************************************************
    // ** We now have an appropriately sized right-hand-side.
    // ** Starting with this estimate for n, we proceed.
    // **************************************************/
    //
    // for( int i = 0; i < iterations; i++ )
    // {
    // n = Math.sqrt(n);
    // }
    //
    // // Return our estimate
    // return n;
    // }

    public static double meanOfList(List<Double> list)
    {
        double sum=0;
        if (!list.isEmpty())
        {
            for (Double currentElement:list)
                sum+=currentElement.doubleValue();

            return sum / list.size();
        }
        return sum;
    }
}
