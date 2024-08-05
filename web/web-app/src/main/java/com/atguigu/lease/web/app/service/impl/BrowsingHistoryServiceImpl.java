package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.BrowsingHistory;
import com.atguigu.lease.web.app.mapper.BrowsingHistoryMapper;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {

    @Resource
    private BrowsingHistoryMapper historyMapper;
    @Override
    public IPage<HistoryItemVo> pageItem(Page<HistoryItemVo> page, Long userId) {
        return historyMapper.pageItem(page,userId);

    }


    @Async
    @Override
    public void saveOrUpdateHistory(Long userId, Long id) {
        // 先判断应该是更新浏览历史的时间还是新增浏览历史
        LambdaQueryWrapper<BrowsingHistory> browsingHistoryQueryWrapper = new LambdaQueryWrapper<>();
        browsingHistoryQueryWrapper.eq(BrowsingHistory::getUserId,userId).
                eq(BrowsingHistory::getRoomId,id);
        BrowsingHistory history = historyMapper.selectOne(browsingHistoryQueryWrapper);
        if (history != null) {
            // 更新浏览时间
            history.setBrowseTime(new Date());
            historyMapper.updateById(history);
        }
        else {
            // 说明应该插入一条记录
            BrowsingHistory browsingHistory = new BrowsingHistory();
            browsingHistory.setBrowseTime(new Date());
            browsingHistory.setRoomId(id);
            browsingHistory.setUserId(userId);
            historyMapper.insert(browsingHistory);
        }
    }
}