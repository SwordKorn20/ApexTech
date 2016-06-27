package skam.apextech.block.storage;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import skam.apextech.ApexTech;

public class MSUCore extends BlockContainer {

    public MSUCore(Material material, String unlocalizedName) {
        super(material);
        this.setUnlocalizedName("MSUCore");
        this.setCreativeTab(ApexTech.apexTab);
        this.setHardness(2.5F);
    }

    public MSUCore(String unlocalisedName) {
        this(Material.ROCK, unlocalisedName);
    }
    
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
    
    @Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMSUCore();
	}
    
}

