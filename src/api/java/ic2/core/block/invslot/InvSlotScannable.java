/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.network.IRpcProvider;
import ic2.core.network.Rpc;
import ic2.core.network.RpcHandler;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.uu.UuGraph;
import ic2.core.uu.UuIndex;
import java.util.concurrent.TimeUnit;
import net.minecraft.item.ItemStack;

public class InvSlotScannable
extends InvSlotConsumable {
    public InvSlotScannable(TileEntityInventory base1, String name1, int count) {
        super(base1, name1, count);
        this.setStackSizeLimit(1);
    }

    @Override
    public boolean accepts(ItemStack stack) {
        if (IC2.platform.isSimulating()) {
            return InvSlotScannable.isValidStack(stack);
        }
        Rpc rpc = RpcHandler.run(ServerScannableCheck.class, new Object[]{stack});
        try {
            return (Boolean)rpc.get(1, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            IC2.log.debug(LogCategory.Block, e, "Scannability check failed.");
            return false;
        }
    }

    private static boolean isValidStack(ItemStack stack) {
        return (stack = UuGraph.find(stack)) != null && UuIndex.instance.get(stack) < Double.POSITIVE_INFINITY;
    }

    static {
        RpcHandler.registerProvider(new ServerScannableCheck());
    }

    public static class ServerScannableCheck
    implements IRpcProvider<Boolean> {
        @Override
        public /* varargs */ Boolean executeRpc(Object ... args) {
            ItemStack stack = (ItemStack)args[0];
            return InvSlotScannable.isValidStack(stack);
        }
    }

}

