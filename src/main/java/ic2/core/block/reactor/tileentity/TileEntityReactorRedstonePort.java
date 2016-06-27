/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.core.block.reactor.tileentity;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.FluidReactorLookup;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import net.minecraft.world.World;

public class TileEntityReactorRedstonePort
extends TileEntityBlock {
    public final Redstone redstone;
    private final FluidReactorLookup lookup;

    public TileEntityReactorRedstonePort() {
        this.redstone = this.addComponent(new Redstone(this));
        this.lookup = this.addComponent(new FluidReactorLookup(this));
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.updateRedstoneLink();
    }

    private void updateRedstoneLink() {
        if (this.worldObj.isRemote) {
            return;
        }
        TileEntityNuclearReactorElectric reactor = this.lookup.getReactor();
        if (reactor != null) {
            this.redstone.linkTo(reactor.redstone);
        }
    }
}

