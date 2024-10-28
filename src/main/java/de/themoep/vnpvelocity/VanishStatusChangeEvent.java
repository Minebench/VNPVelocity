package de.themoep.vnpvelocity;


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
 * <p>
 * <p>
 * Copyright notice of mbaxter's original VNP VanishStatusChangeEvent:
 * <p>
 * Copyright 2012-2013 Matt Baxter
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http:www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * You can find the source of the VanishNoPacket Bukkit plugin here:
 * https://github.com/mbax/VanishNoPacket/
 */

import com.velocitypowered.api.proxy.Player;

/**
 * An event fired whenever a player changes their visibility
 */
public final class VanishStatusChangeEvent {

    private final boolean vanishing;
    private final Player player;

    public VanishStatusChangeEvent(Player player, boolean vanishing) {
        this.vanishing = vanishing;
        this.player = player;
    }

    /**
     * Gets the player changing visibility
     *
     * @return the player changing visibility
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets if this is a vanish or unvanish
     *
     * @return true if vanishing, false is revealing
     */
    public boolean isVanishing() {
        return this.vanishing;
    }
}
