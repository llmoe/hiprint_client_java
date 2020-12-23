****
# xx云打印服务端

#### 介绍
JavaFx+SpringBoot开发的打印服务端软件 ，大概就是，接收socket传送的html模板信息，然后在Java里面把html转换pdf然后使用Java调用打印打印

前端支持： http://hiprint.io/  (感谢大佬的好工具)

注意：虽然是用Java写的。但是可以打包成Windows 或者 Mac 软件使用，JDK需要使用1.8版本


> 参考资料：https://segmentfault.com/a/1190000014037443

> 参考资料：https://www.cnblogs.com/hjieone/p/11139805.html

Maven插件
- [springboot-javafx-support](https://github.com/roskenet/springboot-javafx-support)
- [JavaFX Maven Plugin](https://github.com/javafx-maven-plugin/javafx-maven-plugin)

#### 配置（application.yml）

```yaml
javafx:
  title: JavaFx & SpringBoot
  appicons: /icon/icon.png
  stage:
    width: 640
    height: 480
#    style: DECORATED  # javafx.stage.StageStyle [DECORATED, UNDECORATED, TRANSPARENT, UTILITY, UNIFIED]
#    resizable: false
```

#### 打包教程

`mvn jfx:native`



#### 运行截图

<table>
    <tr>   
        <td><img src="/doc/运行截图/01.png"/></td>
    </tr>   
    <br> 
    <tr> 
         <td><img src="/doc/运行截图/02.png"/></td>
    </tr>
</table>
