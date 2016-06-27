/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.FMLInjectionData
 *  net.minecraftforge.fml.relauncher.IFMLCallHook
 */
package ic2.core.coremod;

import ic2.core.init.Libraries;
import java.io.File;
import java.util.Map;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

public class Setup
implements IFMLCallHook {
    private File mcDir;

    public Void call() throws Exception {
        Libraries.init(this.mcDir, (String)FMLInjectionData.data()[4]);
        return null;
    }

    public void injectData(Map<String, Object> data) {
        this.mcDir = (File)data.get("mcLocation");
    }
}

