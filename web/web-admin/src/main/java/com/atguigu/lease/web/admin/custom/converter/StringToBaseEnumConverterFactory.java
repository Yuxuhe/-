package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * ClassName: StringToBaseEnumConverterFactory
 * PackageName: com.atguigu.lease.web.admin.custom.converter
 * Create: 2024/7/23-9:54
 * Description:统一定义code转枚举类的convertfactory，因为枚举类实现了BaseEnum
 */
@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
           return new Converter<String, T>() {
               @Override
               public T convert(String source) {
                   T[] enumConstants = targetType.getEnumConstants();
                   for (T enumConstant : enumConstants) {
                       if (enumConstant.getCode().equals(Integer.valueOf(source))){
                           return enumConstant;
                       }
                   }
                   throw new IllegalArgumentException("code:"+source+"not legal");
               }
           };
    }
}
