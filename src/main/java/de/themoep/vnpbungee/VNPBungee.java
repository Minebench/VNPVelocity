package de.themoep.vnpbungee;

import com.viaversion.viaversion.BungeePlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * VNPBungee - Bungee bridge for VanishNoPacket
 * Copyright (C) 2015 Max Lee (https://github.com/Phoenix616/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.*
 */
public class VNPBungee extends Plugin {

    private static VNPBungee instance;
    Map<UUID, VanishStatus> statusUUIDMap = new HashMap<>();
    Map<String, VanishStatus> statusNameMap = new HashMap<>();

    private Plugin viaVersion = null;

    public void onEnable() {
        instance = this;
        getProxy().getPluginManager().registerListener(VNPBungee.getInstance(), new EventListeners(this));
        getProxy().registerChannel("vanishStatus");
        getProxy().registerChannel("vanishnopacket:status");
        viaVersion = getProxy().getPluginManager().getPlugin("ViaVersion");
    }

    /**
     * Get the instance of the plugin
     * @return Itself
     */
    public static VNPBungee getInstance() {
        return instance;
    }

    /**
     * Check if one player can se another one
     * @param watcher   The one that watches
     * @param player    The player he tries to see
     * @return          If the watcher can see the player
     */
    public boolean canSee(CommandSender watcher, ProxiedPlayer player) {
        return getVanishStatus(player) == VanishStatus.VISIBLE
                || (watcher instanceof ProxiedPlayer
                        && getVanishStatus((ProxiedPlayer) watcher) == VanishStatus.UNKNOWN
                        && getVanishStatus(player) == VanishStatus.UNKNOWN)
                || watcher.hasPermission("vanish.see");
    }

    /**
     * Set the vanish status of a player<br />
     * This will <strong>not</strong> fire a VanishStatusChangeEvent!
     * @param player    The ProxiedPlayer to set
     * @param vanished  If the user is vanished or not
     * @return          The previously assigned VanishStatus, VanishStatus.UNKNOWN if there weren't one!
     */
    VanishStatus setVanished(ProxiedPlayer player, boolean vanished) {
        VanishStatus pre = statusUUIDMap.put(player.getUniqueId(), (vanished) ? VanishStatus.VANISHED : VanishStatus.VISIBLE);
        statusNameMap.put(player.getName(), (vanished) ? VanishStatus.VANISHED : VanishStatus.VISIBLE);
        return (pre == null) ? VanishStatus.UNKNOWN : pre;
    }

    /**
     * Set the vanish status of a player<br />
     * This will <strong>not</strong> fire a VanishStatusChangeEvent!
     * @param player    The ProxiedPlayer to set
     * @param status    The VanishStatus to set
     * @return          The previously assigned status, VanishStatus.UNKNOWN if there weren't one!
     */
    VanishStatus setVanishStatus(ProxiedPlayer player, VanishStatus status) {
        VanishStatus pre = statusUUIDMap.put(player.getUniqueId(), status);
        statusNameMap.put(player.getName(), status);
        return (pre == null) ? VanishStatus.UNKNOWN : pre;
    }

    /**
     * Get if a player is vanished or not
     * @param player    The ProxiedPlayer to check
     * @return          The VanishStatus of the player, VanishStatus.UNKNOWN if we don't know it!
     */
    public VanishStatus getVanishStatus(ProxiedPlayer player) {
        return getVanishStatus(player.getUniqueId());
    }

    /**
     * Get if a player is vanished or not
     * @param playerId  The UUID of the player to check
     * @return          The VanishStatus of the player, VanishStatus.UNKNOWN if we don't know it!
     */
    public VanishStatus getVanishStatus(UUID playerId) {
        VanishStatus status = statusUUIDMap.get(playerId);
        return (status == null) ? VanishStatus.UNKNOWN : status;
    }

    /**
     * Get if a player is vanished or not
     * @param playername    The name of the player to check
     * @return              The VanishStatus of the player, VanishStatus.UNKNOWN if we don't know it!
     */
    public VanishStatus getVanishStatus(String playername) {
        VanishStatus status = statusNameMap.get(playername);
        return (status == null) ? VanishStatus.UNKNOWN : status;
    }

    /**
     * Clears the player's status data<br />
     * This will <strong>not</strong> fire a VanishStatusChangeEvent!
     * @param player    The ProxiedPlayer to clear the status data of
     * @return          The previously assigned status, VanishStatus.UNKNOWN if there weren't one!
     */
    VanishStatus clearStatusData(ProxiedPlayer player) {
        VanishStatus pre = statusUUIDMap.remove(player.getUniqueId());
        statusNameMap.remove(player.getName());
        return (pre == null) ? VanishStatus.UNKNOWN : pre;
    }

    int getServerVersion(ServerInfo server) {
        if (viaVersion != null) {
            return ((BungeePlugin) viaVersion).protocolDetectorService().serverProtocolVersion(server.getName()).getVersion();
        }
        return -1;
    }

    public enum VanishStatus {
        VANISHED,
        VISIBLE,
        UNKNOWN;
    }
}
