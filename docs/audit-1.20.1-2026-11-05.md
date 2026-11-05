# Ice and Fire CE：Minecraft 1.20.1 全项目复核报告

日期：2026-11-05。分支：1.20.1。审查源码：2d624d664ae3242f4d589643d88fdcf366b750eb。

26.1.2 的修复、测试和报告已在 master 合并为单个本地提交 8a602dde（fix: resolve 26.1.2 audit findings and add regression coverage），提交后工作区干净，再切换到本分支。本报告没有修改 1.20.1 生产代码；没有推送远程。

本版确认 41 项源码缺陷：6 项 P1、27 项 P2、8 项 P3。26.1.2 报告的 41 项逐项复核，其中 32 项在本版仍存在，9 项不适用；另发现 9 项问题。计数以本报告和 findings.json 为准，不能当作 master 修复状态。

最优先处理客户端指定伤害（B01）、未切游戏线程的网络回调（B02）、九头蛇非法头索引（B03）、乘骑坐标信任（N01）、讲台非法按钮（N03）以及 AI 固定错相（A01）。

Fabric 与 Forge 完整构建通过。构建任务没有现成测试源码；未启动这版客户端、专服或 GameTest，也未测试多人、渲染画面和性能基准。构建通过不能证明网络处理、游戏行为和 Mixin 运行正确。

## 范围与方法

配置为 Java 17、Gradle 8.10、Minecraft 1.20.1、Yarn 1.20.1+build.10、Forge 47.3.3、Fabric Loader 0.16.9、Fabric API 0.92.2+1.20.1、Architectury 9.2.14，模组版本 1.2.10。

范围包括 common、fabric、forge 全部 609 个生产 Java 文件（63,030 行），以及 1,971 个资源 JSON、构建配置和 Mixin 配置。全量建立文件清单、SHA256 与风险模式索引，并用 SpotBugs 4.9.8 分析三个模块的编译输出；人工重点追踪网络、库存/存档、AI、实体生命周期、战斗、世界生成、渲染及兼容入口。不是逐行人工证明全部代码正确，也不保证没有其他问题。

没有只检查旧报告或最初十个优化点。新增问题覆盖 multipart 协议、两平台线程语义、NBT 层级、部件重复碰撞、底座库存契约、两平台同步、ProjectE 资源和龙洞生成。涉及版本差异时读取本版依赖源码或 javap 字节码，未照搬 26.1.2 类名/API。

优先级：P1 应优先处理，涉及不可信输入、异常或核心行为长期失效；P2 为可复现的功能/生命周期问题与明显重复工作；P3 为较窄触发、数据边界或低优先级成本。源码确认与游戏运行确认分开记录。

| 分类 | 数量 |
|---|---:|
| P1 | 6 |
| P2 | 27 |
| P3 | 8 |
| 合计 | 41 |

## 26.1.2 问题的版本复核矩阵

编号保留用于对照；“仍存在”表示本次 1.20.1 源码检查结论，不表示 master 的修复无效。原因和修复方向以本版正文为准。

| 编号 | 本版结论 | 版本说明 |
|---|---|---|
| N01 | 仍存在：乘骑控制包采纳客户端坐标，控制者及所有权验证不完整 | 按本版源码重新确认，详见问题正文。 |
| N02 | 仍存在：方块实体状态向全服广播，缺少维度和跟踪范围 | 按本版源码重新确认，详见问题正文。 |
| N03 | 仍存在：讲台按钮先索引数组再限制编号 | 按本版源码重新确认，详见问题正文。 |
| N04 | 仍存在：讲台菜单在方块被拆除或玩家离开后仍有效 | 按本版源码重新确认，详见问题正文。 |
| F01 | 仍存在：锻炉切换使用旧组装状态，且状态替换会散落库存 | 26.1.2 的 setState 单选激活核心结论不能直接套用到本版。 |
| F02 | 仍存在：锻炉输入口永久保留失效核心引用 | 按本版源码重新确认，详见问题正文。 |
| F03 | 仍存在：失去龙焰后进度先减再加，结构查询重复 | 按本版源码重新确认，详见问题正文。 |
| F04 | 仍存在：锻炉输出忽略 NBT，空输出槽容量也取错对象 | 这里使用 1.20.1 的 NBT 语义，不是 26.1.2 DataComponent API。 |
| F05 | 仍存在：锻炉配方返回可变的共享输出模板 | 按本版源码重新确认，详见问题正文。 |
| S01 | 仍存在：召唤水晶的卸载恢复、跨维度及身份校验不完整 | 按本版源码重新确认，详见问题正文。 |
| S02 | 仍存在：精灵罐缺少首次区块同步数据 | 按本版源码重新确认，详见问题正文。 |
| S03 | 仍存在：精灵罐和房屋关键状态变化未完整持久化 | 按本版源码重新确认，详见问题正文。 |
| A01 | 仍存在：七种 AI 使用全局模四搜索，可与选择器固定错相 | 结论依赖本版真实选择器调度，不能把查询次数减少直接视作正确优化。 |
| A02 | 仍存在：龙目标搜索要求初始目标先非空 | 按本版源码重新确认，详见问题正文。 |
| A03 | 仍存在：龙繁殖仍使用未覆盖的旧生命周期方法名 | 按本版源码重新确认，详见问题正文。 |
| A04 | 仍存在：羊跟随独眼巨人的继续条件写反 | 按本版源码重新确认，详见问题正文。 |
| A05 | 仍存在：忽略乘客的观察 Goal 主体字段从未赋值 | 按本版源码重新确认，详见问题正文。 |
| A06 | 仍存在：精灵逃跑只在靠近终点时继续 | 按本版源码重新确认，详见问题正文。 |
| E01 | 仍存在：海蛇每次缩放重建世界部件，未移除旧数组部件 | 1.20.1 使用普通 multipart 实体，不应套用 26.1.2 PartEntity 世界索引说明。 |
| E02 | 仍存在：死亡蠕虫体积寻路没有汇总所有体素的通行性 | 按本版源码重新确认，详见问题正文。 |
| E03 | 仍存在：金属羽鸟群永久持有已移除成员 | 按本版源码重新确认，详见问题正文。 |
| W01 | 不适用：龙息转化地形恢复逻辑写在错误的tick回调 | 不适用：1.20.1 AbstractBlock.randomTick 默认调用 scheduledTick，结合本类 ticksRandomly，恢复回调可被随机 tick 正常触发。已核对实际字节码，不因方法名认定无效。 |
| W02 | 仍存在：精灵村庄少一个道路方向和一种房屋 | 按本版源码重新确认，详见问题正文。 |
| C01 | 仍存在：鸡蛇权杖把耐久累计保存在共享 Item 实例 | 本版 getMaxUseTime=1，不能直接复用 26.1.2 的持续长按用例；还需检查完整使用结束路径。 |
| C02 | 仍存在：权杖部分目标清理绕过同步标记 | 本版保存的是 LivingEntity 引用和临时整数 ID，不是 26.1.2 的 UUID 列表；不能照搬 UUID 回收结论。 |
| C03 | 仍存在：连锁闪电重复入队和查询固定攻击者范围 | 按本版源码重新确认，详见问题正文。 |
| C04 | 仍存在：幽灵剑显式拒绝副手，且生成路径没有服务端保护 | 本版不是“Mixin 总传主手”，错误发生在后续显式主手检查。 |
| R01 | 仍存在：图鉴逐帧两次读资源文本，输入流未关闭 | 按本版源码重新确认，详见问题正文。 |
| R02 | 仍存在：石像每次绘制都创建并烘焙备用猪模型 | 按本版源码重新确认，详见问题正文。 |
| R03 | 仍存在：石像按实体类型缓存假实体，串用首座石像 NBT | 按本版源码重新确认，详见问题正文。 |
| N05 | 仍存在：开始跟踪鸡或村民就触发受攻击警报 | 按本版源码重新确认，详见问题正文。 |
| S04 | 仍存在：锻炉进度存盘缺少标脏且被 short 截断 | 按本版源码重新确认，详见问题正文。 |
| L01 | 仍存在：独眼巨人破树后的减速向量被丢弃 | 按本版源码重新确认，详见问题正文。 |
| V01 | 不适用：底座的非龙蛋物品分支没有提交绘制 | 非蛋分支调用即时 itemRenderer.renderItem，已有实际绘制。 |
| V02 | 不适用：人形护甲的多个槽位共享延迟绘制模型状态 | 本版直接写顶点，不存在 26 的延迟提交队列覆盖槽位姿态机制。 |
| V03 | 不适用：多处自定义几何提交没有保存独立模型姿态 | 本版 Tabula/AdvancedEntityModel 立即绘制；不能套用 26 SubmitNodeCollector 的姿态快照问题。 |
| V04 | 不适用：LegacyMobRenderer无条件提交隐形生物的实体身体 | 龙、精灵等继承原版 MobEntityRenderer/LivingEntityRenderer，未发现对应无条件实体身体提交路径；其他显隐效果仍需实机。 |
| V05 | 不适用：塞壬昼夜判定把日照角度阈值当作原始时钟tick | 消失用 world.isDay，生成用 isNight，没有 26 时钟 tick 与日照角度阈值混淆。 |
| V06 | 不适用：四种方块实体在移植中丢失首次区块同步数据 | EggInIce、PixieHouse、Podium、Lectern 都保留初始区块 NBT 覆盖；Jar 另归 S02。 |
| V07 | 不适用：模型在构造时已就绪会跳过龙/海蛇附加层初始化 | 龙在构造器直接添加五种层；海蛇实际渲染器也在构造链初始化相应层，没有 26 的延迟 model-ready hook。 |
| V08 | 不适用：内置旧版材质包仍声明1.21的资源格式34 | 内置 iaf_legacy 使用 1.20.1 的 pack_format=15；Forge 主资源包也为 15。 |

