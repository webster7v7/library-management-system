# Lombok 编译错误修复指南

## 已修复的问题

### 1. pom.xml 配置优化
- ✅ 添加了 Lombok 版本：1.18.30
- ✅ 在依赖中明确指定版本
- ✅ 添加了 maven-compiler-plugin 配置
- ✅ 配置了 annotationProcessorPaths

## IDE 配置步骤

### IntelliJ IDEA

#### 1. 安装 Lombok 插件
1. 打开 Settings (Ctrl+Alt+S)
2. 导航到 Plugins
3. 搜索 "Lombok"
4. 点击 Install 安装
5. 重启 IDE

#### 2. 启用注解处理
1. 打开 Settings
2. 导航到 Build, Execution, Deployment → Compiler → Annotation Processors
3. ✅ 勾选 "Enable annotation processing"
4. 点击 Apply

#### 3. 刷新 Maven 项目
1. 打开 Maven 工具窗口 (右侧边栏)
2. 点击刷新按钮 🔄
3. 等待依赖下载完成

#### 4. 清理并重新构建
1. 菜单 → Build → Clean
2. 菜单 → Build → Rebuild Project
3. 等待编译完成

### Eclipse

#### 1. 安装 Lombok
1. 下载 lombok.jar: https://projectlombok.org/downloads/lombok.jar
2. 双击运行
3. 选择 Eclipse 安装目录
4. 重启 Eclipse

#### 2. 配置项目
1. 右键项目 → Properties
2. Java Compiler → Annotation Processing
3. ✅ 启用 "Enable project specific settings"
4. ✅ 启用 "Enable annotation processing"
5. 在 "Factory Path" 中添加 lombok.jar

## 验证 Lombok 是否工作

创建测试类验证：

```java
import lombok.Data;

@Data
public class Test {
    private String name;
}
```

如果 `@Data` 注解显示红色，说明 Lombok 未正确配置。

## 常见问题

### Q: 仍然报错 "java.lang.ExceptionInInitializerError"
A: 
1. 确保 Lombok 插件已安装
2. 确保注解处理已启用
3. 清理 IDE 缓存：
   - IntelliJ IDEA: File → Invalidate Caches / Restart
4. 删除 target 目录后重新编译

### Q: 找不到 getter/setter 方法
A:
1. 检查 Lombok 插件是否启用
2. 检查注解处理是否启用
3. 重启 IDE

### Q: Maven 依赖下载失败
A:
1. 配置国内镜像（见下方）
2. 检查网络连接
3. 删除本地仓库中的错误缓存

## 国内 Maven 镜像配置

编辑 `~/.m2/settings.xml` (Windows: `C:\Users\你的用户名\.m2\settings.xml`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>

  <profiles>
    <profile>
      <id>aliyun</id>
      <repositories>
        <repository>
          <id>aliyun</id>
          <url>https://maven.aliyun.com/repository/public</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>aliyun</activeProfile>
  </activeProfiles>

</settings>
```

## 完整启动流程

1. ✅ 修复 pom.xml 配置（已完成）
2. ⏳ 在 IDE 中安装 Lombok 插件
3. ⏳ 启用注解处理
4. ⏳ 刷新 Maven 项目
5. ⏳ 清理并重新构建
6. ⏳ 运行 LibraryManagementSystemApplication
