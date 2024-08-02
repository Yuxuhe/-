package com.atguigu.lease.common.utils;

import java.util.Random;

/**
 * ClassName: RandomCodeUtil
 * PackageName: com.atguigu.lease.common.utils
 * Create: 2024/8/2-10:25
 * Description:随机生成密码
 */
public class RandomCodeUtil {

    public static String codeGenerate(int length){
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
