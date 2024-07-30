package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.web.admin.mapper.LeaseAgreementMapper;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Resource
    private LeaseAgreementMapper leaseAgreementMapper;

    @Override
    public IPage<AgreementVo> pageLease(IPage<AgreementVo> agreementVoPage, AgreementQueryVo queryVo) {
        return leaseAgreementMapper.pageLease(agreementVoPage,queryVo);
    }

    @Override
    public AgreementVo getLeaseAgreementById(Long id) {

        return leaseAgreementMapper.getLeaseAgreementById(id);
    }
}




