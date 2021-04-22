package com.kbk.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.aliyuncs.utils.StringUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;


@Component
public class OSSCOSUtil {
    private Logger log = LoggerFactory.getLogger(OSSCOSUtil.class);
    /**
     * 阿里密钥
     */
    @Value("${AliMail.acceKeyId}")
    private String accessKeyId;
    @Value("${AliMail.accessKeySecret}")
    private String accessKeySecret;


    /**
     * 腾讯密钥
     */
    @Value("${TxSMS.secretId}")
    private String tencentAccessKeId;
    @Value("${TxSMS.secretKey}")
    private String tencentAccessKeySecret;


    /**
     * 阿里图片上传
     */
    @Value("${AliOSS.endpoint}")
    private String endpoint;

    @Value("${AliOSS.bucketName}")
    private String bucketName;

    @Value("${AliOSS.content}")
    private String content;


    /**
     * 腾讯图片上传
     */
    @Value("${TxCOS.endpoint}")
    private String txEndpoint;

    @Value("${TxCOS.bucketName}")
    private String txBucketName;

    @Value("${TxCOS.content}")
    private String txContent;




    /**
     * 上传文件到阿里云OSS
     *
     * @param file
     * @return
     * @throws ImgException
     */
    public String uploadImgOSS(MultipartFile file) throws ImgException {
//        if (file.getSize() > 2048) {
//            throw new ImgException("上传图片大小不能超过2M！");
//        }
        // 获取上传的文件名全称
        String Filename = file.getOriginalFilename();
        // 获取上传文件的后缀名,并改成小写
        String suffix = Filename.substring(Filename.lastIndexOf(".")).toLowerCase();

        // 使用 UUID 给图片重命名，并去掉四个“-”
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;

        try {
            InputStream inputStream = file.getInputStream();
            this.uploadFileOSS(inputStream, newFileName);
            return newFileName;
        } catch (Exception e) {
            throw new ImgException("图片上传失败");
        }
    }

    /**
     * 获得图片路径
     *
     * @param fileUrl
     * @return
     */
    public String getImgUrl(String fileUrl) {
        if (!StringUtils.isEmpty(fileUrl)) {
            String[] split = fileUrl.split("/");
            return this.getUrl(this.content + split[split.length - 1]);
        }
        return null;
    }

    /**
     * 上传到阿里云OSS服务器  如果同名文件会覆盖服务器上的
     *
     * @param instream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFileOSS(InputStream instream, String fileName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String ret = "";
        try {
            //创建上传Object的Metadata,这是用户对object的描述
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(instream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            //设置文件类型
            objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            //上传文件
            PutObjectResult putResult = ossClient.putObject(bucketName, content + fileName, instream, objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            //关闭资源
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ossClient.shutdown();
        return ret;
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return String
     */
    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpeg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }


    //判断上传的文件类型
    private static String checkSuffix(String FilenameExtension) {
        //图片格式
        String[] FILETYPES = new String[]{
                ".jpg", ".bmp", ".jpeg", ".png", ".gif",
                ".JPG", ".BMP", ".JPEG", ".PNG", ".GIF"
        };
        if(!org.apache.commons.lang.StringUtils.isBlank(FilenameExtension)){
            for (int i = 0; i < FILETYPES.length; i++) {
                String fileType = FILETYPES[i];
                if (FilenameExtension.endsWith(fileType)) {
                    FilenameExtension = fileType;
                    break;
                }
            }
        }
        return FilenameExtension;
    }

    /**
     * 获得url链接
     *
     * @param key 上传的文件
     * @return
     */
    public String getUrl(String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        if (url != null) {
            ossClient.shutdown();
            return url.toString();
        }
        return null;
    }


    /**
     * 从阿里云批量下载文件的方法
     */
    public void downLoad(String localPath) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 设置最大个数。
        final int maxKeys = 100;
        //构造ListObjectRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withMaxKeys(maxKeys);
        //设置为“/"时，罗列该文件夹下所有的文件
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setPrefix("image/");

        ObjectListing listing = ossClient.listObjects(listObjectsRequest);

        //遍历该文件夹下的所有文件
        for (OSSObjectSummary ossObjectSummary : listing.getObjectSummaries()) {
            String key = ossObjectSummary.getKey();
            log.info("key=" + key);

            //判断文件所在本地路径是否存在，若无，则创建目录
            File file = new File(localPath + key);

            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            //        下载Object到文件
            ossClient.getObject(new GetObjectRequest(bucketName, key), file);
            log.info("路径=======" + file.getAbsoluteFile());
            log.info("key=====" + key);
        }
        log.info("下载完成");
        ossClient.shutdown();
    }

    /**
     * 图片迁移
     * TODO: 代码逻辑：遍历获取阿里云的文件名，将阿里云上的文件转到本地，最后在上传到腾讯云
     * FIXME:这里图片迁移的逻辑有问题。
     * 1.代码是创建一个父目录，但是没有创建文件。所以它会显示在本地找不到文件。
     * 2.获取key的时候，它一开始直接获取image/  文件夹，并不会获取到详细的文件
     * FIXME:目前解决办法：在本地创建文件，获取文件的绝对位置。然后把本地文件当作一个中转地。把获取key（文件名）注释
     * FIXME:进一步优化的方法：
     * 1.先把阿里云图片全部下载到本地，然后在从本地上传的腾讯云
     * 2.想到能不能把redis当作中转站
     * 3.师兄提供的办法是，看sdk能不能支持云文件
     *
     *
     */
    public void OssAndCos(String localPath) {
        /**
         * 腾讯云信息
         */
        // 1 初始化用户身份信息（secretId, secretKey）。
        String secretId = tencentAccessKeId;
        String secretKey =tencentAccessKeySecret;
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-shenzhen-fsi");
        ClientConfig clientConfig = new ClientConfig(region);

        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        /**
         * 阿里云信息
         */
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        // 设置最大个数。
//        final int maxKeys = 100;
        //构造ListObjectRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        //设置为“/"时，罗列该文件夹下所有的文件
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setPrefix("image/");

        ObjectListing listing = ossClient.listObjects(listObjectsRequest);
        log.info("listing======" + listing);

        //TODO:遍历该文件夹下的所有文件
        for (OSSObjectSummary ossObjectSummary : listing.getObjectSummaries()) {
            String key = ossObjectSummary.getKey();
            log.info("key=" + key);
            //判断文件所在本地路径是否存在，若无，则创建目录
            File file = new File(localPath);

            //获取父目录
         //   File fileParent = file.getParentFile();
            // 创建它所在的文件夹的目录，（该文件夹不存在的话，创建）
//            if (!file.exists()) {
//                file.mkdirs();
//            }
            // 下载Object到文件
            ossClient.getObject(new GetObjectRequest(bucketName, key), file);
            // 指定要上传到的存储桶
            String txbucketName = txBucketName;
            // 指定要上传到 COS 上对象键
            com.qcloud.cos.model.PutObjectRequest putObjectRequest = new  com.qcloud.cos.model.PutObjectRequest(txbucketName, key, file);
            com.qcloud.cos.model.PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            log.info("路径=======" + file.getAbsoluteFile());
            log.info("key======" + key);
        }
        ossClient.shutdown();
        cosClient.shutdown();
    }


}
