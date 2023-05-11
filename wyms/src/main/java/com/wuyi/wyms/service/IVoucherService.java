package com.wuyi.wyms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.wyms.model.domain.Voucher;

import java.util.List;

public interface IVoucherService extends IService<Voucher> {

    List<Voucher> queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
