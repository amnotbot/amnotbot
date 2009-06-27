package org.knix.amnotbot.cmd.utils;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.collections.map.LRUMap;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
class WebPageCache
{
    private Map lruPage;
    static WebPageCache _instance = null;

    public static WebPageCache instance()
    {
        if (_instance == null) {
            _instance = new WebPageCache();
        }
        return _instance;
    }

    protected WebPageCache()
    {
        int cache_size;

        cache_size =
                BotConfiguration.getConfig().getInt("webpages_cache_size", 128);

        this.lruPage = Collections.synchronizedMap( new LRUMap(cache_size) );
    }

    public WebPageInfo get(String url)
    {
        return (WebPageInfo) this.lruPage.get(url);
    }

    public void put(WebPageInfo _info)
    {
        this.lruPage.put(_info.getUrl(), _info);
    }
}
