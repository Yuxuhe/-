package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioProperties properties;

    @Override
    public String upload(MultipartFile file) {
        // 判断桶是否存在
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
            if (!exists) {
                // 不存在的话就创造桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());

                // 给桶设定权限
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(properties.getBucketName())
                        .config(policySet(properties.getBucketName())).build());

            }
            // 给文件设定名字
            String fileName =new SimpleDateFormat("yyyyMMdd").format(new Date())+"/"+ UUID.randomUUID()+"-"+file.getOriginalFilename();
            // 上传文件
            minioClient.putObject(PutObjectArgs.builder().bucket(properties.getBucketName()).
                    stream(file.getInputStream(), file.getSize(), -1).
                    object(fileName).build()); // 将partsize设置成-1的话就会自动为我们分配合适的partsize
            // 返回url
            return String.join("/",properties.getEndpoint(),properties.getBucketName(),fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String policySet(String bucketName){
        return """
            {
              "Statement" : [ {
                "Action" : "s3:GetObject",
                "Effect" : "Allow",
                "Principal" : "*",
                "Resource" : "arn:aws:s3:::%s/*"
              } ],
              "Version" : "2012-10-17"
            }
            """.formatted(bucketName);
    }
}
