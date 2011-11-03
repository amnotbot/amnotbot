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

/**
 *
 * @author gpoppino
 */
public class WebPageInfoProxy implements WebPageInfo
{
    String url;
    WebPageInfo info, parser;

    public WebPageInfoProxy(String url)
    {
        this.url = url;
        this.info = null;
        this.parser = new BotHTMLParser(url);
    }

    private WebPageInfo getWebPageInfo()
    {
        WebPageInfo _info = null;

        _info = WebPageCache.instance().get(this.url);
        if (_info != null) return _info;

        WebPageInfoEntity entity = new WebPageInfoEntity();
        entity.setUrl(this.parser.getUrl());
        entity.setTitle(this.parser.getTitle());
        entity.setDescription(this.parser.getDescription());
        entity.setKeywords(this.parser.getKeywords());
        
        if (entity.getTitle() != null || entity.getDescription() != null
                || entity.getKeywords().length != 0) {
            WebPageCache.instance().put(entity);
            return entity;
        }
        return new WebPageInfoEntity();
    }

    public String getUrl()
    {
        if (this.info != null) {
            return this.info.getUrl();
        }

        this.info = this.getWebPageInfo();

        return this.info.getUrl();
    }

    public String getTitle()
    {
        if (this.info != null) {
            return this.info.getTitle();
        }

        this.info = this.getWebPageInfo();

        return this.info.getTitle();
    }

    public String getDescription()
    {
        if (this.info != null) {
            return this.info.getDescription();
        }

        this.info = this.getWebPageInfo();

        return this.info.getDescription();
    }

    public String[] getKeywords()
    {
        if (this.info != null) {
            return this.info.getKeywords();
        }
        
        this.info = this.getWebPageInfo();
        
        return this.info.getKeywords();
    }
}
