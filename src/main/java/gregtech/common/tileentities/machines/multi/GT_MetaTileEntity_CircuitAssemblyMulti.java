package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.GT_Values.AuthorTheEpicGamer274;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GT_StructureUtility.buildHatchAdder;
import static gregtech.api.util.GT_Utility.clamp;
import static gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_PlasmaForge.DIM_INJECTION_CASING;
import static gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_PlasmaForge.DIM_TRANS_CASING;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GRAY;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.IGlobalWirelessEnergy;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_OverclockCalculator;
import gregtech.api.util.GT_Recipe;
import gregtech.common.items.GT_IntegratedCircuit_Item;

public class GT_MetaTileEntity_CircuitAssemblyMulti
    extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_CircuitAssemblyMulti>
    implements ISurvivalConstructable, IGlobalWirelessEnergy {

    public static int CraftingTier = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> STRUCTURE_DEFINITION = StructureDefinition
        .<GT_MetaTileEntity_CircuitAssemblyMulti>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            transpose(new String[][] { { "hhh", "hhh", "hhh" }, { "h~h", "h-h", "hhh" }, { "hhh", "hhh", "hhh" }, }))
        .addElement(
            'h',
            buildHatchAdder(GT_MetaTileEntity_CircuitAssemblyMulti.class)
                .atLeast(InputHatch, OutputBus, InputBus, Maintenance, Energy.or(ExoticEnergy))
                .casingIndex(DIM_INJECTION_CASING)
                .dot(1)
                .buildAndChain(GregTech_API.sBlockCasings1, DIM_INJECTION_CASING))
        .build();

    public GT_MetaTileEntity_CircuitAssemblyMulti(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_CircuitAssemblyMulti(String aName) {
        super(aName);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Circuit Assembler")
            .addInfo("Assisting in all your Circuit needs.")
            .addInfo("All EU is deducted from wireless EU networks only.")
            .addInfo(AuthorTheEpicGamer274)
            .addSeparator()
            .beginStructureBlock(5, 7, 5, false)
            .addStructureInfo(GOLD + "1+ " + GRAY + "Input Hatch")
            .addStructureInfo(GOLD + "1+ " + GRAY + "Output Bus")
            .addStructureInfo(GOLD + "1+ " + GRAY + "Input Bus")
            .addStructureInfo(GOLD + "1 " + GRAY + "Maintenance Hatch")
            .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.sCircuitAssemblerMulti;
    }

    private String ownerUUID;
    int multiplier = 1;
    long mWirelessEUt = 0;
    long wirelessEUtWithoutParallel = 0;
    private int currentCircuitNumber = 0;

    @Override
    protected long getActualEnergyUsage() {
        return mWirelessEUt;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GT_Recipe recipe) {
                mWirelessEUt = 10L * (long) recipe.mEUt * (long) multiplier;
                if (getUserEU(ownerUUID).compareTo(BigInteger.valueOf(mWirelessEUt * recipe.mDuration)) < 0) {
                    return CheckRecipeResultRegistry.insufficientPower(mWirelessEUt * recipe.mDuration);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GT_Recipe recipe) {
                mWirelessEUt = 10L * (long) recipe.mEUt * (long) multiplier;
                // This will void the inputs if wireless energy has dropped
                // below the required amount between validateRecipe and here.
                if (!addEUToGlobalEnergyMap(ownerUUID, -mWirelessEUt * recipe.mDuration)) {
                    return CheckRecipeResultRegistry.insufficientPower(mWirelessEUt * recipe.mDuration);
                }
                // Energy consumed all at once from wireless net.
                setCalculatedEut(0);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Nonnull
            @Override
            protected GT_OverclockCalculator createOverclockCalculator(@Nonnull GT_Recipe recipe) {
                return new GT_OverclockCalculator().setRecipeEUt(recipe.mEUt)
                    .setParallel(multiplier)
                    .setDuration((int) (recipe.mDuration / pow(2, currentCircuitNumber)))
                    .setAmperage(1)
                    .setEUt(wirelessEUtWithoutParallel)
                    .setDurationDecreasePerOC(0)
                    .setEUtIncreasePerOC(0);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                // Get circuit damage, clamp it and then use it later for overclocking.
                ItemStack circuit = mInputBusses.get(0)
                    .getStackInSlot(0);
                if (circuit != null) {
                    currentCircuitNumber = clamp(circuit.getItemDamage(), 0, 24);
                } else {
                    currentCircuitNumber = 0;
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(() -> {
            ItemStack controllerStack = getControllerSlot();
            if (controllerStack != null && controllerStack.getItem() instanceof GT_IntegratedCircuit_Item) {
                multiplier = controllerStack.stackSize * max(1, controllerStack.getItemDamage());
            }
            return multiplier;
        });
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        // The voltage is only used for recipe finding
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(false);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_CircuitAssemblyMulti(mName);
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {

        super.onPreTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide() && (aTick == 1)) {
            // Adds player to the wireless network if they do not already exist on it.
            ownerUUID = String.valueOf(processInitialSettings(aBaseMetaTileEntity));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("eMultiplier", multiplier);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        multiplier = aNBT.getInteger("eMultiplier");
        super.loadNBTData(aNBT);
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        CraftingTier = -1;

        // Check the main structure
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 1, 1, 0)) {
            return false;
        }

        return (mMaintenanceHatches.size() == 1);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 1, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 1, 1, 0, elementBudget, env, false, true);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][DIM_TRANS_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_ON)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][DIM_TRANS_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_OFF)
                .extFacing()
                .build() };
        }

        return new ITexture[] { casingTexturePages[0][DIM_TRANS_CASING] };
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }
}
