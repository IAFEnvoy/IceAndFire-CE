# #292：龙抓住玩家时客户端崩溃

问题：[Dragon grabbing a player crashes client in updatePreyInMouth](https://github.com/IAFEnvoy/IceAndFire-CE/issues/292)。

修复分支：`master`，Minecraft 26.1.2。本次在项目锁定的 NeoForge 26.1.2.97 / Java 25 上验证；原报告使用 NeoForge 26.1.2.109，未运行完整 ATM11 整合包或双客户端联机验收。

## 原因与修复

`DragonBaseEntity.positionRider` 在客户端和服务端都会调用 `updatePreyInMouth`。抓取动画超过 55 tick 后，原代码直接把 `ClientLevel` 转为 `ServerLevel`，调用 `hurtServer`，引发 `ClassCastException`。旁观者客户端也会定位被抓住的远端玩家，因此同样可能崩溃。

伤害结算分支现在要求 `level() instanceof ServerLevel`，并使用该服务端世界调用伤害 API。伤害、回血、饥饿值及解除乘骑由服务端处理。客户端继续更新嘴部位置，等待服务端解除乘骑同步。

## 验证结果

- 修复前：同一客户端回归在真实集成世界中触发 `ClientLevel cannot be cast to ServerLevel`，堆栈经过 `updatePreyInMouth → positionRider`。
- 修复后：12 组客户端场景通过，结果为 `{"passed":12,"failed":0,"world":"ClientLevel"}`。
- 服务端：24 项必需 GameTest 全部通过，包括新增的 2 项抓取回归。
- 普通发布构建：`clean build` 通过；JAR 不包含回归测试类或测试结构资源。

客户端回归覆盖火龙、冰龙、雷龙，75 天（四阶）和 100 天（五阶），本地玩家与 `RemotePlayer` 实体。每组经过动画 tick 25、55、56、60、64，检查位置持续更新，客户端不改变生命值/饥饿值且不提前解除乘骑。该测试直接调用崩溃路径的 `positionRider`，没有模拟两名玩家的完整网络同步。

新增服务端测试覆盖三种龙、两个阶段：玩家和生物在 tick 55 前不受伤、不被释放，在 tick 56 受伤并被释放；成功攻击按配置回血，拒绝伤害时不回血，玩家不增加饥饿值，生物保持原有进食规则；释放后不重复结算。

## 自动复测

在项目根目录、已配置 Java 25 工具链的终端运行：

```powershell
.\gradlew.bat -Pissue292Regression runClient --console=plain
.\gradlew.bat -PauditRegression runGameTestServer --console=plain
.\gradlew.bat clean build --console=plain
```

客户端会自动创建 `build/issue292-client-runtime` 下的独立超平坦世界，执行断言后退出。结果文件为该目录下的 `issue292-client-result.json`；执行 `clean` 前如需保留请先复制。期望客户端日志包含 `all 12 player-prey scenarios passed`，服务端包含 `All 24 required tests passed`。

最后一条清除 opt-in 测试编译产物并生成普通发布 JAR；不要分发带回归测试源码的构建。

## 联机人工验收

1. 独立测试服与两名客户端都安装相同修复版，使用普通或更高难度。玩家 A 使用生存模式，玩家 B 在附近旁观。
2. 准备四阶或五阶野生龙，让它抓住 A，并等待甩动猎物动画结束。抓取动作有随机性，需要重复尝试。
3. 检查 A、B 均不崩溃，玩家在龙嘴部的位置持续更新；服务端结算伤害后解除抓取，两端显示一致。
4. 对三种龙和两个阶段重复，随后检查驯服龙的正常骑乘仍可用。
