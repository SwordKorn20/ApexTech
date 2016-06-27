/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Biomes
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.kineticgenerator.tileentity;

import ic2.api.energy.tile.IKineticSource;
import ic2.api.item.IKineticRotor;
import ic2.api.tile.IRotorProvider;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotConsumableKineticRotor;
import ic2.core.block.kineticgenerator.container.ContainerWaterKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiWaterKineticGenerator;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.network.NetworkManager;
import ic2.core.util.ConfigUtil;
import ic2.core.util.SideGateway;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityWaterKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IRotorProvider,
IHasGui {
    private boolean firsttick = true;
    private int updateTicker;
    public InvSlotConsumableClass rotorSlot;
    public short type = 0;
    private boolean rightFacing;
    private int distanceToNormalBiome;
    private int crossSection;
    private int obstructedCrossSection;
    private int waterFlow;
    private final double efficiencyRollOffExponent = 2.0;
    private static final float outputModifier = 0.2f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/water");
    private float angle = 0.0f;
    private float rotationSpeed;
    private static final float rotationModifier = 0.1f;
    private long lastcheck;

    public TileEntityWaterKineticGenerator() {
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
        this.rotorSlot = new InvSlotConsumableKineticRotor(this, "rotorslot", InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, IKineticRotor.GearboxType.WATER);
    }

    private int getTickRate() {
        return 20;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.firsttick) {
            this.updateSeaInfo();
            this.firsttick = false;
        }
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        if (this.type == 0) {
            Biome biome = this.worldObj.getBiomeGenForCoords(this.pos);
            this.type = BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.OCEAN) ? 1 : (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.RIVER) ? 2 : -1);
        }
        if (this.type == -1) {
            return;
        }
        boolean needsInvUpdate = false;
        if (!this.rotorSlot.isEmpty()) {
            if (this.checkSpace(1, true) == 0) {
                if (!this.getActive()) {
                    this.setActive(true);
                    needsInvUpdate = true;
                }
            } else if (this.getActive()) {
                this.setActive(false);
                needsInvUpdate = true;
            }
        } else if (this.getActive()) {
            this.setActive(false);
            needsInvUpdate = true;
        }
        if (this.getActive()) {
            this.crossSection = this.getRotorDiameter() / 2 * 2 * 2 + 1;
            this.crossSection *= this.crossSection;
            this.obstructedCrossSection = this.checkSpace(this.getRotorDiameter() * 3, false);
            if (this.obstructedCrossSection > 0 && this.obstructedCrossSection <= (this.getRotorDiameter() + 1) / 2) {
                this.obstructedCrossSection = 0;
            }
            int rotordamage = 0;
            if (this.obstructedCrossSection < 0) {
                boolean update = this.rotationSpeed != 0.0f;
                this.rotationSpeed = 0.0f;
                this.waterFlow = 0;
                if (update) {
                    IC2.network.get(true).updateTileEntityField(this, "rotationSpeed");
                }
            } else if (this.type == 1) {
                float diff = (float)Math.sin((double)this.worldObj.getWorldTime() * 3.141592653589793 / 6000.0);
                diff *= Math.abs(diff);
                this.rotationSpeed = (float)((double)(diff * (float)this.distanceToNormalBiome / 100.0f) * (1.0 - Math.pow((double)this.obstructedCrossSection / (double)this.crossSection, 2.0)));
                this.waterFlow = (int)(this.rotationSpeed * 3000.0f);
                if (this.rightFacing) {
                    this.rotationSpeed *= -1.0f;
                }
                IC2.network.get(true).updateTileEntityField(this, "rotationSpeed");
                this.waterFlow = (int)((float)this.waterFlow * this.getEfficiency());
                rotordamage = 2;
            } else if (this.type == 2) {
                this.rotationSpeed = Math.max(Math.min(this.distanceToNormalBiome, 20), 50) / 50;
                this.waterFlow = (int)(this.rotationSpeed * 1000.0f);
                if (this.getFacing() == EnumFacing.EAST || this.getFacing() == EnumFacing.NORTH) {
                    this.rotationSpeed *= -1.0f;
                }
                IC2.network.get(true).updateTileEntityField(this, "rotationSpeed");
                this.waterFlow = (int)((float)this.waterFlow * (this.getEfficiency() * (1.0f - 0.3f * this.worldObj.rand.nextFloat() - 0.1f * ((float)this.obstructedCrossSection / (float)this.crossSection))));
                rotordamage = 1;
            }
            this.rotorSlot.damage(rotordamage, false);
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("rotationSpeed");
        ret.add("rotorSlot");
        return ret;
    }

    @Override
    public int getRotorDiameter() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            if (this.type == 1) {
                return ((IKineticRotor)stack.getItem()).getDiameter(stack);
            }
            return (((IKineticRotor)stack.getItem()).getDiameter(stack) + 1) * 2 / 3;
        }
        return 0;
    }

    public int checkSpace(int length, boolean onlyrotor) {
        int box = this.getRotorDiameter() / 2;
        int lentemp = 0;
        if (onlyrotor) {
            length = 1;
            lentemp = length + 1;
        } else {
            box *= 2;
        }
        EnumFacing fwdDir = this.getFacing();
        EnumFacing rightDir = fwdDir.rotateAround(EnumFacing.DOWN.getAxis());
        int ret = 0;
        int xCoord = this.pos.getX();
        int yCoord = this.pos.getY();
        int zCoord = this.pos.getZ();
        for (int up = - box; up <= box; ++up) {
            int y = yCoord + up;
            for (int right = - box; right <= box; ++right) {
                boolean occupied = false;
                for (int fwd = lentemp - length; fwd <= length; ++fwd) {
                    int z;
                    int x = xCoord + fwd * fwdDir.getFrontOffsetX() + right * rightDir.getFrontOffsetX();
                    BlockPos pos = new BlockPos(x, y, z = zCoord + fwd * fwdDir.getFrontOffsetZ() + right * rightDir.getFrontOffsetZ());
                    Block block = this.worldObj.getBlockState(pos).getBlock();
                    if (block == Blocks.WATER) continue;
                    occupied = true;
                    if (up == 0 && right == 0 && fwd == 0 || !(this.worldObj.getTileEntity(pos) instanceof TileEntityWaterKineticGenerator) || onlyrotor) continue;
                    return -1;
                }
                if (!occupied) continue;
                ++ret;
            }
        }
        return ret;
    }

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer player) {
        return new ContainerWaterKineticGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiWaterKineticGenerator((ContainerWaterKineticGenerator)this.getGuiContainer(player));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void setFacing(EnumFacing side) {
        super.setFacing(side);
        this.updateSeaInfo();
    }

    public void updateSeaInfo() {
        EnumFacing facing = this.getFacing();
        for (int i = 1; i < 200; ++i) {
            Biome biometmp = this.worldObj.getBiomeGenForCoords(this.pos.offset(facing, i));
            if (!this.isValidBiome(biometmp)) {
                this.distanceToNormalBiome = i;
                this.rightFacing = true;
                return;
            }
            biometmp = this.worldObj.getBiomeGenForCoords(this.pos.offset(facing, i));
            if (this.isValidBiome(biometmp)) continue;
            this.distanceToNormalBiome = i;
            this.rightFacing = false;
            return;
        }
        this.distanceToNormalBiome = 200;
        this.rightFacing = true;
    }

    public boolean isValidBiome(Biome biome) {
        return biome == Biomes.DEEP_OCEAN || biome == Biomes.OCEAN || biome == Biomes.RIVER;
    }

    @Override
    public ResourceLocation getRotorRenderTexture() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getRotorRenderTexture(stack);
        }
        return new ResourceLocation(IC2.textureDomain, "textures/items/rotors/wood_rotor_model.png");
    }

    @Override
    public float getAngle() {
        if (this.rotationSpeed != 0.0f) {
            this.angle += (float)(System.currentTimeMillis() - this.lastcheck) * this.rotationSpeed * 0.1f;
            this.angle %= 360.0f;
        }
        this.lastcheck = System.currentTimeMillis();
        return this.angle;
    }

    @Override
    public int maxrequestkineticenergyTick(EnumFacing directionFrom) {
        return this.getKuOutput();
    }

    @Override
    public int requestkineticenergy(EnumFacing directionFrom, int requestkineticenergy) {
        if (directionFrom.getOpposite() == this.getFacing()) {
            return Math.min(requestkineticenergy, this.getKuOutput());
        }
        return 0;
    }

    public int getKuOutput() {
        if (this.getActive()) {
            return (int)Math.abs((float)this.waterFlow * outputModifier);
        }
        return 0;
    }

    public float getEfficiency() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getEfficiency(stack);
        }
        return 0.0f;
    }

    public String getRotorHealth() {
        if (!this.rotorSlot.isEmpty()) {
            return Localization.translate("ic2.WaterKineticGenerator.gui.rotorhealth", (int)(100.0f - (float)this.rotorSlot.get().getItemDamage() / (float)this.rotorSlot.get().getMaxDamage() * 100.0f));
        }
        return "";
    }

    @Override
    public void setActive(boolean active) {
        if (active != this.getActive()) {
            IC2.network.get(true).updateTileEntityField(this, "rotorSlot");
        }
        super.setActive(active);
    }
}

