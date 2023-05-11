package com.wuyi.wyms.controller;

import com.wuyi.wyms.common.BaseResponse;
import com.wuyi.wyms.common.ResultUtils;
import com.wuyi.wyms.model.domain.ShopType;
import com.wuyi.wyms.service.ShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {

    @Resource
    private ShopTypeService shopTypeService;

    /**
     * 返回商品类型信息
     */
    @GetMapping("/list")
    public BaseResponse<List<ShopType>> showTypeList(){
        return ResultUtils.success(shopTypeService.shopTypeList());
    }
}
