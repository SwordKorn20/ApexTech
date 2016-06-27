package skam.apextech.block.storage;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skam.apextech.ApexTech;

public class ApexTEBlock extends Block {
    public static boolean isBlockModel;

    public ApexTEBlock(String regName, float hardness, boolean isBlockModel) {
        super(Material.IRON);
        this.setUnlocalizedName(regName);
        this.setCreativeTab(ApexTech.apexTab);
        this.setHardness(hardness);
        this.isBlockContainer = true;
        this.isBlockModel = isBlockModel;
    }

    @Override
    public boolean isVisuallyOpaque() {
        if(isBlockModel) {
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ApexTE();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
}

