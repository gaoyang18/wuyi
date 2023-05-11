package com.wuyi.wyms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.wyms.common.ErrorCode;
import com.wuyi.wyms.exception.BusinessException;
import com.wuyi.wyms.mapper.ShopMapper;
import com.wuyi.wyms.model.domain.Shop;
import com.wuyi.wyms.service.ShopService;
import com.wuyi.wyms.utils.CacheClient;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.wuyi.wyms.utils.RedisConstants.*;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private ShopMapper shopMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Shop queryById(Long id) {
        //解决缓存穿透
        Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 互斥锁解决缓存击穿
        // Shop shop = cacheClient
        //         .queryWithMutex(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 逻辑过期解决缓存击穿
        // Shop shop = cacheClient
        //         .queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, this::getById, 20L, TimeUnit.SECONDS);
        return shop;
    }

    @Override
    public Long shopSave(Shop shop) {
        if (shop == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", shop.getName());
        long count = shopMapper.selectCount(queryWrapper);
        if(count >0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        boolean result = this.save(shop);
        if(!result){
            return null;
        }
        Shop resultShop = shopMapper.selectOne(queryWrapper);
        return resultShop.getId();
    }

    @Override
    public long shopUpdate(Shop shop) {
        if(shop == null){
            return -1;
        }
        //更新数据库
        updateById(shop);
        //删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return 1;
    }

    @Override
    public List<Shop> queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        //判断是否需要用坐标查询
        if(x == null || y == null){
            //不需要坐标查询
            Page<Shop> page = query().eq("type_id",typeId).page(new Page<>(current, 5));
            return page.getRecords();
        }
        //计算分页参数
        int from = (current - 1) * 5;
        int end = current * 5;
        //查询redis、排序、分页
        String key = SHOP_GEO_KEY + typeId;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().search(key, GeoReference.fromCoordinate(x, y), new Distance(5000),RedisGeoCommands.GeoRadiusCommandArgs.newGeoSearchArgs().includeDistance());
        //解析出id
        if(results == null){
            return Collections.emptyList();
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            return Collections.emptyList();
        }
        // 截取 from ~ end的部分
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            // 获取店铺id
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            // 获取距离
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });
        //根据id查询Shop
        String idStr = StrUtil.join(",", ids);
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        // 6.返回
        return shops;
    }
}
