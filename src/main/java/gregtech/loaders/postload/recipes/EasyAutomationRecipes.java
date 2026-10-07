package gregtech.loaders.postload.recipes;

import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.IItemContainer;
import tectech.thing.CustomItemList;

/**
 * Custom (not upstream): cheap assembler recipes for ME hatches and wireless energy hatches, so that automation and
 * wireless power are available as soon as the base hatches are.
 */
public class EasyAutomationRecipes implements Runnable {

    /** Indexed by voltage tier, ULV to UXV. ULV has no sensor, so it uses the ULV coil. */
    private static final IItemContainer[] SENSORS = { ItemList.ULV_Coil, ItemList.Sensor_LV, ItemList.Sensor_MV,
        ItemList.Sensor_HV, ItemList.Sensor_EV, ItemList.Sensor_IV, ItemList.Sensor_LuV, ItemList.Sensor_ZPM,
        ItemList.Sensor_UV, ItemList.Sensor_UHV, ItemList.Sensor_UEV, ItemList.Sensor_UIV, ItemList.Sensor_UMV,
        ItemList.Sensor_UXV };

    private static final IItemContainer[] ENERGY_HATCHES = { ItemList.Hatch_Energy_ULV, ItemList.Hatch_Energy_LV,
        ItemList.Hatch_Energy_MV, ItemList.Hatch_Energy_HV, ItemList.Hatch_Energy_EV, ItemList.Hatch_Energy_IV,
        ItemList.Hatch_Energy_LuV, ItemList.Hatch_Energy_ZPM, ItemList.Hatch_Energy_UV, ItemList.Hatch_Energy_UHV,
        ItemList.Hatch_Energy_UEV, ItemList.Hatch_Energy_UIV, ItemList.Hatch_Energy_UMV, ItemList.Hatch_Energy_UXV };

    private static final IItemContainer[] DYNAMO_HATCHES = { ItemList.Hatch_Dynamo_ULV, ItemList.Hatch_Dynamo_LV,
        ItemList.Hatch_Dynamo_MV, ItemList.Hatch_Dynamo_HV, ItemList.Hatch_Dynamo_EV, ItemList.Hatch_Dynamo_IV,
        ItemList.Hatch_Dynamo_LuV, ItemList.Hatch_Dynamo_ZPM, ItemList.Hatch_Dynamo_UV, ItemList.Hatch_Dynamo_UHV,
        ItemList.Hatch_Dynamo_UEV, ItemList.Hatch_Dynamo_UIV, ItemList.Hatch_Dynamo_UMV, ItemList.Hatch_Dynamo_UXV };

    private static final IItemContainer[] WIRELESS_HATCHES = { ItemList.Wireless_Hatch_Energy_ULV,
        ItemList.Wireless_Hatch_Energy_LV, ItemList.Wireless_Hatch_Energy_MV, ItemList.Wireless_Hatch_Energy_HV,
        ItemList.Wireless_Hatch_Energy_EV, ItemList.Wireless_Hatch_Energy_IV, ItemList.Wireless_Hatch_Energy_LuV,
        ItemList.Wireless_Hatch_Energy_ZPM, ItemList.Wireless_Hatch_Energy_UV, ItemList.Wireless_Hatch_Energy_UHV,
        ItemList.Wireless_Hatch_Energy_UEV, ItemList.Wireless_Hatch_Energy_UIV, ItemList.Wireless_Hatch_Energy_UMV,
        ItemList.Wireless_Hatch_Energy_UXV };

    private static final IItemContainer[] WIRELESS_DYNAMOS = { ItemList.Wireless_Dynamo_Energy_ULV,
        ItemList.Wireless_Dynamo_Energy_LV, ItemList.Wireless_Dynamo_Energy_MV, ItemList.Wireless_Dynamo_Energy_HV,
        ItemList.Wireless_Dynamo_Energy_EV, ItemList.Wireless_Dynamo_Energy_IV, ItemList.Wireless_Dynamo_Energy_LuV,
        ItemList.Wireless_Dynamo_Energy_ZPM, ItemList.Wireless_Dynamo_Energy_UV, ItemList.Wireless_Dynamo_Energy_UHV,
        ItemList.Wireless_Dynamo_Energy_UEV, ItemList.Wireless_Dynamo_Energy_UIV, ItemList.Wireless_Dynamo_Energy_UMV,
        ItemList.Wireless_Dynamo_Energy_UXV };

    // Multi-amp hatches exist from EV to UXV.
    private static final IItemContainer[] ENERGY_HATCHES_4A = { CustomItemList.eM_energyMulti4_EV,
        CustomItemList.eM_energyMulti4_IV, CustomItemList.eM_energyMulti4_LuV, CustomItemList.eM_energyMulti4_ZPM,
        CustomItemList.eM_energyMulti4_UV, CustomItemList.eM_energyMulti4_UHV, CustomItemList.eM_energyMulti4_UEV,
        CustomItemList.eM_energyMulti4_UIV, CustomItemList.eM_energyMulti4_UMV, CustomItemList.eM_energyMulti4_UXV };

    private static final IItemContainer[] ENERGY_HATCHES_16A = { CustomItemList.eM_energyMulti16_EV,
        CustomItemList.eM_energyMulti16_IV, CustomItemList.eM_energyMulti16_LuV, CustomItemList.eM_energyMulti16_ZPM,
        CustomItemList.eM_energyMulti16_UV, CustomItemList.eM_energyMulti16_UHV, CustomItemList.eM_energyMulti16_UEV,
        CustomItemList.eM_energyMulti16_UIV, CustomItemList.eM_energyMulti16_UMV, CustomItemList.eM_energyMulti16_UXV };

