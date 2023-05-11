package com.wuyi.wyms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.wyms.model.domain.VoucherOrder;

public interface IVoucherOrderService extends IService<VoucherOrder> {

    Long seckillVoucher(Long voucherId);
}
