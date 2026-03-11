public class Mandelbrot {

    public static int getIterations(double c_re, double c_im, int maxIter) {

        double z_re = 0;
        double z_im = 0;

        int iter = 0;

        while (z_re * z_re + z_im * z_im <= 4 && iter < maxIter) {

            double new_re = z_re * z_re - z_im * z_im + c_re;
            double new_im = 2 * z_re * z_im + c_im;

            z_re = new_re;
            z_im = new_im;

            iter++;
        }

        return iter;
    }
}