    private static final IItemContainer[] ENERGY_HATCHES_64A = { CustomItemList.eM_energyMulti64_EV,
        CustomItemList.eM_energyMulti64_IV, CustomItemList.eM_energyMulti64_LuV, CustomItemList.eM_energyMulti64_ZPM,
        CustomItemList.eM_energyMulti64_UV, CustomItemList.eM_energyMulti64_UHV, CustomItemList.eM_energyMulti64_UEV,
        CustomItemList.eM_energyMulti64_UIV, CustomItemList.eM_energyMulti64_UMV, CustomItemList.eM_energyMulti64_UXV };

    private static final IItemContainer[] WIRELESS_HATCHES_4A = { CustomItemList.eM_energyWirelessMulti4_EV,
        CustomItemList.eM_energyWirelessMulti4_IV, CustomItemList.eM_energyWirelessMulti4_LuV,
        CustomItemList.eM_energyWirelessMulti4_ZPM, CustomItemList.eM_energyWirelessMulti4_UV,
        CustomItemList.eM_energyWirelessMulti4_UHV, CustomItemList.eM_energyWirelessMulti4_UEV,
        CustomItemList.eM_energyWirelessMulti4_UIV, CustomItemList.eM_energyWirelessMulti4_UMV,
        CustomItemList.eM_energyWirelessMulti4_UXV };

    private static final IItemContainer[] WIRELESS_HATCHES_16A = { CustomItemList.eM_energyWirelessMulti16_EV,
        CustomItemList.eM_energyWirelessMulti16_IV, CustomItemList.eM_energyWirelessMulti16_LuV,
        CustomItemList.eM_energyWirelessMulti16_ZPM, CustomItemList.eM_energyWirelessMulti16_UV,
        CustomItemList.eM_energyWirelessMulti16_UHV, CustomItemList.eM_energyWirelessMulti16_UEV,
        CustomItemList.eM_energyWirelessMulti16_UIV, CustomItemList.eM_energyWirelessMulti16_UMV,
        CustomItemList.eM_energyWirelessMulti16_UXV };

    private static final IItemContainer[] WIRELESS_HATCHES_64A = { CustomItemList.eM_energyWirelessMulti64_EV,
        CustomItemList.eM_energyWirelessMulti64_IV, CustomItemList.eM_energyWirelessMulti64_LuV,
        CustomItemList.eM_energyWirelessMulti64_ZPM, CustomItemList.eM_energyWirelessMulti64_UV,
        CustomItemList.eM_energyWirelessMulti64_UHV, CustomItemList.eM_energyWirelessMulti64_UEV,
        CustomItemList.eM_energyWirelessMulti64_UIV, CustomItemList.eM_energyWirelessMulti64_UMV,
        CustomItemList.eM_energyWirelessMulti64_UXV };

    @Override
    public void run() {
        addMEHatchRecipes();
        addWirelessRecipes();
    }

    private static void addMEHatchRecipes() {
        addUpgrade(
            ItemList.Hatch_Maintenance,
            ItemList.Robot_Arm_IV,
            CustomItemList.hatch_CreativeMaintenance,
            TierEU.RECIPE_IV);
        addUpgrade(
            ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly,
            ItemList.Super_Tank_IV,
            ItemList.Hatch_CraftingInput_Bus_ME,
            TierEU.RECIPE_LuV);
        addUpgrade(
            ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly,
            ItemList.Sensor_IV,
            ItemList.Hatch_CraftingInput_Bus_Slave,
            TierEU.RECIPE_LuV);
        addUpgrade(ItemList.Hatch_Input_Bus_ME, ItemList.Super_Tank_IV, ItemList.Hatch_Input_ME, TierEU.RECIPE_LuV);
        addUpgrade(
            ItemList.Hatch_Input_Bus_ME_Advanced,
            ItemList.Super_Tank_IV,
            ItemList.Hatch_Input_ME_Advanced,
            TierEU.RECIPE_LuV);
    }

    private static void addWirelessRecipes() {
        for (int tier = 0; tier < SENSORS.length; tier++) {
            addWireless(ENERGY_HATCHES[tier], SENSORS[tier], WIRELESS_HATCHES[tier]);
            addWireless(DYNAMO_HATCHES[tier], SENSORS[tier], WIRELESS_DYNAMOS[tier]);
        }
        for (int i = 0; i < ENERGY_HATCHES_4A.length; i++) {
            IItemContainer sensor = SENSORS[VoltageIndex.EV + i];
            addWireless(ENERGY_HATCHES_4A[i], sensor, WIRELESS_HATCHES_4A[i]);
            addWireless(ENERGY_HATCHES_16A[i], sensor, WIRELESS_HATCHES_16A[i]);
            addWireless(ENERGY_HATCHES_64A[i], sensor, WIRELESS_HATCHES_64A[i]);
        }
    }

    private static void addUpgrade(IItemContainer base, IItemContainer part, IItemContainer result, long eut) {
        GTValues.RA.stdBuilder()
            .itemInputs(base.get(1), part.get(1))
            .itemOutputs(result.get(1))
            .duration(10 * SECONDS)
            .eut(eut)
            .addTo(assemblerRecipes);
    }

    private static void addWireless(IItemContainer hatch, IItemContainer sensor, IItemContainer wireless) {
        GTValues.RA.stdBuilder()
            .itemInputs(hatch.get(1), sensor.get(1))
            .itemOutputs(wireless.get(1))
            .fluidInputs(Materials.Lubricant.getFluid(250))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(assemblerRecipes);
    }
}
