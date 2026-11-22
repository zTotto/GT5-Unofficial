package gregtech.nei.transfer;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentTranslation;

import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.value.sync.SyncHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.nei.transfer.RecipeTransferRequest.Mode;

/**
 * Carries NEI recipe transfer requests from the client to the machine and carries them out on the server. It also
 * tells the client what the machine is making, which crafting chains count as available.
 * <p>
 * It only exists while the player has the machine GUI open, and it only moves items between that player's inventory
 * and that machine, so a client cannot do anything it could not do by hand.
 */
public class RecipeTransferSyncHandler extends SyncHandler<RecipeTransferSyncHandler> {

    public static final String KEY = "neiRecipeTransfer";
    private static final int TRANSFER = 0;
    private static final int PROCESSING = 1;
    private static final int COLLECT_OUTPUTS = 2;
    private static final ItemStack[] IDLE = new ItemStack[0];

    private final MTEBasicMachine machine;
    /** On the server what was last sent, on the client what was received. */
    private ItemStack[] processing = IDLE;

    public RecipeTransferSyncHandler(MTEBasicMachine machine) {
        this.machine = machine;
        allowC2S();
    }

    @SideOnly(Side.CLIENT)
    void requestTransfer(RecipeTransferRequest request) {
        syncToServer(TRANSFER, request::write);
    }

    /** Asks the server to move the machine outputs into the player inventory. */
    @SideOnly(Side.CLIENT)
    void requestOutputCollection() {
        syncToServer(COLLECT_OUTPUTS);
    }

    /**
     * Outputs of the craft the machine is processing, as last received, empty if it is idle. Safe to call after the
     * GUI is closed, which NEI can do from its own screens.
     */
    @SideOnly(Side.CLIENT)
    ItemStack[] getProcessing() {
        return processing;
    }

    private ItemStack[] processingOnServer() {
        return machine.mMaxProgresstime > 0 ? machine.mOutputItems : IDLE;
    }

    @Override
    public void detectAndSendChanges(boolean init) {
        ItemStack[] current = processingOnServer();
        if (!init && sameStacks(current, processing)) return;

        processing = copy(current);
        syncToClient(PROCESSING, buffer -> {
            buffer.writeVarIntToBuffer(processing.length);
            for (ItemStack stack : processing) NetworkUtils.writeItemStack(buffer, stack);
        });
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {
        if (id != PROCESSING) return;
        ItemStack[] stacks = new ItemStack[buf.readVarIntFromBuffer()];
        for (int i = 0; i < stacks.length; i++) stacks[i] = NetworkUtils.readItemStack(buf);
        processing = stacks;
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) {
        // Only machines that process recipes; others share this GUI but may have slot rules of their own
        if (machine.getRecipeMap() == null) return;

        EntityPlayer player = getSyncManager().getPlayer();
        if (id == COLLECT_OUTPUTS) {
            MachineRecipeTransfer.collectOutputs(machine, player);
            return;
        }
        if (id != TRANSFER) return;
        RecipeTransferRequest request = RecipeTransferRequest.read(buf);
        if (request == null) return;

        MachineRecipeTransfer transfer = new MachineRecipeTransfer(machine, player, request);
        switch (transfer.result()
            .outcome()) {
            case TRANSFERRED -> transfer.apply();
            case NO_ROOM_FOR_RETURNED_ITEMS -> player
                .addChatMessage(new ChatComponentTranslation("GT5U.machines.nei_transfer.no_room"));
            case OTHER_FLUID_IN_TANK -> player
                .addChatMessage(new ChatComponentTranslation("GT5U.machines.nei_transfer.other_fluid"));
            // A crafting chain that loads nothing still collects what the machine made; the "+" button changes nothing
            case NOTHING_TO_ADD, ALREADY_LOADED -> {
                if (request.mode() == Mode.UP_TO) MachineRecipeTransfer.collectOutputs(machine, player);
            }
        }
    }

    private static boolean sameStacks(ItemStack[] a, ItemStack[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (!ItemStack.areItemStacksEqual(a[i], b[i])) return false;
        }
        return true;
    }

    private static ItemStack[] copy(ItemStack[] stacks) {
        return Arrays.stream(stacks)
            .map(stack -> stack == null ? null : stack.copy())
            .toArray(ItemStack[]::new);
    }
}
