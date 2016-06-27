/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GLAllocation
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.resources.IReloadableResourceManager
 *  net.minecraft.client.resources.IResource
 *  net.minecraft.client.resources.IResourceManager
 *  net.minecraft.client.resources.IResourceManagerReloadListener
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.CustomButton;
import ic2.core.gui.GuiElement;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Image
extends GuiElement<Image> {
    private static final Map<ResourceLocation, GlTexture> textures = new HashMap<ResourceLocation, GlTexture>();
    private final ResourceLocation texture;
    private final CustomButton.IOverlaySupplier overlay;
    private final boolean autoWidth;
    private final boolean autoHeight;

    public static Image create(GuiIC2<?> gui, int x, int y, int width, int height, ResourceLocation texture, final int u, final int v) {
        return Image.create(gui, x, y, width, height, texture, new CustomButton.IOverlaySupplier(){

            @Override
            public int getOverlayX() {
                return u;
            }

            @Override
            public int getOverlayY() {
                return v;
            }
        });
    }

    public static Image create(GuiIC2<?> gui, int x, int y, int width, int height, ResourceLocation texture, CustomButton.IOverlaySupplier overlay) {
        boolean autoHeight;
        boolean autoWidth = width < 0;
        boolean bl = autoHeight = height < 0;
        if (autoWidth) {
            width = 0;
        }
        if (autoHeight) {
            height = 0;
        }
        return new Image(gui, x, y, width, height, texture, overlay, autoWidth, autoHeight);
    }

    protected Image(GuiIC2<?> gui, int x, int y, int width, int height, ResourceLocation texture, CustomButton.IOverlaySupplier overlay, boolean autoWidth, boolean autoHeight) {
        super(gui, x, y, width, height);
        if (texture == null) {
            throw new NullPointerException("null texture");
        }
        if (overlay == null) {
            throw new NullPointerException("null overlay");
        }
        this.texture = texture;
        this.overlay = overlay;
        this.autoWidth = autoWidth;
        this.autoHeight = autoHeight;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        super.drawBackground(mouseX, mouseY);
        GlTexture texture = this.getTexture();
        if (texture != null) {
            if (this.autoWidth) {
                this.width = texture.getWidth();
            }
            if (this.autoHeight) {
                this.height = texture.getHeight();
            }
            texture.bind();
            this.gui.drawTexturedRect(this.x, this.y, this.width, this.height, this.overlay.getOverlayX(), this.overlay.getOverlayY(), 1.0 / (double)texture.getCanvasWidth(), 1.0 / (double)texture.getCanvasHeight(), false);
        } else {
            if (this.autoWidth) {
                this.width = 0;
            }
            if (this.autoHeight) {
                this.height = 0;
            }
        }
    }

    private GlTexture getTexture() {
        GlTexture ret = textures.get((Object)this.texture);
        if (ret != null) {
            return ret;
        }
        ret = new GlTexture(this.texture);
        try {
            ret.load(Minecraft.getMinecraft().getResourceManager());
        }
        catch (IOException e) {
            IC2.log.warn(LogCategory.General, "Can't load texture %s", new Object[]{this.texture});
            ret.close();
            ret = null;
        }
        textures.put(this.texture, ret);
        return ret;
    }

    private static void registerReloadHandler() {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        if (manager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)manager).registerReloadListener(new IResourceManagerReloadListener(){

                public void onResourceManagerReload(IResourceManager manager) {
                    for (GlTexture texture : textures.values()) {
                        texture.close();
                    }
                    textures.clear();
                }
            });
        } else {
            IC2.log.warn(LogCategory.General, "The resource manager {} is not reloadable.", new Object[]{manager});
        }
    }

    static {
        Image.registerReloadHandler();
    }

    private static class GlTexture {
        private final ResourceLocation loc;
        private int textureId;
        private int width;
        private int height;
        private int canvasWidth;
        private int canvasHeight;

        public GlTexture(ResourceLocation loc) {
            this.loc = loc;
        }

        public void load(IResourceManager manager) throws IOException {
            IResource resource = manager.getResource(this.loc);
            InputStream is = null;
            try {
                is = resource.getInputStream();
                BufferedImage img = ImageIO.read(is);
                this.width = img.getWidth();
                this.height = img.getHeight();
                this.canvasWidth = Integer.highestOneBit((this.width - 1) * 2);
                this.canvasHeight = Integer.highestOneBit((this.height - 1) * 2);
                this.textureId = GlStateManager.generateTexture();
                IntBuffer buffer = GLAllocation.createDirectIntBuffer((int)(this.canvasWidth * this.canvasHeight));
                int[] tmp = new int[this.canvasWidth * this.canvasHeight];
                img.getRGB(0, 0, this.width, this.height, tmp, 0, this.canvasWidth);
                buffer.put(tmp);
                buffer.flip();
                this.bind();
                GL11.glTexParameteri((int)3553, (int)33085, (int)0);
                GL11.glTexParameterf((int)3553, (int)33082, (float)0.0f);
                GL11.glTexParameterf((int)3553, (int)33083, (float)0.0f);
                GL11.glTexParameteri((int)3553, (int)10242, (int)10496);
                GL11.glTexParameteri((int)3553, (int)10243, (int)10496);
                GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
                GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)this.canvasWidth, (int)this.canvasHeight, (int)0, (int)32993, (int)33639, (IntBuffer)buffer);
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (IOException img) {}
            }
        }

        public void close() {
            if (this.textureId == 0) {
                return;
            }
            GlStateManager.deleteTexture((int)this.textureId);
            this.textureId = 0;
        }

        public void bind() {
            if (this.textureId == 0) {
                throw new IllegalStateException("uninitialized texture");
            }
            GlStateManager.bindTexture((int)this.textureId);
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getCanvasWidth() {
            return this.canvasWidth;
        }

        public int getCanvasHeight() {
            return this.canvasHeight;
        }
    }

}

