public class ColorCompersion {
    public static double PercentageCalculator(int[] FirstRGB, String SecondHex) {
        int[] SecondColorRGB =  HexToRgb(SecondHex);

        double[] FirstColorXYZ = RGBtoXYZ(FirstRGB);
        double[] SecondColorXYZ = RGBtoXYZ(SecondColorRGB);

        double[] FirstColorLab = XYZtoLab(FirstColorXYZ);
        double[] SecondColorLab = XYZtoLab(SecondColorXYZ);

        return CIEDE2000(FirstColorLab,SecondColorLab);
    }

    public static int[] HexToRgb(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new int[]{r, g, b};
    }

    public static double[] RGBtoXYZ(int[] RGB) {
        double var_R = (double) RGB[0] / 255;
        double var_G = (double) RGB[1] / 255;
        double var_B = (double) RGB[2] / 255;

        if ( var_R > 0.04045 ) {
            var_R = Math.pow(( var_R + 0.055 ) / 1.055,2.4);
        }
        else {
            var_R = var_R / 12.92;
        }

        if ( var_G > 0.04045 ) {
            var_G = Math.pow(( var_G + 0.055 ) / 1.055,2.4);
        }
        else {
            var_G = var_G / 12.92;
        }

        if ( var_B > 0.04045 ) {
            var_B = Math.pow(( var_B + 0.055 ) / 1.055,2.4);
        }
        else {
            var_B = var_B / 12.92;
        }

        var_R = var_R * 100;
        var_G = var_G * 100;
        var_B = var_B * 100;

        return new double[]{var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805,
                var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722,
                var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505
        };
    }

    public static double[] XYZtoLab(double[] LAB) {
        double var_X = LAB[0] / 95.047;
        double var_Y = LAB[1] / 100;
        double var_Z = LAB[2] / 108.883;

        if ( var_X > 0.008856 ) {
            var_X = Math.pow(var_X, (double) 1 / 3);
        }
        else {
            var_X = ( 7.787 * var_X ) + ( (double) 16 / 116 );
        }

        if ( var_Y > 0.008856 ) {
            var_Y = Math.pow(var_Y, (double) 1 / 3);
        }
        else {
            var_Y = ( 7.787 * var_Y ) + ( (double) 16 / 116 );
        }

        if ( var_Z > 0.008856 ) {
            var_Z = Math.pow(var_Z, (double) 1 / 3);
        }
        else {
            var_Z = ( 7.787 * var_Z ) + ( (double) 16 / 116 );
        }

        return new double[]{( 116 * var_Y ) - 16,
                500 * ( var_X - var_Y ),
                200 * ( var_Y - var_Z )
        };

    }


    public static double CIEDE2000(double[] lab1, double[] lab2) {
        double L1 = lab1[0], a1 = lab1[1], b1 = lab1[2];
        double L2 = lab2[0], a2 = lab2[1], b2 = lab2[2];

        double kL = 1.0, kC = 1.0, kH = 1.0;

        double C1 = Math.sqrt(a1 * a1 + b1 * b1);
        double C2 = Math.sqrt(a2 * a2 + b2 * b2);
        double CBar = (C1 + C2) / 2.0;

        double G = 0.5 * (1 - Math.sqrt(Math.pow(CBar, 7) / (Math.pow(CBar, 7) + Math.pow(25.0, 7))));
        double a1Prime = (1 + G) * a1;
        double a2Prime = (1 + G) * a2;

        double C1Prime = Math.sqrt(a1Prime * a1Prime + b1 * b1);
        double C2Prime = Math.sqrt(a2Prime * a2Prime + b2 * b2);

        double h1Prime = Math.atan2(b1, a1Prime);
        if (h1Prime < 0) h1Prime += 2 * Math.PI;
        double h2Prime = Math.atan2(b2, a2Prime);
        if (h2Prime < 0) h2Prime += 2 * Math.PI;

        double deltaLPrime = L2 - L1;
        double deltaCPrime = C2Prime - C1Prime;

        double deltahPrime;
        if (C1Prime * C2Prime == 0) {
            deltahPrime = 0;
        } else {
            double dh = h2Prime - h1Prime;
            if (Math.abs(dh) > Math.PI) {
                dh -= Math.signum(dh) * 2 * Math.PI;
            }
            deltahPrime = dh;
        }

        double deltaHPrime = 2 * Math.sqrt(C1Prime * C2Prime) * Math.sin(deltahPrime / 2);

        double LBarPrime = (L1 + L2) / 2.0;
        double CBarPrime = (C1Prime + C2Prime) / 2.0;

        double hBarPrime;
        if (C1Prime * C2Prime == 0) {
            hBarPrime = h1Prime + h2Prime;
        } else {
            double dh = Math.abs(h1Prime - h2Prime);
            if (dh > Math.PI) {
                hBarPrime = (h1Prime + h2Prime + 2 * Math.PI) / 2.0;
            } else {
                hBarPrime = (h1Prime + h2Prime) / 2.0;
            }
        }

        double T = 1 - 0.17 * Math.cos(hBarPrime - Math.toRadians(30)) +
                0.24 * Math.cos(2 * hBarPrime) +
                0.32 * Math.cos(3 * hBarPrime + Math.toRadians(6)) -
                0.20 * Math.cos(4 * hBarPrime - Math.toRadians(63));

        double deltaTheta = Math.toRadians(30) * Math.exp(-Math.pow(Math.toDegrees(hBarPrime) - 275.0, 2) / (25.0 * 25.0));
        double RC = 2 * Math.sqrt(Math.pow(CBarPrime, 7) / (Math.pow(CBarPrime, 7) + Math.pow(25.0, 7)));
        double SL = 1 + ((0.015 * Math.pow(LBarPrime - 50, 2)) / Math.sqrt(20 + Math.pow(LBarPrime - 50, 2)));
        double SC = 1 + 0.045 * CBarPrime;
        double SH = 1 + 0.015 * CBarPrime * T;
        double RT = -Math.sin(2 * deltaTheta) * RC;

        return Math.sqrt(Math.pow(deltaLPrime / (kL * SL), 2) +
                Math.pow(deltaCPrime / (kC * SC), 2) +
                Math.pow(deltaHPrime / (kH * SH), 2) +
                RT * (deltaCPrime / (kC * SC)) * (deltaHPrime / (kH * SH)));
    }
}