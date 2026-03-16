# stc4j

`stc4j` 是一个轻量的 Java 工具库，当前主要包含以下几个方向：

- 配置读取与可排序配置文件处理
- 状态管理器与状态机
- 可复用单线程执行器
- 常用工具类，如断言、键值对、迭代器适配

项目基于 Java 8，适合直接作为业务项目中的基础工具依赖引入。

## Maven 坐标

### Maven

```xml
<dependency>
    <groupId>com.serliunx</groupId>
    <artifactId>stc4j</artifactId>
    <version>1.0.3</version>
</dependency>
```

### Gradle

```gradle
implementation("com.serliunx:stc4j:1.0.3")
```

GAV 坐标如下：

```text
groupId:    com.serliunx
artifactId: stc4j
version:    1.0.3
```

## 模块目录

### `com.serliunx.stc4j.properties`

配置相关能力。

- `ValueBasedProperties`
  提供基于类型的配置读取接口，如 `getInteger`、`getBoolean`、`getLong`。
- `CommandLineArgsProperties`
  用于从命令行参数中解析 `-Dkey=value` 形式的配置。
- `DefaultValueBasedProperties`
  基于内存 `Map` 的简单实现。
- `SortableProperties`
  保留写入顺序，支持注释读写和 UTF-8 配置文件处理。

### `com.serliunx.stc4j.state`

状态管理与状态机相关能力。

- `manager`
  只关注状态切换逻辑，如单向、双向、断路型状态流转。
- `machine`
  在状态流转基础上增加 `entry`、`leave`、`exchange` handler 和事件发布能力。
- `support`
  提供线程池与状态机快速创建工具。
- `handler`
  状态处理器抽象与参数封装。

### `com.serliunx.stc4j.thread`

线程工具。

- `ReusableThreadExecutor`
  一个可重复使用的单线程执行器接口。
- `DefaultReusableThreadExecutor`
  默认实现。
- `thread.support`
  拒绝策略计数、线程名编号等辅助能力。

### `com.serliunx.stc4j.util`

通用工具类。

- `Assert`
  参数和状态断言工具。
- `Pair`
  轻量键值对抽象。
- `IteratorToEnumerationAdapter`
  `Iterator` 到 `Enumeration` 的适配。

## 配置模块用法

配置模块适合两类场景：

- 从命令行参数读取配置
- 读写有顺序、有注释的配置文件

### 1. 使用 `CommandLineArgsProperties`

```java
import com.serliunx.stc4j.properties.CommandLineArgsProperties;
import com.serliunx.stc4j.properties.ValueBasedProperties;

public class Demo {
    public static void main(String[] args) {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[] {
                "-Dspring.name=demo-app",
                "-Dserver.port=8080",
                "-Dfeature.enabled=true",
                "-Djdbc.url=jdbc:mysql://localhost:3306/demo?useUnicode=true"
        });

        String appName = properties.getString("spring.name");
        int port = properties.getInteger("server.port");
        boolean enabled = properties.getBoolean("feature.enabled", false);
        String jdbcUrl = properties.getString("jdbc.url");
    }
}
```

支持默认值读取：

```java
int port = properties.getInteger("server.port", 8080);
boolean enabled = properties.getBoolean("feature.enabled", false);
```

如果你使用的参数风格不是 `-Dkey=value`，也可以自定义分隔符和前缀：

```java
ValueBasedProperties properties = new CommandLineArgsProperties(
        new String[] {
                "--server.port:9090",
                "--feature.enabled:false"
        },
        ":",
        "--"
);
```

### 2. 使用 `DefaultValueBasedProperties`

如果你只是需要一个内存中的、带类型读取能力的配置容器，可以直接使用：

```java
import com.serliunx.stc4j.properties.DefaultValueBasedProperties;
import com.serliunx.stc4j.properties.ValueBasedProperties;

ValueBasedProperties properties = new DefaultValueBasedProperties()
        .merge(new CommandLineArgsProperties(new String[] {
                "-Dserver.port=8080",
                "-Dfeature.enabled=true"
        }));

int port = properties.getInteger("server.port");
```

### 3. 使用 `SortableProperties`

`SortableProperties` 适合需要保留配置项顺序、保留注释并读写文件的场景。

#### 写入配置文件

```java
import com.serliunx.stc4j.properties.SortableProperties;

import java.nio.file.Files;
import java.nio.file.Paths;

SortableProperties properties = new SortableProperties();
properties.setProperty("app.name", "demo");
properties.setProperty("app.port", "8080");

properties.setCommentLines("app.name", "# application name");
properties.setCommentLines("app.port", "# application port");

properties.store(Files.newOutputStream(Paths.get("app.properties")), "# demo config");
```

#### 读取配置文件

```java
SortableProperties properties = new SortableProperties();
properties.load(Files.newInputStream(Paths.get("app.properties")));

String appName = properties.getString("app.name");
int port = properties.getInteger("app.port");
```

#### 获取注释与全部配置

```java
System.out.println(properties.getCommentLines("app.name"));
System.out.println(properties.allProperties());
System.out.println(properties.mappedProperties());
```

## 状态机模块用法

状态模块分两层：

- `StateManager`
  只管理状态本身的切换
- `StateMachine`
  在状态切换之上增加事件处理器和事件发布能力

如果只是想做简单状态推进，用 `manager` 即可；如果需要状态回调、交换事件、异步处理，用 `machine`。

### 1. 使用状态管理器

#### 标准状态管理器

