package com.myiothome.controller;

import com.myiothome.annotation.LoginRequired;
import com.myiothome.entity.User;
import com.myiothome.service.UserService;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeUtils;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class SettingController {

    @Value("${community.path.upload}")
    String uploadPath;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Value("${myiothome.path.domain}")
    String domain;
    @Value("${qiniu.key.access}")
    String accessKey;
    @Value("${qiniu.key.secret}")
    String secretKey;
    @Value("${qiniu.bucket.header.name}")
    String headerBucketName;
    @Value("${qiniu.bucket.header.url}")
    String headerBucketUrl;

    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);


    @LoginRequired
    @RequestMapping(path="/setting",method = {RequestMethod.GET,RequestMethod.POST})
    public String getSettingPage(Model model) {
        //上传文件的名称
        String fileName = MyIotHomeUtils.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody",MyIotHomeUtils.getJSONString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);

        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        System.out.println(uploadToken);
        System.out.println(fileName);
        return "/site/setting";
    }

    //更新头像路径
    @RequestMapping(path = "/setting/updateHeader",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return MyIotHomeUtils.getJSONString(1,"文件名不能为空");
        }

        String headerUrl = headerBucketUrl+"/"+fileName;
        System.out.println(headerUrl);
        userService.updateUserHeaderUrl(hostHolder.getUser().getId(),headerUrl);

        return MyIotHomeUtils.getJSONString(0);
    }

    /**
     * 上传头像，需要登陆才可使用(废弃，采用上传到七牛云的方案)
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path="/setting/uploadHeader",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("picError","图片为空，上传失败！");
            return "/site/setting";
        }
        //获取文件名
        String fileName = headerImage.getOriginalFilename();
        //获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if(StringUtils.isBlank(suffix))
        {
            model.addAttribute("picError","图片格式不正确！");
            return "/site/setting";
        }
        fileName = MyIotHomeUtils.generateUUID() + suffix;
        //文件存储路径
        File dest = new File(uploadPath+"/"+fileName);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("头像上传失败");
            throw new RuntimeException("头像上传失败，服务器异常！",e);
        }

        User user = hostHolder.getUser();
        userService.updateUserHeaderUrl(user.getId(),domain+contextPath+"/user/header/"+fileName);


        return "redirect:/index";
    }

    //废弃，采用七牛云方案
    @RequestMapping(path = "/user/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName,
                          HttpServletResponse response
    ){
        //服务器存放路径
        fileName = uploadPath+"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);

        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
        ){
                byte[] buffer = new byte[1024];
                int b = 0;
                while((b = fis.read(buffer)) != -1){
                    os.write(buffer);
                }
        }catch (IOException e){
            logger.error("头像读取失败！"+e.getMessage());
        }
    }
}
