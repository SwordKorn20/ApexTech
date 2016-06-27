/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.world.World
 */
package ic2.core;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import ic2.core.WorldData;
import java.nio.charset.Charset;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class Ic2Player
extends EntityPlayer {
    private static final String name = "[IC2]";
    private static final UUID uuid = UUID.nameUUIDFromBytes("[IC2]".getBytes(Charsets.UTF_8));

    public static Ic2Player get(World world) {
        return WorldData.get((World)world).fakePlayer;
    }

    Ic2Player(World world) {
        super(world, new GameProfile(uuid, "[IC2]"));
    }

    public boolean canCommandSenderUseCommand(int i, String s) {
        return false;
    }

    public void addChatMessage(ITextComponent var1) {
    }

    public boolean isSpectator() {
        return false;
    }

    public boolean isCreative() {
        return false;
    }
}

