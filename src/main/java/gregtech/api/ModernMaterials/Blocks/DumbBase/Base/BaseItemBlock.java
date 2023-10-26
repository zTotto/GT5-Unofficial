package gregtech.api.ModernMaterials.Blocks.DumbBase.Base;

import static gregtech.api.ModernMaterials.ModernMaterialUtilities.tooltipGenerator;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.ModernMaterials.Blocks.BlocksEnum;
import gregtech.api.ModernMaterials.ModernMaterial;
import gregtech.api.ModernMaterials.ModernMaterialUtilities;

public class BaseItemBlock extends ItemBlock {

    public BaseItemBlock(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(GregTech_API.TAB_GREGTECH_ORES); // todo add new tabs.
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    // Tooltip information.
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@NotNull ItemStack itemStack, EntityPlayer player, List<String> tooltipList,
        boolean aF3_H) {

        final ModernMaterial material = ModernMaterialUtilities.materialIDToMaterial.get(itemStack.getItemDamage());

        for (String line : tooltipGenerator((BaseItemBlock) itemStack.getItem(), material)) {
            tooltipList.add(line);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        final ModernMaterial associatedMaterial = ModernMaterialUtilities.materialIDToMaterial
            .get(itemStack.getItemDamage());

        return getBlockEnum().getLocalisedName(associatedMaterial);
    }

    public BlocksEnum getBlockEnum() {
        return BlocksEnum.FrameBox;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        final ModernMaterial associatedMaterial = ModernMaterialUtilities.materialIDToMaterial
            .get(itemStack.getItemDamage());

        return associatedMaterial.getColor()
            .getRGB();
    }

}