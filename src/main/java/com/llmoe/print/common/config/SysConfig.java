package com.llmoe.print.common.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author llmoe
 * @date 2020/6/14 21:40
 */
@Slf4j
public class SysConfig {

    /**
     * 读取配置
     */
    public static JSONObject config = null;
    /**
     * 软件配置文件地址
     */
    public static String configUrl = FileUtil.getUserHomePath() + File.separator + "print_server.ini";


    /**
     * 获取配置文件
     *
     * @return 结果
     */
    public static JSONObject getConfig() {
        try {
            if (config == null) {
                // 一运行就读取配置文件
                if (FileUtil.exist(configUrl)) {
                    config = JSONUtil.readJSONObject(new File(configUrl), CharsetUtil.CHARSET_UTF_8);
                    log.info("直接读取文件");
                } else {
                    //配置文件不存在则自动创建
                    FileUtil.touch(configUrl);
                    FileWriter writer = new FileWriter(configUrl);
                    writer.write("{\"scaling\":\"SCALE_TO_FIT\",\"powerOn\":false,\"defaultPrinter\":\"\"}");
                    log.info("创建新文件");
                    config = JSONUtil.readJSONObject(new File(configUrl), CharsetUtil.CHARSET_UTF_8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            config = new JSONObject();
        }
        return config;
    }


    /**
     * 保存更新配置文件
     */
    public static void save() {
        FileWriter writer = new FileWriter(configUrl);
        writer.write(JSONUtil.toJsonPrettyStr(JSONUtil.toJsonPrettyStr(config)));
    }


}
