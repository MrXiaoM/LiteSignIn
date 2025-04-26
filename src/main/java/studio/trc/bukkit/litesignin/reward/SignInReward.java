package studio.trc.bukkit.litesignin.reward;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litesignin.api.Storage;
import studio.trc.bukkit.litesignin.reward.command.SignInRewardCommand;
import studio.trc.bukkit.litesignin.reward.util.SignInSound;
import studio.trc.bukkit.litesignin.reward.util.SignInGroup;

public interface SignInReward
{
    /**
     * Give reward.
     */
    void giveReward(Storage playerData);
    /**
     * Get SignInReward permission group.
     */
    SignInGroup getGroup();
    /**
     * Get SignInReward module
     * It is used to indicate the reward form of SignInReward.
     */
    SignInRewardModule getModule();
    List<String> getMessages();
    List<String> getBroadcastMessages();
    List<SignInRewardCommand> getCommands();
    List<SignInSound> getSounds();
    List<ItemStack> getRewardItems(Player player);
}
