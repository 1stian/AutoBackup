/**
 * Created by Stian on 25.05.2016.
 */

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "autobackup", name = "AutoBackup", version = "0.1", description = "Takes backup of the whole minecraft server directory")
public class autoBackup {

    public static autoBackup autobackup;
    public static ConsoleSource console;
    public static CommandManager cManager;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfig;

    public static autoBackup getAuto()
    {
        return autobackup;
    }

    @Inject
    public Logger logger;

    @Listener
    public void onPreInit(GamePreInitializationEvent event){
        autobackup = this;
        console = Sponge.getServer().getConsole();
        cManager = Sponge.getCommandManager();

        if (!Files.exists(defaultConfig)){
            if (Files.exists(defaultConfig.resolveSibling("autobackup"))){
                try
                {
                    Files.move(defaultConfig.resolveSibling("autobackup"), defaultConfig);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    Files.createDirectories(defaultConfig);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        Config.getConfig().init();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        //logger.info("#############################");
        //logger.info("AutoBackup created by stiiaN");
        //logger.info("www.craftyaxes.net");
        //logger.info("#############################");

        buildCommands();
    }

    public void buildCommands() {
        CommandSpec runBackup = CommandSpec.builder()
                .description(Text.of("Will take backup of the server"))
                .permission("autobackup.run")
                .executor(new backupCommand())
                .build();

        Sponge.getCommandManager().register(this, runBackup, "autobackup", "ab");

        backupTimer();
    }

    public void backupTimer(){
        boolean status = Config.getTimerStatus();
        int interval = Config.getTimerInterval();

        if (status){
            backup.runAsync();
        }
    }

    public Path getConfigDir()
    {
        return defaultConfig;
    }
}