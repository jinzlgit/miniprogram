package com.king.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * 文件操作工具类，继承自Hutool，主要增加了下载文件名编码方法
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/24 15:01
 */
public class FileUtils extends FileUtil {

    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        if (agent.contains("MSIE")) {
            // IE浏览器
            fileName = URLUtil.encode(fileName, "UTF-8").replace("+", " ");
        } else if (agent.contains("FireFox")) {
            // 火狐浏览器
            fileName = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // 谷歌浏览器
            fileName = URLUtil.encode(fileName, "UTF-8");
        } else {
            // 其它浏览器
            fileName = URLUtil.encode(fileName, "UTF-8");
        }
        return fileName;
    }

}
