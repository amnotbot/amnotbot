package com.github.amnotbot.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.cmd.utils.Utf8ResourceBundle;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.lang.SystemUtils;

/**
 * Bot configuration
 */
public final class BotConfiguration
{

    private static Configuration config = null;
    private static Configuration commands = null;
    private static Configuration tasks = null;
    private static Configuration appProperties = null;
    private static BotConfiguration botConfig = null;
    private static File home = null;

    protected BotConfiguration()
    {
        this.init();
    }

    public static void setHomeDir(final String home) {
        BotConfiguration.home = new File(home);
    }

    private void init() {
        try {

            if (BotConfiguration.home == null) {
                BotConfiguration.home = new File(SystemUtils.getUserHome(), ".amnotbot");
            }
            BotConfiguration.home.mkdirs();

            boolean firstRun = false;
            if (this.copyConfigFile("amnotbot.config")) {
                firstRun = true;
            }
            this.copyConfigFile("tasks.config");
            this.copyConfigFile("commands.config");

            if (firstRun) {
                this.runFirstSetupAndExit();
            }

        } catch (final FileNotFoundException e) {
            BotLogger.getDebugLogger().debug(e);
        } catch (final IOException e) {
            BotLogger.getDebugLogger().debug(e);
        }
    }

    private void runFirstSetupAndExit() {
        Configuration appConfig;
        Locale currentLocale;
        ResourceBundle welcomeMessage;
        appConfig = BotConfiguration.getAppProperties();

        currentLocale = new Locale(BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        welcomeMessage = Utf8ResourceBundle.getBundle("WelcomeMessageBundle", currentLocale);

        String version, buildNumber;
        version = appConfig == null ? "Unknown" : appConfig.getString("application.version");
        buildNumber = appConfig == null ? "Unknown" : appConfig.getString("application.buildnumber");
        final Object[] messageArguments = { version, buildNumber,
                BotConfiguration.home + File.separator + "amnotbot.config", };

        final MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(welcomeMessage.getString("template"));

        final String output = formatter.format(messageArguments);

        System.out.println("\n\n");
        System.out.println(output);
        System.out.println("\n\n");
    }

    private boolean copyConfigFile(final String filename) throws FileNotFoundException, IOException {
        final File fileOnDisk = new File(BotConfiguration.home, File.separator + filename);
        if (fileOnDisk.exists()) {
            return false;
        }

        System.out.println("Copying file " + filename + " to " + fileOnDisk.getAbsolutePath());
        final ClassLoader classLoader = this.getClass().getClassLoader();

        final InputStream in = classLoader.getResourceAsStream(filename);
        final OutputStream out = new FileOutputStream(fileOnDisk);

        int len;
        final byte[] buf = new byte[1024];
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

        return true;
    }

    private static String getConfigFilePath(final String filename) {
        final File f = new File(BotConfiguration.home, File.separator + filename);
        if (f.exists()) {
            return f.getAbsolutePath();
        }

        return filename;
    }

    public static Configuration getConfig() {
        if (botConfig == null) {
            botConfig = new BotConfiguration();

            BotLogger.getDebugLogger().debug("Welcome!");

            try {
                final String filename = BotConfiguration.getConfigFilePath("amnotbot.config");
                config = new PropertiesConfiguration(filename);
            } catch (final ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
                if (config == null) {
                    config = new PropertiesConfiguration();
                }
            }
        }
        return config;
    }

    public static Configuration getCommandsConfig() {
        if (commands == null) {
            try {
                final String filename = BotConfiguration.getConfigFilePath("commands.config");
                commands = new PropertiesConfiguration(filename);
            } catch (final ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return commands;
    }

    public static Configuration getTasksConfig() {
        if (tasks == null) {
            try {
                final String filename = BotConfiguration.getConfigFilePath("tasks.config");
                tasks = new PropertiesConfiguration(filename);
            } catch (final ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return tasks;
    }

    public static Configuration getAppProperties() {
        if (appProperties == null) {
            try {
                appProperties = new PropertiesConfiguration("application.properties");
            } catch (final ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return appProperties;
    }
}
