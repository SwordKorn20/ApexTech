package skam.apextech.block.Storage;

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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

	TileEntity te = world.getTileEntity(pos);

	     TileMSUCore MSU = (TileMSUCore)te;

	     player.addChatComponentMessage(new TextComponentString("Energy Stored: " + MSU.getEnergyStored(side) + "/" + MSU.getMaxEnergyStored(side) + " RF"));
	     return true;
	     
	     if(MSU.getEnergyStored(side) >= 1) {
	    	 
	     }
	
	
	}

    @Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMSUCore();
	}
    
}

