# Ice and Fire CE 1.20.1 修复报告

本轮以 `docs/audit-1.20.1-2026-11-05.md` 的 41 项问题为依据，已逐项实现代码修复。原审查报告及其历史证据保持原样；本报告的“已修复”指代码已落地，运行验证范围见下文，不表示所有多人、视觉和长时间场景均已验收。

- 分支：`1.20.1`；修复基线：`2d624d664ae3242f4d589643d88fdcf366b750eb`。
- Minecraft 1.20.1 / Yarn 1.20.1+build.10 / Java 17 / Gradle 8.10。
- Fabric Loader 0.16.9 / Fabric API 0.92.2+1.20.1 / Forge 47.3.3 / Architectury 9.2.14。
- 41 项（6 P1、27 P2、8 P3）全部有实现；W01 和 V01–V08 在原审查中不属于本版待修问题，未套用其他版本的 API 或行为。
- 本轮未提交或推送代码。

## 行为与兼容性

两条旧 multipart C2S 通道保留无操作接收器，不再执行客户端指定的伤害、目标和头索引。更新后的客户端通过原版实体包交互，因此联机双方应同时更新。攻击先在服务端校验部件与距离，再转交父实体，并用 `interruptFalse()` 取消后续原版路径：实际 Architectury Forge 只在结果为 false 时取消 AttackEntityEvent，Fabric 将 false 转为 FAIL。单纯 interruptDefault 会回落原版流程，不能用于防止重复攻击。

召唤水晶使用服务端可信位置和临时区块票据。超时为 100 tick（正常 TPS 下约 5 秒），成功、换栈/离线/死亡/换维度取消或停服都会释放；不写入永久 forced-chunk 列表。主人校验比较保存的 UUID，使主人与龙处于不同维度时也能工作。跨维度 moveToWorld 返回的新龙才是清理绑定的对象。旧存档若从未记录卸载龙的位置，服务端无法凭空恢复其区块；重新加载并绑定后会生成记录。失败保留水晶绑定与其他 NBT。

鸡蛇权杖原先使用时长为 1 tick，现设为 72000 tick 支持持续使用。耐久在服务端扣到当前使用栈，结束/停止/破损立即清目标。存盘中的整数目标 ID 不跨会话恢复；客户端同步在实体尚未出现时最多重试 100 tick。

ProjectE 鹰马爪冲突值采用 4096 EMC，删除重复的 128；这是本轮选定的设计值，安装 ProjectE 的整合包可通过资源覆盖另行调整。

另修复九头蛇保存时移除全部部件、颈部更新写死为 `HEADS + 1` 和客户端主动生成部件三个相邻问题，并加入保存纯度回归。

## 逐项结果

下表均已实现，验证列区分自动回归与仍需人工覆盖的场景。

