package com.wuyi.wyms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.wyms.mapper.ShopTypeMapper;
import com.wuyi.wyms.model.domain.ShopType;
import com.wuyi.wyms.service.ShopTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType>
        implements ShopTypeService {

    @Resource
    private ShopTypeMapper shopTypeMapper;

    @Override
    public List<ShopType> shopTypeList() {
        QueryWrapper<ShopType> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        List<ShopType> showTypeList = shopTypeMapper.selectList(queryWrapper);
        return getSafetyList(showTypeList);
    }

    public List<ShopType> getSafetyList(List<ShopType> shopTypeList){
        for(ShopType shopType : shopTypeList){
            shopType.setCreateTime(null);
            shopType.setUpdateTime(null);
        }
        return shopTypeList;
    }
}
