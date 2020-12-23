package com.llmoe.print.common.config;

import com.llmoe.print.common.constant.Constants;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Parent;
import org.springframework.stereotype.Component;

/**
 * 启动动画
 *
 * @author llmoe
 * @date 2020/6/10 12:59
 */
@Component
public class CustomSplash extends SplashScreen {

    public CustomSplash() {
        super();
    }

    @Override
    public Parent getParent() {
        return super.getParent();
    }

    /**
     * 是否显示动画
     */
    @Override
    public boolean visible() {
        return true;
    }

    /**
     * 启动图
     */
    @Override
    public String getImagePath() {
        return Constants.DEFAULT_IMAGE;
    }
}
