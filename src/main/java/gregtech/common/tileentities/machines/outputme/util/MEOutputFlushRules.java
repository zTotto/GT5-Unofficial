package gregtech.common.tileentities.machines.outputme.util;

/**
 * Custom (not upstream): when an ME output bus or hatch pushes its cache into the ME network. Kept free of AE2 and
 * Minecraft so it can be unit tested.
 */
public final class MEOutputFlushRules {

    private MEOutputFlushRules() {}

    /**
     * The cache is due once at least {@code refreshTime} ticks have passed since the last real flush. The caller does
     * not move {@code lastFlushTick} while the cache is empty, so the first output after a pause is due right away.
     */
    public static boolean isFlushDue(long tick, long lastFlushTick, int refreshTime) {
        return tick - lastFlushTick >= refreshTime;
    }
}
