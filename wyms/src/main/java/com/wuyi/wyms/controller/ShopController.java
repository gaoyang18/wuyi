package com.wuyi.wyms.controller;

import com.wuyi.wyms.common.BaseResponse;
import com.wuyi.wyms.common.ResultUtils;
import com.wuyi.wyms.model.domain.Shop;
import com.wuyi.wyms.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    /**
     * 用id查询商铺信息
     * @param id 商铺id
     */
    @GetMapping("/{id}")
    public BaseResponse<Shop> queryShowById(@PathVariable("id") Long id){
        return ResultUtils.success(shopService.queryById(id));
    }

    /**
     * 添加商铺信息
     * @param shop 商铺信息
     */
    @PostMapping("/add")
    public BaseResponse<Long> saveShop(@RequestBody Shop shop){
        //写入数据库
        return ResultUtils.success(shopService.shopSave(shop));
    }

    /**
     * 修改商铺信息
     * @param shop 商铺信息
     */
    @PutMapping("/modify")
    public BaseResponse<Long> updateShop(@RequestBody Shop shop){
        return ResultUtils.success(shopService.shopUpdate(shop));
    }

    @GetMapping("/type")
    public BaseResponse<List<Shop>> queryShopByType(@RequestParam("typeId") Integer typeId, @RequestParam(value = "current", defaultValue = "1") Integer current, @RequestParam(value = "x", required = false) Double x,@RequestParam(value = "y", required = false) Double y){
        return ResultUtils.success(shopService.queryShopByType(typeId, current, x, y));
    }
}
