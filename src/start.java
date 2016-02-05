import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by GreMal on 30.05.2015.
 */

public class start {
    private final static String LAST_VERSION_ARCHIVE_FILE_NAME = "./cloudsticker.zip";
    private final static String OUTPUT_FOLDER = ".";
    private final static String NEW_FILE_PREFIX = "new_";
    private static List<String> entryList = new ArrayList<String>();
    // Настройки файлов лога
    private static final String LOGFILE_PATTERN = "./start%g.log"; // имя лог-файла
    private static final int LOGFILE_SIZE = 10000; // размер одного лог-файла
    private static final int LOGFILES_NUMBER = 5; // количество лог-файлов
    private static final boolean LOGFILE_APPEND = true;
    protected static final Level LOG_LEVEL = Level.FINE; // уровень сообщений лога
    protected static Level LOG_TRIGGER = Level.OFF; // включаем отключаем логирование

    // private static Logger log = Logger.getLogger(start.class.getName());

    public static void main(String[] args) throws IOException{
        FileHandler handler = new FileHandler(LOGFILE_PATTERN, LOGFILE_SIZE, LOGFILES_NUMBER, LOGFILE_APPEND);
        handler.setFormatter(new SimpleFormatter());
        // handler.pattern = application_log.txt
        // java.util.logging.FileHandler.limit = 50
        // java.util.logging.FileHandler.count = 7
        // java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter
        handler.setLevel(LOG_TRIGGER);
        handler.publish(new LogRecord(LOG_LEVEL, "Input in main."));

        File zipFile = new File(LAST_VERSION_ARCHIVE_FILE_NAME);
        if(zipFile.exists()){
            handler.publish(new LogRecord(LOG_LEVEL, "Zip exist."));
            //create output directory is not exists
/*            File folder = new File(OUTPUT_FOLDER);
            if(!folder.exists()){
                folder.mkdir();
            }*/

            //System.out.println("Зип существует");


            ZipInputStream zis = new ZipInputStream(new FileInputStream(LAST_VERSION_ARCHIVE_FILE_NAME));
            FileOutputStream fos;
            ZipEntry zipEntry;
            File currentFile;
            byte[] buffer = new byte[1024];

            while ((zipEntry = zis.getNextEntry()) != null) {
                String relativeFileName = OUTPUT_FOLDER + "/" + NEW_FILE_PREFIX + zipEntry.getName();
                entryList.add(zipEntry.getName());
                handler.publish(new LogRecord(LOG_LEVEL, "Extracting: " + zipEntry.getName()));
                currentFile = new File(relativeFileName);
                if(currentFile.exists()){ currentFile.delete(); }
                fos = new FileOutputStream(relativeFileName);
                int readed = 0;
                while ((readed = zis.read(buffer)) != -1){
                    fos.write(buffer, 0, readed);
                }
                //System.out.println(zipEntry.getTime());
                zis.closeEntry();
                fos.flush();
                fos.close();
            }
            zis.close();

            for(String str : entryList){
                String relativeFileName = OUTPUT_FOLDER + "/" + NEW_FILE_PREFIX + str;
                handler.publish(new LogRecord(LOG_LEVEL, "Source: " + relativeFileName));
                File source = new File(relativeFileName);
                handler.publish(new LogRecord(LOG_LEVEL, "Destination: " + "./"+str));
                File destination = new File("./"+str);

                // пропускаем, чтобы случайно не пытаться перезаписать данный файл (start)
                // и чтобы не затереть ini файл с userID и deviceID
                if(destination.getName().equals("start.jar")){
                    handler.publish(new LogRecord(LOG_LEVEL, "Ignore: " + destination.getName()));
                    continue;
                }
                if(destination.getName().equals("cloudsticker.ini")){
                    handler.publish(new LogRecord(LOG_LEVEL, "Delete source: " + destination.getName()));
                    source.delete();
                    continue;
                }

                // раскомментировать
                handler.publish(new LogRecord(LOG_LEVEL, "Destination exist? " + destination.exists() + " : " + destination.getName()));
                if(destination.exists()){
                    handler.publish(new LogRecord(LOG_LEVEL, "Delete destination: " + destination.getName()));
                    destination.delete();
                }

                // раскомментировать
                handler.publish(new LogRecord(LOG_LEVEL, "Rename source: " + source.getName() + " to " + destination.getName()));
                source.renameTo(destination);
            }

            // раскомментировать
            handler.publish(new LogRecord(LOG_LEVEL, "Delete ZipFile"));
            zipFile.delete();
        }



/*        File newFile = new File("./lastversion.jar");
        File oldFile = new File("./CloudNotes.jar");
        if(newFile.exists()){
            if(oldFile.exists()){
                if(oldFile.delete()){ newFile.renameTo(oldFile); }
            }
        }*/
        //System.out.println("?????? ????????");
        Runtime.getRuntime().exec("java -jar CloudSticker.jar start");
        handler.close();
    }

}
