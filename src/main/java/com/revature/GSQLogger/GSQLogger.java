package com.revature.GSQLogger;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GSQLogger {
    private static final GSQLogger logger = new GSQLogger();
    private PrintWriter log;
    private DateTimeFormatter format;

    /**
     * private constructor for Logger class.
     * creates a new static instance of class.
     */
    private GSQLogger() {
        try {
            log = new PrintWriter(new BufferedWriter(new FileWriter("src/main/resources/GSQLog.txt",false)));
            format = DateTimeFormatter.ofPattern("yy-MM-dd: HH:mm:ss");

        }catch(IOException ioe) {
            //do something here.
        }
    }

    public static GSQLogger getInstance() {
        return logger;
    }

    private void writeToLogger(final String message) {
        System.out.println("Sorry, there was a problem with your request. Please show log file to a smart developer.");
        log.write("\n======================================\n");
        log.write(LocalDateTime.now().format(format) + "\n\t");
        log.write(message);
    }

    public void writeError(final String error) {
        writeToLogger(error);
        log.flush();
    }


    public void writeError(final Exception e) {
        writeToLogger("");
        e.printStackTrace(log);
        log.flush();
    }
}
