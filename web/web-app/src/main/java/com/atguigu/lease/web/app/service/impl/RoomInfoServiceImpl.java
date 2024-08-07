package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.service.GraphInfoService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {
    @Resource
    private RoomInfoMapper mapper;
    @Resource
    private ApartmentInfoService apartmentInfoService;
    @Resource
    private GraphInfoMapper graphInfoMapper;
    @Resource
    private AttrValueMapper attrValueMapper;
    @Resource
    private FacilityInfoMapper facilityInfoMapper;
    @Resource
    private LabelInfoMapper labelInfoMapper;
    @Resource
    private PaymentTypeMapper paymentTypeMapper;
    @Resource
    private LeaseTermMapper leaseTermMapper;
    @Resource
    private FeeValueMapper feeValueMapper;
    @Resource
    private BrowsingHistoryService browsingHistoryService;
    @Resource
    private RedisTemplate<String, Object> stringObjectRedisTemplate;

    @Override
    public IPage<RoomItemVo> pageItem(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        return mapper.pageItem(page, queryVo);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        // 先看看redis里面是否有这个id对应的数据
        String key = RedisConstant.APP_ROOM_PREFIX + id;
        RoomDetailVo detailVo = (RoomDetailVo) stringObjectRedisTemplate.opsForValue().get(key);
        if (detailVo == null) {
            RoomInfo roomInfo = mapper.selectById(id);

            // 获取公寓基本信息
            ApartmentItemVo apartmentInfo = apartmentInfoService.getApartmentVoById(roomInfo.getApartmentId());

            // 获取图片列表
            List<GraphVo> graphVoList = graphInfoMapper.getGraphVoById(ItemType.ROOM, id);

            // 获取属性列表
            List<AttrValueVo> attrValueVoList = attrValueMapper.getAttrValueById(id);

            // 获取配套列表
            List<FacilityInfo> facilityInfoList = facilityInfoMapper.getFacilityInfoById(id);

            // 获取标签列表
            List<LabelInfo> labelInfoList = labelInfoMapper.getLabelInfoById(id);

            // 支付方式列表
            List<PaymentType> paymentTypeList = paymentTypeMapper.getPaymentById(id);

            // 杂费列表
            List<FeeValueVo> feeValueVoList = feeValueMapper.getFeeValueVoById(roomInfo.getApartmentId());

            // 查询租期列表
            List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

            // 组装对象
            detailVo = new RoomDetailVo();
            BeanUtils.copyProperties(roomInfo, detailVo);
            detailVo.setApartmentItemVo(apartmentInfo);
            detailVo.setFacilityInfoList(facilityInfoList);
            detailVo.setLabelInfoList(labelInfoList);
            detailVo.setFeeValueVoList(feeValueVoList);
            detailVo.setGraphVoList(graphVoList);
            detailVo.setLeaseTermList(leaseTermList);
            detailVo.setPaymentTypeList(paymentTypeList);
            detailVo.setAttrValueVoList(attrValueVoList);

            stringObjectRedisTemplate.opsForValue().set(key,detailVo);
        }

        // 保存此次浏览历史
        browsingHistoryService.saveOrUpdateHistory(LoginUserHolder.getLoginUser().getUserId(), id);


        return detailVo;

    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> page, Long id) {
        return mapper.pageItemByApartmentId(page, id);
    }
}




