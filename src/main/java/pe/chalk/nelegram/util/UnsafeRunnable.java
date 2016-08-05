package pe.chalk.nelegram.util;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-05
 */
public interface UnsafeRunnable extends Runnable {
    @Override
    default void run() {
        try {
            this.runUnsafe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void runUnsafe() throws Exception;

    static void start(UnsafeRunnable runnable){
        runnable.run();
    }
}
