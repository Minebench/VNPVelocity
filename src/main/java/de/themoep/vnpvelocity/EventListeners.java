package de.themoep.vnpvelocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;

import java.util.Iterator;
import java.util.Optional;

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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class EventListeners {

    private final VNPVelocity plugin;

    public EventListeners(VNPVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPluginMessageReceive(PluginMessageEvent event) {
        if (event.getTarget() instanceof Player player
                && (event.getIdentifier().equals(VNPVelocity.LEGACY_CHANNEL) || event.getIdentifier().equals(VNPVelocity.MODERN_CHANNEL))) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            byte status = in.readByte();
            plugin.getProxy().getEventManager().fire(new VanishStatusChangeEvent(player, status == 1));
        }
    }

    @Subscribe
    public void onStatusChange(VanishStatusChangeEvent event) {
        VNPVelocity.VanishStatus pre = plugin.setVanished(event.getPlayer(), event.isVanishing());
        plugin.getLogger().info("{} {}vanished! Previous status: {}", event.getPlayer().getUsername(), event.isVanishing() ? "" : "un", pre.toString());
    }

    @Subscribe
    public void onServerSwitch(ServerPostConnectEvent event) {
        plugin.clearStatusData(event.getPlayer());
        event.getPlayer().getCurrentServer().ifPresent(s -> s.sendPluginMessage(VNPVelocity.MODERN_CHANNEL, "check".getBytes()));
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        plugin.clearStatusData(event.getPlayer());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onTabCompletion(TabCompleteEvent event) {
        for (Iterator<String> it = event.getSuggestions().iterator(); it.hasNext(); ) {
            Optional<Player> player = plugin.getProxy().getPlayer(it.next());
            if (player.isPresent() && !plugin.canSee(event.getPlayer(), player.get())) {
                it.remove();
            }
        }
    }
}
