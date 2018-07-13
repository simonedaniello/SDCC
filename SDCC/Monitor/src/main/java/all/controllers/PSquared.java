package all.controllers;

import java.util.Arrays;

public class PSquared {

    final int MARKERS = 5;

    // Percentile to find
    final float p;

    // Last percentile value
    float pValue;

    // Initial observations
    float[] initial = new float[MARKERS];
    int initialCount = 0;
    boolean initialized = false;

    // Marker heights
    float[] q = new float[MARKERS];

    // Marker positions
    int[] n = new int[MARKERS];

    // Desired marker positions
    float[] n_desired = new float[MARKERS];

    // Precalculated desired marker increments
    float[] dn = new float[MARKERS];

    // Last k value
    int lastK;

    public PSquared(float p) {
        // Set percentile
        this.p = p;
    }

    private void init() {
        // Set initialized flag
        initialized = true;

        // Process initial observations
        for (int i = 0; i < MARKERS; i++) {
            // Set initial marker heights
            q[i] = initial[i];

            // Initial marker positions
            n[i] = i;
        }

        // Desired marker positions
        n_desired[0] = 0;
        n_desired[1] = 2 * p;
        n_desired[2] = 4 * p;
        n_desired[3] = 2 + 2 * p;
        n_desired[4] = 4;

        // Precalculated desired marker increments
        dn[0] = 0;
        dn[1] = (float) p / 2f;
        dn[2] = p;
        dn[3] = (1f + (float) p) / 2f;
        dn[4] = 1;
    }

    private boolean acceptInitial(float x) {
        if (initialCount < MARKERS) {
            initial[initialCount++] = x;
            Arrays.sort(initial, 0, initialCount);
            return false;
        }

        // Enough values available
        Arrays.sort(initial);
        init();
        return true;
    }

    private float initialSetPercentile() {
        int n = (int) (p * (float) initialCount);
        return initial[n];
    }

    public float accept(float x) {
        // Still recording initial values
        if (!initialized) {
            if (!acceptInitial(x)) {
                pValue = initialSetPercentile();
                return pValue;
            }
        }

        int k = -1;
        if (x < q[0]) {
            // Update minimum value
            q[0] = x;
            k = 0;
        } else if (q[0] <= x && x < q[1])
            k = 0;
        else if (q[1] <= x && x < q[2])
            k = 1;
        else if (q[2] <= x && x < q[3])
            k = 2;
        else if (q[3] <= x && x <= q[4])
            k = 3;
        else if (q[4] < x) {
            // Update maximum value
            q[4] = x;
            k = 3;
        }

        // Check if k is set properly
        assert (k >= 0);
        lastK = k;

        // Increment all positions starting at marker k+1
        for (int i = k + 1; i < MARKERS; i++)
            n[i]++;

        // Update desired marker positions
        for (int i = 0; i < MARKERS; i++)
            n_desired[i] += dn[i];

        // Adjust marker heights 2-4 if necessary
        for (int i = 1; i < MARKERS - 1; i++) {
            float d = n_desired[i] - n[i];

            if ((d >= 1 && (n[i + 1] - n[i]) > 1) || (d <= -1 && (n[i - 1] - n[i]) < -1)) {
                int ds = sign(d);

                // Try adjusting q using P-squared formula
                float tmp = parabolic(ds, i);
                if (q[i - 1] < tmp && tmp < q[i + 1]) {
                    q[i] = tmp;
                } else {
                    q[i] = linear(ds, i);
                }

                n[i] += ds;
            }

        }

        // Set current percentile value for later retrieval
        pValue = q[2];
        return q[2];
    }

    public float getPValue() {
        return pValue;
    }

    float linear(int d, int i) {
        return q[i] + d * (q[i + d] - q[i]) / (n[i + d] - n[i]);
    }

    float parabolic(float d, int i) {
        float a = (float) d / (float) (n[i + 1] - n[i - 1]);

        float b = (float) (n[i] - n[i - 1] + d) * (q[i + 1] - q[i]) / (float) (n[i + 1] - n[i])
                + (float) (n[i + 1] - n[i] - d) * (q[i] - q[i - 1]) / (float) (n[i] - n[i - 1]);

        return (float) q[i] + a * b;
    }

    int sign(float d) {
        if (d >= 0)
            return 1;

        return -1;
    }

    void dump() {
        System.out.println("initial: " + Arrays.toString(initial));
        System.out.println("k: " + lastK);
        System.out.println("q: " + Arrays.toString(q));
        System.out.println("n: " + Arrays.toString(n));
        System.out.println("n': " + Arrays.toString(n_desired));
    }
}