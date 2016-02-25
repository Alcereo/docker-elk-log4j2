package example.rolandkofler.logstash.log4j2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.UUID;

import org.apache.logging.log4j.EventLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.message.StructuredDataMessage;



public class EventLoggerMain {
	private static NumberFormat numberFormat;

	String[] account = new String [] {"Marlene Dietrich", "Anna Sacher", "Massimo Carisi",
			"George Washington", "Pierre Boulez", "Werner Heissenberg", "Lise Meitner", "Keiko Abe"};

	private Logger logger = LogManager.getLogger(EventLoggerMain.class.getName());
	public EventLoggerMain() {
		int transactionCount = 10000;
		for(;transactionCount>0; transactionCount--) {
			long millis= getExponentiallyDistributedRandomVariable();
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
			}
			StructuredDataMessage msg = transferMoney();
			//EventLogger.logEvent(msg);
			logger.info(msg);

		}
	}

	private long getExponentiallyDistributedRandomVariable() {
		double lambda=2;
		return Math.round(Math.log(1-Math.random()*1000)/(-lambda));
	}

	private StructuredDataMessage transferMoney() {
		String confirm = UUID.randomUUID().toString().substring(0, 4);
        StructuredDataMessage msg = new StructuredDataMessage(confirm, null, "transfer");
        int size = account.length-1;
		int i = (int) Math.round(Math.random()*size);
		int j = (int) Math.round(Math.random()*size);
		double amount = Math.random()*1000*1000;
		msg.put("toAccount", account[i]);
        msg.put("fromAccount", account[j]);
		msg.put("amount", numberFormat.format(amount));
        msg.put("currency", "USD");
		return msg;
	}

	public static void main(String[] args) {
		ConfigurationSource source = initLogger();
		Configurator.initialize(null, source);

		initNumberFormat();
		
		EventLoggerMain m= new EventLoggerMain();

	}

	private static void initNumberFormat() {
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
	}

	private static ConfigurationSource initLogger() {
		String log4jConfigFile = "./log4j2.xml";
		ConfigurationSource source=null;
		try {
			ClassLoader cl = EventLoggerMain.class.getClassLoader();
			InputStream resource = cl.getResourceAsStream(log4jConfigFile);
			source = new ConfigurationSource(new BufferedInputStream(resource));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return source;
	}

}
