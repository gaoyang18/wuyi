package com.wuyi.wyms.controller;

import com.wuyi.wyms.common.BaseResponse;
import com.wuyi.wyms.common.ResultUtils;
import com.wuyi.wyms.service.IVoucherOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @PostMapping("seckill/{id}")
    public BaseResponse<Long> seckillVoucher(@PathVariable("id") Long voucherId) {
        return ResultUtils.success(voucherOrderService.seckillVoucher(voucherId));
    }
}
