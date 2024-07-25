package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Resource
    private GraphInfoServiceImpl graphInfoService;

    @Resource
    private ApartmentFacilityServiceImpl facilityService;

    @Resource
    private ApartmentLabelServiceImpl labelService;

    @Resource
    private ApartmentFeeValueServiceImpl feeValueService;

    /**
     * 保存或更新公寓信息
     * @param apartmentSubmitVo
     */
    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        // 先判断是更新还是删除操作
        boolean flag = apartmentSubmitVo.getId() == null;

        //先针对apartmentInfo里面的属性进行添加或修改
        super.saveOrUpdate(apartmentSubmitVo);

        // 如果是更新操作的话
        if (!flag) {
            // 1.删除图片
            LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphInfoQueryWrapper);
            // 2.删除配套
            LambdaQueryWrapper<ApartmentFacility> facilityInfoQueryWrapper = new LambdaQueryWrapper<>();
            facilityInfoQueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentSubmitVo.getId());
            facilityService.remove(facilityInfoQueryWrapper);
            // 3.删除标签
            LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            labelService.remove(labelQueryWrapper);
            // 4.删除杂费
            LambdaQueryWrapper<ApartmentFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
            feeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            feeValueService.remove(feeValueQueryWrapper);
        }
        // 1.插入图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            List<GraphInfo> list = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                list.add(graphInfo);
            }
            graphInfoService.saveBatch(list);
        }
        // 2.插入配套
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            List<ApartmentFacility> apartmentFacilities = new ArrayList<>();
            for (Long facilityInfoId :facilityInfoIds) {
                ApartmentFacility apartmentFacility = ApartmentFacility.builder().apartmentId(apartmentSubmitVo.getId())
                        .facilityId(facilityInfoId).build();
                apartmentFacilities.add(apartmentFacility);
            }
            facilityService.saveBatch(apartmentFacilities);
        }

        // 3.插入标签
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)){
            List<ApartmentLabel> list = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = ApartmentLabel.builder().apartmentId(apartmentSubmitVo.getId())
                        .labelId(labelId).build();
                list.add(apartmentLabel);
            }
            labelService.saveBatch(list);
        }
        // 4.插入杂费
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)){
            List<ApartmentFeeValue> list = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = ApartmentFeeValue.builder().apartmentId(apartmentSubmitVo.getId())
                        .feeValueId(feeValueId).build();
                list.add(apartmentFeeValue);
            }
            feeValueService.saveBatch(list);
        }

    }
}




