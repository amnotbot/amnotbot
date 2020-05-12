package com.github.amnotbot.hbm;

import java.util.Date;

public class Quote {
    private Long id;

    private String server;
    private String channel;
    private String nick;
    private String text;
    private Date date;

    public Quote() {
        // this form used by Hibernate
    }

    public Quote(String server, String channel, String nick, String text, Date date) {
        // for application use, to create new events
        this.server = server;
        this.channel = channel;
        this.nick = nick;
		this.text = text;
		this.date = date;
	}

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

	public Long getId() {
		return this.id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
