package de.themoep.vnpvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * VNPVelocity - Velocity bridge for VanishNoPacket
 * Copyright (C) 2015 Max Lee (https://github.com/Phoenix616/)
 * <p>
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
public class VNPVelocity {

    public static ChannelIdentifier MODERN_CHANNEL = MinecraftChannelIdentifier.create("vanishnopacket", "status");
    public static ChannelIdentifier LEGACY_CHANNEL = new LegacyChannelIdentifier("vanishStatus");
    private static VNPVelocity instance;
    private final ProxyServer proxy;
    private final Logger logger;
    Map<UUID, VanishStatus> statusUUIDMap = new HashMap<>();
    Map<String, VanishStatus> statusNameMap = new HashMap<>();

    @Inject
    public VNPVelocity(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    /**
     * executed on startup
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        getProxy().getEventManager().register(this, new EventListeners(this));
        getProxy().getChannelRegistrar().register(LEGACY_CHANNEL);
        getProxy().getChannelRegistrar().register(MODERN_CHANNEL);
    }

    ProxyServer getProxy() {
        return proxy;
    }

    /**
     * Get the instance of the plugin
     * @return Itself
     */
    public static VNPVelocity getInstance() {
        return instance;
    }

    /**
     * Check if one player can se another one
     * @param watcher The one that watches
     * @param player  The player he tries to see
     * @return If the watcher can see the player
     */
    public boolean canSee(CommandSource watcher, Player player) {
        return getVanishStatus(player) == VanishStatus.VISIBLE
                || (watcher instanceof Player
                && getVanishStatus((Player) watcher) == VanishStatus.UNKNOWN
                && getVanishStatus(player) == VanishStatus.UNKNOWN)
                || watcher.hasPermission("vanish.see");
    }

    /**
     * Set the vanish status of a player<br />
     * This will <strong>not</strong> fire a VanishStatusChangeEvent!
     * @param player   The Player to set
     * @param vanished If the user is vanished or not
     * @return The previously assigned VanishStatus, VanishStatus.UNKNOWN if there weren't one!
     */
    VanishStatus setVanished(Player player, boolean vanished) {
        VanishStatus pre = statusUUIDMap.put(player.getUniqueId(), (vanished) ? VanishStatus.VANISHED : VanishStatus.VISIBLE);
        statusNameMap.put(player.getUsername(), (vanished) ? VanishStatus.VANISHED : VanishStatus.VISIBLE);
        return (pre == null) ? VanishStatus.UNKNOWN : pre;
    }

    /**
     * Set the vanish status of a player<br />
     * This will <strong>not</strong> fire a VanishStatusChangeEvent!
     * @param player The Player to set
     * @param status The VanishStatus to set
     * @return The previously assigned status, VanishStatus.UNKNOWN if there weren't one!
     */
    VanishStatus setVanishStatus(Player player, VanishStatus status) {
        VanishStatus pre = statusUUIDMap.put(player.getUniqueId(), status);
        statusNameMap.put(player.getUsername(), status);
        return (pre == null) ? VanishStatus.UNKNOWN : pre;
    }

    /**
     * Get if a player is vanished or not
     * @param player The Player to check
     * @return The VanishStatus of the player, VanishStatus.UNKNOWN if we don't know it!
     */
    public VanishStatus getVanishStatus(Player player) {
        return getVanishStatus(player.getUniqueId());
    }

    /**
     * Get if a player is vanished or not
     * @param playerId The UUID of the player to check
     * @return The VanishStatus of the player, VanishStatus.UNKNOWN if we don't know it!
     */
    public VanishStatus getVanishStatus(UUID playerId) {
        VanishStatus status = statusUUIDMap.get(playerId);
        return (status == null) ? VanishStatus.UNKNOWN : status;
    }

    /**
     * Get if a player is vanished or not
     * @param playername The name of the player to check
     * @return The VanishStatus of the player, VanishStatus.UNKNOWN if we don't know it!
     */
    public VanishStatus getVanishStatus(String playername) {
        VanishStatus status = statusNameMap.get(playername);
        return (status == null) ? VanishStatus.UNKNOWN : status;
    }

    /**
     * Clears the player's status data<br />
     * This will <strong>not</strong> fire a VanishStatusChangeEvent!
     * @param player The Player to clear the status data of
     * @return The previously assigned status, VanishStatus.UNKNOWN if there weren't one!
     */
    VanishStatus clearStatusData(Player player) {
        VanishStatus pre = statusUUIDMap.remove(player.getUniqueId());
        statusNameMap.remove(player.getUsername());
        return (pre == null) ? VanishStatus.UNKNOWN : pre;
    }

    public Logger getLogger() {
        return logger;
    }

    public enum VanishStatus {
        VANISHED,
        VISIBLE,
        UNKNOWN;
    }
}
