/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

    public GoogleResult(GoogleSearchImp.searchType sType, JSONObject answer) throws JSONException
    {
        switch (sType) {
            case WEB_SEARCH:
                this.result = answer.getJSONArray("items").getJSONObject(0);
                break;
            case SPELLING_SEARCH:
                this.result = answer.getJSONObject("spelling");
                break;
        }
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
        return this.optString("title");
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
