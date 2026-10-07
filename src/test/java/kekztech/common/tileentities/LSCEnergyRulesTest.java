package kekztech.common.tileentities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import kekztech.common.tileentities.MTELapotronicSuperCapacitor.Capacitor;

class LSCEnergyRulesTest {

    private static int[] noCapacitors() {
        return new int[Capacitor.VALUES.length];
    }

    @Test
    void realCapacitorCountIgnoresEmptyCasings() {
        int[] caps = noCapacitors();
        caps[Capacitor.None.ordinal()] = 5;
        caps[Capacitor.EV.ordinal()] = 2;
        caps[Capacitor.UHV.ordinal()] = 1;

        assertEquals(3, LSCEnergyRules.realCapacitorCount(caps));
    }

    @Test
    void realCapacitorCountIsZeroWithOnlyEmptyCasings() {
        int[] caps = noCapacitors();
        caps[Capacitor.None.ordinal()] = 4;

        assertEquals(0, LSCEnergyRules.realCapacitorCount(caps));
    }

    @Test
    void subUHVReserveIsHalfOfTheirCapacity() {
        int[] caps = noCapacitors();
        caps[Capacitor.EV.ordinal()] = 2; // 2 x 60M
        caps[Capacitor.UV.ordinal()] = 1; // 600G

        assertEquals(BigInteger.valueOf(300_060_000_000L), LSCEnergyRules.subUHVWirelessReserve(caps));
    }

    @Test
    void subUHVReserveCountsEveryTierFromEVToUV() {
        int[] caps = noCapacitors();
        caps[Capacitor.EV.ordinal()] = 1;
        caps[Capacitor.IV.ordinal()] = 1;
        caps[Capacitor.LuV.ordinal()] = 1;
        caps[Capacitor.ZPM.ordinal()] = 1;
        caps[Capacitor.UV.ordinal()] = 1;

        // (60M + 600M + 6G + 60G + 600G) / 2
        assertEquals(BigInteger.valueOf(333_330_000_000L), LSCEnergyRules.subUHVWirelessReserve(caps));
    }

    @Test
    void subUHVReserveIgnoresUHVPlusAndEmptyCasings() {
        int[] caps = noCapacitors();
        caps[Capacitor.UHV.ordinal()] = 3;
        caps[Capacitor.UEV.ordinal()] = 1;
        caps[Capacitor.UIV.ordinal()] = 1;
        caps[Capacitor.UMV.ordinal()] = 1;
        caps[Capacitor.None.ordinal()] = 2;

        assertEquals(BigInteger.ZERO, LSCEnergyRules.subUHVWirelessReserve(caps));
    }
}
