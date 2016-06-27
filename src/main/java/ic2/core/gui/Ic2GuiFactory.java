/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraftforge.fml.client.IModGuiFactory
 *  net.minecraftforge.fml.client.IModGuiFactory$RuntimeOptionCategoryElement
 *  net.minecraftforge.fml.client.IModGuiFactory$RuntimeOptionGuiHandler
 *  net.minecraftforge.fml.client.config.ConfigGuiType
 *  net.minecraftforge.fml.client.config.DummyConfigElement
 *  net.minecraftforge.fml.client.config.DummyConfigElement$DummyCategoryElement
 *  net.minecraftforge.fml.client.config.DummyConfigElement$DummyListElement
 *  net.minecraftforge.fml.client.config.GuiConfig
 *  net.minecraftforge.fml.client.config.IConfigElement
 */
package ic2.core.gui;

import ic2.core.init.MainConfig;
import ic2.core.util.Config;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class Ic2GuiFactory
implements IModGuiFactory {
    public void initialize(Minecraft mc) {
    }

    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return IC2ConfigGuiScreen.class;
    }

    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class IC2ConfigGuiScreen
    extends GuiConfig {
        private static final Pattern IS_BOOLEAN = Pattern.compile("true|false");
        private static final Pattern IS_INT = Pattern.compile("\\d");
        private static final Pattern IS_DOUBLE = Pattern.compile("\\d\\.\\d");
        private static final Pattern COMMA_PATTERN = Pattern.compile("^([A-Za-z0-9_]+:[A-Za-z0-9_]+((,){1}( )*|$))+?");

        public IC2ConfigGuiScreen(GuiScreen parent) {
            super(parent, IC2ConfigGuiScreen.sinkCategoryLevel(MainConfig.get(), "."), "IC2", false, false, "IC2 Configuration");
        }

        private static List<IConfigElement> sinkCategoryLevel(Config config, String parentName) {
            ArrayList<IConfigElement> list = new ArrayList<IConfigElement>(config.getNumberOfSections() + config.getNumberOfConfigs());
            if (config.hasChildSection()) {
                Iterator<Config> configCategories = config.sectionIterator();
                while (configCategories.hasNext()) {
                    Config category = configCategories.next();
                    list.add((IConfigElement)new DummyConfigElement.DummyCategoryElement(category.name, "ic2.config.sub." + category.name, IC2ConfigGuiScreen.sinkCategoryLevel(category, parentName + category.name + ".")));
                }
                if (!config.isEmptySection()) {
                    IC2ConfigGuiScreen.getConfigs(list, config.valueIterator(), parentName);
                }
            } else {
                IC2ConfigGuiScreen.getConfigs(list, config.valueIterator(), parentName);
            }
            return list;
        }

        private static void getConfigs(List<IConfigElement> list, Iterator<Config.Value> configs, String parentName) {
            while (configs.hasNext()) {
                Config.Value conf = configs.next();
                if (conf.value.isEmpty() || conf.value.contains(",") || conf.comment.contains("comma")) {
                    Object[] array = conf.value.split("( )*,( )*");
                    if (array.length == 1 && array[0].isEmpty()) {
                        array = new String[]{};
                    }
                    list.add((IConfigElement)new DummyConfigElement.DummyListElement(conf.name, array, ConfigGuiType.STRING, "ic2.config" + parentName + conf.name, COMMA_PATTERN));
                    continue;
                }
                ConfigGuiType type = IS_DOUBLE.matcher(conf.value).matches() ? ConfigGuiType.DOUBLE : (IS_INT.matcher(conf.value).matches() ? ConfigGuiType.INTEGER : (IS_BOOLEAN.matcher(conf.value).matches() ? ConfigGuiType.BOOLEAN : ConfigGuiType.STRING));
                list.add((IConfigElement)new DummyConfigElement(conf.name, (Object)conf.value, type, "ic2.config" + parentName + conf.name));
            }
        }

        public void onGuiClosed() {
            for (IConfigElement config : this.configElements) {
                this.saveConfig(config);
            }
            MainConfig.save();
            super.onGuiClosed();
        }

        private void saveConfig(IConfigElement config) {
            if (config.getChildElements() != null) {
                for (Object[] subConfig : config.getChildElements()) {
                    this.saveConfig((IConfigElement)subConfig);
                }
            }
            if (config.getDefaults() == null) {
                if (!Objects.equals(config.get(), config.getDefault())) {
                    MainConfig.get().set(config.getLanguageKey().substring("ic2.config.".length()).replace('.', '/'), config.getDefault());
                }
            } else if (!config.getDefaults().equals(config.getList())) {
                String out = "";
                for (Object o : config.getDefaults()) {
                    out = out + o + ", ";
                }
                MainConfig.get().set(config.getLanguageKey().substring("ic2.config.".length()).replace('.', '/'), out.length() > 2 ? out.substring(0, out.length() - 2) : out);
            }
        }
    }

}