## 问题清单

| 编号 | 优先级 | 问题 | 首要位置 |
|---|---|---|---|
| A01 | P1 | 七种 AI 使用全局模四搜索，可与选择器固定错相 | [AmphithereAIFleePlayer.java:36](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/AmphithereAIFleePlayer.java:36) |
| B01 | P1 | multipart 请求允许客户端指定任意正伤害及远距离目标 | [ServerNetworkHelper.java:57](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:57) |
| B02 | P1 | 多处网络接收器直接修改世界，未排入游戏线程 | [ServerNetworkHelper.java:20](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:20) |
| B03 | P1 | 九头蛇头索引来自客户端，负值可导致后续伤害数组越界 | [ServerNetworkHelper.java:81](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:81) |
| N01 | P1 | 乘骑控制包采纳客户端坐标，控制者及所有权验证不完整 | [ServerNetworkHelper.java:42](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:42) |
| N03 | P1 | 讲台按钮先索引数组再限制编号 | [LecternScreenHandler.java:123](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/handler/LecternScreenHandler.java:123) |
| A02 | P2 | 龙目标搜索要求初始目标先非空 | [DragonAITarget.java:31](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAITarget.java:31) |
| A03 | P2 | 龙繁殖仍使用未覆盖的旧生命周期方法名 | [DragonAIMate.java:47](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAIMate.java:47) |
| A04 | P2 | 羊跟随独眼巨人的继续条件写反 | [EntitySheepAIFollowCyclops.java:50](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntitySheepAIFollowCyclops.java:50) |
| A06 | P2 | 精灵逃跑只在靠近终点时继续 | [PixieAIFlee.java:69](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/PixieAIFlee.java:69) |
| B04 | P2 | 权杖目标 ID 序列化和反序列化位于不同 NBT 层级 | [MiscData.java:103](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/data/component/MiscData.java:103) |
| B05 | P2 | 慢跟随部件每 tick 重复执行碰撞查询和推挤 | [EntityMultipartPart.java:157](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityMultipartPart.java:157) |
| B06 | P2 | 底座库存变更的持久化和增量同步不完整 | [BlockEntityPodium.java:81](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPodium.java:81) |
| C01 | P2 | 鸡蛇权杖把耐久累计保存在共享 Item 实例 | [ItemCockatriceScepter.java:29](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemCockatriceScepter.java:29) |
| C02 | P2 | 权杖部分目标清理绕过同步标记 | [ItemCockatriceScepter.java:49](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemCockatriceScepter.java:49) |
| C03 | P2 | 连锁闪电重复入队和查询固定攻击者范围 | [DragonSteelOverrides.java:96](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/tool/DragonSteelOverrides.java:96) |
| C04 | P2 | 幽灵剑显式拒绝副手，且生成路径没有服务端保护 | [ItemGhostSword.java:32](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/tool/ItemGhostSword.java:32) |
| E01 | P2 | 海蛇每次缩放重建世界部件，未移除旧数组部件 | [EntitySeaSerpent.java:197](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntitySeaSerpent.java:197) |
| E02 | P2 | 死亡蠕虫体积寻路没有汇总所有体素的通行性 | [NodeProcessorDeathWorm.java:62](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/pathfinding/NodeProcessorDeathWorm.java:62) |
| E03 | P2 | 金属羽鸟群永久持有已移除成员 | [StymphalianBirdFlock.java:54](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/util/StymphalianBirdFlock.java:54) |
| F01 | P2 | 锻炉切换使用旧组装状态，且状态替换会散落库存 | [BlockEntityDragonForge.java:66](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:66) |
| F02 | P2 | 锻炉输入口永久保留失效核心引用 | [BlockEntityDragonForgeInput.java:30](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForgeInput.java:30) |
| F03 | P2 | 失去龙焰后进度先减再加，结构查询重复 | [BlockEntityDragonForge.java:70](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:70) |
| F04 | P2 | 锻炉输出忽略 NBT，空输出槽容量也取错对象 | [BlockEntityDragonForge.java:246](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:246) |
| N02 | P2 | 方块实体状态向全服广播，缺少维度和跟踪范围 | [BlockEntityPodium.java:86](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPodium.java:86) |
| N04 | P2 | 讲台菜单在方块被拆除或玩家离开后仍有效 | [BlockEntityLectern.java:177](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityLectern.java:177) |
| N05 | P2 | 开始跟踪鸡或村民就触发受攻击警报 | [IceAndFire.java:73](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/IceAndFire.java:73) |
| R01 | P2 | 图鉴逐帧两次读资源文本，输入流未关闭 | [BestiaryScreen.java:687](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/gui/bestiary/BestiaryScreen.java:687) |
| R02 | P2 | 石像每次绘制都创建并烘焙备用猪模型 | [RenderStoneStatue.java:68](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/render/entity/RenderStoneStatue.java:68) |
| R03 | P2 | 石像按实体类型缓存假实体，串用首座石像 NBT | [RenderStoneStatue.java:85](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/render/entity/RenderStoneStatue.java:85) |
| S01 | P2 | 召唤水晶的卸载恢复、跨维度及身份校验不完整 | [ItemSummoningCrystal.java:92](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemSummoningCrystal.java:92) |
| S02 | P2 | 精灵罐缺少首次区块同步数据 | [BlockEntityJar.java:100](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityJar.java:100) |
| S03 | P2 | 精灵罐和房屋关键状态变化未完整持久化 | [BlockEntityJar.java:64](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityJar.java:64) |
| A05 | P3 | 忽略乘客的观察 Goal 主体字段从未赋值 | [EntityAIWatchClosestIgnoreRider.java:9](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntityAIWatchClosestIgnoreRider.java:9) |
| B07 | P3 | 传送门状态无变化仍每玩家每 tick 同步 | [PortalDataComponent.java:27](E:/Java/IceAndFire-CE/fabric/src/main/java/com/iafenvoy/iceandfire/fabric/component/PortalDataComponent.java:27) |
| B08 | P3 | ProjectE 自定义转换资源重复定义鹰马爪且数值冲突 | [iceandfire_default.json:37](E:/Java/IceAndFire-CE/common/src/main/resources/data/iceandfire/pe_custom_conversions/iceandfire_default.json:37) |
| B09 | P3 | 龙洞分支生成的方向随机上界少一个 | [DragonCaveStructure.java:127](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/world/structure/DragonCaveStructure.java:127) |
| F05 | P3 | 锻炉配方返回可变的共享输出模板 | [DragonForgeRecipe.java:77](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/recipe/DragonForgeRecipe.java:77) |
| L01 | P3 | 独眼巨人破树后的减速向量被丢弃 | [EntityCyclops.java:324](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityCyclops.java:324) |
| S04 | P3 | 锻炉进度存盘缺少标脏且被 short 截断 | [BlockEntityDragonForge.java:182](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:182) |
| W02 | P3 | 精灵村庄少一个道路方向和一种房屋 | [PixieVillageStructure.java:76](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/world/structure/PixieVillageStructure.java:76) |

