package gregtech.common.tileentities.machines.outputme.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MEOutputFlushRulesTest {

    @Test
    void oneTickIntervalFlushesOnTheNextTick() {
        assertFalse(MEOutputFlushRules.isFlushDue(100, 100, 1));
        assertTrue(MEOutputFlushRules.isFlushDue(101, 100, 1));
    }

    @Test
    void fortyTickIntervalFlushesOnTheFortiethTick() {
        assertFalse(MEOutputFlushRules.isFlushDue(139, 100, 40));
        assertTrue(MEOutputFlushRules.isFlushDue(140, 100, 40));
    }

    @Test
    void longPauseFlushesRightAway() {
        assertTrue(MEOutputFlushRules.isFlushDue(10_000, 100, 40));
    }
}
