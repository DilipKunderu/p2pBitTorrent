package logger;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Logger {

    int peer_ID;

    protected final String defaultLogFile;

    public Logger(int peer_ID){
        this.peer_ID = peer_ID;
        this.defaultLogFile = "\\project\\log_peer_"+peer_ID+".log";
    }

    public void log(String s) throws IOException {
        log(defaultLogFile, s);
    }

    public void log(String f, String s) throws IOException {
        TimeZone tz = TimeZone.getTimeZone("EST"); // or PST, MID, etc ...
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy.mm.dd hh:mm:ss ");
        df.setTimeZone(tz);
        String currentTime = df.format(now);
        FileWriter aWriter = new FileWriter(f, true);
        aWriter.write(currentTime + ":" + peer_ID + s + "\n");
        aWriter.flush();
        aWriter.close();
    }
}