## 逐项证据与修复方向

### N01 / P1 / 旧项复核：乘骑控制包采纳客户端坐标，控制者及所有权验证不完整

位置：[ServerNetworkHelper.java:42](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:42)；[ServerNetworkHelper.java:50](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:50)。

**原因：**DRAGON_CONTROL 只检查递归乘客关系；海马、死亡蠕虫、羽蛇直接采用包内 BlockPos。海马与羽蛇的所有权 if 只约束控制位，setPos 位于 if 外。

**影响：**自定义客户端可移动所乘实体到未经服务端验证的位置；普通客户端的整数坐标还会丢失小数精度。

**已做验证：**已追踪三种实体分支；未在服务器发送伪造包。

**后续修复方向：**服务端计算移动，限制为真正控制乘客；统一验证所有者、控制位和实体生命周期。

### N02 / P2 / 旧项复核：方块实体状态向全服广播，缺少维度和跟踪范围

位置：[BlockEntityPodium.java:86](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPodium.java:86)；[ClientNetworkHelper.java:73](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ClientNetworkHelper.java:73)。

**原因：**Podium、Jar、PixieHouse 的自定义包只带 BlockPos。实际 Uranus ServerHelper.sendToAll 遍历全服玩家，接收端在玩家当前世界取同坐标方块实体。

**影响：**不同维度同坐标的展示物、精灵或产粉状态可能被错误改写；无关玩家也收到更新。

**已做验证：**已核对发送点、接收点和实际依赖字节码。

**后续修复方向：**发给同维度正在跟踪该区块的玩家，使用目标版本标准 BE 同步或显式维度标识。

### N03 / P1 / 旧项复核：讲台按钮先索引数组再限制编号

位置：[LecternScreenHandler.java:123](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/handler/LecternScreenHandler.java:123)；[LecternScreenHandler.java:106](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/handler/LecternScreenHandler.java:106)。

**原因：**onButtonClick 在验证 id 属于 0..2 前读取 possiblePagesInt[id]；getPossiblePages 又把上界钳制为 size 而非有效索引上界。

**影响：**讲台已放入足量手稿时，异常按钮编号可触发数组越界；异常页字段可触发列表越界。具体断连或服务端异常处理取决于加载器。

**已做验证：**已核对条件求值顺序与实际数组大小；未发送异常客户端请求。

**后续修复方向：**在任何访问前拒绝非法按钮编号；对页索引作范围验证，非法状态返回失败。

### N04 / P2 / 旧项复核：讲台菜单在方块被拆除或玩家离开后仍有效

位置：[BlockEntityLectern.java:177](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityLectern.java:177)；[LecternScreenHandler.java:69](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/handler/LecternScreenHandler.java:69)。

**原因：**BlockEntityLectern.canPlayerUse 恒为 true，菜单直接代理该返回值。

**影响：**玩家远离或方块被移除后，旧菜单仍可操作旧库存引用。

**已做验证：**已对照同版本锻炉和底座的身份及距离检查。

**后续修复方向：**验证世界内 BE 身份、同维度和交互距离。

### N05 / P2 / 旧项复核：开始跟踪鸡或村民就触发受攻击警报

位置：[IceAndFire.java:73](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/IceAndFire.java:73)；[ServerEvents.java:156](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/event/ServerEvents.java:156)。

**原因：**START_TRACKING_TAIL 被接到 onLivingSetTarget；函数把跟踪中的鸡或村民当作被玩家攻击的对象，并发送附近保护生物警报。

**影响：**玩家进入跟踪范围就可能招致鸡蛇或羽蛇敌意，且进入视距会触发额外邻近实体查询。

**已做验证：**已核对注册入口、回调签名和两种警报函数。

**后续修复方向：**跟踪事件只用于首次同步；警报应由真实攻击或受伤事件触发。

### F01 / P2 / 旧项复核：锻炉切换使用旧组装状态，且状态替换会散落库存

位置：[BlockEntityDragonForge.java:66](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:66)；[BlockDragonForgeCore.java:94](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/block/BlockDragonForgeCore.java:94)。

**原因：**tick 发现组装状态变化后把 prevAssembled 传给 setState。setState 的 active/disabled 分支本身正确，但 onStateReplaced 无同机器转换保护，先散落库存、移除 BE，再恢复旧 BE 引用。

**影响：**核心外观和组装状态反向或滞后；发生不同核心方块转换时库存被散落。ItemScatterer 逐次 split 原栈，因此本项证据不支持宣称必然复制物品。

**已做验证：**已核对本版 setState 和 ItemScatterer 的实际字节码；未运行多方块测试。

**后续修复方向：**一次计算新组装状态并据此切换；同机器状态转换保留库存和 BE，真正拆除时才散落。

**版本差异：**26.1.2 的 setState 单选激活核心结论不能直接套用到本版。

### F02 / P2 / 旧项复核：锻炉输入口永久保留失效核心引用

