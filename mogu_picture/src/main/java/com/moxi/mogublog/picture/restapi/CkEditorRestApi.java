package com.moxi.mogublog.picture.restapi;

import com.moxi.mogublog.picture.global.SysConf;
import com.moxi.mogublog.picture.service.FileService;
import com.moxi.mogublog.picture.util.Aboutfile;
import com.moxi.mogublog.utils.FileUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/ckeditor")
public class CkEditorRestApi {

    /**
     * 获取基本路径
     */
    @Value(value = "${file.upload.path}")
    private String basePath;

    /**
     * 图片路径前缀
     */
    @Value(value = "${data.image.url}")
    private String imgURL;

    /**
     * 图像存放路径
     * 图像存放路径
     */
    private String uploadImageUrl = "ckEditorUploadImg";

    /**
     * 文件存放路径
     */
    private String uploadFileUrl = "ckEditorUploadFile";


    @Autowired
    FileService fileService;
    /**
     * 图像中的图片上传
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/imgUpload", method = RequestMethod.POST)
    public Object imgUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        //引用自己设计的一个工具类
        Aboutfile af = new Aboutfile();

        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            MultipartFile file = multiRequest.getFile(iter.next());
            if (file != null) {

                //获取旧名称
                String oldName = file.getOriginalFilename();

                //获取扩展名
                String expandedName = FileUtils.getPicExpandedName(oldName);

                //判断是否是图片
                if (!af.isPic(expandedName)) {
                    map.put("uploaded", 0);
                    errorMap.put("message", "请上传正确的图片");
                    map.put("error", errorMap);
                    return map;
                }

                //对图片大小进行限制
                if (file.getSize() > (5 * 1024 * 1024)) {
                    map.put("uploaded", 0);
                    errorMap.put("message", "图片大小不能超过5M");
                    map.put("error", errorMap);
                    return map;
                }

                // 设置图片上传服务必要的信息
                request.setAttribute("userUid", "uid00000000000000000000000000000000");
                request.setAttribute("adminUid", "uid00000000000000000000000000000000");
                request.setAttribute("projectName", "blog");
                request.setAttribute("sortName", "admin");

                List<MultipartFile> fileData = new ArrayList<>();
                fileData.add(file);
                String result = fileService.uploadImgs(basePath, request, fileData);
                Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
                String code = resultMap.get(SysConf.CODE).toString();
                if(SysConf.SUCCESS.equals(code)) {
                    List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) resultMap.get(SysConf.DATA);
                    if(resultList.size() > 0) {
                        Map<String, Object> picture = resultList.get(0);
                        String fileName = picture.get("picName").toString();
                        String qiNiuUrl = picture.get("qiNiuUrl").toString();
                        String url = imgURL + picture.get("picUrl").toString();
                        map.put("uploaded", 1);
                        map.put("fileName", fileName);
                        map.put("url", qiNiuUrl);
                    }
                    return map;
                } else {
                    map.put("uploaded", 0);
                    errorMap.put("message", "上传失败");
                    map.put("error", errorMap);
                    return map;
                }

            }
        }
        return null;

    }

    /**
     * 工具栏“插入\编辑超链接”的文件上传
     *
     * @return
     * @throws IOException
     */

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public Object fileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {


        Map<String, Object> map = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        //引用自己设计的一个工具类
        Aboutfile af = new Aboutfile();

        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            MultipartFile file = multiRequest.getFile(iter.next());
            if (file != null) {

                // 获取旧名称
                String oldName = file.getOriginalFilename();
                // 获取扩展名
                String expandedName = FileUtils.getPicExpandedName(oldName);
                // 判断是否安全文件
                if (!af.isSafe(expandedName)) {
                    map.put("uploaded", 0);
                    errorMap.put("message", "请上传正确格式的文件");
                    map.put("error", errorMap);
                    return map;
                }

                //对文件大小进行限制
                if (file.getSize() > (50 * 1024 * 1024)) {
                    map.put("uploaded", 0);
                    errorMap.put("message", "文件大小不能超过50M");
                    map.put("error", errorMap);
                    return map;
                }

                // 设置文件上传服务必要的信息
                request.setAttribute("userUid", "uid00000000000000000000000000000000");
                request.setAttribute("adminUid", "uid00000000000000000000000000000000");
                request.setAttribute("projectName", "blog");
                request.setAttribute("sortName", "admin");

                List<MultipartFile> fileData = new ArrayList<>();
                fileData.add(file);
                String result = fileService.uploadImgs(basePath, request, fileData);
                Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
                String code = resultMap.get(SysConf.CODE).toString();
                if(SysConf.SUCCESS.equals(code)) {
                    List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) resultMap.get(SysConf.DATA);
                    if(resultList.size() > 0) {
                        Map<String, Object> picture = resultList.get(0);
                        String fileName = picture.get("picName").toString();
                        String qiNiuUrl = picture.get("qiNiuUrl").toString();
                        String url = imgURL + picture.get("picUrl").toString();
                        map.put("uploaded", 1);
                        map.put("fileName", fileName);
                        map.put("url", qiNiuUrl);
                    }
                    return map;
                } else {
                    map.put("uploaded", 0);
                    errorMap.put("message", "上传失败");
                    map.put("error", errorMap);
                    return map;
                }

            }
        }
        return null;
    }
}
