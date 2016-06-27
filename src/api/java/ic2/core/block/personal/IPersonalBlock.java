/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package ic2.core.block.personal;

import com.mojang.authlib.GameProfile;

public interface IPersonalBlock {
    public boolean permitsAccess(GameProfile var1);

    public GameProfile getOwner();

    public void setOwner(GameProfile var1);
}

