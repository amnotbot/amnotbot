package com.github.amnotbot.cmd;

import java.io.IOException;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;

import org.json.JSONObject;

public class CryptoCoinPriceCommand implements BotCommand
{
    @Override
    public void execute(BotMessage msg)
    {
        CryptoCoinPrice cryptoCoinPrice = new CryptoCoinPrice();
        try {
            JSONObject answer = cryptoCoinPrice.query(msg.getParams());

            if (answer.optString("symbol").isEmpty()) {
                msg.getConn().doPrivmsg(msg.getTarget(), "Symbols not found!");
            } else {
                msg.getConn().doPrivmsg(msg.getTarget(), "Symbols: " + answer.optString("symbol") + ", Last trade: $" + answer.optString("last_trade_price") + " , Last 24H: $" + answer.optString("price_24h"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String help() {
        return null;
    }
}
