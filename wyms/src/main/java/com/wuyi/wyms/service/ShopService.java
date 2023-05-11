package com.wuyi.wyms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.wyms.model.domain.Shop;

import java.util.List;

public interface ShopService extends IService<Shop> {
    Shop queryById(Long id);

    Long shopSave(Shop shop);

    long shopUpdate(Shop shop);

    List<Shop> queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
