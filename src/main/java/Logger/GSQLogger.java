package Logger;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GSQLogger {
    private static final GSQLogger logger = new GSQLogger();
    private PrintWriter log;
    private final SimpleDateFormat formatter;

    private GSQLogger() {
        super();
        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            log = new PrintWriter(new BufferedWriter(new FileWriter("src/main/resources/GSQL_log.txt",false)));
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static GSQLogger getInstance() {
        return logger;
    }

    public void writeError(final Exception e) {
        System.out.println("Sorry, there was a problem with your request. Please show log file to a developer.");
        log.write("\n======================================\n");
        log.write(formatter.format(new Date()) + "\n");
        e.printStackTrace(log);
        log.flush();
    }
}
