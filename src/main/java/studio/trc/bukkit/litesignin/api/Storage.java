package studio.trc.bukkit.litesignin.api;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import studio.trc.bukkit.litesignin.database.storage.MySQLStorage;
import studio.trc.bukkit.litesignin.database.storage.YamlStorage;
import studio.trc.bukkit.litesignin.database.storage.SQLiteStorage;
import studio.trc.bukkit.litesignin.database.engine.MySQLEngine;
import studio.trc.bukkit.litesignin.database.engine.SQLiteEngine;
import studio.trc.bukkit.litesignin.database.DatabaseTable;
import studio.trc.bukkit.litesignin.util.SignInDate;
import studio.trc.bukkit.litesignin.util.PluginControl;
import studio.trc.bukkit.litesignin.reward.util.SignInGroup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.trc.bukkit.litesignin.database.engine.SQLQuery;

/**
 * Data Storage for Users
 * @author Dean
 */
public interface Storage
    extends Statistics
{
    /**
     * Give user rewards.
     * @param retroactiveDate Date of wanting to retroactively sign-in, if not, it's null.
     */
    void giveReward(SignInDate retroactiveDate);
    
    /**
     * Get the year of last sign in.
     */
    int getYear();
    
    /**
     * Get the month of last sign in.
     */
    int getMonth();
    
    /**
     * Get the date of last sign in.
     */
    int getDay();
    
    /**
     * Get the hour of last sign in.
     */
    int getHour();
    
    /**
     * Get the minute of last sign in.
     */
     int getMinute();
    
    /**
     * Get the second of last sign in.
     */
    int getSecond();
    
    /**
     * Get the number of consecutive sign in for users.
     */
    int getContinuousSignIn();
    
    /**
     * Get the number of consecutive sign in of this month for users.
     */
     int getContinuousSignInOfMonth();
    
    /**
     * Get the number of retroactive cards.
     */
    int getRetroactiveCard();
    
    /**
     * Get the Player's instance.
     */
    Player getPlayer();
    
    /**
     * Get the player's name in database.
     */
    String getName();
    
    /**
     * Get the player's group.
     */
    SignInGroup getGroup();
    
    /**
     * Get the player's all matched groups.
     */
     List<SignInGroup> getAllGroup();
    
    /**
     * Obtaining historical records.
     */
    List<SignInDate> getHistory();
    
    /**
     * Setting the user's sign in history.
     */
    void setHistory(List<SignInDate> history, boolean saveData);
    
    /**
     * Set the specified time to the user's sign in time.
     */
    void signIn();
    
    /**
     * Set the current time to the user's sign as historicalDate.
     */
    void signIn(SignInDate historicalDate);
    
    /**
     * Give player a specified number of cards.
     */
    void giveRetroactiveCard(int amount);
    
    /**
     * Remove the specified number of cards from the player.
     */
    void takeRetroactiveCard(int amount);
    
    /**
     * Set the specified number of cards from the player.
     * Only the virtual prop mode is vaild.
     */
    void setRetroactiveCard(int amount, boolean saveData);
    
    /**
     * Set the specified time to the user's sign in time.
     */
    void setSignInTime(SignInDate date, boolean saveData);
    
    /**
     * Set the number of consecutive sign in.
     */
    void setContinuousSignIn(int number, boolean saveData);
    
    /**
     * Save user data.
     */
    void saveData();
    
    static Storage getPlayer(Player player) {
        if (PluginControl.useMySQLStorage()) {
            return MySQLStorage.getPlayerData(player);
        } else if (PluginControl.useSQLiteStorage()) {
            return SQLiteStorage.getPlayerData(player);
        } else {
            return YamlStorage.getPlayerData(player);
        }
    }
    
    static Storage getPlayer(String playername) {
        if (PluginControl.useMySQLStorage()) {
            for (MySQLStorage data : MySQLStorage.cache.values()) {
                if (data.getName().equalsIgnoreCase(playername)) {
                    return data;
                }
            }
            UUID uuid = null;
            try (SQLQuery query = MySQLEngine.getInstance().executeQuery("SELECT UUID FROM " + MySQLEngine.getInstance().getTableSyntax(DatabaseTable.PLAYER_DATA) + " WHERE Name = ?", playername)) {
                ResultSet rs = query.getResult();
                if (rs.next()) {
                    uuid = UUID.fromString(rs.getString("UUID"));
                }
            } catch (SQLException ignored) {}
            return uuid != null ? MySQLStorage.getPlayerData(uuid) : null;
        } else if (PluginControl.useSQLiteStorage()) {
            for (SQLiteStorage data : SQLiteStorage.cache.values()) {
                if (data.getName().equalsIgnoreCase(playername)) {
                    return data;
                }
            }
            UUID uuid = null;
            try (SQLQuery query = SQLiteEngine.getInstance().executeQuery("SELECT UUID FROM " + SQLiteEngine.getInstance().getTableSyntax(DatabaseTable.PLAYER_DATA) + " WHERE Name = ?", playername)) {
                ResultSet rs = query.getResult();
                if (rs.next()) {
                    uuid = UUID.fromString(rs.getString("UUID"));
                }
            } catch (SQLException ignored) {}
            return uuid != null ? SQLiteStorage.getPlayerData(uuid) : null;
        } else {
            return YamlStorage.getPlayerData(Bukkit.getOfflinePlayer(playername).getUniqueId());
        }
    }
    
    static Storage getPlayer(UUID uuid) {
        if (PluginControl.useMySQLStorage()) {
            return MySQLStorage.getPlayerData(uuid);
        } else if (PluginControl.useSQLiteStorage()) {
            return SQLiteStorage.getPlayerData(uuid);
        } else {
            return YamlStorage.getPlayerData(uuid);
        }
    }
}
