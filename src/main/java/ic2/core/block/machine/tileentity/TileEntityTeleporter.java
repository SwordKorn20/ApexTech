/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.boss.EntityDragon
 *  net.minecraft.entity.boss.EntityWither
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityMinecart
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.tile.IEnergyStorage;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBlock;
import ic2.core.init.MainConfig;
import ic2.core.network.NetworkManager;
import ic2.core.util.ConfigUtil;
import ic2.core.util.SideGateway;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTeleporter
extends TileEntityBlock
implements INetworkTileEntityEventListener {
    private BlockPos target;
    private AudioSource audioSource = null;
    private int targetCheckTicker = IC2.random.nextInt(1024);
    private static final int EventTeleport = 0;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("targetX")) {
            this.target = new BlockPos(nbt.getInteger("targetX"), nbt.getInteger("targetY"), nbt.getInteger("targetZ"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.target != null) {
            nbt.setInteger("targetX", this.target.getX());
            nbt.setInteger("targetY", this.target.getY());
            nbt.setInteger("targetZ", this.target.getZ());
        }
        return nbt;
    }

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.worldObj.isBlockPowered(this.pos) && this.target != null) {
            this.setActive(true);
            List entitiesNearby = this.worldObj.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB((double)(this.pos.getX() - 1), (double)this.pos.getY(), (double)(this.pos.getZ() - 1), (double)(this.pos.getX() + 2), (double)(this.pos.getY() + 3), (double)(this.pos.getZ() + 2)));
            if (!entitiesNearby.isEmpty() && this.verifyTarget()) {
                double minDistanceSquared = Double.MAX_VALUE;
                Entity closestEntity = null;
                for (Entity entity : entitiesNearby) {
                    double distSquared;
                    if (entity.getRidingEntity() != null || (distSquared = this.pos.distanceSqToCenter(entity.posX, entity.posY, entity.posZ)) >= minDistanceSquared) continue;
                    minDistanceSquared = distSquared;
                    closestEntity = entity;
                }
                assert (closestEntity != null);
                this.teleport(closestEntity, Math.sqrt(minDistanceSquared));
            } else if (++this.targetCheckTicker % 1024 == 0) {
                this.verifyTarget();
            }
        } else {
            this.setActive(false);
        }
    }

    private boolean verifyTarget() {
        TileEntity te = this.worldObj.getTileEntity(this.target);
        if (te instanceof TileEntityTeleporter) {
            return true;
        }
        this.target = null;
        this.setActive(false);
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        if (this.getActive()) {
            this.spawnBlueParticles(2, this.pos);
        }
    }

    @Override
    protected int getComparatorInputOverride() {
        return this.target != null ? 15 : 0;
    }

    public void teleport(Entity user, double distance) {
        int weight = this.getWeightOf(user);
        if (weight == 0) {
            return;
        }
        int energyCost = (int)((double)weight * Math.pow(distance + 10.0, 0.7) * 5.0);
        if (energyCost > this.getAvailableEnergy()) {
            return;
        }
        this.consumeEnergy(energyCost);
        if (user instanceof EntityPlayerMP) {
            ((EntityPlayerMP)user).setPositionAndUpdate((double)this.target.getX() + 0.5, (double)this.target.getY() + 1.5 + user.getYOffset(), (double)this.target.getZ() + 0.5);
        } else {
            user.setPositionAndRotation((double)this.target.getX() + 0.5, (double)this.target.getY() + 1.5 + user.getYOffset(), (double)this.target.getZ() + 0.5, user.rotationYaw, user.rotationPitch);
        }
        IC2.network.get(true).initiateTileEntityEvent(this, 0, true);
        if (user instanceof EntityPlayer && distance >= 1000.0) {
            IC2.achievements.issueAchievement((EntityPlayer)user, "teleportFarAway");
        }
    }

    public void spawnBlueParticles(int n, BlockPos pos) {
        Random rnd = this.worldObj.rand;
        for (int i = 0; i < n; ++i) {
            this.worldObj.spawnParticle(EnumParticleTypes.REDSTONE, (double)((float)pos.getX() + rnd.nextFloat()), (double)((float)(pos.getY() + 1) + rnd.nextFloat()), (double)((float)pos.getZ() + rnd.nextFloat()), -1.0, 0.0, 1.0, new int[0]);
            this.worldObj.spawnParticle(EnumParticleTypes.REDSTONE, (double)((float)pos.getX() + rnd.nextFloat()), (double)((float)(pos.getY() + 2) + rnd.nextFloat()), (double)((float)pos.getZ() + rnd.nextFloat()), -1.0, 0.0, 1.0, new int[0]);
        }
    }

    public void consumeEnergy(int energy) {
        LinkedList<IEnergyStorage> energySources = new LinkedList<IEnergyStorage>();
        for (EnumFacing dir : EnumFacing.VALUES) {
            IEnergyStorage energySource;
            TileEntity target = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(target instanceof IEnergyStorage) || !(energySource = (IEnergyStorage)target).isTeleporterCompatible(dir.getOpposite()) || energySource.getStored() <= 0) continue;
            energySources.add(energySource);
        }
        while (energy > 0) {
            int drain = (energy + energySources.size() - 1) / energySources.size();
            Iterator it = energySources.iterator();
            while (it.hasNext()) {
                IEnergyStorage energySource = (IEnergyStorage)it.next();
                if (drain > energy) {
                    drain = energy;
                }
                if (energySource.getStored() <= drain) {
                    energy -= energySource.getStored();
                    energySource.setStored(0);
                    it.remove();
                    continue;
                }
                energy -= drain;
                energySource.addEnergy(- drain);
            }
        }
    }

    public int getAvailableEnergy() {
        int energy = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            IEnergyStorage storage;
            TileEntity target = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(target instanceof IEnergyStorage) || !(storage = (IEnergyStorage)target).isTeleporterCompatible(dir.getOpposite())) continue;
            energy += storage.getStored();
        }
        return energy;
    }

    public int getWeightOf(Entity user) {
        boolean teleporterUseInventoryWeight = ConfigUtil.getBool(MainConfig.get(), "balance/teleporterUseInventoryWeight");
        int weight = 0;
        if (user instanceof EntityItem) {
            ItemStack[] is = ((EntityItem)user).getEntityItem();
            weight += 100 * is.stackSize / is.getMaxStackSize();
        } else if (user instanceof EntityAnimal || user instanceof EntityMinecart || user instanceof EntityBoat) {
            weight += 100;
        } else if (user instanceof EntityPlayer) {
            weight += 1000;
            if (teleporterUseInventoryWeight) {
                for (ItemStack stack : ((EntityPlayer)user).inventory.mainInventory) {
                    if (stack == null) continue;
                    weight += 100 * stack.stackSize / stack.getMaxStackSize();
                }
            }
        } else if (user instanceof EntityGhast) {
            weight += 2500;
        } else if (user instanceof EntityWither) {
            weight += 5000;
        } else if (user instanceof EntityDragon) {
            weight += 10000;
        } else if (user instanceof EntityCreature) {
            weight += 500;
        }
        if (teleporterUseInventoryWeight && user instanceof EntityLivingBase) {
            ItemStack hand;
            EntityLivingBase living = (EntityLivingBase)user;
            for (ItemStack stack : living.getEquipmentAndArmor()) {
                if (stack == null) continue;
                weight += 100 * stack.stackSize / stack.getMaxStackSize();
            }
            if (user instanceof EntityPlayer && (hand = living.getHeldItemMainhand()) != null) {
                weight -= 100 * hand.stackSize / hand.getMaxStackSize();
            }
        }
        for (Entity passenger : user.getPassengers()) {
            weight += this.getWeightOf(passenger);
        }
        return weight;
    }

    @Override
    protected boolean canEntityDestroy(Entity entity) {
        return !(entity instanceof EntityDragon) && !(entity instanceof EntityWither);
    }

    public boolean hasTarget() {
        return this.target != null;
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public void setTarget(BlockPos pos) {
        this.target = pos;
        IC2.network.get(true).updateTileEntityField(this, "target");
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("target");
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active")) {
            if (this.audioSource == null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/Teleporter/TeleChargedLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
            }
            if (this.getActive()) {
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
            } else if (this.audioSource != null) {
                this.audioSource.stop();
            }
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public void onNetworkEvent(int event) {
        switch (event) {
            case 0: {
                IC2.audioManager.playOnce(this, PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, IC2.audioManager.getDefaultVolume());
                IC2.audioManager.playOnce(new AudioPosition(this.worldObj, this.pos), PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, IC2.audioManager.getDefaultVolume());
                this.spawnBlueParticles(20, this.pos);
                this.spawnBlueParticles(20, this.target);
                break;
            }
            default: {
                IC2.platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + event + ", tile entity below)\n" + "T: " + this + " (" + (Object)this.pos + ")", new Object[0]);
            }
        }
    }
}

