package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ClassName: ScheduledTasks
 * PackageName: com.atguigu.lease.web.admin.schedule
 * Create: 2024/7/30-10:24
 * Description: 定时任务，用来检查房间的租期是否过期
 */
@Component
public class ScheduledTasks {
    @Resource
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * *") // 每天都检查一遍
    public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> leaseAgreementUpdateWrapper = new LambdaUpdateWrapper<>();
        leaseAgreementUpdateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING); // 排除那些租期已经到期了的房间
        leaseAgreementUpdateWrapper.le(LeaseAgreement::getLeaseEndDate,new Date());
        leaseAgreementUpdateWrapper.set(LeaseAgreement::getStatus,LeaseStatus.EXPIRED);

        leaseAgreementService.update(leaseAgreementUpdateWrapper);
    }
}
