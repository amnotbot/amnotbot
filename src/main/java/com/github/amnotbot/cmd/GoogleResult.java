package com.github.amnotbot.cmd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.htmlparser.util.ParserUtils;
import org.htmlparser.util.Translate;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleResult
{
    JSONObject result;

    public GoogleResult(JSONObject answer) throws JSONException
    {
        JSONObject data = answer.getJSONObject("responseData");
        this.result = data.getJSONArray("results").getJSONObject(0);
    }

    public String optStringRaw(String opt)
    {
        return this.result.optString(opt);
    }
    
    public String optString(String opt)
    {
        String text;
        text = Translate.decode(this.result.optString(opt));
        text = ParserUtils.trimAllTags(text, false);
        return text;
    }

    public String decodedUrl(String opt) throws UnsupportedEncodingException
    {
        String url;
        url = URLDecoder.decode(this.result.optString(opt), "UTF-8");
        return url;
    }

    public String title()
    {
        return this.optString("titleNoFormatting");
    }

    public String shortDate(String dateOpt) throws ParseException
    {
        SimpleDateFormat p;
        p = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z",
                Locale.US);
        SimpleDateFormat df;
        df = new SimpleDateFormat("MMM' 'dd', 'yyyy", Locale.US);

        Date date;
        date = p.parse( this.result.optString(dateOpt) );

        return ( df.format(date) );
    }
}
