/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.resources.IReloadableResourceManager
 *  net.minecraft.client.resources.IResourceManager
 *  net.minecraft.client.resources.IResourceManagerReloadListener
 *  net.minecraft.client.resources.Locale
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.util.text.translation.LanguageMap
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.init;

import com.google.common.base.Charsets;
import ic2.core.IC2;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.PriorityExecutor;
import ic2.core.util.ReflectionUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.Locale;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Localization {
    private static final String defaultLang = "en_US";
    private static final String ic2LangKey = "ic2.";
    private static Map<String, Map<String, String>> langTable = new HashMap<String, Map<String, String>>();
    private static Future<?> loadFuture;

    public static void preInit(final File modSourceFile) {
        loadFuture = IC2.getInstance().threadPool.submit(new Runnable(){

            @Override
            public void run() {
                Localization.loadLocalizations(modSourceFile);
            }
        });
    }

    public static void postInit() {
        try {
            loadFuture.get();
        }
        catch (InterruptedException e) {
            IC2.log.debug(LogCategory.Resource, e, "Load interrupted.");
        }
        catch (ExecutionException e) {
            IC2.log.warn(LogCategory.Resource, e, "Load failed.");
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            Localization.registerResourceReloadHook();
        } else {
            Map<String, String> defaultMap = langTable.get("en_US");
            Map<String, String> map = Localization.getStringTranslateMap();
            if (defaultMap != null) {
                map.putAll(defaultMap);
            }
        }
    }

    private static void loadLocalizations(File modSourceFile) {
        if (modSourceFile.isDirectory()) {
            File langFolder = new File(modSourceFile, "ic2/lang");
            if (langFolder.isDirectory()) {
                for (File langFile : langFolder.listFiles(new FilenameFilter(){

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".properties");
                    }
                })) {
                    InputStream is = null;
                    try {
                        is = new BufferedInputStream(new FileInputStream(langFile));
                        Localization.loadLocalization(is, langFile.getName().split("\\.")[0]);
                    }
                    catch (Exception e) {
                        IC2.log.warn(LogCategory.Resource, e, "Can't read language file %s.", langFile);
                    }
                    finally {
                        if (is != null) {
                            try {
                                is.close();
                            }
                            catch (IOException e) {}
                        }
                    }
                }
                IC2.log.debug(LogCategory.Resource, "Translations loaded from folder %s.", modSourceFile);
            } else {
                IC2.log.warn(LogCategory.Resource, "Can't list language files (from folder %s).", langFolder);
            }
        } else {
            if (modSourceFile.exists() && modSourceFile.getName().endsWith(".jar")) {
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(modSourceFile);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (!name.startsWith("ic2/lang/") || (name = name.substring("ic2/lang/".length())).contains("/") || !name.endsWith(".properties")) continue;
                        InputStream is = null;
                        try {
                            is = zipFile.getInputStream(entry);
                            Localization.loadLocalization(is, name.split("\\.")[0]);
                            continue;
                        }
                        finally {
                            if (is == null) continue;
                            try {
                                is.close();
                                continue;
                            }
                            catch (IOException is) {}
                            continue;
                        }
                    }
                    IC2.log.debug(LogCategory.Resource, "Translations loaded from file %s.", modSourceFile);
                }
                catch (Exception e) {
                    IC2.log.warn(LogCategory.Resource, e, "Can't list language files (from jar %s).", zipFile);
                }
                finally {
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        }
                        catch (IOException e) {}
                    }
                }
            }
            IC2.log.warn(LogCategory.Resource, "Can't find language files, invalid source: %s.", modSourceFile);
        }
    }

    private static void loadLocalization(InputStream inputStream, String lang) throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(inputStream, Charsets.UTF_8));
        Map<String, String> map = Localization.getLangMap(lang);
        for (Map.Entry entries : properties.entrySet()) {
            Object key = entries.getKey();
            Object value = entries.getValue();
            if (!(key instanceof String) || !(value instanceof String)) continue;
            String newKey = (String)key;
            if (!(newKey.startsWith("achievement.") || newKey.startsWith("itemGroup.") || newKey.startsWith("death."))) {
                newKey = "ic2." + newKey;
            }
            map.put(newKey, (String)value);
        }
    }

    protected static Map<String, String> getLangMap(String lang) {
        Map<String, String> ret = langTable.get(lang);
        if (ret == null) {
            ret = new HashMap<String, String>();
            langTable.put(lang, ret);
        }
        return ret;
    }

    @SideOnly(value=Side.CLIENT)
    private static void registerResourceReloadHook() {
        IResourceManager resManager = Minecraft.getMinecraft().getResourceManager();
        if (resManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)resManager).registerReloadListener(new IResourceManagerReloadListener(){

                public void onResourceManagerReload(IResourceManager manager) {
                    Map<String, String> map = Localization.getStringTranslateMap();
                    Map<String, String> map2 = Localization.getLocaleMap();
                    map.putAll(Localization.getLangMap("en_US"));
                    map.putAll(Localization.getLangMap(Minecraft.getMinecraft().gameSettings.language));
                    map2.putAll(Localization.getLangMap("en_US"));
                    map2.putAll(Localization.getLangMap(Minecraft.getMinecraft().gameSettings.language));
                }
            });
        }
    }

    protected static Map<String, String> getStringTranslateMap() {
        for (Method method : LanguageMap.class.getDeclaredMethods()) {
            if (method.getReturnType() != LanguageMap.class) continue;
            method.setAccessible(true);
            Field mapField = ReflectionUtil.getField(LanguageMap.class, Map.class);
            try {
                return (Map)mapField.get(method.invoke(null, new Object[0]));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    protected static Map<String, String> getLocaleMap() {
        Field localeField = ReflectionUtil.getField(I18n.class, Locale.class);
        Field mapField = ReflectionUtil.getField(Locale.class, Map.class);
        try {
            return (Map)mapField.get(localeField.get(null));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String translate(String key) {
        return net.minecraft.util.text.translation.I18n.translateToLocal((String)key);
    }

    public static /* varargs */ String translate(String key, Object ... args) {
        return net.minecraft.util.text.translation.I18n.translateToLocalFormatted((String)key, (Object[])args);
    }

}