位置：[BlockEntityDragonForgeInput.java:30](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForgeInput.java:30)。

**原因：**核心只在缓存为 null 时搜索；未核对 removed、当前位置身份、相邻关系和龙种。

**影响：**拆换核心后仍可能向已失效对象供能，输入口不能可靠恢复。

**已做验证：**已追踪 tick、核心搜索和供能路径。

**后续修复方向：**每次使用前轻量校验缓存，失效后清空并按本版相邻结构重新解析。

### F03 / P2 / 旧项复核：失去龙焰后进度先减再加，结构查询重复

位置：[BlockEntityDragonForge.java:70](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:70)；[BlockEntityDragonForge.java:78](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:78)。

**原因：**isBurning 根据 cookTime>0 判断。无焰且可熔炼时先减一，剩余进度仍大于零就再加一；同 tick 还多次 assembled，并在客户端执行 updateGrills。

**影响：**进度初值≥2时可保持平台值，不能按设计冷却；重复方块读取增加多锻炉负载。

**已做验证：**控制流模拟确认初始 2 和 20 在 100 个无焰 tick 后保持原值；该模拟不是实际锻炉 GameTest。

**后续修复方向：**分离火焰供能与进度状态，服务端单次缓存组装结果，明确余热和衰减规则。

### F04 / P2 / 旧项复核：锻炉输出忽略 NBT，空输出槽容量也取错对象

位置：[BlockEntityDragonForge.java:246](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:246)；[BlockEntityDragonForge.java:250](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:250)。

**原因：**areItemsEqual 只比较物品种类；NBT 不同仍允许合并。容量上限从当前输出栈取，空槽时不能正确反映配方结果的最大堆叠数。

**影响：**数据包输出带名称、附魔或其他 NBT 时可能被错误叠加或丢失数据；异常数量输出缺少正确容量约束。

**已做验证：**已核对 ItemStack.areItemsEqual/canCombine 字节码。独立 JVM 配方探针在原版注册表 bootstrap 时失败，未算作通过。

**后续修复方向：**用本版包含 NBT 的合并检查；按结果栈、槽位和容器上限校验总数量。

**版本差异：**这里使用 1.20.1 的 NBT 语义，不是 26.1.2 DataComponent API。

### F05 / P3 / 旧项复核：锻炉配方返回可变的共享输出模板

位置：[DragonForgeRecipe.java:77](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/recipe/DragonForgeRecipe.java:77)；[DragonForgeRecipe.java:72](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/recipe/DragonForgeRecipe.java:72)。

**原因：**craft、getOutput、getResultItem 均直接返回 this.result。

**影响：**调用方修改返回 ItemStack 会污染后续配方结果。正常锻炉 smeltItem 在空输出分支调用 copy，因此不能据此断言正常熔炼必然污染模板。

**已做验证：**已核对所有返回方法和正常消费路径；运行探针受 loader 访问转换限制，未取得运行确认。

**后续修复方向：**生产产出返回副本；查询展示结果也应明确只读或复制约定。

### S01 / P2 / 旧项复核：召唤水晶的卸载恢复、跨维度及身份校验不完整

位置：[ItemSummoningCrystal.java:92](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemSummoningCrystal.java:92)；[ItemSummoningCrystal.java:115](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemSummoningCrystal.java:115)；[EntityDragonBase.java:2961](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityDragonBase.java:2961)。

**原因：**只在玩家当前维度查 UUID；外层服务端分支内又判断 isClient 才强制加载，路径不可达。服务器卸载记录维护入口也有侧别错误。召回仅要求查到任意 Entity，不复查龙种或所有者；UUID 读取在 try 外。

**影响：**远处卸载的龙或异维度龙无法正常召回。转交他人绑定水晶后缺少召回时鉴权；损坏 UUID NBT 可抛异常。

**已做验证：**已核对绑定记录、持久状态和 summonEntity 调用链。

**后续修复方向：**服务端记录维度及位置；有期限地加载并重试、释放票据；验证绑定、实体类型及主人，安全解析 NBT。

### S02 / P2 / 旧项复核：精灵罐缺少首次区块同步数据

位置：[BlockEntityJar.java:100](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityJar.java:100)。

**原因：**仅实现 toUpdatePacket，未覆盖 toInitialChunkDataNbt；本版 BlockEntity 默认初始数据为空 NBT。

**影响：**后来加入、重新进入区块的客户端无法可靠得到精灵颜色、是否有精灵、产粉等完整初始状态。

**已做验证：**已核对 Jar 的完整实现和 1.20.1 BlockEntity 默认方法字节码。

**后续修复方向：**返回完整可同步的初始数据，并核对首次加载与增量更新字段一致性。

### S03 / P2 / 旧项复核：精灵罐和房屋关键状态变化未完整持久化

位置：[BlockEntityJar.java:64](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityJar.java:64)；[BlockEntityJar.java:118](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityJar.java:118)；[BlockEntityPixieHouse.java:103](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPixieHouse.java:103)；[BlockJar.java:70](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/block/BlockJar.java:70)。

**原因：**产粉、取粉、释放等修改缺少 markDirty；释放没有内部 hasPixie 幂等守卫，持物引用和主人等字段未完整清空。Jar 释放还在两侧 spawnEntity；UPDATE_PIXIE_HOUSE 名称虽然别扭，但接收端明确兼容 Jar，并非单凭通道名就不工作。

**影响：**区块没有其他脏写时，状态可能在重载后回退；重复调用释放可产生额外精灵或重复持物风险；客户端可能出现临时虚假实体。

**已做验证：**已检查正常入口的 hasPixie 条件，未声称普通单次右键必然重复生成。

**后续修复方向：**统一服务端原子释放/取粉方法，迁移物品后清空引用；标脏并同步，客户端只展示。

### S04 / P3 / 旧项复核：锻炉进度存盘缺少标脏且被 short 截断

位置：[BlockEntityDragonForge.java:182](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:182)；[BlockEntityDragonForge.java:316](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityDragonForge.java:316)。

**原因：**供能和进度变化不总是 markDirty；CookTime 写入 int 前强转 short，lastDragonFlameTimer 未存储，writeNbt 也未调用保存父类 Lock/CustomName 的实现。

**影响：**仅进度变化时重载可能回退；高于 32767 tick 的自定义配方进度可能变负。外部 NBT 提供的父类状态也不能完整保存。

**已做验证：**已核对本版存取格式和父类序列化方法；长配方边界为源码推导。

**后续修复方向：**按状态变化标脏，保留完整 int 并验证读入边界；明确供能重载策略并保留父类数据。

### A01 / P1 / 旧项复核：七种 AI 使用全局模四搜索，可与选择器固定错相

位置：[AmphithereAIFleePlayer.java:36](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/AmphithereAIFleePlayer.java:36)；[AmphithereAITargetItems.java:53](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/AmphithereAITargetItems.java:53)；[CockatriceAITargetItems.java:46](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/CockatriceAITargetItems.java:46)；[DragonAITargetItems.java:65](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAITargetItems.java:65)；[DreadAIRideHorse.java:28](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/DreadAIRideHorse.java:28)；[PixieAIFlee.java:39](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/PixieAIFlee.java:39)；[PixieAIPickupItem.java:52](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/PixieAIPickupItem.java:52)。

