package all;

import all.controllers.CrossroadController;
import main.java.system.Printer;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {

//	    configureLog4j("WARN");


		SpringApplication.run(Runner.class, args);
		new CrossroadController("crossroadexample", "address example");
	}

    private static void configureLog4j(String level) {
        Properties props = new Properties();
        props.put("log4j.rootLogger", level+", stdlog");
        props.put("log4j.appender.stdlog", "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.stdlog.target", "System.out");
        props.put("log4j.appender.stdlog.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.stdlog.layout.ConversionPattern",
                "%d{HH:mm:ss} %-5p %-25c{1} :: %m%n");
        // Execution logging
        props.put("log4j.logger.com.hp.hpl.jena.arq.info", level);
        props.put("log4j.logger.com.hp.hpl.jena.arq.exec", level);
        // TDB loader
        props.put("log4j.logger.org.apache.jena.tdb.loader", level);
        // Everything else in Jena
        props.put("log4j.logger.com.hp.hpl.jena", level);
        props.put("log4j.logger.org.apache.jena.riot", level);
        // TDB
        // TDB syslog.
        props.put("log4j.logger.TDB", level);
        props.put("log4j.logger.com.hp.hpl.jena.tdb", level);
        props.put("log4j.logger.com.hp.hpl.jena.tdb.transaction", level);
        props.put("log4j.logger.com.hp.hpl.jena.tdb.transaction.NodeTableTrans",
                level);
        props.put("log4j.logger.com.hp.hpl.jena.tdb.transaction.TransactionManager",level);
        props.put("log4j.logger.com.hp.hpl.jena.tdb.transaction.TestTransSystem",level);
        // Joseki server
        props.put("log4j.logger.org.joseki", level);
        props.put("log4j.logger.org.springframework", level);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);

        Printer.getInstance().print("current loggers are: " + LogManager.getCurrentLoggers(), "yellow");

        Printer.getInstance().print("settings changed", "yellow");
    }
}
