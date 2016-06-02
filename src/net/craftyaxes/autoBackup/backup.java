import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Stian on 03.06.2016.
 */
public class backup {

    public static void backupWork(){
        MessageChannel all = MessageChannel.TO_ALL;

        final String desti = Config.getDesti();
        final String source = Config.getSource();

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        final Date date = new Date();

        all.send(Text.of("Server backup started - There might be some lagg."));

        //Send commands
        consoleSender.recCommand("save-all");
        consoleSender.recCommand("save-off");

        try {
            zipFolder(source, desti + "\\minecraft-" + dateFormat.format(date) + ".zip");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void runAsync() {
        int interval = Config.getTimerInterval();

        Scheduler schedula = Sponge.getScheduler();
        Task.Builder taskBuilder = schedula.createTaskBuilder();

        Task task = (Task) taskBuilder.execute(() -> backupWork()).async().delay(TimeUnit.MINUTES.toMillis(interval), TimeUnit.MILLISECONDS)
                .interval(TimeUnit.MINUTES.toMillis(interval), TimeUnit.MILLISECONDS)
                .name("AutoBackup - backup process has started!").submit(autoBackup.getAuto());
    }

    public static void runAsyncNow(){
        int interval = Config.getTimerInterval();

        Scheduler schedula = Sponge.getScheduler();
        Task.Builder taskBuilder = schedula.createTaskBuilder();

        Task task = (Task) taskBuilder.execute(() -> backupWork()).async()
                .name("AutoBackup - backup process has started!").submit(autoBackup.getAuto());
    }

    static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
        MessageChannel all = MessageChannel.TO_ALL;
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();

        consoleSender.recCommand("save-on");
        all.send(Text.of("Server backup finished"));
    }

    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws Exception {

        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }
}
