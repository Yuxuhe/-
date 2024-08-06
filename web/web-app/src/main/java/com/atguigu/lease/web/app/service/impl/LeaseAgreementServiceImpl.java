package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.LeaseAgreementMapper;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Resource
    private LeaseAgreementMapper agreementMapper;

    @Override
    public List<AgreementItemVo> listItem(String phone) {
        return agreementMapper.listItem(phone);
    }

    @Override
    public AgreementDetailVo getDetailById(Long id) {
        return agreementMapper.getDetailById(id);
    }

    @Override
    public void updateStatus(Long id, LeaseStatus leaseStatus) {
        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseAgreement::getId,id).set(LeaseAgreement::getStatus,leaseStatus);
        this.update(updateWrapper);
    }
}