| ID | 优先级 | 修复 | 验证 |
| --- | --- | --- | --- |
| N01 | P1 | 忽略客户端位置；仅真正控制乘客或主人肩乘幼龙可设置控制位，过滤高位。 | multipartReachAndController |
| N02 | P2 | 底座、罐、房屋改走原版完整 BE 更新与区块跟踪；龙实体包限制为所在世界的跟踪玩家。 | podiumInventoryContract, jarInitialStateAndSingleRelease；多人跨维度显示需人工验收 |
| N03 | P1 | 先检查按钮编号与菜单有效性，再访问数组；页索引严格检查 0 <= index < size。 | lecternBoundsAndLifetime |
| N04 | P2 | 校验同世界、世界内 BE 身份及 8 格距离。 | lecternBoundsAndLifetime |
| N05 | P2 | 移除 START_TRACKING_TAIL 警报注册；保留实际攻击事件的鸡/村民警报。 | 源码复核；进视距时敌意需人工验收 |
| F01 | P2 | 用当前组装结果切换核心；同种核心启停转换保留 BE 和库存，避免重复设置方块。 | forgeStateKeepsInventory |
| F02 | P2 | 每次使用前校验核心身份、移除状态、邻接距离和龙种，失效后重新查找。 | forgeInputRevalidatesCore |
| F03 | P2 | 服务端每 tick 只检查一次组装，分开供能/余热/衰减逻辑，无热时进度每 tick 减一。 | forgeCoolingAndPersistence |
| F04 | P2 | 使用包含 NBT 的 ItemStack.canCombine；结果物品自身最大堆叠数约束空槽，long 汇总数量。 | forgeOutputNbtAndCapacity |
| F05 | P3 | craft、getOutput、getResultItem 均返回副本。 | recipeReturnsCopies |
| S01 | P2 | 安全解析绑定 UUID；服务端跨世界查找、主人 UUID/龙种/存活/绑定校验；有限临时票据加载；成功后更新返回的新龙并只清 Dragon 标签；取消、超时、停服释放票据；服务端保存龙位置。 | crystalValidatesOwnerAndPreservesOtherNbt, crystalCrossDimensionClearsReturnedEntity, crystalMalformedUuidAndPositionDirtiness, crystalTimeoutReleasesTickets, crystalChangingStackCancelsTickets；磁盘卸载后恢复成功需人工验收 |
| S02 | P2 | Jar 首次区块数据包含精灵、主人、持物和产粉状态。 | jarInitialStateAndSingleRelease |
| S03 | P2 | 产粉截止时间保存；收取、进入、释放时统一标脏/同步，释放仅服务端执行并原子清空原库存与主人。 | jarInitialStateAndSingleRelease；重启后的产粉时间需人工验收 |
| S04 | P3 | CookTime 保存 int，兼容旧 short 数值读取；保存余热倒计时；供能、熔炼和库存变化标脏。 | forgeCoolingAndPersistence |
| A01 | P1 | 7 个 Goal 各自持有搜索截止时间；按实体 ID 错开首次搜索，隔 tick 调用也能到期，不再依赖全局模 4 等于零。 | searchCooldownEveryOtherTick, itemGoalFindsFoodAfterSpawn |
| A02 | P2 | 先由父 Goal 搜索新目标，再验证龙的目标条件。 | 源码及三模块编译；进世界观察初次索敌 |
| A03 | P2 | 覆盖 shouldContinue，补充伴侣非空及可移动条件。 | 源码及三模块编译；进世界繁殖回归 |
| A04 | P2 | 修正独眼巨人存活条件。 | 源码复核；进世界羊跟随回归 |
| A05 | P3 | 初始化观察者字段，同时排除观察者与目标互相乘骑。 | 源码复核；进世界乘骑回归 |
| A06 | P2 | 目标存活、持物、未驯服且尚未到达藏身点时继续逃跑。 | 源码复核；进世界精灵逃跑回归 |
| E01 | P2 | 海蛇复用 9 个部件，仅缺失/移除时创建；避免构造期重复初始化；缩放和写 NBT 不移除存活部件。 | seaSerpentReusesPartsAndSaveIsPure |
| E02 | P2 | 遍历占用体积全部方块，任一阻挡即拒绝，再汇总 BREACH。 | 源码复核；混合沙/石体积的实际寻路需人工验收 |
| E03 | P2 | 群更新每世界 tick 一次；成员去重，清死亡/移除/跨世界/离群对象，只从活成员选首领。 | flockReelectsLiveLeader |
| W02 | P3 | 村庄方向取完整 4 方向，房型覆盖全部 6 种。 | 源码复核；大样本生成分布未量化 |
| C01 | P2 | 删除 Item 单例累计耐久，在服务端对当前使用栈即时扣除；使用时长改为 72000 tick。 | scepterDamagesOnlyActiveStackAndClears |
| C02 | P2 | 目标列表只读访问；统一删除/清空方法标脏；停用、结束和耗尽耐久时清理。 | miscTargetsSerializeAndClear, scepterDamagesOnlyActiveStackAndClears |
| C03 | P2 | 闪电入队立即去重；攻击者范围只查一次；按已发现实体数限制预算。 | 源码复核；密集实体实际伤害/性能需人工验收 |
| C04 | P2 | 由挥动手传递真实手栈，仅服务端生成，并按对应手发送破损状态。 | ghostSwordOffhandOnlyOnce |
| R01 | P2 | 按语言/页面缓存文本并关闭资源流；缺页缓存为空；资源重载清缓存。 | 客户端 smoke：缓存复用、英语回退、缺页和资源重载 |
| R02 | P2 | 渲染器构造时烘焙一次备用猪模型。 | 源码及客户端渲染器初始化 smoke；未测实际帧耗时 |
| R03 | P2 | 每座石像缓存自己的假实体，类型或完整 NBT 改变时重建；不由全局类型缓存持有世界。 | 源码复核；不同 NBT 同类型石像的画面需人工验收 |
| L01 | P3 | 将减速后的向量写回 setVelocity。 | 源码复核；连续破树速度需人工验收 |
| B01 | P1 | 禁用接受客户端伤害/目标的旧通道；走原版实体攻击/交互包，校验真实部件、父实体、近战范围与可见性；伤害由服务端计算。 | multipartReachAndController, vanillaMultipartAttackHitsParentOnce |
| B02 | P1 | 接收回调只解码；ctx.queue 后重新解析玩家/世界/实体，再修改状态。 | 源码核对接收器；真实多人线程压力未测 |
| B03 | P1 | 头索引来自服务端命中部件；九头蛇内部也对负值和上界防御。 | hydraIndexDefense |
| B04 | P2 | 统一 miscData 子层读写，兼容旧外层；空更新清旧状态；服务端不恢复会话整数 ID，客户端有限重试解析。 | miscTargetsSerializeAndClear；实际光束同步需人工验收 |
| B05 | P2 | 慢跟随分支内重复碰撞调用删除，每 tick 一次。 | 源码复核；实际推挤需人工验收 |
| B06 | P2 | 底座 set/remove/clear 满足库存契约并统一标脏、同步；实现菜单构造。 | podiumInventoryContract |
| B07 | P3 | 传送门字段变化才置脏；Fabric 仅服务端变化时同步，Forge isDirty 消费真实脏标记。 | portalSyncOnlyOnChange；双平台实际网络字节未测 |
| B08 | P3 | 鹰马爪只保留一项 4096 EMC，采用资源中前面的稀有材料值。 | 1964 个 JSON 解析及重复键检查；未装 ProjectE 实测 |
| B09 | P3 | 龙洞分支、箱子朝向按完整方向数组长度随机。 | 源码复核；大样本生成分布未量化 |

