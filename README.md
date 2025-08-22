# Byte Buddy Demo

本工程是一个 [Byte Buddy](https://bytebuddy.net) 的功能示例库，旨在通过具体的代码实例，深入浅出地展示其在不同场景下的核心用法。

## 功能模块

工程主要包含以下几个模块，每个模块都演示了 Byte Buddy 的一种核心能力：

1.  **动态代理 (`dynamicproxy`)**:
    *   展示了如何为接口或类创建动态代理，实现方法调用的拦截，类似于 Spring AOP。

2.  **类增强 (`enhance`)**:
    *   演示了如何在不修改源码的情况下，通过创建子类的方式为现有类添加或重写方法。

3.  **Java Agent (`agent`)**:
    *   这是 Byte Buddy 最强大的功能之一。通过 Java Agent 技术，在 JVM 加载类时动态地修改字节码，实现全局、无侵入的功能注入。
    *   示例中包含了两种 Agent 实现方式：
        *   `ToStringAgent`: 使用**反射**在运行时动态生成 `toString` 方法，简单易懂。
        *   `ToStringAgentEx`: 直接**生成字节码**来实现 `toString` 方法，性能更高。
    *   同时，也展示了 `redefine` (重定义) 和 `rebase` (变基) 两种在运行时修改已加载类的不同策略。

4.  **实体类 (`entity`)**:
    *   包含 `Person`, `Employee`, `Manager` 等 POJO 类，作为字节码操作的目标对象。

## 如何运行

### 1. 构建项目

本项目使用 Maven 构建。首先，在项目根目录下执行以下命令，打包生成 JAR 文件：

```bash
mvn clean package
```

### 2. 运行示例

#### 运行 Main 方法 (动态 Attach Agent)

可以直接运行 `org.example.Main` 类的 `main` 方法。该方法通过 `ByteBuddyAgent.install()` 在运行时动态地加载并应用 Agent。

```java
public class Main {
	public static void main(String[] args) {
		// 动态 attach agent
		ToStringAgent.premain("", ByteBuddyAgent.install());
		System.out.println(new Person());
	}
}
```

#### 通过 -javaagent 参数启动

你也可以使用标准的 `-javaagent` JVM 参数来加载代理。首先需要修改 `pom.xml` 中的 `maven-jar-plugin` 配置，将 `Premain-Class` 指向你希望启动的 Agent (例如 `org.example.agent.ToStringAgent`)，然后执行：

```bash
# 确保 pom.xml 中已配置好 Premain-Class
java -javaagent:target/bytebuddy-demo-1.0-SNAPSHOT.jar -cp target/bytebuddy-demo-1.0-SNAPSHOT.jar org.example.entity.Person
```

#### 运行其他模块

对于非 Agent 的示例 (如 `dynamicproxy`, `enhance`)，可以直接在 IDE 中运行其对应的 `main` 方法来查看效果。