**原因：**1.20.1 MobEntity 按 (serverTicks+entityId)%2 交替运行完整 GoalSelector.tick 和仅运行目标 tick。搜索再要求 worldTime%4==0，部分固定相位下 canStart 永远碰不到搜索窗口。

**影响：**如果出生初期未搜索到目标，在相位保持时可能持续不捡食、不逃跑或不找马；批量查询还集中到同一世界 tick。

**已做验证：**已核对真实 MobEntity/GoalSelector 字节码；四种奇偶组合的 200 tick 控制流模拟中，两组搜索次数为零。未运行实体 GameTest。

**后续修复方向：**使用实体自己的到期时间或可递减冷却，在可调用的调度点保证最终搜索，并分散相位。

**版本差异：**结论依赖本版真实选择器调度，不能把查询次数减少直接视作正确优化。

### A02 / P2 / 旧项复核：龙目标搜索要求初始目标先非空

位置：[DragonAITarget.java:31](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAITarget.java:31)。

**原因：**父类 super.canStart 才负责搜索，但它被放在 targetEntity 非空分支内。

**影响：**新 Goal 的 targetEntity 初始为空时不能进入首次搜索，主动寻敌/觅食目标链失效。

**已做验证：**已核对 ActiveTargetGoal 搜索生命周期及本类调用顺序。

**后续修复方向：**先按父类契约完成搜索，再对找到的候选执行龙的额外限制。

### A03 / P2 / 旧项复核：龙繁殖仍使用未覆盖的旧生命周期方法名

位置：[DragonAIMate.java:47](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAIMate.java:47)。

**原因：**continueExecuting 不是本版 Goal.shouldContinue 的覆盖。实际默认 shouldContinue 每次调用 canStart，导致运行期间重新搜索并替换伴侣。

**影响：**额外邻近查询及伴侣漂移，活体、恋爱状态和繁殖计时的继续条件没有按作者实现执行；不等同于完全无法繁殖。

**已做验证：**独立 JVM 对实际 Goal 类调用两次 shouldContinue，确认两次都调用 canStart。

**后续修复方向：**覆盖本版 shouldContinue，加 @Override，使用当前伴侣验证继续条件。

### A04 / P2 / 旧项复核：羊跟随独眼巨人的继续条件写反

位置：[EntitySheepAIFollowCyclops.java:50](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntitySheepAIFollowCyclops.java:50)。

**原因：**shouldContinue 在独眼巨人活着时返回 false。

**影响：**羊启动跟随后很快停止，活体牧羊行为不连续。

**已做验证：**已核对 canStart、start、shouldContinue。

**后续修复方向：**拒绝空或死亡目标，并按距离及路径状态继续跟随。

### A05 / P3 / 旧项复核：忽略乘客的观察 Goal 主体字段从未赋值

位置：[EntityAIWatchClosestIgnoreRider.java:9](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntityAIWatchClosestIgnoreRider.java:9)；[EntityAIWatchClosestIgnoreRider.java:25](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntityAIWatchClosestIgnoreRider.java:25)。

**原因：**构造函数只传给父类，未给 this.entity 赋值；canStart 又要求目标乘客树中包含该 null 引用。

**影响：**该观察 Goal 实际不能正常启动。此递归不会解引用 null，不能报告为必然空指针。

**已做验证：**已核对字段全部引用和递归逻辑。

**后续修复方向：**保存主体，按忽略乘客的实际意图检查关系及布尔方向。

### A06 / P2 / 旧项复核：精灵逃跑只在靠近终点时继续

位置：[PixieAIFlee.java:69](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/ai/PixieAIFlee.java:69)。

**原因：**shouldContinue 的距离条件要求到 hidePlace 的平方距离小于 2，而逃跑启动后通常离终点更远。

**影响：**逃跑过程在正常距离条件下立即退出并反复重启。

**已做验证：**已核对逃跑目标设置、tick 和继续条件。

**后续修复方向：**在仍有威胁且尚未到达终点时继续，必要时设失效和超时条件。

### E01 / P2 / 旧项复核：海蛇每次缩放重建世界部件，未移除旧数组部件

位置：[EntitySeaSerpent.java:197](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntitySeaSerpent.java:197)；[EntitySeaSerpent.java:204](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntitySeaSerpent.java:204)。

**原因：**updateScale 首先覆盖 segments 数组，随后生成九个普通 EntitySlowPart。旧数组部件不被 removeParts 访问，父实体仍存活时其 tick 不会自行清除。

**影响：**重复缩放可累积独立部件实体、碰撞查询及网络同步；保存还会触发当前部件重建生命周期，需要额外回归。

**已做验证：**已核对普通实体注册、缩放调用、removeParts 和父实体检查；未测实际实体数量。

**后续修复方向：**保留部件对象并原位更新尺度，或先移除旧部件后重建。

**版本差异：**1.20.1 使用普通 multipart 实体，不应套用 26.1.2 PartEntity 世界索引说明。

### E02 / P2 / 旧项复核：死亡蠕虫体积寻路没有汇总所有体素的通行性

位置：[NodeProcessorDeathWorm.java:62](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/pathfinding/NodeProcessorDeathWorm.java:62)。

**原因：**循环覆盖 isPassable，末尾只由最后一个位置决定；早期 BREACH 分支也可在未遍历完整体积时直接返回。

**影响：**大型蠕虫可能选择局部被挡住的路径，碰墙和重复寻路会抵消表面的计算节省。

**已做验证：**已核对三维循环和调用方节点类型。

**后续修复方向：**按完整包围体积聚合阻塞、流体与破沙边界，增加混合阻挡场景测试。

### E03 / P2 / 旧项复核：金属羽鸟群永久持有已移除成员

位置：[StymphalianBirdFlock.java:54](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/util/StymphalianBirdFlock.java:54)；[StymphalianBirdFlock.java:51](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/util/StymphalianBirdFlock.java:51)。

**原因：**成员只添加，未清理死亡、移除、跨世界或脱群对象；重新选首领仍从未清理的列表随机选取。

**影响：**长时间存活的群会保留旧实体引用，遍历成本累积，并可能反复选到死亡首领。

**已做验证：**已核对群全部成员维护方法及鸟的 update 入口。

**后续修复方向：**按生命周期移除成员，清理后再选存活首领，并避免每只鸟同 tick 重复更新整群。

### W02 / P3 / 旧项复核：精灵村庄少一个道路方向和一种房屋

位置：[PixieVillageStructure.java:76](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/world/structure/PixieVillageStructure.java:76)；[PixieVillageStructure.java:87](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/world/structure/PixieVillageStructure.java:87)。

**原因：**nextInt(3) 只产生三个水平编号；nextInt(5) 永远到不了 switch 的 case 5。

**影响：**生成存在固定方向偏差，深色橡木房屋分支不可达。

**已做验证：**已核对随机上界为排他上界以及六个房屋分支。

**后续修复方向：**按方向数组和可用房屋数量取随机值，避免手写错边界。

### C01 / P2 / 旧项复核：鸡蛇权杖把耐久累计保存在共享 Item 实例

位置：[ItemCockatriceScepter.java:29](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemCockatriceScepter.java:29)；[ItemCockatriceScepter.java:133](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemCockatriceScepter.java:133)。

