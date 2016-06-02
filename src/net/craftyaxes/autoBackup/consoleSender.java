import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

/**
 * Created by Stian on 02.06.2016.
 */
public class consoleSender {

    public static void recCommand(String command){
        Scheduler schedula = Sponge.getScheduler();
        Task.Builder taskBuilder = schedula.createTaskBuilder();

        Task taskSave = (Task) taskBuilder.execute(() -> consoleSender.sendCommand(command))
                .name("AutoBackup - Send some commands!").submit(autoBackup.getAuto());
    }

    public static void sendCommand(String command){
        //Get console
        CommandSource console = autoBackup.console;
        java.util.Optional<? extends CommandMapping> optCommandMapoping = autoBackup.cManager.get(command, console);
        if (optCommandMapoping.isPresent()){
            CommandMapping mapping = optCommandMapoping.get();
            try
            {
                mapping.getCallable().process(console, "");
            }catch (CommandException e){
                e.printStackTrace();
            }
        }
    }
}
