package com.llmoe.print.controller;

import com.llmoe.print.common.config.SysConfig;
import com.llmoe.print.common.constant.Constants;
import com.xwintop.xcore.util.javafx.AlertUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 打印设置页码
 *
 * @author llmoe
 * @date 2020/6/10 12:59
 */
@Slf4j
@FXMLController
public class SettingController implements Initializable {

    /**
     * 实际大小。缩小。拉伸。自适应。自定义
     */
    private static ObservableList<String> scalingList = FXCollections.observableArrayList(
            FXCollections.observableArrayList(
                    Constants.SCALE_TO_FIT, Constants.ACTUAL_SIZE, Constants.SHRINK_TO_FIT, Constants.STRETCH_TO_FIT, Constants.CUSTOMIZE
            )
    );
    /**
     * 设置打印模式
     */
    public ChoiceBox<String> scaling;
    /**
     * 开机自启
     */
    public CheckBox isBootStart;

    /**
     * 是否删除边距
     */
    public CheckBox isDelMargin;
    /**
     * 边距X
     */
    public TextField marginX;
    /**
     * 边距Y
     */
    public TextField marginY;


    /**
     * 初始化加载窗口
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scaling.setItems(scalingList);
        //设置选中的模式
        scaling.setValue(SysConfig.getConfig().getStr("scaling"));
        //设置是否开机自启
        isBootStart.setSelected(SysConfig.getConfig().getBool("powerOn"));
    }

    /**
     * 保存设置
     */
    public void save(ActionEvent actionEvent) {
        //打印模式
        String selectedItem = scaling.getSelectionModel().getSelectedItem();
        //是否开机自启
        boolean powerOn = isBootStart.selectedProperty().get();
        SysConfig.getConfig().set("scaling", selectedItem).set("powerOn", powerOn);
        SysConfig.save();
        AlertUtil.showInfoAlert("保存成功了哦~(*^▽^*)");
    }


    /**
     * 限制只能输入数字
     *
     * @param keyEvent 键盘事件
     */
    public void limitNumber(KeyEvent keyEvent) {

    }
}
