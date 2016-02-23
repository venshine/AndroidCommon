package com.wx.android.common.util;

import java.util.Random;

/**
 * Random
 *
 * @author fengwx
 */
public class RandomUtils {

    /**
     * Returns a pseudo-random uniformly distributed {@code int}.
     *
     * @return
     */
    public static int randomInt() {
        Random random = new Random();
        return random.nextInt();
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int} in the half-open range [0, n).
     *
     * @param n
     * @return
     */
    public static int randomInt(int n) {
        Random random = new Random();
        return random.nextInt(n);
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int} in the half-open range [min, max].
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int} in the half-open range [0, n).
     *
     * @param n
     * @return
     */
    public static int randomInt2(int n) {
        return (int) (System.currentTimeMillis() % n);
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int} in the half-open range [0, n).
     *
     * @param n
     * @return
     */
    public static int randomInt3(int n) {
        return (int) (Math.random() * 100);
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code float} in the half-open range [0.0, 1.0).
     *
     * @return
     */
    public static float randomFloat() {
        Random random = new Random();
        return random.nextFloat();
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code double} in the half-open range [0.0, 1.0).
     *
     * @return
     */
    public static double randomDouble() {
        Random random = new Random();
        return random.nextDouble();
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code long}.
     *
     * @return
     */
    public static long randomLong() {
        Random random = new Random();
        return random.nextLong();
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code boolean}.
     *
     * @return
     */
    public static boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    /**
     * Returns a pseudo-random (approximately) normally distributed {@code double} with mean 0.0 and standard deviation
     * 1.0. This method uses the <i>polar method</i> of G. E. P. Box, M. E. Muller, and G. Marsaglia, as described by
     * Donald E. Knuth in <i>The Art of Computer Programming, Volume 2: Seminumerical Algorithms</i>, section 3.4.1,
     * subsection C, algorithm P.
     *
     * @return
     */
    public static double randomGaussian() {
        Random random = new Random();
        return random.nextGaussian();
    }

    /**
     * Fills {@code buf} with random bytes.
     *
     * @param buf
     */
    public static void randomBytes(byte[] buf) {
        Random random = new Random();
        random.nextBytes(buf);
    }

    /**
     * Get a random string
     *
     * @param source
     * @param length
     * @return
     */
    public static String randomString(String source, int length) {
        return StringUtils.isEmpty(source) ? null : randomString(source.toCharArray(), length);
    }

    /**
     * Get a random string
     *
     * @param sourceChar
     * @param length
     * @return
     */
    public static String randomString(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(sourceChar[randomInt(sourceChar.length)]);
        }
        return builder.toString();
    }

    /**
     * Shuffling a int array
     *
     * @param intArray
     * @return
     */
    public static int[] shuffle(int[] intArray) {
        if (intArray == null) {
            return null;
        }
        return shuffle(intArray, intArray.length);
    }

    /**
     * Shuffling a int array
     *
     * @param intArray
     * @param shuffleCount
     * @return
     */
    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray == null || shuffleCount < 0 || (length = intArray.length) < shuffleCount) {
            return null;
        }

        int[] out = new int[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) {
            int random = randomInt(length - i);
            out[i - 1] = intArray[random];
            int temp = intArray[length - i];
            intArray[length - i] = intArray[random];
            intArray[random] = temp;
        }
        return out;
    }

}
