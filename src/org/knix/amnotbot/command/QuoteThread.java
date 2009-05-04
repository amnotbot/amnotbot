/*
 * Author: Geronimo Poppino.
 */
package org.knix.amnotbot.command;

import org.knix.amnotbot.command.utils.CommandOptions;
import org.knix.amnotbot.command.utils.CmdStringOption;
import org.knix.amnotbot.*;
import java.util.Random;

import org.apache.commons.lang.SystemUtils;

import SQLite.Database;
import SQLite.Exception;
import SQLite.TableResult;

public class QuoteThread extends Thread
{

    private Database db;
    private BotMessage msg;
    private String db_filename;    
    private CommandOptions opts;

    public QuoteThread(BotMessage msg)
    {
        this.msg = msg;
        this.db_filename = SystemUtils.getUserHome() + "/" + ".amnotbot" +
                "/" + "quotes.db";
        this.db = new Database();
        this.opts = new CommandOptions(msg.getText());

        this.opts.addOption(new CmdStringOption("text", '"'));
        this.opts.addOption(new CmdStringOption("id"));
        this.opts.addOption(new CmdStringOption("op"));

        start();
    }

    public void run()
    {
        try {
            this.db.open(this.db_filename, 0);
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e.getMessage());
            return;
        }

        this.opts.buildArgs();

        if (this.opts.getOption("op").hasValue()) {
            String op = this.opts.getOption("op").stringValue();

            if (op.compareTo("set") == 0) {
                this.createNewQuote(this.opts.getOption("text").stringValue());
            } else if (op.compareTo("del") == 0) {
                this.deleteQuote(this.opts.getOption("id").stringValue());
            } else if (op.compareTo("get") == 0) {
                this.getRandomQuote();
            } else if (op.compareTo("info") == 0) {
                this.getInfoAboutQuote(this.opts.getOption("id").stringValue());
            }
        } else {
            this.getRandomQuote();
        }

        try {
            this.db.close();
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }

    private void createNewQuote(String text)
    {     
        if (text == null) return;

        String query;
        query = "INSERT INTO quotes (nick, desc) VALUES (" + "'" +
                this.msg.getUser().getNick() + "'" + ", " + "\"" + text +
                "\"" + ");";

        this.execQuery(query);

        String m;
        if (this.db.changes() > 0) {
            m = "Quote (" + this.db.last_insert_rowid() +
                    ") successfully created!";
            this.msg.getConn().doPrivmsg(this.msg.getTarget(), m);
        } else {
            m = "Quote creation failed!";
            this.msg.getConn().doPrivmsg(this.msg.getTarget(), m);
        }
    }

    private void deleteQuote(String id)
    {
        String query;

        if (id == null) return;
 
        query = "DELETE FROM quotes WHERE id=" + Integer.valueOf(id);

        this.execQuery(query);
    }

    private void execQuery(String query)
    {
        BotLogger.getDebugLogger().debug(query);

        try {
            this.db.exec(query, new QuoteTableFmt());
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }

    private TableResult runQuery(String query)
    {
        TableResult results;

        try {
            results = this.db.get_table(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (results.nrows <= 0) return null;
        return results;
    }

    private void getRandomQuote()
    {
        Random rand;
        String query;
        TableResult results;

        query = "SELECT * FROM quotes";

        results = this.runQuery(query);

        if (results != null) {
            rand = new Random();
            String[] r = 
                    (String[]) results.rows.get(rand.nextInt(results.nrows));
            String m = "(" + r[0] + ")" + ": " + "\"" + r[2] + "\"";
            this.msg.getConn().doPrivmsg(this.msg.getTarget(), "Quote " + m);
        }
    }

    private void getInfoAboutQuote(String id)
    {
        if (id == null) return;

        String query;
        TableResult results;

        query = "SELECT * FROM quotes WHERE id=" + Integer.valueOf(id);
        results = this.runQuery(query);
        if (results != null) {
            String[] r = (String[]) results.rows.get(0);
            this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                    "Quote (" + id + ") submitted by " + r[1]);
        }
    }
}

class QuoteTableFmt implements SQLite.Callback
{

    public QuoteTableFmt() { }
   
    public void columns(String[] arg0) { }

    public boolean newrow(String[] arg0)
    {
        return false;
    }

    public void types(String[] arg0) { }
}
