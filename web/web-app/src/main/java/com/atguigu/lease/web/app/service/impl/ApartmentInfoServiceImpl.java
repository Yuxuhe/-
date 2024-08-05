package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private GraphInfoMapper graphInfoMapper;
    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;

    @Override
    public ApartmentItemVo getApartmentVoById(Long id) {
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);

        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        List<GraphVo> graphVoList = graphInfoMapper.getGraphVoById(ItemType.APARTMENT, id);

        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentItemVo);
        apartmentItemVo.setGraphVoList(graphVoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(minRent);

        return apartmentItemVo;
    }

    @Override
    public ApartmentDetailVo getApartmentDetailById(Long id) {
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);
        List<GraphVo> graphVoList = graphInfoMapper.getGraphVoById(ItemType.APARTMENT, id);
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.getFacilityInfoApById(id);
        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

        ApartmentDetailVo detailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo,detailVo);
        detailVo.setFacilityInfoList(facilityInfoList);
        detailVo.setLabelInfoList(labelInfoList);
        detailVo.setGraphVoList(graphVoList);
        detailVo.setMinRent(minRent);

        return detailVo;
    }
}




