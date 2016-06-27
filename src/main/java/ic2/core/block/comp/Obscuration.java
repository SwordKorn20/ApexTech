/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package ic2.core.block.comp;

import ic2.api.event.RetextureEvent;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.state.BlockStateUtil;
import ic2.core.item.tool.ItemObscurator;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.Util;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Obscuration
extends TileEntityComponent {
    private final Runnable changeHandler;
    private ObscurationData[] dataMap;

    public Obscuration(TileEntityBlock parent, Runnable changeHandler) {
        super(parent);
        this.changeHandler = changeHandler;
    }

    @Override
    public void readFromNbt(NBTTagCompound nbt) {
        if (nbt.hasNoTags()) {
            return;
        }
        for (EnumFacing facing : EnumFacing.VALUES) {
            String variant;
            byte rawSide;
            Block block;
            IBlockState state;
            NBTTagCompound cNbt;
            if (!nbt.hasKey(facing.getName(), 10) || (block = Util.getBlock((cNbt = nbt.getCompoundTag(facing.getName())).getString("block"))) == null || (state = BlockStateUtil.getState(block, variant = cNbt.getString("variant"))) == null || (rawSide = cNbt.getByte("side")) < 0 || rawSide >= EnumFacing.VALUES.length) continue;
            EnumFacing side = EnumFacing.VALUES[rawSide];
            int[] colorMultipliers = ItemObscurator.internColorMultipliers(cNbt.getIntArray("colorMuls"));
            ObscurationData data = new ObscurationData(state, variant, side, colorMultipliers);
            if (this.dataMap == null) {
                this.dataMap = new ObscurationData[EnumFacing.VALUES.length];
            }
            this.dataMap[facing.ordinal()] = data.intern();
        }
    }

    @Override
    public NBTTagCompound writeToNbt() {
        if (this.dataMap == null) {
            return null;
        }
        NBTTagCompound ret = new NBTTagCompound();
        for (EnumFacing facing : EnumFacing.VALUES) {
            ObscurationData data = this.dataMap[facing.ordinal()];
            if (data == null) continue;
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("block", Util.getName(data.state.getBlock()).toString());
            nbt.setString("variant", data.variant);
            nbt.setByte("side", (byte)data.side.ordinal());
            nbt.setIntArray("colorMuls", data.colorMultipliers);
            ret.setTag(facing.getName(), (NBTBase)nbt);
        }
        return ret;
    }

    public boolean applyObscuration(EnumFacing side, ObscurationData data) {
        if (this.dataMap != null && data.equals(this.dataMap[side.ordinal()])) {
            return false;
        }
        if (this.dataMap == null) {
            this.dataMap = new ObscurationData[EnumFacing.VALUES.length];
        }
        this.dataMap[side.ordinal()] = data.intern();
        this.changeHandler.run();
        return true;
    }

    public void clear() {
        this.dataMap = null;
        this.changeHandler.run();
    }

    public ObscurationData[] getRenderState() {
        if (this.dataMap == null) {
            return null;
        }
        return Arrays.copyOf(this.dataMap, this.dataMap.length);
    }

    public static class ObscurationData {
        public final IBlockState state;
        public final String variant;
        public final EnumFacing side;
        public final int[] colorMultipliers;

        public ObscurationData(IBlockState state, String variant, EnumFacing side, int[] colorMultipliers) {
            this.state = state;
            this.variant = variant;
            this.side = side;
            this.colorMultipliers = colorMultipliers;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ObscurationData)) {
                return false;
            }
            ObscurationData o = (ObscurationData)obj;
            return o.state.equals((Object)this.state) && o.variant.equals(this.variant) && o.side == this.side && Arrays.equals(o.colorMultipliers, this.colorMultipliers);
        }

        public int hashCode() {
            return (this.state.hashCode() * 7 + this.side.ordinal()) * 23;
        }

        public ObscurationData intern() {
            return this;
        }
    }

    public static class ObscurationComponentEventHandler {
        public static void init() {
            new ObscurationComponentEventHandler();
        }

        private ObscurationComponentEventHandler() {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }

        @SubscribeEvent
        public void onObscuration(RetextureEvent event) {
            if (event.state.getBlock() != BlockName.te.getInstance()) {
                return;
            }
            TileEntity teRaw = event.getWorld().getTileEntity(event.pos);
            if (!(teRaw instanceof TileEntityBlock)) {
                return;
            }
            Obscuration obscuration = (Obscuration)((TileEntityBlock)teRaw).getComponent(Obscuration.class);
            if (obscuration == null) {
                return;
            }
            ObscurationData data = new ObscurationData(event.refState, event.refVariant, event.refSide, event.refColorMultipliers);
            if (obscuration.applyObscuration(event.side, data)) {
                event.applied = true;
                event.setCanceled(true);
            }
        }
    }

}

