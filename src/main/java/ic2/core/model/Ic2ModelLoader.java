/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.IResourceManager
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.ICustomModelLoader
 *  net.minecraftforge.client.model.IModel
 */
package ic2.core.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class Ic2ModelLoader
implements ICustomModelLoader {
    private static final Map<ResourceLocation, IModel> models = new HashMap<ResourceLocation, IModel>();

    public void register(String path, IModel model) {
        this.register(new ResourceLocation("ic2", path), model);
    }

    public void register(ResourceLocation location, IModel model) {
        models.put(location, model);
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    public boolean accepts(ResourceLocation modelLocation) {
        return models.containsKey((Object)modelLocation);
    }

    public IModel loadModel(ResourceLocation modelLocation) throws IOException {
        return models.get((Object)modelLocation);
    }
}

