package kekztech.common.tileentities;

import java.math.BigInteger;
import java.util.EnumSet;
import java.util.Set;

import kekztech.common.tileentities.MTELapotronicSuperCapacitor.Capacitor;

/**
 * Custom (not upstream): rules for the Lapotronic Supercapacitor's capacitors below UHV, kept outside the tile entity
 * so they can be unit tested. Count arrays are indexed by {@link Capacitor#ordinal()}, like
 * {@code MTELapotronicSuperCapacitor.capacitors}.
 */
final class LSCEnergyRules {

    private static final Set<Capacitor> SUB_UHV = EnumSet
        .of(Capacitor.EV, Capacitor.IV, Capacitor.LuV, Capacitor.ZPM, Capacitor.UV);

    private LSCEnergyRules() {}

    /** Number of real capacitors of any tier, not counting empty casings. */
    static int realCapacitorCount(int[] capacitors) {
        int count = 0;
        for (Capacitor capacitor : Capacitor.VALUES) {
            if (capacitor != Capacitor.None) count += capacitors[capacitor.ordinal()];
        }
        return count;
    }

    /** EU that the capacitors below UHV keep inside the LSC when wireless mode rebalances: half of their capacity. */
    static BigInteger subUHVWirelessReserve(int[] capacitors) {
        BigInteger capacity = BigInteger.ZERO;
        for (Capacitor capacitor : SUB_UHV) {
            capacity = capacity.add(
                capacitor.getProvidedCapacity()
                    .multiply(BigInteger.valueOf(capacitors[capacitor.ordinal()])));
        }
        return capacity.shiftRight(1);
    }
}
