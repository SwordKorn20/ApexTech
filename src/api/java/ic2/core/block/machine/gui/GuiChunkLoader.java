/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import com.google.common.collect.ImmutableSet;
import ic2.core.ChunkLoaderLogic;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerChunkLoader;
import ic2.core.block.machine.tileentity.TileEntityChunkloader;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.network.NetworkManager;
import ic2.core.util.Ic2BlockPos;
import ic2.core.util.SideGateway;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiChunkLoader
extends GuiIC2<ContainerChunkLoader> {
    private static final ResourceLocation background = new ResourceLocation("ic2", "textures/gui/GUIChunkLoader.png");

    public GuiChunkLoader(ContainerChunkLoader container) {
        super(container, 250);
        this.addElement(EnergyGauge.asBolt(this, 12, 125, (TileEntityBlock)container.base));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ChunkPos mainChunk = ChunkLoaderLogic.getChunkCoords(((TileEntityChunkloader)((ContainerChunkLoader)this.container).base).getPos());
        ImmutableSet<ChunkPos> loadedChunks = ((TileEntityChunkloader)((ContainerChunkLoader)this.container).base).getLoadedChunks();
        int amountLoadedChunks = 0;
        for (int i = -4; i <= 4; ++i) {
            for (int j = -4; j <= 4; ++j) {
                ChunkPos currentChunk = new ChunkPos(mainChunk.chunkXPos + i, mainChunk.chunkZPos + j);
                int xpos = - this.guiLeft + 89 + 16 * i;
                int ypos = - this.guiTop + 80 + 16 * j;
                this.drawChunkAt(xpos, ypos, currentChunk);
                if (loadedChunks.contains((Object)currentChunk)) {
                    this.drawColoredRect(xpos, ypos, 16, 16, 805371648);
                    ++amountLoadedChunks;
                    continue;
                }
                this.drawColoredRect(xpos, ypos, 16, 16, 822018048);
            }
        }
        GlStateManager.enableAlpha();
        this.fontRendererObj.drawSplitString("" + amountLoadedChunks + " / " + ChunkLoaderLogic.getInstance().getMaxChunksPerTicket(), 8, 16, 15, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private void drawChunkAt(int x, int y, ChunkPos chunkPos) {
        World world = ((TileEntityChunkloader)((ContainerChunkLoader)this.container).base).getWorld();
        Chunk chunk = world.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos);
        Ic2BlockPos worldPos = new Ic2BlockPos();
        for (int cx = 0; cx < 16; ++cx) {
            worldPos.setX(chunkPos.chunkXPos << 4 | cx);
            for (int cz = 0; cz < 16; ++cz) {
                worldPos.setZ(chunkPos.chunkZPos << 4 | cz);
                worldPos.setY(chunk.getHeightValue(cx, cz));
                IBlockState state = chunk.getBlockState((BlockPos)worldPos);
                if (state.getBlock().isAir(state, (IBlockAccess)world, (BlockPos)worldPos)) {
                    worldPos.moveDown();
                    state = chunk.getBlockState((BlockPos)worldPos);
                }
                this.drawColoredRect(x + cx, y + cz, 1, 1, this.getColor(state));
            }
        }
    }

    private int getColor(IBlockState state) {
        return state.getMapColor().colorValue | -16777216;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            ChunkPos mainChunk = ChunkLoaderLogic.getChunkCoords(((TileEntityChunkloader)((ContainerChunkLoader)this.container).base).getPos());
            for (int i = -4; i <= 4; ++i) {
                for (int j = -4; j <= 4; ++j) {
                    if (mouseX - this.guiLeft <= 89 + 16 * i || mouseX - this.guiLeft > 89 + 16 * i + 16 || mouseY - this.guiTop <= 80 + 16 * j || mouseY - this.guiTop > 80 + 16 * j + 16) continue;
                    this.changeChunk(new ChunkPos(mainChunk.chunkXPos + i, mainChunk.chunkZPos + j));
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void changeChunk(ChunkPos chunk) {
        ChunkPos mainChunk = ChunkLoaderLogic.getChunkCoords(((TileEntityChunkloader)((ContainerChunkLoader)this.container).base).getPos());
        IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)((ContainerChunkLoader)this.container).base, chunk.chunkXPos - mainChunk.chunkXPos + 8 & 15 | (chunk.chunkZPos - mainChunk.chunkZPos + 8 & 15) << 4);
    }
}