```java
import com.serliunx.stc4j.state.manager.StandardStateManager;

StandardStateManager<String> manager =
        new StandardStateManager<>(new String[] {"INIT", "RUNNING", "DONE"});

manager.switchTo("RUNNING");
System.out.println(manager.current()); // RUNNING
manager.reset();
```

#### 单向状态流转

```java
import com.serliunx.stc4j.state.manager.DefaultUnidirectionalStateManager;

DefaultUnidirectionalStateManager<String> manager =
        new DefaultUnidirectionalStateManager<>(new String[] {"NEW", "PROCESSING", "DONE"});

manager.switchNext();
manager.switchNext();
```

#### 断路型单向状态管理器

最后一个状态不允许继续推进，也不允许重置：

```java
import com.serliunx.stc4j.state.manager.BreakageUnidirectionalStateManager;

BreakageUnidirectionalStateManager<String> manager =
        new BreakageUnidirectionalStateManager<>(new String[] {"INIT", "RUNNING", "DONE"});

manager.switchNext();
manager.switchNext();
```

### 2. 使用标准状态机

标准状态机适合大多数业务场景。

```java
import com.serliunx.stc4j.state.machine.StateMachine;
import com.serliunx.stc4j.state.machine.StateMachineBuilder;

StateMachine<String> machine = StateMachineBuilder.from(new String[] {"INIT", "RUNNING", "DONE"})
        .withInitial("INIT")
        .whenEntry("RUNNING", params -> {
            System.out.println("entry: " + params.getTo());
        })
        .whenLeave("INIT", params -> {
            System.out.println("leave: " + params.getFrom());
        })
        .exchange("INIT", "RUNNING", params -> {
            System.out.println("exchange: " + params.getFrom() + " -> " + params.getTo());
        })
        .async(false)
        .build();

machine.switchTo("RUNNING");
machine.reset();
machine.close();
```

说明：

- `withInitial("RUNNING")` 会同时影响初始状态和 `reset()` 后回到的默认状态
- `whenEntry(state, handler)` 在进入某状态时触发
- `whenLeave(state, handler)` 在离开某状态时触发
- `exchange(from, to, handler)` 只在指定迁移发生时触发

### 3. 使用并发状态机

如果状态变更发生在多线程场景中，可以使用并发状态机：

```java
import com.serliunx.stc4j.state.machine.ConcurrentStateMachine;
import com.serliunx.stc4j.state.machine.StateMachineBuilder;

ConcurrentStateMachine<String> machine = StateMachineBuilder
        .from(new String[] {"INIT", "RUNNING", "DONE"})
        .withInitial("INIT")
        .concurrent()
        .async(false)
        .build();

boolean updated = machine.compareAndSet("INIT", "RUNNING");
```

当前实现中，并发状态机已经保证 handler 看到的是本次真实成功切换的前后状态。

### 4. 发布业务事件

除了状态切换回调，还可以让状态机订阅业务事件：

```java
StateMachine<String> machine = StateMachineBuilder.from(new String[] {"INIT", "RUNNING", "DONE"})
        .whenHappened("start", sm -> sm.switchTo("RUNNING", false))
        .whenHappened("finish", sm -> sm.switchTo("DONE", false))
        .async(false)
        .build();

machine.publish("start");
machine.publish("finish");
```

### 5. 异步 handler

全局开启异步：

```java
StateMachine<String> machine = StateMachineBuilder.from(new String[] {"INIT", "RUNNING"})
        .async(true)
        .build();
```

指定执行器：

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

StateMachine<String> machine = StateMachineBuilder.from(new String[] {"INIT", "RUNNING"})
        .executor(executor)
        .async(true)
        .build();
```

也可以只让某个 handler 异步执行：

```java
machine = StateMachineBuilder.from(new String[] {"INIT", "RUNNING"})
        .whenEntry("RUNNING", params -> {
            System.out.println("async entry handler");
        }, true, executor)
        .build();
```

### 6. 快速创建状态机

如果你只需要“纯状态切换”，不关心 handler 和事件，可以使用工具类：

```java
import com.serliunx.stc4j.state.support.StateMachines;

StateMachine<String> normalMachine =
        StateMachines.defaultStateMachine(new String[] {"A", "B", "C"});

ConcurrentStateMachine<String> concurrentMachine =
        StateMachines.concurrentStateMachine(new String[] {"A", "B", "C"});
```

## 线程模块用法

`thread` 模块当前核心是可复用的单线程执行器。

```java
import com.serliunx.stc4j.thread.executor.DefaultReusableThreadExecutor;
import com.serliunx.stc4j.thread.executor.ReusableThreadExecutor;

import java.util.concurrent.ArrayBlockingQueue;

ReusableThreadExecutor executor = new DefaultReusableThreadExecutor(new ArrayBlockingQueue<>(16));
executor.execute(() -> System.out.println("hello"));
executor.shutdown();
```

适合串行消费、轻量后台处理、单线程任务复用等场景。

## 工具模块用法

### `Assert`

```java
import com.serliunx.stc4j.util.Assert;

Assert.notNull(name, "name must not be null");
Assert.hasText(text);
Assert.isTrue(port > 0);
```

### `Pair`

```java
import com.serliunx.stc4j.util.Pair;

Pair<String, Integer> pair = Pair.of("port", 8080);
System.out.println(pair.left());
System.out.println(pair.right());
```

## 说明

- 项目当前以基础工具库为定位，API 整体偏轻量
- 配置模块和状态机模块目前是功能最完整、最适合直接落地使用的部分
- 如果你在业务中使用状态机，建议优先通过 `StateMachineBuilder` 或 `StateMachines` 进行创建
- 如果你只需要类型安全的配置读取，优先使用 `ValueBasedProperties` 体系
