/*
 * Author: Geronimo Poppino.
 */
package com.github.amnotbot.cmd;



import java.sql.SQLException;

import com.github.amnotbot.*;
import com.github.amnotbot.cmd.db.BotDBFactory;
import com.github.amnotbot.cmd.db.QuoteDAO;
import com.github.amnotbot.cmd.db.QuoteEntity;
import com.github.amnotbot.cmd.utils.CmdOptionImp;
import com.github.amnotbot.cmd.utils.CommandOptions;

public class QuoteImp
{
    private BotMessage msg;
    private QuoteDAO quoteDAO;
    private CommandOptions opts;    

    public QuoteImp(BotMessage msg)
    {
        this.msg = msg;
        this.quoteDAO = null;
        this.opts = new CommandOptions(msg.getText());

        this.opts.addOption(new CmdOptionImp("text"));
        this.opts.addOption(new CmdOptionImp("id"));
        this.opts.addOption(new CmdOptionImp("op"));
    }

    public void run()
    {
        this.quoteDAO = BotDBFactory.instance().createQuoteDAO();
        try {                        
            this.performAction();
        } catch (SQLException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }       
    }

    private void performAction() throws SQLException
    {
        this.opts.buildArgs();
        
        if (this.opts.getOption("op").hasValue()) {
            String op = this.opts.getOption("op").tokens()[0];

            if (op.compareTo("set") == 0) {
                this.createNewQuote(this.opts.getOption("text").tokens()[0]);
            } else if (op.compareTo("del") == 0) {
                this.deleteQuote(this.opts.getOption("id").tokens()[0]);
            } else if (op.compareTo("get") == 0) {
                this.getRandomQuote();
            } else if (op.compareTo("info") == 0) {
                this.getInfoAboutQuote(this.opts.getOption("id").tokens()[0]);
            }
        } else {
            this.getRandomQuote();
        }
    }
   
    private void createNewQuote(String text) throws SQLException
    {     
        QuoteEntity quote = new QuoteEntity();
        
        quote.setQuote(text);
        quote.setUser( this.msg.getUser().getNick() );

        boolean success = false;
        success = this.quoteDAO.save(quote);

        String m;
        if (success) {
            m = "Quote successfully created!";           
        } else {
            m = "Quote creation failed!";       
        }
        this.msg.getConn().doPrivmsg(this.msg.getTarget(), m);
    }

    private void deleteQuote(String id) throws SQLException
    {
        boolean success = false;
        success = this.quoteDAO.delete( Integer.parseInt(id) );
        String m;
        if (success) {
            m = "Quote successfully deleted!";
        } else {
            m = "Quote deletion failed!";
        }
        this.msg.getConn().doPrivmsg(this.msg.getTarget(), m);
    }

    private void getRandomQuote() throws SQLException
    {
        QuoteEntity quote;

        quote = this.quoteDAO.findRandom();
        
        if (quote.getId() >= 0) {
            String m = "(" + Integer.toString( quote.getId() ) + ")" + ": " +
                    "\"" + quote.getQuote() + "\"";
            this.msg.getConn().doPrivmsg(this.msg.getTarget(), "Quote " + m);            
        }
    }

    private void getInfoAboutQuote(String id) throws SQLException
    {
        QuoteEntity quote;
        
        quote = this.quoteDAO.findById( Integer.parseInt(id) );

        if (quote.getId() >= 0) {
            this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                    "Quote (" + quote.getId() + ") submitted by " +
                    quote.getUser());
        }
    }
}