## 已执行验证

- `:common:compileJava :fabric:compileJava :forge:compileJava` 通过。
- `:fabric:runGametest`：25/25 通过，真实 Fabric 服务端，包含原版攻击包路径、锻炉库存/NBT/配方、AI 错相搜索、海蛇/九头蛇部件、权杖耐久、水晶同维度与跨维度、超时/取消票据释放。完整 JUnit XML：`fixes-1.20.1/gametest.xml`。
- `:fabric:build :forge:build` 通过，包含重映射和完整两端打包。
- `:fabric:runClientRegression` 通过：客户端启动/渲染器初始化、1000 次页面缓存复用、英语回退、缺页处理、完整资源重载后缓存失效均正常。
- 1964 个资源 JSON 均可解析，无重复键。
- `git diff --check` 通过。已检查生产 jar，不包含测试类或测试模组元数据。

测试 mod 位于独立的 `fabric/src/gametest/`，不进入发布产物。`build` 默认不启动 GameTest；需显式运行下述命令。

## 复测方法

PowerShell，在项目根目录使用 Java 17：

```powershell
$env:JAVA_HOME = 'C:/Users/tanks/.gradle/jdks/eclipse_adoptium-17-amd64-windows.2'
.\gradlew.bat :fabric:runGametest :fabric:build :forge:build --console=plain
.\gradlew.bat :fabric:runClientRegression --console=plain
```

GameTest 报告输出到 `fabric/build/reports/gametest.xml`；客户端 smoke 启动、检查并自动退出，结果位于 `fabric/build/client-smoke-run/client-smoke-result.txt`，应以 PASS 开头。

人工联机复测建议合并为以下场景，无需逐 issue 重复操作：

1. 两名玩家分别在主世界/下界相同坐标放置底座、精灵罐及房屋，放物/取物/进出精灵后重新进入视距并重启服务器，确认状态不串维度、不丢物、不复制；观察产粉周期。
2. 搭建三种锻炉，装料后反复拆/补结构、换输入口相邻核心；停火观察进度回落；存读后验证库存、长进度和余热；用数据包输出不同 NBT 物品验证拒绝合并。
3. 主人绑定龙后离开并等待区块卸载，再同维度和跨维度召唤；成功后检查绑定清除，换栈、换维度或超时后检查无残留票据。旧存档没有位置记录时先加载龙并重新绑定。
4. 分别骑乘海马/羽蛇/死亡蠕虫/龙，测试升降、攻击、下坐骑及肩乘幼龙。近距离攻击/右键各部件、用龙角收龙，确认只发生一次；隔墙和远距离操作应无效。测试实际玩家攻击冷却及九头蛇断头。
5. 刷出不同初始时刻的生物，之后再投食或靠近，观察 7 类 AI；验证龙首次索敌/繁殖、羊跟随、精灵持物逃跑。让蠕虫穿过沙/石混合墙；卸载或杀死鸟群首领后观察成员与新首领。
6. 两名玩家各持权杖连续使用/切换手栈，检查耐久独立、停用光束消失；副手挥动幽灵剑应只生成一次投射物；密集实体下测试闪电唯一命中和预算。对比图鉴切页/切语言/F3+T、不同 NBT 的同类石像和重进世界画面。

## 验证边界与性能结论

GameTest 是 Fabric 真实加载器内的服务端验证。Forge 共用代码和平台适配均编译、打包；尚未在 Forge 专服/多人客户端重放整套 GameTest。未完成多人图像与光束、实际骑乘手感、磁盘卸载龙的成功恢复、ProjectE 整合及长时间压力测试。客户端 smoke 不等于进世界目视验收。开发启动仍有可选集成缺失类、非二次幂贴图降低 mip 级别、原版缺失声音/着色器 sampler 等非致命日志；本轮没有把这些日志计作回归通过或扩展为新的待修项。

性能改动消除了已确认的重复查询、无变化包和每帧文件读取/模型烘焙路径，但本轮没有进行固定硬件的 MSPT、FPS 或带宽 A/B 基准，不能给出性能提升百分比。

产物及 SHA-256 清单：`fixes-1.20.1/artifacts.json`。产物：`fabric/build/libs/IceAndFireCE-fabric-1.2.10.jar` 与 `forge/build/libs/IceAndFireCE-forge-1.2.10.jar`。源码快照哈希见 `fixes-1.20.1/source-sha256.json`，修复状态机器可读清单见 `fixes-1.20.1/fixes.json`。
