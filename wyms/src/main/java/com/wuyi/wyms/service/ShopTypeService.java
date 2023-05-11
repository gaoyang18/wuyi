package com.wuyi.wyms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.wyms.model.domain.ShopType;

import java.util.List;

public interface ShopTypeService extends IService<ShopType> {
    List<ShopType> shopTypeList();
}
