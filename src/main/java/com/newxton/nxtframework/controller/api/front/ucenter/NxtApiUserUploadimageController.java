package com.newxton.nxtframework.controller.api.front.ucenter;

import com.newxton.nxtframework.component.NxtUploadImageComponent;
import com.newxton.nxtframework.entity.NxtUser;
import com.newxton.nxtframework.service.NxtUserService;
import com.newxton.nxtframework.struct.NxtStructApiResult;
import com.qiniu.util.IOUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author soyojo.earth@gmail.com
 * @time 2020/11/25
 * @address Shenzhen, China
 * @copyright NxtFramework
 */
@RestController
public class NxtApiUserUploadimageController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private NxtUploadImageComponent nxtUploadImageComponent;

    @RequestMapping(value = "/api/user/uploadimage", method = RequestMethod.POST)
    public NxtStructApiResult index() throws IOException {

        ServletInputStream inputStream = request.getInputStream();

        byte[] fileBytesAll = IOUtils.toByteArray(inputStream);

        if (fileBytesAll.length < 4){
            return new NxtStructApiResult(54,"请上传图片文件");
        }

        String fileExt;

        if (fileBytesAll[0] == (byte) 0xff && fileBytesAll[1] == (byte) 0xd8 && fileBytesAll[2] == (byte) 0xff){
            fileExt = "jpg";
        }
        else if (fileBytesAll[0] == (byte)0x89 && fileBytesAll[1] == (byte)0x50 && fileBytesAll[2] == (byte)0x4e && fileBytesAll[3] == (byte)0x47){
            fileExt = "png";
        }
        else if (fileBytesAll[0] == (byte)0x47 && fileBytesAll[1] == (byte)0x49 && fileBytesAll[2] == (byte)0x46 && fileBytesAll[3] == (byte)0x38){
            fileExt = "gif";
        }
        else {
            fileExt = null;
        }

        if (fileExt == null){
            return new NxtStructApiResult(54,"只能上传图片：png、jpg、jpeg、gif");
        }

        Map<String, Object> result = nxtUploadImageComponent.saveUploadImage(fileBytesAll,fileExt);

        if (result.containsKey("id") && result.containsKey("message")) {
            Map<String,Object> data = new HashMap<>();
            data.put("id",result.get("id"));
            data.put("url",result.get("url"));
            return new NxtStructApiResult(data);
        }
        else {
            return new NxtStructApiResult(43,"上传失败，请重试");
        }

    }

}
