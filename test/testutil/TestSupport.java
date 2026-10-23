package testutil;

/**
 * Tiny assertion helper so the combat tests do not need a JUnit dependency.
 */
public final class TestSupport {

    private int passed;
    private int failed;

    public void check(String name, boolean condition) {
        if (condition) {
            passed++;
            return;
        }
        failed++;
        System.err.println("FAIL  " + name);
    }

    public void checkEquals(String name, long expected, long actual) {
        check(name + " (expected " + expected + ", got " + actual + ")", expected == actual);
    }

    public void checkEquals(String name, double expected, double actual, double epsilon) {
        boolean ok = Double.isFinite(actual) && Math.abs(expected - actual) <= epsilon;
        check(name + " (expected " + expected + ", got " + actual + ")", ok);
    }

    public void expectThrows(String name, Class<? extends RuntimeException> type, Runnable body) {
        try {
            body.run();
            check(name + " should have thrown " + type.getSimpleName(), false);
        } catch (RuntimeException e) {
            check(name, type.isInstance(e));
        }
    }

    public int getPassed() {
        return passed;
    }

    public int getFailed() {
        return failed;
    }
}
