# #285：冰中龙蛋同步断线和异常颜色存档加载失败

问题：[Freezing ice dragon egg disconnects nearby players](https://github.com/IAFEnvoy/IceAndFire-CE/issues/285)。修复适用 `master` / Minecraft 26.1.2；验证环境为 NeoForge 26.1.2.97、Java 25。

## 原因与修改

此前提交 `8a602dde` 已为冰中龙蛋补上 `getUpdateTag()`，解决正常颜色未进入同步数据的问题。但空冰蛋仍把 `Color` 写为字节 `0`，加载时按字符串读取并调用抛异常的 `DragonColor.getById`。因此命令放置的冰蛋仍可能使客户端收到 `Network Protocol Error`，缺失或未知颜色的存档也会加载失败。

本次修改限制在 `EggInIceBlockEntity` 的读写边界：

- 有效颜色仍保存为字符串；没有颜色时省略 `Color`，不再写字节哨兵值。
- 加载缺失、空字符串、旧字节、错误类型、未注册或格式非法的颜色时保留 `type == null`。不抛异常，也不使用默认红蛋代替未知颜色。支持原短名称和完整命名空间 ID。
- 颜色无效时仍读取并保留孵化进度和合法主人 UUID。新保存的无主冰蛋省略 `OwnerUUID`；重复加载无主数据时清除旧主人，避免同步后残留。
- 保留完整初次区块数据和更新包。空冰蛋也能同步清空旧颜色状态，客户端继续由现有空值分支处理渲染和孵化。

无效颜色无法推断出原来的蛋色，保持为空冰蛋；已在旧版本加载失败并被后续存档覆盖的数据无法由本修复还原。

## 验证

修复前，新增服务端回归重现 `NoSuchElementException` 和 `BlockEntity.loadStatic` 返回空值；真实集成世界中 `/setblock` 创建空冰蛋后，客户端在 `onDataPacket → loadAdditional → DragonColor.getById` 抛异常并断开，原因是 `Network Protocol Error`。

修复后：

- 26 项必需服务端 GameTest 全部通过。新增 2 项覆盖七类无效颜色输入、正常四色、年龄和主人、磁盘格式的 `loadStatic` 往返、初次同步、更新包和重复加载清空状态。
- 真实客户端 10 组网络场景通过。空冰蛋由实际 `/setblock` 命令创建；蓝、白、宝蓝、银四种冰蛋通过生产 `updateEggCondition()` 冻结，测试仅加速随机等待。先验证附近客户端收到的方块实体更新，再删除客户端副本并发送实际完整区块包，验证重新构建后的颜色和主人。
- 普通 `clean build` 通过；发布 JAR 不包含回归测试类或测试结构资源。

客户端测试使用集成服务器和一名真实客户端；未进行外部专服多客户端或完整 FTB/ATM 整合包验收。存档回归验证实际游戏序列化和加载入口，未覆盖关服重启的完整磁盘流程。

## 自动复测

在已配置 Java 25 工具链的项目根目录运行：

```powershell
.\gradlew.bat -PauditRegression runGameTestServer --console=plain
.\gradlew.bat -Pissue285Regression runClient --console=plain
.\gradlew.bat clean build --console=plain
```

客户端自动创建 `build/issue285-client-runtime` 中的独立世界，测试后退出。期望日志为 `all 10 ice egg network scenarios passed`，结果文件 `issue285-client-result.json` 中 `passed:10, failed:0`。执行 `clean` 前复制需要保留的结果；最后一条命令移除测试编译产物并生成普通发布 JAR。

## 人工验收

1. 在新的测试世界或测试服附近执行 `/setblock ~1 ~ ~ iceandfire:egginice`，确认所有附近玩家保持连接；空冰蛋不凭空出现红蛋或孵化。
2. 将四种冰龙蛋分别放入水中等待冻结，确认附近玩家不掉线，颜色正确；让另一玩家首次进入该区块，再离开并返回检查。
3. 等待一段孵化时间后保存、关服并重启，检查颜色、进度和孵化后主人的归属。另用测试存档验证 `Color:0b`、缺少颜色和未知颜色的冰蛋不会因加载异常被丢弃。
