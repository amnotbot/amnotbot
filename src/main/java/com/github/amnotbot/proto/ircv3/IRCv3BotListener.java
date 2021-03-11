package com.github.amnotbot.proto.ircv3;

import java.util.List;

import com.github.amnotbot.BotMessage;
import com.github.amnotbot.BotMessageNotifier;
import com.github.amnotbot.BotTaskBuilderFile;
import com.github.amnotbot.BotTaskConstructor;
import com.github.amnotbot.BotTaskManager;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionEstablishedEvent;

import net.engio.mbassy.listener.Handler;

public class IRCv3BotListener {

    IRCv3BotConnection conn = null;
    List<String> channels;
    private BotTaskManager taskManager;

    public IRCv3BotListener(IRCv3BotConnection conn, List<String> channels) {
        this.conn = conn;
        this.channels = channels;
    }

    private void initTaskManager() {
        BotTaskConstructor tc = new BotTaskConstructor(new BotTaskBuilderFile());
        this.taskManager = tc.construct(conn, channels);
    }

    @Handler
    public void onPrivmsg(ChannelMessageEvent event) {
        User user = event.getActor();

        BotMessageNotifier.instance().notify(
            new BotMessage(this.conn, event.getChannel().getMessagingName(), new IRCv3BotUser(user), event.getMessage()));
    }

    @Handler
    public void onDisconnect(ClientConnectionClosedEvent event) {
        this.taskManager.cancelTasks();
        this.taskManager = null;
    }

    @Handler
    public void onReconnect(ClientConnectionEstablishedEvent event) {
        this.initTaskManager();
        this.channels.stream().forEach(c -> event.getClient().addChannel(c));
    }
}
