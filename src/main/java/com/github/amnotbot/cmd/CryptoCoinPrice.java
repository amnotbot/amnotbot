package com.github.amnotbot.cmd;

import com.github.amnotbot.cmd.utils.BotURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

public class CryptoCoinPrice {
    private final String QUERY_URL = "https://api.blockchain.com/v3/exchange/tickers/";
    private final String[] defaultSymbols = {"BTC", "USD"};

    public CryptoCoinPrice() {
    }

    public JSONObject query(String args) throws IOException {
        BotURLConnection conn = new BotURLConnection(this.buildTickersURL(args));

        return (new JSONObject(conn.fetchURL()));
    }

    private URL buildTickersURL(String args)
        throws MalformedURLException
    {
        String[] symbols;
        if (args != null && !args.isEmpty()) {
            symbols = args.split(" ");
            if (symbols.length == 1) {
                symbols = new String[] {symbols[0], defaultSymbols[1]};
            }
        } else {
            symbols = defaultSymbols;
        }

        String url = this.QUERY_URL + symbols[0].toUpperCase() + "-" + symbols[1].toUpperCase();

        return (new URL(url));
    }
}
