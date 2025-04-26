package studio.trc.bukkit.litesignin;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import studio.trc.bukkit.litesignin.config.ConfigurationType;
import studio.trc.bukkit.litesignin.config.ConfigurationUtil;
import studio.trc.bukkit.litesignin.command.SignInCommand;
import studio.trc.bukkit.litesignin.command.SignInSubCommandType;
import studio.trc.bukkit.litesignin.thread.LiteSignInThread;
import studio.trc.bukkit.litesignin.util.MessageUtil;
import studio.trc.bukkit.litesignin.database.util.BackupUtil;
import studio.trc.bukkit.litesignin.database.storage.MySQLStorage;
import studio.trc.bukkit.litesignin.database.storage.SQLiteStorage;
import studio.trc.bukkit.litesignin.database.engine.MySQLEngine;
import studio.trc.bukkit.litesignin.database.engine.SQLiteEngine;
import studio.trc.bukkit.litesignin.event.Menu;
import studio.trc.bukkit.litesignin.event.Quit;
import studio.trc.bukkit.litesignin.event.Join;
import studio.trc.bukkit.litesignin.nms.NMSManager;
import studio.trc.bukkit.litesignin.util.PluginControl;
import studio.trc.bukkit.litesignin.util.SignInPluginProperties;
import studio.trc.bukkit.litesignin.util.SkullsUtil;
import studio.trc.bukkit.litesignin.util.woodsignscript.WoodSignEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Do not resell the source code of this plug-in.
 * @author TRCStudioDean
 */
public class Main
    extends JavaPlugin
{
    /**
     * Main instance
     */
    private static Main main;
    private final FoliaLib foliaLib;
    public Main() {
        foliaLib = new FoliaLib(this);
    }

    public PlatformScheduler getScheduler() {
        return foliaLib.getScheduler();
    }
    
    @Override
    public void onEnable() {
        main = this;
        SkullsUtil.init();
        
        SignInPluginProperties.reloadProperties();
        
        if (!getDescription().getName().equals("LiteSignIn")) {
            SignInPluginProperties.sendOperationMessage("PluginNameChange");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        registerCommandExecutor();
        registerEvent();
        PluginControl.reload();
        NMSManager.reloadNMS();
        SignInPluginProperties.sendOperationMessage("PluginEnabledSuccessfully", MessageUtil.getDefaultPlaceholders());
    }
    
    @Override
    public void onDisable() {
        LiteSignInThread.getTaskThread().setRunning(false);
        SignInPluginProperties.sendOperationMessage("AsyncThreadStopped", MessageUtil.getDefaultPlaceholders());
        if (PluginControl.useMySQLStorage()) {
            MySQLStorage.cache.values().forEach(MySQLStorage::saveData);
        } else if (PluginControl.useSQLiteStorage()) {
            SQLiteStorage.cache.values().forEach(SQLiteStorage::saveData);
        }
        if (ConfigurationUtil.getConfig(ConfigurationType.CONFIG).getBoolean("Database-Management.Backup.Auto-Backup")) {
            MessageUtil.sendMessage(getServer().getConsoleSender(), "Database-Management.Backup.Auto-Backup");
            BackupUtil.startSyncBackup(getServer().getConsoleSender());
        }
        if (SQLiteEngine.getInstance() != null) {
            SQLiteEngine.getInstance().disconnect();
        }
        if (MySQLEngine.getInstance() != null) {
            MySQLEngine.getInstance().disconnect();
        }
        foliaLib.getScheduler().cancelAllTasks();
    }
    
    public static Main getInstance() {
        return main;
    }

    private void registerEvent() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Join(), Main.getInstance());
        pm.registerEvents(new Menu(), Main.getInstance());
        pm.registerEvents(new Quit(), Main.getInstance());
        pm.registerEvents(new WoodSignEvent(), Main.getInstance());
        SignInPluginProperties.sendOperationMessage("PluginListenerRegistered");
    }
    
    private void registerCommandExecutor() {
        PluginCommand command = getCommand("signin");
        if (command != null) {
            SignInCommand commandExecutor = new SignInCommand();
            command.setExecutor(commandExecutor);
            command.setTabCompleter(commandExecutor);
            for (SignInSubCommandType subCommandType : SignInSubCommandType.values()) {
                SignInCommand.getSubCommands().put(subCommandType.getSubCommandName(), subCommandType.getSubCommand());
            }
            SignInPluginProperties.sendOperationMessage("PluginCommandRegistered");
        }
    }
}
