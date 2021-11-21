package bsu.pischule.csab.imdb.config;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.DateFormatter;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class AppConfig {
    public final static Locale APP_LOCALE = new Locale("ru", "RU");
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
}
