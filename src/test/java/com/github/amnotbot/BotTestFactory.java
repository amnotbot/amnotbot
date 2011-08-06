package com.github.amnotbot;


import com.github.amnotbot.cmd.db.HsqldbQuoteTableOperations;
import com.github.amnotbot.cmd.db.SqliteQuoteTableOperations;
import com.github.amnotbot.cmd.db.TableOperations;
import com.github.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotTestFactory
{
    private String backend;

    public BotTestFactory()
    {
        BotConfiguration.setHomeDir("target/.amnotbot");
        this.backend = BotConfiguration.getConfig().getString("backend");
    }

    public TableOperations createTableOperationsObject()
    {
        if (this.backend.equals("sqlite")) {
            return new SqliteQuoteTableOperations();
        } else if (this.backend.equals("hsqldb")) {
            return new HsqldbQuoteTableOperations();
        }
        return null;
    }
}
