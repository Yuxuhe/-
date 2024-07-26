package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
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

    @Resource
    private ApartmentInfoMapper mapper;
    @Resource
    private GraphInfoMapper graphInfoMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;

    @Resource
    private FeeValueMapper feeValueMapper;

    /**
     * 保存或更新公寓信息
     *
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
            facilityInfoQueryWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            facilityService.remove(facilityInfoQueryWrapper);
            // 3.删除标签
            LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(ApartmentLabel::getApartmentId, apartmentSubmitVo.getId());
            labelService.remove(labelQueryWrapper);
            // 4.删除杂费
            LambdaQueryWrapper<ApartmentFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
            feeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId, apartmentSubmitVo.getId());
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
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = ApartmentFacility.builder().apartmentId(apartmentSubmitVo.getId())
                        .facilityId(facilityInfoId).build();
                apartmentFacilities.add(apartmentFacility);
            }
            facilityService.saveBatch(apartmentFacilities);
        }

        // 3.插入标签
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
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
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            List<ApartmentFeeValue> list = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = ApartmentFeeValue.builder().apartmentId(apartmentSubmitVo.getId())
                        .feeValueId(feeValueId).build();
                list.add(apartmentFeeValue);
            }
            feeValueService.saveBatch(list);
        }

    }

    @Override
    public IPage<ApartmentItemVo> pageItem(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return mapper.pageItem(page, queryVo);
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        // 先查询公寓的基本信息，也就是ApartmentInfo里面的字段
        ApartmentInfo apartmentInfo = this.getById(id);

        // 获取graphInfo的信息
        List<GraphVo> graphVoListlist = graphInfoMapper.selectGraphVoById(ItemType.APARTMENT,id);

        // 获取标签信息
        List<LabelInfo> labelInfoList = labelInfoMapper.selectLableInfoList(id);

        // 查询配套列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectFacilityInfoList(id);

        // 查询杂费列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectFeeList(id);

        // 组装对象
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setGraphVoList(graphVoListlist);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return apartmentDetailVo;

    }
}




