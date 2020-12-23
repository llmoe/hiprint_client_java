package com.llmoe.print.controller;

import cn.hutool.core.net.NetUtil;
import com.llmoe.print.PrintApplication;
import com.llmoe.print.common.config.SysConfig;
import com.llmoe.print.common.utils.HtmlToPdfPrintUtil;
import com.llmoe.print.pojo.PrintBodyVo;
import com.llmoe.print.pojo.PrinterDTO;
import com.llmoe.print.service.PrinterService;
import com.llmoe.print.view.HelpView;
import com.llmoe.print.view.SettingView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 主界面控制器
 *
 * @author llmoe
 * @date 2020/6/10 12:59
 */
@Slf4j
@FXMLController
public class MainController implements Initializable {

    private static ObservableList<PrinterDTO> printList = FXCollections.observableArrayList();
    private static ObservableList<PrintBodyVo> bodyData = FXCollections.observableArrayList();
    /*
     * 打印日志
     */
    public TableView<PrintBodyVo> printBody;
    //日志绑定列
    public TableColumn<PrintBodyVo, String> printTime;
    public TableColumn<PrintBodyVo, String> printTemplate;
    public TableColumn<PrintBodyVo, String> printUser;
    public TableColumn<PrintBodyVo, String> printName;

    /**
     * 打印机列表
     */
    public ListView<PrinterDTO> printerList;
    /**
     * IP地址
     */
    public TextField ipAddress;

    @Autowired
    private PrinterService printerService;

    @Autowired
    private SettingController settingController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize: {}", location.getPath());

        List<PrinterDTO> printerList = printerService.getPrinterList();
        printList.addAll(printerList);
        this.printerList.setItems(printList);


        //绑定值
        printBody.setItems(bodyData);
        printName.setCellValueFactory(new PropertyValueFactory<PrintBodyVo, String>("printName"));
        printTime.setCellValueFactory(new PropertyValueFactory<PrintBodyVo, String>("printTime"));
        printTemplate.setCellValueFactory(new PropertyValueFactory<PrintBodyVo, String>("printTemplate"));
        printUser.setCellValueFactory(new PropertyValueFactory<PrintBodyVo, String>("printUser"));

        String ip = NetUtil.getLocalhostStr();
        ipAddress.setText(ip);
    }

    /**
     * 点击测试打印
     */
    public void testPrint(ActionEvent actionEvent) {
        boolean okCancel = AlertUtil.confirmOkCancel("提示", "确认要进行测试打印吗?");
        if (okCancel) {
            //不能在主线程运行这玩意
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PrinterDTO selectedPrinter = printerList.getSelectionModel().getSelectedItem();
                    if (selectedPrinter == null) {
                        Platform.runLater(() -> {
                            AlertUtil.showInfoAlert("还没有选择打印机");
                        });
                    } else {
                        String selectedItem = SysConfig.getConfig().getStr("scaling");
                        HtmlToPdfPrintUtil.htmlPrint("<h1>测试打印【xx云打印助手】<h1>", selectedPrinter.getName(), 1, 72,100,100,selectedItem);
                    }
                }
            });
        }
    }

    /***
     * 点击刷新打印机列表
     */
    public void refreshPrinter(ActionEvent actionEvent) {
        //清除旧的数据
        printList.clear();
        List<PrinterDTO> printerList = printerService.getPrinterList();
        printList.addAll(printerList);
    }

    /**
     * 点击帮助文档
     */
    public void helpDoc(ActionEvent actionEvent) {
        //打开帮助文档窗口
        PrintApplication.showView(HelpView.class, Modality.NONE);
    }

    /**
     * 点击关于
     */
    public void about(ActionEvent actionEvent) throws FileNotFoundException {
        AlertUtil.showInfoAlert("xx智能云打印助手,开发：LLmoe");
    }

    /**
     * linux和windows下通用
     *
     * @return 获取项目地址
     */
    private String getJarFilePath() {
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        return jarFile.getParentFile().toString();
    }

    /**
     * 清空日志
     */
    public void clearLog(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("提示");
        alert.setHeaderText("提示");
        alert.setContentText("确认要清空日志吗?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            printBody.getItems().clear();
        }

    }

    /**
     * 打印设置
     */
    public void setting(ActionEvent actionEvent) {
        PrintApplication.showView(SettingView.class, Modality.NONE);
        //设置选中的模式
        settingController.scaling.setValue(SysConfig.getConfig().getStr("scaling"));
        //设置是否开机自启
        settingController.isBootStart.setSelected(SysConfig.getConfig().getBool("powerOn"));
    }

    /**
     * 设置默认打印机
     */
    public void setDefaultPrinter(ActionEvent actionEvent) {
        int index = printerList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            AlertUtil.showInfoAlert("没有选中任何打印机(右边打印机列表选择即可)");
        } else {
            ObservableList<PrinterDTO> list = printerList.getItems();
            List<PrinterDTO> dtoList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                System.out.println(i);
                System.out.println("index:" + index);
                if (i == index) {
                    list.get(i).setIsDefault(true);
                    SysConfig.getConfig().set("defaultPrinter", list.get(i).getName());
                    SysConfig.save();
                } else {
                    list.get(i).setIsDefault(false);
                }
                dtoList.add(list.get(i));
            }
            printList.clear();
            printList.addAll(dtoList);
        }
    }


    public void openLogFolderAction(ActionEvent actionEvent) {
        JavaFxSystemUtil.openDirectory("logs/");
    }
}
