package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * ClassName: StringToItemTypeConverter
 * PackageName: com.atguigu.lease.web.admin.custom.converter
 * Create: 2024/7/23-9:20
 * Description: 定义从String到enum的自定义converter
 */
@Component
public class StringToItemTypeConverter implements Converter<String, ItemType> {

    @Override
    public ItemType convert(String source) {
        ItemType[] values = ItemType.values();
        for (ItemType value : values) {
            if (value.getCode().equals(Integer.valueOf(source))){
                return value;
            }
        }
        throw new RuntimeException("该code不合法");
    }
}
