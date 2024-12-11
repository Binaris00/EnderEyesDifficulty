package eu.asangarin.endereyesdifficulty.event;

import eu.asangarin.endereyesdifficulty.EnderDifficulty;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;

public class WorldLoadListener {
    private List DIFFICULTIES = List.of("Peaceful", "Normal", "Hard", "Nightmare");

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load evt) {
        final GameRules rules = evt.getLevel().getLevelData().getGameRules();

        System.out.println("Difficulty: " + evt.getLevel().getDifficulty());


        if(evt.getLevel().getDifficulty() == Difficulty.EASY) {
            rules.getRule(GameRules.RULE_KEEPINVENTORY).set(true, evt.getLevel().getServer());
        }
        else if(evt.getLevel().getDifficulty() == Difficulty.NORMAL) {
            rules.getRule(GameRules.RULE_KEEPINVENTORY).set(false, evt.getLevel().getServer());
        }
        else if(evt.getLevel().getDifficulty() == Difficulty.HARD) {
            rules.getRule(GameRules.RULE_KEEPINVENTORY).set(false, evt.getLevel().getServer());
        }

        evt.getLevel().getServer().getPlayerList().getPlayers().forEach(GameStageHelper::syncPlayer);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event){
        if(event.getServer().getLevel(Level.OVERWORLD).getGameTime() % 100 == 0){
            Difficulty difficulty = event.getServer().overworld().getDifficulty();

            if(difficulty == Difficulty.EASY) {
                remover(event.getServer());
                event.getServer().getPlayerList().getPlayers().forEach(player -> {
                    GameStageHelper.addStage(player, "normal");
                });
            }
            else if(difficulty == Difficulty.NORMAL) {
                remover(event.getServer());
                event.getServer().getPlayerList().getPlayers().forEach(player -> {
                    GameStageHelper.addStage(player, "hard");
                });
            }
            else if(difficulty == Difficulty.HARD) {
                remover(event.getServer());
                event.getServer().getPlayerList().getPlayers().forEach(player -> {
                    GameStageHelper.addStage(player, "nightmare");
                });
            }
        }
    }



    private void remover(MinecraftServer server){
        server.getPlayerList().getPlayers().forEach(player ->{
            if(GameStageHelper.hasAnyOf(player, "peaceful", "normal", "hard", "nightmare")){
                GameStageHelper.removeStage(player, "peaceful");
                GameStageHelper.removeStage(player, "normal");
                GameStageHelper.removeStage(player, "hard");
                GameStageHelper.removeStage(player, "nightmare");
            }
        });
    }


//    public static MinecraftServer getMinecraftServer() {
//        if (minecraftServer == null) {
//            minecraftServer = ServerLifecycleHooks.getCurrentServer();
//        }
//        return minecraftServer;
//    }

}
