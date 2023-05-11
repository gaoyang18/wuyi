package com.wuyi.wyms.controller;

import com.wuyi.wyms.common.BaseResponse;
import com.wuyi.wyms.common.ResultUtils;
import com.wuyi.wyms.model.domain.Voucher;
import com.wuyi.wyms.service.IVoucherService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    /**
     * 新增秒杀券
     * @param voucher 优惠券信息，包含秒杀信息
     */
    @PostMapping("seckill")
    public BaseResponse<Long> addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return ResultUtils.success(voucher.getId());
    }

    /**
     * 新增普通券
     * @param voucher 优惠券信息
     */
    @PostMapping
    public BaseResponse<Long> addVoucher(@RequestBody Voucher voucher) {
        voucherService.save(voucher);
        return ResultUtils.success(voucher.getId());
    }


    /**
     * 查询店铺的优惠券列表
     * @param shopId 店铺id
     */
    @GetMapping("/list/{shopId}")
    public BaseResponse<List<Voucher>> queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
       return ResultUtils.success(voucherService.queryVoucherOfShop(shopId));
    }
}
