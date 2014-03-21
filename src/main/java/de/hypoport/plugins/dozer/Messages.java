package de.hypoport.plugins.dozer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class Messages extends ResourceBundle {

  private static final Properties properties = new Properties();

  static {
    try {
      properties.load(Messages.class.getResourceAsStream("Messages.properties"));
    }
    catch (IOException e) {
      throw new RuntimeException("Laden der Properties fehlgeschlagen", e);
    }
  }

  @Override
  protected Object handleGetObject(String key) {
    return properties.getProperty(key);
  }

  @SuppressWarnings("unchecked")
  @NotNull
  @Override
  public Enumeration<String> getKeys() {
    return (Enumeration<String>) properties.propertyNames();
  }

  public static String getMessage(String key, Object... args) {
    try {
      String message = properties.getProperty(key);

      if (args == null || args.length == 0) {
        return message;
      }
      else {
        return MessageFormat.format(message, args);
      }
    }
    catch (Exception e) {
      return "???" + key + "???";
    }
  }
}