**原因：**多个玩家和 ItemStack 共用 specialWeaponDmg；攻击计数两端都可改变，停止时向当前栈结算并清零。

**影响：**满足攻击计时的多玩家交错使用可串扣或错扣耐久。

**已做验证：**已核对 Item 单例状态和计数入口；未进行多人交错操作验证。

**后续修复方向：**按使用者/物品栈保存使用状态，仅服务端结算。

**版本差异：**本版 getMaxUseTime=1，不能直接复用 26.1.2 的持续长按用例；还需检查完整使用结束路径。

### C02 / P2 / 旧项复核：权杖部分目标清理绕过同步标记

位置：[ItemCockatriceScepter.java:49](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/ItemCockatriceScepter.java:49)；[MiscData.java:75](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/data/component/MiscData.java:75)。

**原因：**onStoppedUsing 直接 clear 返回列表，checkScepterTarget 的 removeIf 也不 markDirty。removeScepterTarget 本身已经标脏，不能把所有删除 API 一概认作错误。

**影响：**当这次清理是唯一状态变化时，远端可能保留旧光束/目标表现；B04 的序列化错误会叠加干扰该状态。

**已做验证：**已核对客户端更新调用和 NeedUpdateData 脏标志。

**后续修复方向：**用统一清理接口在实际变更时标脏，明确服务器权威及客户端展示清理。

**版本差异：**本版保存的是 LivingEntity 引用和临时整数 ID，不是 26.1.2 的 UUID 列表；不能照搬 UUID 回收结论。

### C03 / P2 / 旧项复核：连锁闪电重复入队和查询固定攻击者范围

位置：[DragonSteelOverrides.java:96](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/tool/DragonSteelOverrides.java:96)；[DragonSteelOverrides.java:114](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/tool/DragonSteelOverrides.java:114)。

**原因：**attacked 只记录已出队对象，未排除已经在队列中的对象。每次出队仍以攻击者 pos 查询同一范围。

**影响：**预算允许时同一实体可被重复处理、伤害和绘制连线，邻近查询及线性 contains 成本放大。

**已做验证：**已核对当前分支已跳过源目标额外伤害且使用攻击者归属；控制流模拟四个候选时 C 出队两次、D 三次。

**后续修复方向：**入队即用 Set 去重，按设计决定单次收集候选还是围绕当前目标扩散，保留有界预算。

### C04 / P2 / 旧项复核：幽灵剑显式拒绝副手，且生成路径没有服务端保护

位置：[ItemGhostSword.java:32](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/item/tool/ItemGhostSword.java:32)；[LivingEntityMixin.java:35](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/mixin/LivingEntityMixin.java:35)。

**原因：**Mixin 已按触发 hand 取得栈，但 spawnGhostSwordEntity 只接受主手同一栈，并且两侧调用均可生成投射物、扣耐久及设置冷却。

**影响：**副手挥动无法触发；主手在客户端可能生成预测性虚假投射物和短暂错误耐久。

**已做验证：**已核对 swingHand 双端入口和生成函数；未进行客户端画面验证。

**后续修复方向：**明确副手是否支持，按触发手选择属性与扣损槽位；服务端生成实体并结算，客户端只播放必要反馈。

**版本差异：**本版不是“Mixin 总传主手”，错误发生在后续显式主手检查。

### R01 / P2 / 旧项复核：图鉴逐帧两次读资源文本，输入流未关闭

位置：[BestiaryScreen.java:687](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/gui/bestiary/BestiaryScreen.java:687)；[BestiaryScreen.java:782](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/screen/gui/bestiary/BestiaryScreen.java:782)。

**原因：**绘制图片和文字分别查找、打开并解析当前页资源，没有 try-with-resources 或缓存。

**影响：**图鉴常开时持续产生 I/O、字符串及列表分配，输入流生命周期失控；具体帧耗时和句柄影响尚无实测。

**已做验证：**已追踪 render 到两个读取点；未报告 FPS 或内存提升百分比。

**后续修复方向：**资源重载时按语言/页缓存解析结果，规范关闭流。

### R02 / P2 / 旧项复核：石像每次绘制都创建并烘焙备用猪模型

位置：[RenderStoneStatue.java:68](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/render/entity/RenderStoneStatue.java:68)。

**原因：**模型缓存查找前就执行 context.getPart(PIG) 并创建 PigEntityModel，即使稍后取到缓存也已付出成本。

**影响：**同屏大量石像时每帧重复模型树创建与分配。

**已做验证：**已核对渲染路径和缓存顺序；未进行 GPU/FPS 基准。

**后续修复方向：**在构造时准备备用模型或只在缓存未命中且确有需要时创建。

### R03 / P2 / 旧项复核：石像按实体类型缓存假实体，串用首座石像 NBT

位置：[RenderStoneStatue.java:85](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/render/entity/RenderStoneStatue.java:85)；[RenderStoneStatue.java:93](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/render/entity/RenderStoneStatue.java:93)。

**原因：**假实体以实体类型字符串作缓存键，只有首次创建时读取 trapped NBT，之后同类石像直接复用。

**影响：**同类不同年龄、变种、装备或九头蛇头数等表现可能串用；缓存也跨当前石像生命周期保留。

**已做验证：**已核对 NBT 读取位置和 Map 键；未在世界中比较实际画面。

**后续修复方向：**按石像身份或渲染状态隔离实例，或安全地在每次使用前更新所需状态；换世界和重载时清理。

### L01 / P3 / 旧项复核：独眼巨人破树后的减速向量被丢弃

位置：[EntityCyclops.java:324](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityCyclops.java:324)。

**原因：**Vec3d 不可变，multiply 返回新向量；该调用没有将结果写回实体速度。

**影响：**独眼巨人破树后预期的减速不生效。它不是一个有效的向量复用优化。

**已做验证：**源码和 SpotBugs 的无副作用返回值丢弃告警一致。

**后续修复方向：**按设计使用 setVelocity 保存计算结果，并测试连续破坏树木时移动速度。

### B01 / P1 / 本次新增：multipart 请求允许客户端指定任意正伤害及远距离目标

位置：[ServerNetworkHelper.java:57](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:57)；[ServerNetworkHelper.java:65](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:65)；[ServerNetworkHelper.java:79](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:79)。

**原因：**MULTIPART_INTERACT 接受 UUID 和 float，只要求目标是 LivingEntity 且距离<100 就直接 damage。PLAYER_HIT_MULTIPART 同样只做 100 格距离检查；没有真正部件类型、近战命中、遮挡和服务端伤害计算约束。

**影响：**知道 UUID/实体 ID 的自定义客户端可绕过正常近战范围；前一通道还能自行选伤害量。玩家也是 LivingEntity，影响包含 PvP。

**已做验证：**已核对所有接收条件和实际 damage 调用；没有创建或发送攻击流量。

**后续修复方向：**包只表示受约束的操作意图，由服务端校验真正父/子部件、合法交互范围、可见性与冷却，并计算伤害。

### B02 / P1 / 本次新增：多处网络接收器直接修改世界，未排入游戏线程

位置：[ServerNetworkHelper.java:20](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:20)；[ServerNetworkHelper.java:72](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:72)；[ClientNetworkHelper.java:42](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ClientNetworkHelper.java:42)。

