package org.knix.amnotbot;

import java.io.IOException;

/**
 *
 * @author gpoppino
 */
public interface BotConnection {

    public void connect() throws IOException;

    public void doPrivmsg(String target, String msg);

    public void doNick(String nick);

    public void doQuit();

    public void doJoin(String room);

    public boolean isConnected();    

    public void print(String msg);

    public void print(String target, String msg);

    public void setTimeout(int millis);

    public void setEncoding(String encoding);

    public void setBotLogger(BotLogger logger);

    public BotLogger getBotLogger();

    public String getHost();

    public String getNick();

}
