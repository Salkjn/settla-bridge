/*******************************************************************************
 * This file is part of ASkyBlock.
 *
 *     ASkyBlock is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ASkyBlock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.settla.local;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * Helper class for Vault Economy and Permissions
 */
public class VaultHelper {
	
	public static Economy ECONOMY = null;

    /**
     * Sets up the economy instance
     *
     * @return true if successful
     */
    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            ECONOMY = economyProvider.getProvider();
        }
        return ECONOMY != null;
    }
	
    public static Permission PERMISSION = null;

    /**
     * Sets up the permissions instance
     * 
     * @return true if successful
     */
    public static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = LocalPlugin.getInstance().getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            PERMISSION = permissionProvider.getProvider();
        }
        return (PERMISSION != null);
    }

    /**
     * Checks permission of player in world or in any world
     * 
     * @param player
     * @param perm
     * @return true if the player has the perm
     */
    public static boolean checkPerm(final Player player, final String perm) {
        return PERMISSION.has(player, perm);
    }

    /**
     * Adds permission to player
     * 
     * @param player
     * @param perm
     */
    public static void addPerm(final Player player, final String perm) {
        PERMISSION.playerAdd(player, perm);
    }

    /**
     * Removes a player's permission
     * 
     * @param player
     * @param perm
     */
    public static void removePerm(final Player player, final String perm) {
        PERMISSION.playerRemove(player, perm);
    }
    
    public static boolean checkPerm(final UUID uuid, final String perm) {
        Player player = Bukkit.getPlayer(uuid);
        return player == null ? false : checkPerm(player, perm);
    }

}