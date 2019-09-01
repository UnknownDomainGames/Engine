package nullengine.client.rendering.model;

import java.util.Random;

class ModelUtilsTest {

    private static void benchmarkTestBooleanArrayDirectionCheck() {
        boolean[][] cullFaces = new boolean[100000][6];
        Random random = new Random();
        for (boolean[] cullFace : cullFaces) {
            for (int i = 0; i < cullFace.length; i++) {
                cullFace[i] = random.nextBoolean();
            }
        }

        boolean[] result = new boolean[100000];
        long start = System.nanoTime();
        for (int i = 0; i < result.length; i++) {
            result[i] = isCullFace(cullFaces[0], cullFaces[i]);
        }
        System.out.println((System.nanoTime() - start) / 1000L);
    }

    private static boolean isCullFace(boolean[] coveredFaces, boolean[] cullFaces) {
        for (int i = 0; i < 6; i++) {
            if (cullFaces[i] && !coveredFaces[i]) {
                return false;
            }
        }
        return true;
    }

    private static void benchmarkTestIntDirectionCheck() {
        int[] cullFaces = new int[100000];
        Random random = new Random();
        for (int i = 0; i < cullFaces.length; i++) {
            cullFaces[i] = random.nextInt(0b0011_1111);
        }

        boolean[] result = new boolean[100000];
        long start = System.nanoTime();
        for (int i = 0; i < result.length; i++) {
            result[i] = ModelUtils.checkCullFace(cullFaces[0], cullFaces[i]);
        }
        System.out.println((System.nanoTime() - start) / 1000L);
    }
}