package com.usian.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 图片上传
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {
    @Autowired
    private FastFileStorageClient storageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg","image/gif");
    /**
     * 图片上传
     */
    @RequestMapping("/upload")
    public Result fileUpload(MultipartFile file) {

        try {
            String originalFilename = file.getOriginalFilename();
            // 校验文件的类型
            String contentType = file.getContentType();
            if (!CONTENT_TYPES.contains(contentType)){

                return Result.error("文件类型不合法:"+originalFilename);
            }

            // 校验文件的内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                return Result.error("文件内容不合法：" + originalFilename);
            }

            // 保存到服务器
            String filename = file.getOriginalFilename();
            String lastname = StringUtils.substringAfterLast(filename,".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), lastname, null);

            // 生成url地址，返回
            return Result.ok("http://image.usian.com/" + storePath.getFullPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("服务器内部错误");
    }
}
