package com.llmoe.print;

import com.llmoe.print.common.config.CustomSplash;
import com.llmoe.print.view.MainView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@SpringBootApplication
public class PrintApplication extends AbstractJavaFxApplicationSupport implements ApplicationRunner {

    public static void main(String[] args) {
        launch(PrintApplication.class, MainView.class, new CustomSplash(), args);
    }

    /**
     * Spring 容器启动时执行一些初始化操作，如：加载自定义资源...
     * 此方法自行完之后，JavaFx应用程序启动画面才会关闭，原因分析：
     * 1 de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport[row:120].init() 重写了 javafx.application.Application.init()
     * 2 先启动SpringBoot应用，当SpringBoot应用启动完毕时，执行了两个异步操作，第二个异步操作是关闭启动画面
     *
     * @param args 信息
     * @throws Exception 异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: 显示启动首屏时加载自定义资源...
    }

    @Override
    public void init() throws Exception {
        log.info("初始化");
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.info("开始");
        //全局异常捕捉处理
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("程序错误");
            alert.setHeaderText("捕捉到了异常,请查看");
            alert.setContentText(throwable.getMessage());

            System.out.println(throwable.getLocalizedMessage());
            System.out.println(throwable.toString());
            System.out.println(throwable.getMessage());
            String exceptionText = "测试";

            Label label = new Label("下面是异常详细信息:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();

        });

        super.start(stage);
    }

    @Override
    public void beforeShowingSplash(Stage splashStage) {
        super.beforeShowingSplash(splashStage);
        log.info("beforeShowingSplash");
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        log.info("beforeInitialView");
        super.beforeInitialView(stage, ctx);
        // 在非JavaFX应用程序主线程上运行指定的Runnable
//        Platform.runLater(() -> {
//        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.info("停止JavaFx");
        //System.exit(0);
    }

    /**
     * 虽然在application.yml中可以设置应用图标，但是首屏启动时的应用图标未改变，建议在此处覆盖默认图标
     */
    @Override
    public Collection<Image> loadDefaultIcons() {
        return Collections.singletonList(new Image(getClass().getResource("/icon/icon.png").toExternalForm()));
    }

}
