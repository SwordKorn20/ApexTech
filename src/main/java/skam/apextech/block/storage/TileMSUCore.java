package skam.apextech.block.storage;

import cofh.api.energy.EnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileMSUCore extends ApexTE {


	protected EnergyStorage storage = new EnergyStorage(20000);

	public TileMSUCore(EnergyStorage storage) {
		super(storage);
	}
	
	public TileMSUCore() {
		
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		nbt = super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public int getEnergyStored(EnumFacing from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}
}
