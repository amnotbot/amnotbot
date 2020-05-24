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
package com.github.amnotbot.cmd.utils;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;

import com.github.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
class WebPageCache
{
    private Map<String, WebPageInfo> lruPage;
    static WebPageCache _instance = null;

    public static WebPageCache instance() {
        if (_instance == null) {
            _instance = new WebPageCache();
        }
        return _instance;
    }

    protected WebPageCache() {
        int cache_size;

        cache_size = BotConfiguration.getConfig().getInt("webpages_cache_size", 128);

        this.lruPage = Collections.synchronizedMap((Map<String, WebPageInfo>) new LRUMap<String, WebPageInfo>(cache_size));
    }

    public WebPageInfo get(final String url) {
        return this. lruPage.get(url);
    }

    public void put(final WebPageInfo _info)
    {
        this.lruPage.put(_info.getUrl(), _info);
    }
}
