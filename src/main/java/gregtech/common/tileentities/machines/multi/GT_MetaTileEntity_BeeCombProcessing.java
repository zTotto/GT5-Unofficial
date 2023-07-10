package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.GT_HatchElement.Energy;
import static gregtech.api.enums.GT_HatchElement.InputBus;
import static gregtech.api.enums.GT_HatchElement.InputHatch;
import static gregtech.api.enums.GT_HatchElement.Maintenance;
import static gregtech.api.enums.GT_HatchElement.Muffler;
import static gregtech.api.enums.GT_HatchElement.OutputBus;
import static gregtech.api.enums.GT_HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY_GLOW;
import static gregtech.api.util.GT_StructureUtility.*;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.*;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_ExtendedPowerMultiBlockBase;
import gregtech.api.multitileentity.multiblock.casing.Glasses;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.CombType;

public class GT_MetaTileEntity_BeeCombProcessing
    extends GT_MetaTileEntity_ExtendedPowerMultiBlockBase<GT_MetaTileEntity_BeeCombProcessing>
    implements ISurvivalConstructable {

    protected static final String STRUCTURE_PIECE_MAIN = "main";

    private static final int CASING_INDEX1 = GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings8, 7);
    private static final int CASING_INDEX2 = GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings4, 1);

    public GT_MetaTileEntity_BeeCombProcessing(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_BeeCombProcessing(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_BeeCombProcessing(this.mName);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, 8, 9, 1) && mMaintenanceHatches.size() <= 1
            && !mMufflerHatches.isEmpty();
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_BeeCombProcessing> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private static final IStructureDefinition<GT_MetaTileEntity_BeeCombProcessing> STRUCTURE_DEFINITION = StructureDefinition
        .<GT_MetaTileEntity_BeeCombProcessing>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            transpose(
                new String[][] {
                    { "           ", "           ", "       WWW ", "       WWW ", "           ", "           " },
                    { "           ", "       sss ", "      sppps", "      sppps", "       sss ", "           " },
                    { "           ", "       sss ", "      s   s", "      s   s", "       sss ", "           " },
                    { "           ", "       sss ", "      sppps", "      sppps", "       sss ", "           " },
                    { "           ", "       sss ", "      s   s", "      s   s", "       sss ", "           " },
                    { "           ", "       sss ", "      sppps", "      sppps", "       sss ", "           " },
                    { "iiiiii     ", "iIIIIiisssi", "iIIIIis   s", "iIIIIis   s", "iIIIIiisssi", "iiiiii     " },
                    { "iggggi     ", "gt  t isssi", "g xx  sppps", "g xx  sppps", "gt  t isssi", "iggggi     " },
                    { "iggggi     ", "gt  t isssi", "g xx  s   s", "g xx  s   s", "gt  t isssi", "iggggi     " },
                    { "iggggi     ", "gt  t is~si", "g xx  spppO", "g xx  spppO", "gt  t isssi", "iggggi     " },
                    { "iggggi     ", "gt  t isssi", "g xx  s   O", "g xx  s   O", "gt  t isssi", "iggggi     " },
                    { "EEEEEE     ", "EEEEEEEEEEE", "EEEEEEEEEEE", "EEEEEEEEEEE", "EEEEEEEEEEE", "EEEEEE     " } }))
        .addElement('i', ofBlock(GregTech_API.sBlockCasings8, 7))
        .addElement('s', ofBlock(GregTech_API.sBlockCasings4, 1))
        .addElement('g', Glasses.chainAllGlasses())
        .addElement('x', ofBlock(GregTech_API.sBlockCasings2, 3))
        .addElement('p', ofBlock(GregTech_API.sBlockCasings2, 15))
        .addElement('t', ofFrame(Materials.TungstenSteel))
        .addElement(
            'E',
            buildHatchAdder(GT_MetaTileEntity_BeeCombProcessing.class).atLeast(Energy, Maintenance)
                .casingIndex(CASING_INDEX1)
                .dot(1)
                .buildAndChain(GregTech_API.sBlockCasings8, 7))
        .addElement(
            'I',
            buildHatchAdder(GT_MetaTileEntity_BeeCombProcessing.class).atLeast(InputBus)
                .casingIndex(CASING_INDEX1)
                .dot(2)
                .buildAndChain(GregTech_API.sBlockCasings8, 7))
        .addElement(
            'W',
            buildHatchAdder(GT_MetaTileEntity_BeeCombProcessing.class).atLeast(InputHatch, Muffler)
                .casingIndex(CASING_INDEX2)
                .dot(3)
                .buildAndChain(GregTech_API.sBlockCasings4, 1))
        .addElement(
            'O',
            buildHatchAdder(GT_MetaTileEntity_BeeCombProcessing.class).atLeast(OutputBus, OutputHatch)
                .casingIndex(CASING_INDEX2)
                .dot(4)
                .buildAndChain(GregTech_API.sBlockCasings4, 1))
        .build();

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Comb Processor")
            .addInfo("Controller Block for the Integrated Ore Factory")
            .addInfo("It is OP. I mean ore processor.")
            .addInfo("Do all ore procession in one step.")
            .addInfo("Can process up to 1024 ores per time.")
            .addInfo("Every ore costs 30EU/t, 2L lubricant, 200L distilled water.")
            .addInfo("Process time is depend on mode.")
            .addInfo("Use a screwdriver to switch mode.")
            .addInfo("Sneak click with screwdriver to void the stone dusts.")
            .addSeparator()
            .beginStructureBlock(6, 12, 11, false)
            .addController("The third layer")
            .addStructureInfo("128 advanced iridium plated machine casing")
            .addStructureInfo("105 clean stainless steel machine casing")
            .addStructureInfo("48 reinforced glass")
            .addStructureInfo("30 tungstensteel pipe casing")
            .addStructureInfo("16 tungstensteel frame box")
            .addStructureInfo("16 steel gear box casing")
            .addEnergyHatch("Button Casing", 1)
            .addMaintenanceHatch("Button Casing", 1)
            .addInputBus("Input ore/crushed ore", 2)
            .addInputHatch("Input lubricant/distilled water/washing chemicals", 3)
            .addMufflerHatch("Output Pollution", 3)
            .addOutputBus("Output products", 4)
            .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX2),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX2), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX2) };
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        List<ItemStack> tInput = getStoredInputs();
        List<FluidStack> tInputFluid = getStoredFluids();
        for (ItemStack comb : tInput) {
            if (getCombFromItemStack(comb) instanceof CombType) {
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .sendChatMsg(new ChatComponentText("ALELUJA"));
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public CombType getCombFromItemStack(ItemStack stack) {
        return CombType.valueOf(stack.getItemDamage());
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
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> !r.isUpsideDown() && !f.isVerticallyFliped();
    }

    @Override
    public void construct(ItemStack itemStack, boolean b) {
        buildPiece(STRUCTURE_PIECE_MAIN, itemStack, b, 8, 9, 1);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 8, 9, 1, elementBudget, env, false, true);
    }
}
