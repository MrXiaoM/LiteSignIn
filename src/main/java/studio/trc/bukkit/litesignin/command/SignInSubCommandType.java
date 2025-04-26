package studio.trc.bukkit.litesignin.command;

import lombok.Getter;

import org.jetbrains.annotations.Nullable;
import studio.trc.bukkit.litesignin.command.subcommand.*;

public enum SignInSubCommandType
{
    /**
     * /signin help
     */
    HELP("help", new HelpCommand(), "Help"),
    
    /**
     * /signin reload
     */
    RELOAD("reload", new ReloadCommand(), "Reload"),
    
    /**
     * /signin gui
     */
    GUI("gui", new GUICommand(), "GUI"),
    
    /**
     * /signin click
     */
    CLICK("click", new ClickCommand(), "Click"),
    
    /**
     * /signin save
     */
    SAVE("save", new SaveCommand(), "Save"),
    
    /**
     * /signin info
     */
    INFO("info", new InfoCommand(), "Info"),
    
    /**
     * /signin database
     */
    DATABASE("database", new DatabaseCommand(), "Database"),
    
    /**
     * /signin leaderboard
     */
    LEADERBOARD("leaderboard", new LeaderboardCommand(), "LeaderBoard"),
    
    /**
     * /signin itemcollection
     */
    ITEM_COLLECTION("itemcollection", new ItemCollectionCommand(), "ItemCollection"),
    
    /**
     * /signin retroactivecard
     */
    RETROACTIVE_CARD("retroactivecard", new RetroactiveCardCommand(), "RetroactiveCard"),
    
    /**
     * /signin reward
     */
    REWARD("reward", new RewardCommand(), "Reward");

    @Getter
    private final String subCommandName;
    @Getter
    private final SignInSubCommand subCommand;
    @Getter
    private final String commandPermissionPath;

    SignInSubCommandType(String subCommandName, SignInSubCommand subCommand, @Nullable String commandPermissionPath) {
        this.subCommandName = subCommandName;
        this.subCommand = subCommand;
        this.commandPermissionPath = commandPermissionPath;
    }

    /**
     * @param subCommand Sub command's name.
     */
    public static SignInSubCommandType getCommandType(String subCommand) {
        for (SignInSubCommandType type : values()) {
            if (type.getSubCommandName().equalsIgnoreCase(subCommand)) {
                return type;
            }
        }
        return null;
    }
}
