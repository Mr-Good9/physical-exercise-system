package com.good.physicalexercisesystem.utils;

import com.good.physicalexercisesystem.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioUtils {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传文件
     * @param file 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String fileName) {
        try {
            log.info("开始上传文件: {}", fileName);
            
            // 检查存储桶是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .build());
            if (!found) {
                log.info("存储桶不存在，创建新的存储桶: {}", minioConfig.getBucket());
                // 创建存储桶
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .build());
            }

            // 上传文件
            log.info("正在上传文件到MinIO...");
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 上传文件后直接返回可访问的URL
            String url = String.format("%s/%s/%s",
                minioConfig.getEndpoint().replaceAll("/$", ""),
                minioConfig.getBucket(),
                fileName
            );
            
            log.info("文件上传成功，URL: {}", url);
            return url;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @PostConstruct
    public void init() {
        try {
            // 检查存储桶是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .build());
            if (!found) {
                // 创建存储桶
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .build());

                // 设置桶策略为公共读
                String policy = String.format(
                    "{" +
                    "    \"Version\": \"2012-10-17\"," +
                    "    \"Statement\": [" +
                    "        {" +
                    "            \"Effect\": \"Allow\"," +
                    "            \"Principal\": {\"AWS\": [\"*\"]}," +
                    "            \"Action\": [" +
                    "                \"s3:GetObject\"," +
                    "                \"s3:GetBucketLocation\"," +
                    "                \"s3:ListBucket\"" +
                    "            ]," +
                    "            \"Resource\": [" +
                    "                \"arn:aws:s3:::%1$s/*\"," +
                    "                \"arn:aws:s3:::%1$s\"" +
                    "            ]" +
                    "        }" +
                    "    ]" +
                    "}", 
                    minioConfig.getBucket()
                );

                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .config(policy)
                        .build());
            }
        } catch (Exception e) {
            log.error("初始化 MinIO 配置失败", e);
            throw new RuntimeException("初始化 MinIO 配置失败: " + e.getMessage());
        }
    }
} 