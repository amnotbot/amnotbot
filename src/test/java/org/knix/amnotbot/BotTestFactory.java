package org.knix.amnotbot;

import org.knix.amnotbot.cmd.db.TableOperations;
import org.knix.amnotbot.cmd.db.SqliteQuoteTableOperations;
import org.knix.amnotbot.cmd.db.HsqldbQuoteTableOperations;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotTestFactory
{
    private String backend;

    public BotTestFactory()
    {
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
