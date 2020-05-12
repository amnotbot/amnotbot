package com.github.amnotbot.cmd;

import java.util.Date;
import java.util.List;

import com.github.amnotbot.BotMessage;
import com.github.amnotbot.hbm.BotHBSessionFactory;
import com.github.amnotbot.hbm.Quote;

import org.hibernate.Session;

public class QuoteCommandImp {

    public QuoteCommandImp() {

    }
    public void showRandomQuote(final BotMessage message) {
        final Session session = BotHBSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
        final List results = session.createQuery("from Quote order by rand()").list();
        if (results != null) {
            Quote quote = (Quote) results.get(0);
            message.getConn().doPrivmsg(message.getTarget(), "Quote: '" + quote.getText() + "' - in " + quote.getChannel()
                    + " by " + quote.getNick() + " on " + quote.getDate());
        }
        session.getTransaction().commit();
        session.close();
    }

    public void addQuote(final BotMessage message) {
        final Session session = BotHBSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
        session.save( new Quote(message.getConn().getHost(), message.getTarget(), message.getUser().getNick(), message.getParams(), new Date()) );
        session.getTransaction().commit();
        session.close();
    }
}
