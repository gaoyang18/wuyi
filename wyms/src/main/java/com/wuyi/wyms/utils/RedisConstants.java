package com.wuyi.wyms.utils;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 30L;

    public static final String CACHE_SHOP_KEY = "cache:shop:";

    public static final String LOCK_SHOP_KEY = "lock:shop:";

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";

    public static final String SHOP_GEO_KEY = "shop:geo:";
}
