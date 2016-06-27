/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.stats.Achievement
 *  net.minecraft.stats.AchievementList
 *  net.minecraft.stats.StatBase
 *  net.minecraftforge.common.AchievementPage
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 */
package ic2.core;

import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.block.wiring.CableType;
import ic2.core.item.tfbp.Tfbp;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.IngotResourceType;
import ic2.core.item.type.MiscResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import java.util.Collection;
import java.util.HashMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class IC2Achievements {
    public HashMap<String, Achievement> achievementList = new HashMap();
    private final int achievementBaseX = -4;
    private final int achievementBaseY = -5;

    public IC2Achievements() {
        this.registerAchievement("acquireResin", 2, 0, ItemName.misc_resource.getItemStack(MiscResourceType.resin), AchievementList.MINE_WOOD, false);
        this.registerAchievement("mineOre", 4, 0, BlockName.resource.getItemStack(ResourceBlock.copper_ore), AchievementList.BUILD_BETTER_PICKAXE, false);
        this.registerAchievement("acquireRefinedIron", 0, 0, ItemName.ingot.getItemStack(IngotResourceType.steel), AchievementList.ACQUIRE_IRON, false);
        this.registerAchievement("buildCable", 0, 2, ItemName.cable.getItemStack(CableType.copper), AchievementList.ACQUIRE_IRON, false);
        this.registerAchievement("buildGenerator", 6, 2, BlockName.te.getItemStack(TeBlock.generator), "buildCable", false);
        this.registerAchievement("buildMacerator", 6, 0, BlockName.te.getItemStack(TeBlock.macerator), "buildGenerator", false);
        this.registerAchievement("buildCoalDiamond", 8, 0, ItemName.crafting.getItemStack(CraftingItemType.industrial_diamond), "buildMacerator", false);
        this.registerAchievement("buildElecFurnace", 8, 2, BlockName.te.getItemStack(TeBlock.electric_furnace), "buildGenerator", false);
        this.registerAchievement("buildIndFurnace", 10, 2, BlockName.te.getItemStack(TeBlock.induction_furnace), "buildElecFurnace", false);
        this.registerAchievement("buildCompressor", 4, 4, BlockName.te.getItemStack(TeBlock.compressor), "buildGenerator", false);
        this.registerAchievement("dieFromOwnNuke", 0, 4, BlockName.te.getItemStack(TeBlock.nuke), "compressUranium", true);
        this.registerAchievement("buildExtractor", 8, 4, BlockName.te.getItemStack(TeBlock.extractor), "buildGenerator", false);
        this.registerAchievement("buildBatBox", 6, 6, BlockName.te.getItemStack(TeBlock.batbox), "buildGenerator", false);
        this.registerAchievement("buildDrill", 8, 6, ItemName.drill.getItemStack(), "buildBatBox", false);
        this.registerAchievement("buildDDrill", 10, 6, ItemName.diamond_drill.getItemStack(), "buildDrill", false);
        this.registerAchievement("buildIDrill", 12, 6, ItemName.iridium_drill.getItemStack(), "buildDDrill", true);
        this.registerAchievement("buildChainsaw", 4, 6, ItemName.chainsaw.getItemStack(), "buildBatBox", false);
        this.registerAchievement("killCreeperChainsaw", 2, 6, ItemName.chainsaw.getItemStack(), "buildChainsaw", true);
        this.registerAchievement("buildMFE", 6, 8, BlockName.te.getItemStack(TeBlock.mfe), "buildBatBox", false);
        this.registerAchievement("buildMassFab", 8, 8, BlockName.te.getItemStack(TeBlock.matter_generator), "buildBatBox", false);
        this.registerAchievement("replicateObject", 10, 8, BlockName.te.getItemStack(TeBlock.replicator), "buildMassFab", false);
        this.registerAchievement("buildQArmor", 12, 8, ItemName.quantum_chestplate.getItemStack(), "replicateObject", false);
        this.registerAchievement("starveWithQHelmet", 14, 8, ItemName.crafting.getItemStack(CraftingItemType.tin_can), "buildQArmor", true);
        this.registerAchievement("buildMiningLaser", 4, 8, ItemName.mining_laser.getItemStack(), "buildMFE", false);
        this.registerAchievement("killDragonMiningLaser", 2, 8, ItemName.mining_laser.getItemStack(), "buildMiningLaser", true);
        this.registerAchievement("buildMFS", 6, 10, BlockName.te.getItemStack(TeBlock.mfsu), "buildMFE", false);
        this.registerAchievement("buildTeleporter", 4, 10, BlockName.te.getItemStack(TeBlock.teleporter), "buildMFS", false);
        this.registerAchievement("teleportFarAway", 2, 10, BlockName.te.getItemStack(TeBlock.teleporter), "buildTeleporter", true);
        this.registerAchievement("buildTerraformer", 8, 10, BlockName.te.getItemStack(TeBlock.terraformer), "buildMFS", false);
        this.registerAchievement("terraformEndCultivation", 10, 10, ItemName.tfbp.getItemStack(Tfbp.TfbpType.cultivation), "buildTerraformer", true);
        AchievementPage.registerAchievementPage((AchievementPage)new AchievementPage("IndustrialCraft 2", this.achievementList.values().toArray((T[])new Achievement[this.achievementList.size()])));
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public Achievement registerAchievement(String textId, int x, int y, ItemStack icon, Achievement requirement, boolean special) {
        Achievement achievement = new Achievement("ic2." + textId, textId, -4 + x, -5 + y, icon, requirement);
        if (special) {
            achievement.setSpecial();
        }
        achievement.registerStat();
        this.achievementList.put(textId, achievement);
        return achievement;
    }

    public Achievement registerAchievement(String textId, int x, int y, ItemStack icon, String requirement, boolean special) {
        Achievement achievement = new Achievement("ic2." + textId, textId, -4 + x, -5 + y, icon, this.getAchievement(requirement));
        if (special) {
            achievement.setSpecial();
        }
        achievement.registerStat();
        this.achievementList.put(textId, achievement);
        return achievement;
    }

    public void issueAchievement(EntityPlayer entityplayer, String textId) {
        if (this.achievementList.containsKey(textId)) {
            entityplayer.addStat((StatBase)this.achievementList.get(textId));
        }
    }

    public Achievement getAchievement(String textId) {
        if (this.achievementList.containsKey(textId)) {
            return this.achievementList.get(textId);
        }
        return null;
    }

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        EntityPlayer player = event.player;
        ItemStack stack = event.crafting;
        if (player == null) {
            return;
        }
        if (stack == null) {
            return;
        }
        if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.generator))) {
            this.issueAchievement(player, "buildGenerator");
        } else if (stack.getItem() == ItemName.cable.getInstance()) {
            this.issueAchievement(player, "buildCable");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.macerator))) {
            this.issueAchievement(player, "buildMacerator");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.electric_furnace))) {
            this.issueAchievement(player, "buildElecFurnace");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.compressor))) {
            this.issueAchievement(player, "buildCompressor");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.batbox))) {
            this.issueAchievement(player, "buildBatBox");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.mfe))) {
            this.issueAchievement(player, "buildMFE");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.teleporter))) {
            this.issueAchievement(player, "buildTeleporter");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.matter_generator))) {
            this.issueAchievement(player, "buildMassFab");
        } else if (stack.getItem() == ItemName.quantum_boots.getInstance() || stack.getItem() == ItemName.quantum_chestplate.getInstance() || stack.getItem() == ItemName.quantum_helmet.getInstance() || stack.getItem() == ItemName.quantum_leggings.getInstance()) {
            this.issueAchievement(player, "buildQArmor");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.extractor))) {
            this.issueAchievement(player, "buildExtractor");
        } else if (stack.getItem() == ItemName.drill.getInstance()) {
            this.issueAchievement(player, "buildDrill");
        } else if (stack.getItem() == ItemName.diamond_drill.getInstance()) {
            this.issueAchievement(player, "buildDDrill");
        } else if (stack.getItem() == ItemName.iridium_drill.getInstance()) {
            this.issueAchievement(player, "buildIDrill");
        } else if (stack.getItem() == ItemName.chainsaw.getInstance()) {
            this.issueAchievement(player, "buildChainsaw");
        } else if (stack.getItem() == ItemName.mining_laser.getInstance()) {
            this.issueAchievement(player, "buildMiningLaser");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.mfsu))) {
            this.issueAchievement(player, "buildMFS");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.terraformer))) {
            this.issueAchievement(player, "buildTerraformer");
        } else if (stack.isItemEqual(ItemName.crafting.getItemStack(CraftingItemType.coal_chunk))) {
            this.issueAchievement(player, "buildCoalDiamond");
        } else if (stack.isItemEqual(BlockName.te.getItemStack(TeBlock.induction_furnace))) {
            this.issueAchievement(player, "buildIndFurnace");
        }
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (event.getItem().getEntityItem().equals((Object)BlockName.resource.getItemStack(ResourceBlock.copper_ore)) || event.getItem().getEntityItem().equals((Object)BlockName.resource.getItemStack(ResourceBlock.tin_ore)) || event.getItem().getEntityItem().equals((Object)BlockName.resource.getItemStack(ResourceBlock.lead_ore)) || event.getItem().getEntityItem().equals((Object)BlockName.resource.getItemStack(ResourceBlock.uranium_ore))) {
            this.issueAchievement(event.getEntityPlayer(), "mineOre");
        }
    }
}

