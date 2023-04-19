package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.BotURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenAICommandImp
{
    private final String key;
    private final String model;

    public OpenAICommandImp(String key, String model)
    {
        this.key = key;
        this.model = model;
    }

    private JSONObject buildPayload(String userContent)
    {
        JSONObject payload = new JSONObject();
        payload.put("model", this.model);

        JSONArray array = new JSONArray();
        JSONObject content = new JSONObject().put("role", "user").put("content", userContent);
        array.put(content);

        payload.put("messages", array);

        return payload;
    }


    public void doChat(BotMessage message)
    {
        String openAIURL = "https://api.openai.com/v1/chat/completions";

        BotURLConnection conn = null;
        try {
            conn = new BotURLConnection(new URL(openAIURL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        conn.addHeader("Authorization", "Bearer " + this.key);
        conn.addHeader("Content-Type", "application/json");

        String response = null;
        try {
            response = conn.postToURL( this.buildPayload( message.getParams() ).toString() );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JSONObject jsonResponse = new JSONObject(response);
        message.getConn().doPrivmsg(message.getTarget(),
                jsonResponse.getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").optString("content")
                        .replaceAll("(?m)\\R", " "));
    }
}
