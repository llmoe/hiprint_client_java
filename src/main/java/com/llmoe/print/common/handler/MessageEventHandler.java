package com.llmoe.print.common.handler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.llmoe.print.common.config.SysConfig;
import com.llmoe.print.common.constant.Constants;
import com.llmoe.print.common.utils.HtmlToPdfPrintUtil;
import com.llmoe.print.controller.MainController;
import com.llmoe.print.pojo.PrintBodyVo;
import com.llmoe.print.service.PrinterService;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Socket 消息处理
 *
 * @author llmoe
 * @date 2020/6/6 10:45
 */
@Component
@Slf4j
public class MessageEventHandler {
    /**
     * 用来存已连接的客户端
     */
    private static Map<String, SocketIOClient> clientMap = new HashMap<>();

    @Autowired
    private SocketIOServer server;

    @Autowired
    private PrinterService printerService;

    @Autowired
    private MainController mainController;


    /**
     * 链接
     *
     * @param client 客户端
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            String uuid = client.getSessionId().toString();
            clientMap.put(uuid, client);
            log.info("连接成功：ip: " + client.getRemoteAddress().toString() + ",uuid: " + uuid + " connect");

            //发送打印机信息
            client.sendEvent(Constants.PRINTER_LIST, printerService.getPrinterList());
        } catch (Exception e) {
            e.printStackTrace();
            client.sendEvent("error", "连接客户端失败: " + e.getMessage());
        }
    }


    /**
     * 客户端断开
     *
     * @param client 客户端
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String uuid = client.getSessionId().toString();
        clientMap.remove(uuid);
        log.info("客户端关闭：ip: " + client.getRemoteAddress().toString() + ",uuid: " + uuid + " disConnect");
    }

    /**
     * 消息
     *
     * @param client 客户端
     * @param data   消息
     */
    @OnEvent(value = Socket.EVENT_MESSAGE)
    public void message(SocketIOClient client, AckRequest ackRequest, Object data) {
        if (ackRequest.isAckRequested()) {
            // send ack response with data to client
            ackRequest.sendAckData("服务器回答Socket.EVENT_MESSAGE", "好的");
        }
        System.out.println("data = " + data);
        sendMessageToAllClient("recieve", "get out ...");
    }


    /**
     * 接收打印请求
     *
     * @param data   消息
     * @param client 客户端
     */
    @OnEvent(value = Constants.NEWS)
    public void news(SocketIOClient client, AckRequest ackRequest, Object data) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(data);
            //获取打印机
            Console.log("打印机：" + jsonObject.get("printer"));
            //打印模式
            String selectedItem = SysConfig.getConfig().getStr("scaling");

            //获取打印机
            if (StrUtil.isBlank(jsonObject.getStr("printer"))) {
                //如果没有打印机选择默认打印机
                jsonObject.set("printer", SysConfig.getConfig().get("defaultPrinter"));
            }

            HtmlToPdfPrintUtil.htmlPrint(jsonObject.getStr("html"),
                    jsonObject.getStr("printer"),
                    jsonObject.getInt("printNum", 1),
                    jsonObject.getInt("dpi", 72),
                    jsonObject.getInt("height", 0),
                    jsonObject.getInt("width", 0),
                    selectedItem);

            //sendMessageToAllClient("all", "发送广播全部消息");
            //给客户端返回成功消息
            client.sendEvent("successs", getData(jsonObject.getStr("templateId"), "打印请求发送成功"));

            //插入软件表格日志
            PrintBodyVo bodyVo = new PrintBodyVo();
            bodyVo.setPrintTime(DateUtil.formatDateTime(new DateTime()));
            bodyVo.setPrintTemplate(jsonObject.getStr("templateId"));
            bodyVo.setPrintUser(StrUtil.blankToDefault(jsonObject.getStr("userName", "无"), "无"));
            bodyVo.setPrintNum(jsonObject.getStr("printNum", "1"));
            bodyVo.setPrintName(jsonObject.getStr("printer"));
            mainController.printBody.getItems().add(0, bodyVo);

            //如果日志太多。自动清空 emmm沙雕操作
            int size = mainController.printBody.getItems().size();
            if (size > 200) {
                mainController.printBody.getItems().remove(100, 201);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (jsonObject != null) {
                JSONObject object = getData(jsonObject.getStr("templateId"), "错误：" + e.getMessage());
                client.sendEvent("error", object);
            } else {
                JSONObject object = getData("", "错误：" + e.getMessage());
                client.sendEvent("error", object);
            }
        }
    }


    /**
     * 全体消息推送
     *
     * @param eventType 前台根据类型接收消息，所以接收的消息类型不同，收到的通知就不同 推送的事件类型
     * @param message   推送的内容
     */
    public void sendMessageToAllClient(String eventType, String message) {
        Collection<SocketIOClient> clients = server.getAllClients();
        for (SocketIOClient client : clients) {
            client.sendEvent(eventType, message);
        }
    }


    /**
     * 给具体的客户端推送消息
     *
     * @param uuid      设备ID
     * @param eventType 推送事件类型
     * @param message   推送的消息内容
     */
    public void sendMessageToOneClient(String uuid, String eventType, String message) {
        try {
            if (uuid != null && !"".equals(uuid)) {
                SocketIOClient client = (SocketIOClient) clientMap.get(uuid);
                if (client != null) {
                    client.sendEvent(eventType, message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通用返回消息
     *
     * @param templateId 模板ID
     * @param msg        消息
     * @return 结果
     */
    private JSONObject getData(String templateId, String msg) {
        JSONObject row = new JSONObject();
        row.set("templateId", templateId);
        row.set("msg", msg);
        return row;
    }

}
