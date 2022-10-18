package gregtech.api.metatileentity.implementations;

import static gregtech.api.enums.GT_Values.GT;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import gregtech.api.enums.GT_Values;
import gregtech.api.gui.modularui.GT_UITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_Cleanroom;

public abstract class GT_MetaTileEntity_TieredMachineBlock extends MetaTileEntity {
    /**
     * Value between [0 - 9] to describe the Tier of this Machine.
     * PLZ [0-15] works - READ! GT_Values class.
     */
    public final byte mTier;

    @Deprecated
    public final String mDescription;

    /**
     * A simple Description.
     */
    public final String[] mDescriptionArray;

    /**
     * Contains all Textures used by this Block.
     */
    public final ITexture[][][] mTextures;

    public GT_MetaTileEntity_TieredMachineBlock(
            int aID,
            String aName,
            String aNameRegional,
            int aTier,
            int aInvSlotCount,
            String aDescription,
            ITexture... aTextures) {
        super(aID, aName, aNameRegional, aInvSlotCount);
        mTier = (byte) Math.max(0, Math.min(aTier, 14));
        mDescriptionArray = aDescription == null ? new String[0] : new String[] {aDescription};
        mDescription = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
        // must always be the last call!
        if (GT.isClientSide()) mTextures = getTextureSet(aTextures);
        else mTextures = null;
    }

    public GT_MetaTileEntity_TieredMachineBlock(
            int aID,
            String aName,
            String aNameRegional,
            int aTier,
            int aInvSlotCount,
            String[] aDescription,
            ITexture... aTextures) {
        super(aID, aName, aNameRegional, aInvSlotCount);
        mTier = (byte) Math.max(0, Math.min(aTier, 15));
        mDescriptionArray = aDescription == null ? new String[0] : aDescription;
        mDescription = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";

        // must always be the last call!
        if (GT.isClientSide()) mTextures = getTextureSet(aTextures);
        else mTextures = null;
    }

    @Override
    public Class<?> getType() {
        return GT_MetaTileEntity_Cleanroom.class;
    }

    public GT_MetaTileEntity_TieredMachineBlock(
            String aName, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures) {
        super(aName, aInvSlotCount);
        mTier = (byte) aTier;
        mDescriptionArray = aDescription == null ? new String[0] : new String[] {aDescription};
        mDescription = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
        mTextures = aTextures;
    }

    public GT_MetaTileEntity_TieredMachineBlock(
            String aName, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aInvSlotCount);
        mTier = (byte) aTier;
        mDescriptionArray = aDescription == null ? new String[0] : aDescription;
        mDescription = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
        mTextures = aTextures;
    }

    @Override
    public byte getTileEntityBaseType() {
        return (byte) (Math.min(3, mTier <= 0 ? 0 : 1 + ((mTier - 1) / 4)));
    }

    @Override
    public long getInputTier() {
        return mTier;
    }

    @Override
    public long getOutputTier() {
        return mTier;
    }

    @Override
    public String[] getDescription() {
        return mDescriptionArray;
    }

    /**
     * Used Client Side to get a Texture Set for this Block.
     * Called after setting the Tier and the Description so that those two are accessible.
     *
     * @param aTextures is the optional Array you can give to the Constructor.
     */
    public abstract ITexture[][][] getTextureSet(ITexture[] aTextures);

    protected void addChargerSlot(ModularWindow.Builder builder, int x, int y) {
        String batterySlotTooltipKey;
        Object[] batterySlotTooltipArgs;
        String pTier1 = powerTierName(mTier);
        if (mTier == GT_Values.VN.length - 1) {
            batterySlotTooltipKey = BATTERY_SLOT_TOOLTIP_ALT;
            batterySlotTooltipArgs = new String[] {pTier1};
        } else {
            batterySlotTooltipKey = BATTERY_SLOT_TOOLTIP;
            batterySlotTooltipArgs = new String[] {pTier1, powerTierName((byte) (mTier + 1))};
        }
        addChargerSlot(builder, x, y, batterySlotTooltipKey, batterySlotTooltipArgs);
    }

    protected void addChargerSlot(
            ModularWindow.Builder builder, int x, int y, String tooltipKey, Object[] tooltipArgs) {
        builder.widget(new SlotWidget(inventoryHandler, rechargerSlotStartIndex())
                .disableShiftInsert()
                .setGTTooltip(() -> mTooltipCache.getData(tooltipKey, tooltipArgs))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setBackground(getSlotBackground(), GT_UITextures.OVERLAY_SLOT_CHARGER)
                .setPos(x, y));
    }

    /**
     * Apply proper coloration to a machine's power tier short name
     * @param machineTier
     * @return colored power tier short name
     */
    private String powerTierName(byte machineTier) {
        return GT_Values.TIER_COLORS[machineTier] + GT_Values.VN[machineTier];
    }
}
