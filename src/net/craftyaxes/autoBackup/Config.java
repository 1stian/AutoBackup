import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Stian on 26.05.2016.
 */
public class Config {

    private static Config config = new Config();

    private Config(){
        ;
    }

    public static Config getConfig(){
        return  config;
    }

    private Path configFile = Paths.get(autoBackup.getAuto().getConfigDir() + "/autobackup.conf");
    private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
    private CommentedConfigurationNode configNode;


    public void init(){
        if (!Files.exists(configFile)){
            try {
                Files.createFile(configFile);
                load();
                populate();
                save();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            load();
        }
    }

    public void load(){
        try{
            configNode = configLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void save(){
        try
        {
            configLoader.save(configNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void populate() {
        get().getNode("autoBackup", "source").setValue("c:/minecraft").setComment("Where the minecraft server directory is located.");
        get().getNode("autoBackup", "destination").setValue("c:/backup").setComment("Where you want the backup files to be located. Please use a folder outside the source directory. Or else you will be backing up your backups.");
        get().getNode("Timer", "enabled").setValue("false").setComment("The timer is disabled by default");
        get().getNode("Timer", "interval").setValue("1440").setComment("The timer is set to 1440 which is 24 hours. Please specify the timer in minutes.");
    }

    public CommentedConfigurationNode get()
    {
        return configNode;
    }

    public static String getSource(){
        String source = Config.getConfig().get().getNode("autoBackup", "source").getString();
        source = source.replace("\\", "/");
        return source;
    }

    public static String getDesti(){
        String dest = Config.getConfig().get().getNode("autoBackup", "destination").getString();
        dest = dest.replace("\\", "/");
        return dest;
    }

    public static boolean getTimerStatus(){
        return Config.getConfig().get().getNode("Timer", "enabled").getBoolean();
    }

    public static int getTimerInterval(){
        return Config.getConfig().get().getNode("Timer", "interval").getInt();
    }

}
