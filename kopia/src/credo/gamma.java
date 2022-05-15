/*//w  w  w. j  a v  a 2 s .co  m
 * ------------------------------------------------------------------------=
 * Copyright (C) 1997 - 1998 by Visual Numerics, Inc. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is freely
 * granted by Visual Numerics, Inc., provided that the copyright notice
 * above and the following warranty disclaimer are preserved in human
 * readable form.
 *
 * Because this software is licenses free of charge, it is provided
 * "AS IS", with NO WARRANTY.  TO THE EXTENT PERMITTED BY LAW, VNI
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO ITS PERFORMANCE, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * VNI WILL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE
 * OF OR INABILITY TO USE THIS SOFTWARE, INCLUDING BUT NOT LIMITED TO DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, PUNITIVE, AND EXEMPLARY DAMAGES, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
 *
 * ------------------------------------------------------------------------=
 */
//package com.java2s;
package credo;

/**
 *
 * @author XXX
 */
public class gamma {
    private static final double GAMMA_COEF[] = {
            .8571195590989331421920062399942e-2,
            .4415381324841006757191315771652e-2,
            .5685043681599363378632664588789e-1,
            -.4219835396418560501012500186624e-2,
            .1326808181212460220584006796352e-2,
            -.1893024529798880432523947023886e-3,
            .3606925327441245256578082217225e-4,
            -.6056761904460864218485548290365e-5,
            .1055829546302283344731823509093e-5,
            -.1811967365542384048291855891166e-6,
            .3117724964715322277790254593169e-7,
            -.5354219639019687140874081024347e-8,
            .9193275519859588946887786825940e-9,
            -.1577941280288339761767423273953e-9,
            .2707980622934954543266540433089e-10,
            -.4646818653825730144081661058933e-11,
            .7973350192007419656460767175359e-12,
            -.1368078209830916025799499172309e-12,
            .2347319486563800657233471771688e-13,
            -.4027432614949066932766570534699e-14,
            .6910051747372100912138336975257e-15,
            -.1185584500221992907052387126192e-15,
            .2034148542496373955201026051932e-16,
            -.3490054341717405849274012949108e-17,
            .5987993856485305567135051066026e-18,
            -.1027378057872228074490069778431e-18 };
    private static final double R9LGMC_COEF[] = {
            .166638948045186324720572965082e0,
            -.138494817606756384073298605914e-4,
            .981082564692472942615717154749e-8,
            -.180912947557249419426330626672e-10,
            .622109804189260522712601554342e-13,
            -.339961500541772194430333059967e-15,
            .268318199848269874895753884667e-17 };

    /**
     * Returns the Gamma function of a double.
     * 
     * @param x
     *            A double value.
     * @return The Gamma function of x. If x is a negative integer, the result
     *         is NaN.
     */
    static public double gamma(double x) {
        double ans;
        double y = Math.abs(x);

        if (y <= 10.0) {
            /*
             * Compute gamma(x) for |x|<=10. First reduce the interval and find
             * gamma(1+y) for 0 <= y < 1.
             */
            int n = (int) x;
            if (x < 0.0)
                n--;
            y = x - n;
            n--;
            ans = 0.9375 + csevl(2.0 * y - 1.0, GAMMA_COEF);
            if (n == 0) {
            } else if (n < 0) {
                // Compute gamma(x) for x < 1
                n = -n;
                if (x == 0.0) {
                    ans = Double.NaN;
                } else if (y < 1.0 / Double.MAX_VALUE) {
                    ans = Double.POSITIVE_INFINITY;
                } else {
                    double xn = n - 2;
                    if (x < 0.0 && x + xn == 0.0) {
                        ans = Double.NaN;
                    } else {
                        for (int i = 0; i < n; i++) {
                            ans /= x + i;
                        }
                    }
                }
            } else { // gamma(x) for x >= 2.0
                for (int i = 1; i <= n; i++) {
                    ans *= y + i;
                }
            }
        } else { // gamma(x) for |x| > 10
            if (x > 171.614) {
                ans = Double.POSITIVE_INFINITY;
            } else if (x < -170.56) {
                ans = 0.0; // underflows
            } else {
                // 0.9189385332046727 = 0.5*log(2*PI)
                ans = Math.exp((y - 0.5) * Math.log(y) - y
                        + 0.9189385332046727 + r9lgmc(y));
                if (x < 0.0) {
                    double sinpiy = Math.sin(Math.PI * y);
                    if (sinpiy == 0 || Math.round(y) == y) {
                        ans = Double.NaN;
                    } else {
                        ans = -Math.PI / (y * sinpiy * ans);
                    }
                }
            }
        }
        return ans;
    }

    static double csevl(double x, double coef[]) {
        double b0, b1, b2, twox;
        int i;
        b1 = 0.0;
        b0 = 0.0;
        b2 = 0.0;
        twox = 2.0 * x;
        for (i = coef.length - 1; i >= 0; i--) {
            b2 = b1;
            b1 = b0;
            b0 = twox * b1 - b2 + coef[i];
        }
        return 0.5 * (b0 - b2);
    }

    static double r9lgmc(double x) {
        double ans;

        if (x < 10.0) {
            ans = Double.NaN;
        } else if (x < 9.490626562e+07) {
            // 9.490626562e+07 = 1/Math.sqrt(EPSILON_SMALL)
            double y = 10.0 / x;
            ans = csevl(2.0 * y * y - 1.0, R9LGMC_COEF) / x;
        } else if (x < 1.39118e+11) {
            // 1.39118e+11 = exp(min(log(amach(2) / 12.0), -log(12.0 *
            // amach(1))));
            // See A&S 6.1.41
            ans = 1.0 / (12.0 * x);
        } else {
            ans = 0.0; // underflows
        }
        return ans;
    }

    /**
     * Rounds value.
     *
     * @param value the number
     * @param decimalplaces number of decimal places
     * @return rounded value as string
     */
    static public String round(double value, double decimalplaces) {

        double mult = Math.pow(10.0, decimalplaces);

        return String.valueOf(Math.round(value * mult) / mult);
    }
}