**原因：**DRAGON_CONTROL、PLAYER_HIT_MULTIPART、START_RIDING_MOB_C2S 直接读写实体；多数 S2C 接收器也直接修改 BE、骑乘和视角。实际 Architectury 9.2.14 Fabric/Forge 实现直接调用 receiver，只有 ctx.queue 才 execute/enqueueWork。

**影响：**网络线程与游戏 tick 竞争实体、乘客列表和方块实体状态，可能造成竞态、状态异常或线程检查错误；正常多人使用即可走到这些分支。

**已做验证：**已核对两平台真实 NetworkManagerImpl 源码，MULTIPART_INTERACT 与闪电 S2C 已 queue，未误列为缺失。

**后续修复方向：**网络回调内只解码不可变数据；进入对应主线程后重新解析玩家、世界和实体，再鉴权并修改状态。

### B03 / P1 / 本次新增：九头蛇头索引来自客户端，负值可导致后续伤害数组越界

位置：[ServerNetworkHelper.java:81](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHelper.java:81)；[EntityHydra.java:436](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityHydra.java:436)；[EntityHydra.java:326](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityHydra.java:326)。

**原因：**triggerHeadFlags 原样保存索引。damage 只处理 lastHitHead>headCount，没有检查负值，等于 headCount 也未覆盖。

**影响：**异常负索引会在九头蛇下一次受伤时用于数组访问并抛异常；本版额外存在等于最大头数时的边界风险。

**已做验证：**已静态追踪包→字段→damage；未发送异常包或故意触发服务器崩溃。

**后续修复方向：**在解码后的主线程同时检查部件和活动头范围；实体内部也应防御非法索引，校验应在攻击前完成。

### B04 / P2 / 本次新增：权杖目标 ID 序列化和反序列化位于不同 NBT 层级

位置：[MiscData.java:103](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/data/component/MiscData.java:103)；[MiscData.java:114](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/data/component/MiscData.java:114)。

**原因：**serialize 写入外层 tag，deserialize 却从 miscData 子复合读取；空列表更新还不主动清掉旧的待恢复 ID 字段。

**影响：**服务器发送的权杖目标列表不能正常恢复，客户端缺少目标光束状态；普通标脏修复不足以解决。

**已做验证：**独立 JVM 调用实际 MiscData.deserialize：其他 miscData 标量成功恢复，外层目标 ID 列表被忽略。

**后续修复方向：**统一读写层级并处理空更新；整数实体 ID 只用于会话同步，持久化应单独考虑身份格式。

### B05 / P2 / 本次新增：慢跟随部件每 tick 重复执行碰撞查询和推挤

位置：[EntityMultipartPart.java:157](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityMultipartPart.java:157)；[EntityMultipartPart.java:164](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntityMultipartPart.java:164)；[EntitySlowPart.java:44](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/EntitySlowPart.java:44)。

**原因：**isSlowFollow 分支内执行一次碰撞，分支结束后又无条件执行一次；EntitySlowPart 固定返回 true。

**影响：**服务端每个慢跟随部件双倍邻近查询和推挤，海蛇九部件会放大成本及物理作用。

**已做验证：**已核对两次调用处、父实体侧别和子类覆盖。

**后续修复方向：**每 tick 更新部件后只执行一次必要碰撞，回归与其他实体接触时的推挤。

### B06 / P2 / 本次新增：底座库存变更的持久化和增量同步不完整

位置：[BlockEntityPodium.java:81](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPodium.java:81)；[BlockEntityPodium.java:162](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPodium.java:162)；[BlockEntityPodium.java:53](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/entity/block/BlockEntityPodium.java:53)。

**原因：**setStack 把数据写进临时 NBT 后丢弃，未 markDirty。数量移除和 clear 没统一通知；无数量 removeStack 恒返回 EMPTY。旧 getStackInSlotOnClosing 把原栈写回本身，也不是正确的移除契约。

**影响：**直接库存 API 路径可能不保存或不刷新展示；调用方期望的移除操作失败。菜单 Slot.markDirty 能覆盖部分正常交互，不能概括为每次放蛋都丢失。

**已做验证：**已核对 Inventory 方法、菜单和增量包；未进行自动化存读测试。

**后续修复方向：**统一库存变更通知，满足本版 Inventory 各移除方法契约；把标脏和可见状态同步分开实现。

### B07 / P3 / 本次新增：传送门状态无变化仍每玩家每 tick 同步

位置：[PortalDataComponent.java:27](E:/Java/IceAndFire-CE/fabric/src/main/java/com/iafenvoy/iceandfire/fabric/component/PortalDataComponent.java:27)；[PortalDataStorage.java:33](E:/Java/IceAndFire-CE/forge/src/main/java/com/iafenvoy/iceandfire/forge/component/PortalDataStorage.java:33)。

**原因：**Fabric tick 每次调用 CCA sync；Forge isDirty 恒为 true。真实 Uranus helper 在服务端 tick 后检测 isDirty 并序列化发送。

**影响：**远离传送门的玩家仍每 tick 产生无变化状态包；发送和跟踪者数量随在线玩家增加。实际带宽未测量。

**已做验证：**已核对 CCA ComponentKey 与 Forge CapabilitySyncHelper 字节码；没有将客户端无接收者的 sync 推断为必然崩溃。

**后续修复方向：**仅在状态改变或首次跟踪时同步，若倒计时无需公开则进一步缩小同步字段和接收者。

### B08 / P3 / 本次新增：ProjectE 自定义转换资源重复定义鹰马爪且数值冲突

位置：[iceandfire_default.json:37](E:/Java/IceAndFire-CE/common/src/main/resources/data/iceandfire/pe_custom_conversions/iceandfire_default.json:37)；[iceandfire_default.json:45](E:/Java/IceAndFire-CE/common/src/main/resources/data/iceandfire/pe_custom_conversions/iceandfire_default.json:45)。

**原因：**同一 JSON 对象中同一个键分别设置为 4096 和 128。语法解析可通过，但标准 Map 解析通常保留后值。

**影响：**安装支持此资源的 ProjectE 兼容环境时，配置含义不唯一，通常实际生效为 128。没有据此推断无 ProjectE 时影响基础游戏。

**已做验证：**全资源 JSON 重复键扫描与源码资源核对；未启动 ProjectE 集成环境。

**后续修复方向：**确定设计数值后只保留一项，为数据资源启用重复键检查。

### B09 / P3 / 本次新增：龙洞分支生成的方向随机上界少一个

位置：[DragonCaveStructure.java:127](E:/Java/IceAndFire-CE/common/src/main/java/com/iafenvoy/iceandfire/world/structure/DragonCaveStructure.java:127)。

**原因：**四个水平向量只在 [0,length-2] 取值，最后一个方向永久排除。

**影响：**洞穴分支布局固定偏向三个方向；和 W02 属于不同生成器的独立问题。

**已做验证：**已核对 HORIZONTALS 长度为 4 和该随机调用。

**后续修复方向：**按完整数组长度取随机索引，并检查同文件其他手写 nextInt(3) 水平方向选择。

## 其他扫描结果及未升级为缺陷的观察

- SpotBugs 共 967 条告警：common 956、fabric 6、forge 5。它们包含风格建议、公开可变字段、框架生命周期无法推导的 world-null 告警，以及 Mixin 的“impossible cast”误报。没有把告警数当作缺陷数。EmiPort 和两个 mcdev 环境注解类未能解析，兼容路径的自动分析不完整。
- 1,971 个 JSON 语法全部通过，但 B08 有冲突重复键。额外检查 820 个资源 JSON 的 343 个本模组模型/父模型引用和 631 个贴图引用，发现 4 处缺失：amythest_gem、copper_block、copper_ore、pixie_jar_5 旧模型。当前注册路径未见这四个 ID，故只记为旧资源清理项，不宣称正常注册物品必然紫黑。
- W01 在本版不成立：已从实际 AbstractBlock 字节码确认 randomTick 会默认转调 scheduledTick，原方法配合 ticksRandomly 可收到随机 tick。本版无需仅因回调名称而迁移代码。
- 渲染器中仍有逐帧 new ModelDragonEgg/ModelPixie 等分配可继续测量；RenderJar 的 Supplier 缓存策略应结合调用频率评估。即时渲染共享模型不自动构成 26 的延迟姿态覆盖问题。
- 世界生成部分使用临时 Random 或耗时地形写入，尚未做固定种子/区块耗时对照。ChunkRegionMixin 只抑制超出可写范围时的日志，没有扩大结构可写区；不应把日志减少称作生成算法优化。
- 两平台 build 证明当前依赖可编译和打包。可选 JEI/EMI/Jade、饰品、ProjectE 安装/缺省组合未作运行矩阵，弃用警告和旧 Loom 版本未被直接认定为运行崩溃。

## 验证记录

1. 使用 JDK 17 执行 .\gradlew.bat build --console=plain，BUILD SUCCESSFUL in 3m 16s，28 个任务执行，Fabric/Forge 均产生发布构建；test 任务为 NO-SOURCE。
2. 使用外部临时 init script 解析实际编译类路径并执行三个模块的 SpotBugs；分析运行成功。该脚本不修改工程构建文件。
3. 对真实编译类执行独立 JVM 语义探针，两项确认：MiscData 读取层级错误、Goal.shouldContinue 委派 canStart。没有创建世界或运行实体 AI。
4. 执行 AI 奇偶相位、无火进度和闪电队列控制流模拟。模拟解释代码路径，不等同于加载器内集成测试。
5. 尝试原版 bootstrap 下的配方探针；javac 成功，执行遇到 SimpleRegistry→RegistryEntry.Reference.setRegistryKey 的 IllegalAccessError。普通 JVM 未应用 Loom/loader 访问转换，探针未到达两个断言，因此记为未完成，而非模组加载缺陷或已通过测试。
6. 未启动 1.20.1 客户端/专服或 GameTest：当前仓库没有现成测试源码，本轮采用双平台构建、源码/字节码与有限语义探针审查，尚未建立加载器内测试场景。所有画面、多人和吞吐结论均保留为待实机确认。

## 建议的集中回归方法

以下为后续测试方案，尚未执行。使用备份存档分别在 Fabric 与 Forge 复现；优先在隔离本地专服运行涉及多人和协议的用例。

### 网络与交互

两名玩家分别测试自己的与他人的海马/羽蛇控制、主乘客与普通乘客、上下坐骑。核对服务端位置连续性和控制归属。在两个维度相同坐标放精灵罐/底座，一边更新，另一边不能改变。打开讲台后走远、拆除讲台，菜单必须失效。

协议边界使用本地测试桩调用服务端处理函数，覆盖非法按钮、非 multipart 目标、过远目标、不存在实体和负头索引；不必对实际服务器发送攻击流量。验收是拒绝且不修改状态、不抛异常。在线程测试中断言世界访问和状态修改都发生在正确游戏线程。

### 锻炉、召唤与存档

三种锻炉各完成“建成→供能→停火→拆换核心→重建”，记录组装、核心外观、物品总数和进度。测试输出空槽、上限附近堆叠、相同物品不同 NBT，以及高于 32767 tick 的自定义配方。

精灵罐产粉/取粉/释放、房屋进出、底座放取物后分别执行保存、区块卸载和重启；新玩家加入与重新进入区块应得到完整显示。留意其他方块导致区块变脏会掩盖缺标记问题。水晶覆盖已加载、卸载、跨维度、主人不符、损坏绑定数据和失败后加载票据释放。

### AI、实体和战斗

七种节流 AI 按至少两类实体 ID 奇偶分组，出生初期无目标，稳定后再投放目标/威胁，观察多于 200 tick；所有组都必须最终搜索。再测试龙主动寻敌、两只以上候选伴侣、羊跟随、精灵逃跑完整路程。

对同一海蛇重复缩放并保存/重载，统计世界部件总数、唯一身份与每 tick 碰撞调用数；每条正常海蛇应仅保留当前九个部件。蠕虫测体积中间有障碍、末格通行的混合空间。羽鸟群反复生成、死亡和卸载，成员集合应回收。

密集怪群用闪电武器，记录唯一命中与队列预算；同一链不能重复伤害同一目标。两人交错使用权杖并比较各栈耐久，注意本版最大使用时间为 1；观察服务器目标列表与其他客户端光束。幽灵剑分别测试主副手，客户端和服务器都只保留有效投射物。

### 渲染与性能

比较同类型但不同变种/年龄/装备的两座石像。图鉴长期开页和大量石像场景记录 CPU profile、分配率及帧时间分位数；把内容缓存的收益与纯粹减少搜索但丢失行为区分开。

性能测试固定版本、模组组合、存档、视距和实体数量，预热后采集同一长度的服务端 MSPT、GC 与客户端帧时间。先确认行为正确，再比较优化收益；本报告未给出任何未经测量的 FPS/MSPT 提升数值。

## 附件

- [机器可读问题清单](E:/Java/IceAndFire-CE/docs/audit-1.20.1/findings.json)；[41 项版本复核矩阵](E:/Java/IceAndFire-CE/docs/audit-1.20.1/version-matrix.json)。
- [609 个源码文件清单与风险扫描覆盖](E:/Java/IceAndFire-CE/docs/audit-1.20.1/coverage.csv)；[验证环境、限制与文件哈希](E:/Java/IceAndFire-CE/docs/audit-1.20.1/validation-manifest.json)。
- [本版 API 源码/字节码摘录](E:/Java/IceAndFire-CE/docs/audit-1.20.1/api-evidence.md)；[双平台构建日志](E:/Java/IceAndFire-CE/docs/audit-1.20.1/build.txt)。
- [静态告警候选](E:/Java/IceAndFire-CE/docs/audit-1.20.1/spotbugs-summary.json)；[资源引用扫描](E:/Java/IceAndFire-CE/docs/audit-1.20.1/resource-refs.json)；[资源语法与范围统计](E:/Java/IceAndFire-CE/docs/audit-1.20.1/scope.json)。
- [成功语义探针](E:/Java/IceAndFire-CE/docs/audit-1.20.1/semantic-probes.txt)；[未完成配方探针](E:/Java/IceAndFire-CE/docs/audit-1.20.1/recipe-probes.txt)；[控制流模拟](E:/Java/IceAndFire-CE/docs/audit-1.20.1/control-flow-probes.json)。探针 Java 源码随附件保存，方便后续迁入实际加载器测试环境。
