# Ice and Fire CE 26.1.2 全项目复审报告

审查日期：2026-11-05。目标提交：`c67d90775dfc9c4fb87fd89ed9e211ce57c74f7b`，分支 `master`。本轮只审查并编写报告，未修复26.1.2生产代码。

## 1. 结论

**26.1.2可以构建并完成隔离GameTest服务端启动，但仍有需要处理的逻辑和迁移问题。**旧报告33项中，28项仍存在，3项部分解决，2项已解决；本轮另确认8项版本相关发现，共39项当前待处理问题。该数量不代表风险相同，也不包括静态工具的所有原始提示。

最先处理N01（坐骑位置输入）、N03（菜单按钮越界）、A01（AI搜索相位饥饿），再处理V06（区块初始同步）、V02/V03/V07（新版渲染状态和初始化）与V05（昼夜时钟）。未进行性能基准，不能给出TPS/FPS提升比例。

当前源码与1.21.1的修复后源码是两条分支。**不能因为1.21.1已经提交修复，就认为master已包含这些修复。**同时，旧报告中“核心切换掉库存”“配方共享输出栈”“进度short回绕”“每帧烘焙猪模型”等结论也不能直接套到26.1.2。详见复核矩阵。

## 2. 提交、版本与范围

- 已把原工作区所有改动放入一个提交：`1e7855e1`，`fix: address project audit findings and add regression coverage`，位于 `1.21.1`。59个文件，1328行新增、470行删除，包含原有mod_version到2.2的调整。未push。
- 已切换到26.1.2对应的现有 `master`，没有名为26.1.2的独立分支。本次没有把1.21.1修复cherry-pick到master。
- 配置：Minecraft 26.1.2、NeoForge 26.1.2.97、mod 2.2-beta.4、Java toolchain 25、Gradle 9.2.1、ModDevGradle 2.0.144。
- 全部612个Java文件、61,288行进入风险扫描；实际编译606个，另外6个为被build.gradle排除的EMI/Ponder/ProjectE/Ars Nouveau兼容代码。全部2577个JSON已做语法和重复键检查。
- 范围覆盖注册/配置、全部实体和AI、寻路/部件、物品/装备/能力、方块实体/菜单、网络/附件、持久化、世界生成/战利品、客户端渲染/GUI/粒子、兼容代码以及构建/资源。对旧33项和26迁移高风险链路做人工深读，并运行全编译字节码静态检查。
- “全项目”表示所有源码进入检查范围，并对风险链路深入追踪；不表示每个文件每行均经人工形式证明。扫描清单和人工深读清单分别记录，未把文件被扫描等同于全部行为已验证。

被排除源码：

- `src/main/java/com/iafenvoy/iceandfire/compat/emi/ForgeRecipeHolder.java`
- `src/main/java/com/iafenvoy/iceandfire/compat/emi/IceAndFireEmiPlugin.java`
- `src/main/java/com/iafenvoy/iceandfire/compat/IceAndFireArsNouveauCompat.java`
- `src/main/java/com/iafenvoy/iceandfire/compat/ponder/DragonForgeStoryBoard.java`
- `src/main/java/com/iafenvoy/iceandfire/compat/ponder/IceAndFirePonderPlugin.java`
- `src/main/java/com/iafenvoy/iceandfire/compat/ProjectEDragonForgeMapper.java`

它们未进入当前编译/字节码分析与游戏运行；保留在目录中不等于当前版本支持这些集成。

## 3. 实际验证与限制

| 验证 | 结果与范围 |
| --- | --- |
| Gradle build | 通过，1m24s；test任务为NO-SOURCE，不能视为回归测试通过 |
| SpotBugs 4.9.8 | 727个class，587条原始警报，0分析错误、0缺类；人工筛选，没有把587条当587个bug |
| 隔离runGameTestServer | 通过注册/数据加载、创建测试世界和默认1个测试；未运行本模组的逐项回归测试 |
| 隔离runData | 入口运行成功，生成0文件；无数据提供器覆盖，不能证明模型/配方datagen完整 |
| JSON | 2577个解析成功、0重复键；JSON语法正确不代表注册表引用/视觉语义正确 |
| 本模组资源引用 | 检查863处模型和624处纹理引用，3处缺失均为未注册旧资源，见观察项 |
| 控制流模型 | 复核A01相位、C03重复入队、V02延迟状态和V05时间边界；不是Minecraft集成测试 |
| 目标API | 对照当前build产出的26.1.2.97 sources.jar；Mob、Goal、BlockEntity、LevelChunk、ServerLevel、CustomFeatureRenderer、ItemStackTemplate、Timelines等关键源码与本地参考解压件一致；Level/ClientLevel仅有本项目AT造成的字段访问权限差异，所核对方法一致 |
| 未执行 | 客户端交互/截图、多人网络复现、定向实体GameTest、性能采样、JEI运行和可选依赖组合矩阵、正式启动器中安装产物的完整验收 |

所有运行目录位于独立证据目录，未使用正式存档，也未写入接受EULA。GameTest使用框架测试世界。构建/运行入口通过只说明本次依赖组合及开发环境的这些阶段能完成。

服务端启动仍出现Jade `Failed to collect shearable blocks: Registry minecraft:loot_table not found`，随后服务端继续启动、测试通过；异常栈在Jade的LootTableMineableCollector，不足以归因本模组。另有首次创建配置时Jupiter找不到文件的日志、Curios开发refmap警告和可选AutoConfig缺类警告。保留原日志供区分依赖问题与本项目问题。

静态误报筛选例：Mixin中 `(Target)(Object)this` 的“impossible cast”符合Mixin变换语义；HippocampusDepthGoal的target在canUse判非空后才启动，不据此认定必空指针；createMenu的两参数空实现另有实际使用的三参数覆写，不据此认定所有打开菜单必崩。

## 4. 旧报告33项复核矩阵

| ID | 26.1.2状态 | 当前结论 |
| --- | --- | --- |
| N01 | 仍存在 | 乘骑控制包直接采纳客户端坐标。相关逻辑仍保留。 |
| N02 | 仍存在 | 方块实体同步全服广播，缺少维度归属。相关逻辑仍保留。 |
| N03 | 仍存在 | 讲台菜单未验证按钮编号即访问数组。相关逻辑仍保留。 |
| N04 | 仍存在 | 讲台菜单有效性恒为true。相关逻辑仍保留。 |
| F01 | 部分解决 | 锻炉结构失效后仍切换到激活核心。新版通过 shouldChangedStateKeepBlockEntity 保留核心BE，旧的切换掉库存路径已消除；但 setState 仍只取激活方块，拆坏结构后不能切回 disabled。 |
| F02 | 仍存在 | 锻炉输入口永久保留旧核心引用。相关逻辑仍保留。 |
| F03 | 仍存在 | 无龙焰时锻炉进度先减再加，无法正常衰减。相关逻辑仍保留。 |
| F04 | 仍存在 | 锻炉输出合并只比物品类型，忽略数据组件。相关逻辑仍保留。 |
| F05 | 已解决 | 配方assemble返回共享模板对象。result 改为不可变模板 ItemStackTemplate；assemble/getResultItem 均调用 create() 返回新栈。 |
| S01 | 部分解决 | 召唤水晶的卸载实体恢复链仍不完整。原错误的 isClientSide 条件已改正，位置表Codec及dirty也已实现；服务端写入入口仍缺失，加载后也没有重试召唤及解除强制区块。 |
| S02 | 仍存在 | 小精灵罐缺少初次区块同步数据。Jar仍没有 getUpdateTag；另外四种BE在26迁移中删除该方法，见V06。 |
| S03 | 仍存在 | 小精灵罐/房屋关键状态变化未标记持久化。相关逻辑仍保留。 |
| A01 | 仍存在 | 七种AI的全局模数节流可能永久错过搜索。26.1.2 Mob.serverAiStep 仍隔tick交替调用完整GoalSelector与tickRunningGoals；全局模4门槛仍可能被固定相位错过。 |
| A02 | 仍存在 | DragonAITargetGoal初始目标为空却先要求非空。相关逻辑仍保留。 |
| A03 | 仍存在 | 龙繁殖Goal使用旧生命周期方法名。相关逻辑仍保留。 |
| A04 | 仍存在 | 羊跟随独眼巨人的继续条件写反。相关逻辑仍保留。 |
| A05 | 仍存在 | 羽蛇观察邻近实体Goal的主体字段从未赋值。相关逻辑仍保留。 |
| A06 | 仍存在 | 小精灵逃跑Goal只在接近终点时继续。相关逻辑仍保留。 |
| E01 | 仍存在 | 海蛇缩放替换部件对象，世界部件索引可能保留旧对象。已适配 NeoForge PartEntity 和连续ID，但 updateScale 仍整体替换9个部件，未同步刷新两端世界索引。 |
| E02 | 仍存在 | 死亡蠕虫体积寻路只以最后一个位置决定阻塞。相关逻辑仍保留。 |
| E03 | 仍存在 | 金属羽鸟群成员从不清理已移除实体。相关逻辑仍保留。 |
| W01 | 仍存在 | 龙息转化地形恢复逻辑写在错误的tick回调。相关逻辑仍保留。 |
| W02 | 仍存在 | 小精灵村庄随机范围使一个道路方向和一种房屋不可生成。相关逻辑仍保留。 |
| C01 | 仍存在 | 鸡蛇权杖将耐久累计放在全局Item实例。相关逻辑仍保留。 |
| C02 | 仍存在 | 权杖目标列表清理绕过附件同步，未找到的UUID也不回收。相关逻辑仍保留。 |
| C03 | 仍存在 | 连锁闪电BFS重复入队、重复查询相同范围。相关逻辑仍保留。 |
| C04 | 仍存在 | 幽灵剑能力忽略触发手，始终使用主手物品。相关逻辑仍保留。 |
| R01 | 仍存在 | 图鉴每帧两次打开并解析文本，输入流未关闭。相关逻辑仍保留。 |
| R02 | 已解决 | 石像每次render都重新烘焙备用猪模型。getModel 先查 modelMap，命中直接返回；PigModel/bakeLayer 只在类型首次缓存未命中时建立。 |
| R03 | 仍存在 | 石像假实体按类型缓存，串用第一座石像的NBT。相关逻辑仍保留。 |
| N05 | 仍存在 | 开始跟踪鸡或村民就触发受攻击警报。相关逻辑仍保留。 |
| S04 | 部分解决 | 锻炉进度修改仍未完整标记持久化。保存改为 int，旧short回绕消除；运行中的进度/供能修改仍未完整标记dirty。 |
| L01 | 仍存在 | 独眼巨人破坏树木的减速计算被丢弃。相关逻辑仍保留。 |

## 5. 26.1.2迁移新增发现

| ID | 优先级 | 问题 |
| --- | --- | --- |
| V01 | P2 | 底座的非龙蛋物品分支没有提交绘制 |
| V02 | P2 | 人形护甲的多个槽位共享延迟绘制模型状态 |
| V03 | P2 | 多处自定义几何提交没有保存独立模型姿态 |
| V04 | P2 | LegacyMobRenderer无条件提交隐形生物的实体身体 |
| V05 | P2 | 塞壬昼夜判定把日照角度阈值当作原始时钟tick |
| V06 | P2 | 四种方块实体在移植中丢失首次区块同步数据 |
| V07 | P2 | 模型在构造时已就绪会跳过龙/海蛇附加层初始化 |
| V08 | P3 | 内置旧版材质包仍声明1.21的资源格式34 |

### V01 [P2] 底座的非龙蛋物品分支没有提交绘制

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[PodiumBlockEntityRenderer.java:76](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/block/PodiumBlockEntityRenderer.java:76)；[PodiumMenu.java:13](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/screen/menu/PodiumMenu.java:13)。

触发条件：底座槽位放入非DragonEggItem物品（菜单允许普通物品），或旧存档中已有普通物品。

原因：非蛋分支只对PoseStack做平移旋转缩放，注释称下一pass绘制，但State没有ItemStackRenderState，也没有resolver/update或submit调用；全链没有补交。

影响：槽位中的普通物品在世界中缺失显示，龙蛋分支仍能绘制。

验证方法：底座先放龙蛋再放普通物品，关闭菜单观察；重进区块再对照槽位内容。

后续修复方向：在extract阶段准备物品渲染状态，在submit阶段实际提交。

### V02 [P2] 人形护甲的多个槽位共享延迟绘制模型状态

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[BipedArmorFeatureRenderer.java:45](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/feature/BipedArmorFeatureRenderer.java:45)；[BipedArmorFeatureRenderer.java:51](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/feature/BipedArmorFeatureRenderer.java:51)。

触发条件：同一恐惧人形实体同时装备头、胸、脚等多件护甲；或同屏多只使用同一renderer。

原因：CHEST/FEET/HEAD复用同一个modelArmor；提交前setModelAttributes/setModelSlotVisible修改姿态和HideableModelRenderer.invisible，lambda执行时才读模型。CustomFeatureRenderer.Storage仅复制PoseStack矩阵并存回调，不复制模型。

影响：后提交槽位覆盖前面槽位的可见部件和姿态，例如全套装备的外层三次绘制均可能读到最后HEAD状态，胸甲/靴子缺失或纹理画到错误部件。此问题单个实体就能触发。

验证方法：让恐惧骑士/巫妖穿四件不同材质护甲，分别单件及全套对照。控制流模型已证明回调读取最后状态，但不是GPU画面实测。

后续修复方向：每次提交保存完整模型姿态及invisible等可见性，或把装备渲染改为真正由独立渲染状态驱动；只缓存PoseStack不足。

### V03 [P2] 多处自定义几何提交没有保存独立模型姿态

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[HydraHeadFeatureRenderer.java:46](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/feature/HydraHeadFeatureRenderer.java:46)；[LegacyEntityModelRenderer.java:48](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/LegacyEntityModelRenderer.java:48)；[PixieHouseBlockEntityRenderer.java:66](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/block/PixieHouseBlockEntityRenderer.java:66)；[DeathwormGauntletRenderer.java:28](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/item/DeathwormGauntletRenderer.java:28)；[DragonSkullEntityRenderer.java:80](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/DragonSkullEntityRenderer.java:80)。

触发条件：同帧绘制两只不同断头/攻击进度的九头蛇、两枚不同动画状态的蛋、多个精灵屋、不同开合状态的蠕虫手套或不同朝向的龙头骨。

原因：这些路径在submit中修改共享模型，随后将renderToBuffer封装为延迟回调，却未捕获姿态。九头蛇MODELS按头编号全局共享；DragonEgg的基础桥、精灵屋、手套也共享模型。LegacyMobRenderer主身体已有ModelPose.capture，但该机制没有覆盖这些路径。

影响：较早实体/物品可读取较晚实例的动画或可见性，例如两只九头蛇的不同断头状态串用、两枚蛋同姿态。引用live entity本身未被泛化为线程安全漏洞。

验证方法：同屏放两只九头蛇并让它们断不同编号的头，交换观察/提交顺序；另放静止与孵化中的同类蛋，以及闭合与伸出的手套。

后续修复方向：逐个审计custom geometry回调的可变捕获，捕获每次提交需要的模型状态；共享的不可变网格仍可保留。

### V04 [P2] LegacyMobRenderer无条件提交隐形生物的实体身体

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[LegacyMobRenderer.java:111](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/LegacyMobRenderer.java:111)。

触发条件：龙、小精灵、恐惧生物等使用此渲染桥的生物获得隐形状态。

原因：桥继承基础EntityRenderer，submit无条件使用entityCutout提交整个身体，未执行LivingEntityRenderer的isBodyVisible/getRenderType分支。基础EntityRenderer虽提取isInvisible，但不会替子类取消这次自定义几何提交。

影响：隐形状态下仍可绘制完整身体，失去原MobRenderer对隐形、旁观/队伍半透明和仅轮廓的处理。

验证方法：对小精灵或龙使用/effect give ... minecraft:invisibility，再检查普通玩家视角和发光轮廓组合。

后续修复方向：按26版LivingEntityRenderer语义选择身体RenderType和提交条件，同时单独保留应显示的光效/装备层。

### V05 [P2] 塞壬昼夜判定把日照角度阈值当作原始时钟tick

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[SirenMorningDespawn.java:44](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/world/siren/SirenMorningDespawn.java:44)；[SirenSpawnPlacements.java:53](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/world/siren/SirenSpawnPlacements.java:53)。

触发条件：启用塞壬清晨消失/夜间海滩生成，在默认主世界时钟运行。

原因：isDay使用[6000,18000)，isNight使用其补集；26.1.2 Timelines仍定义day=1000、noon=6000、night=13000、midnight=18000。旧isDay的0.25/0.75是经过偏移/缓动的太阳角度，不能直接乘24000作为clock ticks。

影响：1000（白天）被当作夜间，13000（夜晚）被当作白天；自然生成且带标签的塞壬在前半夜可能被清晨清理，清晨却不清理。生成还有Monster亮度等其他门槛，因此不能据此声称晴天上午必能刷出。

验证方法：暂停时钟，依次设1000、6000、13000、18000；用真正自然生成或带iceandfire_night_siren标签的未命名塞壬观察至少20tick。/summon默认不会触发自然生成标签，不能直接拿它证明未消失。

后续修复方向：依据目标版本环境属性/时间轴定义昼夜语义，至少以官方时钟标记校验默认时段，考虑无默认时钟和自定义时间轴。

### V06 [P2] 四种方块实体在移植中丢失首次区块同步数据

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[EggInIceBlockEntity.java:77](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/EggInIceBlockEntity.java:77)；[PixieHouseBlockEntity.java:84](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/PixieHouseBlockEntity.java:84)；[PodiumBlockEntity.java:148](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/PodiumBlockEntity.java:148)；[LecternBlockEntity.java:218](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/LecternBlockEntity.java:218)。

触发条件：客户端首次跟踪已放置的冰中龙蛋、小精灵房屋、装物品的底座或讲台，尤其重连/离开再返回区块。

原因：与aa2f11a6比较，这四个类迁移ValueInput/ValueOutput时删除了getUpdateTag覆写。目标BlockEntity默认返回空CompoundTag；getUpdatePacket(create(this))也使用getUpdateTag，不会自动把saveAdditional完整数据同步给客户端。

影响：房屋hasPixie等保持客户端默认值，底座物品和讲台书本展示缺失；冰中龙蛋客户端type缺失导致蛋显示不正确或不显示。菜单槽位另有容器同步，不能把打开菜单后的正常显示当首次区块同步正确。Jar的同类旧缺陷单独保留为S02，不重复计数。

验证方法：服务端先填入这些BE的数据，让第二客户端首次进入，且不打开GUI；然后重连、卸载重载区块和触发更新，检查两端字段及渲染。

后续修复方向：恢复包含必要展示字段的getUpdateTag，并使用标准区块跟踪范围的BE更新；存档Codec迁移与客户端同步必须分别验证。

### V07 [P2] 模型在构造时已就绪会跳过龙/海蛇附加层初始化

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[LegacyMobRenderer.java:39](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/LegacyMobRenderer.java:39)；[LegacyMobRenderer.java:52](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/LegacyMobRenderer.java:52)；[DragonBaseEntityRenderer.java:23](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/DragonBaseEntityRenderer.java:23)；[SeaSerpentEntityRenderer.java:22](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/SeaSerpentEntityRenderer.java:22)。

触发条件：注册所用Supplier构造renderer时已能返回Tabula模型，例如模型已缓存后重建renderer/资源重载的路径。

原因：LegacyMobRenderer构造函数把非null模型直接标为modelInitialized=true；ensureModel仅在false时调用onModelAvailable。注册使用的DragonBaseEntityRenderer Supplier重载本身不addDragonLayers，SeaSerpent也仅在onModelAvailable添加古代层。

影响：满足该条件时龙的护甲、旗帜、骑手、雄性/眼睛层根本没有加入；海蛇古代层同样可能缺失。若首次构造返回null再稍后成功则不会触发此缺陷，因此不是所有启动场景必现。

验证方法：记录构造时supplier是否返回非null、onModelAvailable调用次数和layers数量；已有模型时重建renderer，检查龙护甲、旗帜和骑手，随后F3+T再检查。

后续修复方向：将模型就绪和附加层已初始化分开，在安全的构造完成后统一执行一次初始化；处理资源重载后的模型引用更新。

### V08 [P3] 内置旧版材质包仍声明1.21的资源格式34

状态：**新增发现**。证据：当前源码与26.1.2.97目标API确认；客户端画面未实测。

定位：[IceAndFireClient.java:56](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/IceAndFireClient.java:56)；[pack.mcmeta:3](E:/Java/IceAndFire-CE/src/main/resources/resourcepacks/iaf_legacy/pack.mcmeta:3)。

触发条件：玩家在26.1.2资源包界面选择内置iaf_legacy材质包。

原因：resourcepacks/iaf_legacy/pack.mcmeta仍为pack_format:34，没有声明支持26.1.2的84.0。目标版本PackFormat保留旧格式解析，但将其支持范围解释为旧格式，不能把更新主模组版本等同于更新内置包声明。

影响：该内置包会被判断为版本不匹配；可能需要玩家确认强制启用。未把它报告为必定崩溃/完全不能使用。

验证方法：打开资源包选择界面查看iaf_legacy兼容性提示；启用后实际检查其覆盖纹理。

后续修复方向：验证覆盖资源适用26.1.2后，以新版min_format/max_format等字段声明经过验证的支持范围。

## 6. 旧问题在当前版本的详细依据

### N01 [P1] 乘骑控制包直接采纳客户端坐标

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[ServerNetworkHandlers.java:47](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/network/ServerNetworkHandlers.java:47)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：玩家与海马、死亡蠕虫或羽蛇有乘骑关系，并发送 dragon_control。

原因：服务端对 payload.pos 直接 setPos，没有位移、速度、碰撞或世界边界校验。海马和羽蛇的所有权检查只包住 setControlState，没有包住 setPos；乘骑关系检查确实存在，因此不是任意移动全世界实体。

影响：客户端可提交远距离位置移动所骑实体。正常包使用 BlockPos，也会把带小数的坐标取整，控制状态变化时存在位置跳动风险。

验证方法：在隔离测试服骑上述实体，记录按控制键前后两端坐标；针对越界输入做本地处理器测试。审查未向任何服务器发送构造包。

后续修复方向：由服务端权威移动逻辑决定位置，控制包只传意图；统一验证控制者及所有权。

### N02 [P2] 方块实体同步全服广播，缺少维度归属

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[ClientNetworkHandlers.java:69](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/network/ClientNetworkHandlers.java:69)；[PodiumBlockEntity.java:83](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/PodiumBlockEntity.java:83)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：不同维度的相同坐标存在讲台底座、小精灵房屋或罐子，任意一处更新。

原因：UpdatePodium / UpdatePixieHouse / UpdatePixieJar 只有 BlockPos，发送端 sendToAllPlayers，接收端在玩家当前 Level 按同坐标修改对象。API实现是全服务器 broadcastAll。

影响：其他维度同坐标同类方块实体可能显示错误物品、小精灵颜色或产出状态；不跟踪该区块的玩家也收到流量。这里确认的是客户端错态，未证明服务端库存被改写。

验证方法：两名玩家分别在主世界和下界同坐标放对应方块，改变一端，观察另一端；同时统计接收包数量。

后续修复方向：按维度和区块跟踪范围分发，或在包中附维度并校验；优先使用标准方块实体更新机制。

### N03 [P1] 讲台菜单未验证按钮编号即访问数组

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[LecternMenu.java:125](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/screen/menu/LecternMenu.java:125)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：讲台中至少有三张手稿，客户端提交当前菜单ID和超出0–2的按钮编号。

原因：possiblePagesInt[id] 在 Mth.clamp 之前执行。ServerGamePacketListenerImpl 只检查菜单ID、旁观者及 stillValid，未对本菜单按钮范围作检查。

影响：可稳定到达数组越界异常；具体表现是断连、任务异常日志或其他影响，取决于包异常处理层。本报告不把它夸大为已验证的整服崩溃。

验证方法：用独立菜单测试传入 -1、3 等编号，并满足手稿前置条件；无需在生产服发送异常包。

后续修复方向：处理入口先验证编号范围，再读取数组和扣材料。

### N04 [P2] 讲台菜单有效性恒为true

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[LecternBlockEntity.java:178](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/LecternBlockEntity.java:178)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：玩家打开讲台后离开有效距离，或讲台被替换、移除。

原因：菜单委托的 LecternBlockEntity.stillValid 无条件返回 true，缺少距离与当前位置方块实体身份检查。

影响：服务端无法使过期菜单自然失效，玩家仍持有旧容器引用并可尝试操作。拆除方块本身会掉落库存，不能据此断言物品复制。

验证方法：开界面后由第二名玩家移除讲台，或保持界面移动/传送离开，再尝试翻页与取放。

后续修复方向：校验玩家距离、Level及当前坐标的方块实体身份。

### F01 [P2] 锻炉结构失效后仍切换到激活核心

状态：**部分解决**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonForgeCoreBlock.java:48](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/DragonForgeCoreBlock.java:48)；[DragonForgeCoreBlock.java:78](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/DragonForgeCoreBlock.java:78)；[DragonForgeBlockEntity.java:87](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/DragonForgeBlockEntity.java:87)。

版本复核：新版通过 shouldChangedStateKeepBlockEntity 保留核心BE，旧的切换掉库存路径已消除；但 setState 仍只取激活方块，拆坏结构后不能切回 disabled。

触发条件：已组装锻炉被拆坏，prevAssembled由true变false。

原因：tick检测assembled变化后调用不带布尔状态的setState；setState始终使用ACTIVATED_MAP。26版新增shouldChangedStateKeepBlockEntity，目标LevelChunk在保留BE时跳过preRemoveSideEffects，旧报告的掉库存原因不再成立。

影响：激活的核心在拆坏多方块后仍保持激活外观/发光。不能沿用旧报告的库存丢失结论。

验证方法：向锻炉放入物品后完成结构，再拆一角；分别核对库存保留、方块ID和亮度。

后续修复方向：显式传递assembled状态，保留BE并选择正确的启用/禁用方块，或改用同一方块的状态属性。

### F02 [P2] 锻炉输入口永久保留旧核心引用

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonForgeInputBlockEntity.java:35](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/DragonForgeInputBlockEntity.java:35)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：输入口已连接核心，保留输入口而拆除、替换原核心。

原因：core 只在 null 时查询，之后不检查 isRemoved 或 level.getBlockEntity(corePos) 是否仍是它。供能与可工作检查继续访问旧对象。

影响：输入口可能向脱离世界的旧核心供能，或因旧库存不可冶炼而停止诱龙，新核心无法正常工作。该问题不适用于每tick重查邻居的锻炉砖。

验证方法：工作一次后保留输入口，拆掉核心并重放，给新核心装料，比较重放输入口前后的受火结果。

后续修复方向：缓存必须随邻居变化、对象移除及BE身份变化失效。

### F03 [P2] 无龙焰时锻炉进度先减再加，无法正常衰减

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonForgeBlockEntity.java:92](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/DragonForgeBlockEntity.java:92)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：结构有效、材料可冶炼、lastDragonFlameTimer已归零，cookTime至少为2。

原因：先 cookTime--，随后 isBurning 仍以 cookTime>0 判断，又 cookTime++。另一条 !isBurning() && cookTime>0 分支按当前定义永不可满足。

影响：进度维持旧值，而非按代码意图回退。模型中2连续五步保持2、1则降到0；没有证据表明无火能自动无限出货，40tick余热也需与此区分。

验证方法：给少量火后停止供火，等timer耗尽，持续观察进度；对照模型结果。

后续修复方向：分离“有热源”和“有进度”状态，明确余热、停止、衰减规则。

### F04 [P2] 锻炉输出合并只比物品类型，忽略数据组件

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonForgeBlockEntity.java:249](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/DragonForgeBlockEntity.java:249)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：输出槽有相同物品但不同 components 的栈，例如自定义名字、属性或自定义数据的配方结果。

原因：canSmelt 仅 ItemStack.isSameItem，smeltItem 直接 grow 旧输出，不比组件。

影响：新产物被并入旧组件栈，输出属性错误；普通无组件龙钢配方通常看不出此问题。

验证方法：用数据包定义同物品不同组件的结果，预放另一组件栈后完成一次冶炼。

后续修复方向：使用包含组件的堆叠兼容性检查，并同时遵守输入/输出栈上限。

### F05 [P3] 配方assemble返回共享模板对象

状态：**已解决**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonForgeRecipe.java:61](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/recipe/DragonForgeRecipe.java:61)。

版本复核：result 改为不可变模板 ItemStackTemplate；assemble/getResultItem 均调用 create() 返回新栈。

触发条件：外部配方消费者取得 assemble 结果后修改数量或组件。

原因：26版改用ItemStackTemplate，创建结果栈时调用result.create()。

影响：旧共享输出栈问题在当前实现中已解决。

验证方法：调用两次assemble，修改第一次的数量/组件，确认第二次及后续结果独立。

后续修复方向：保留模板创建新栈的语义。

### S01 [P2] 召唤水晶的卸载实体恢复链仍不完整

状态：**部分解决**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[SummoningCrystalItem.java:115](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/SummoningCrystalItem.java:115)；[DragonBaseEntity.java:2982](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/DragonBaseEntity.java:2982)；[DragonPosWorldData.java:45](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/world/DragonPosWorldData.java:45)。

版本复核：原错误的 isClientSide 条件已改正，位置表Codec及dirty也已实现；服务端写入入口仍缺失，加载后也没有重试召唤及解除强制区块。

触发条件：打开chunkLoadSummonCrystal后，绑定龙所在区块卸载；强制加载分支还要求存在有效位置记录。

原因：仅在DragonBaseEntity.onClientRemoval调用addDragon，而DragonPosWorldData.get对客户端返回null；正常服务端位置表缺少维护入口。条件改正后，若位置表中已有记录，代码能setChunkForced(true)，但没有加载完成后的再次查找/召唤，也没有对应setChunkForced(false)。

影响：常规情况下仍无法靠水晶找回已卸载龙；如果记录由其他路径预先写入，区块可永久保持强制加载且本次交互不自动完成召唤。位置表Codec和dirty已改善，不能再说它们完全没有实现。

验证方法：分别测试已加载龙、刚卸载龙、重启后龙；诊断位置表及/forceload query，验证触发后会重试且区块票据最终被释放。

后续修复方向：服务端维护带维度的位置记录，异步加载后重查实体，完成或失败时释放仅由本操作创建的区块加载请求。

### S02 [P2] 小精灵罐缺少初次区块同步数据

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[JarBlockEntity.java:98](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/JarBlockEntity.java:98)。

版本复核：Jar仍没有 getUpdateTag；另外四种BE在26迁移中删除该方法，见V06。

触发条件：新玩家进入已有小精灵罐的区块，或客户端重载该区块。

原因：Jar只提供getUpdatePacket，没有override getUpdateTag；默认tag为空，持久化中的PixieType/HasProduced等未进入初次同步。

影响：客户端可能保持pixieType=0、默认产出状态等，非0颜色罐的粒子/模型状态错误，直到其他更新偶然纠正。服务端NBT本身不因此丢失。

验证方法：放置非0颜色罐并产粉，另一客户端首次进入或重进区块，比较显示和交互状态。

后续修复方向：定义完整且适量的初次同步tag，状态变化时发送标准更新。

### S03 [P2] 小精灵罐/房屋关键状态变化未标记持久化

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[JarBlockEntity.java:66](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/JarBlockEntity.java:66)；[PixieHouseBlockEntity.java:100](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/PixieHouseBlockEntity.java:100)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：区块已保存后产粉、收粉、小精灵进入或释放，且没有其他变化使该区块重新dirty。

原因：hasProduced、hasPixie及相关库存/主人字段直接变更，关键路径未调用setChanged。网络广播不等于标记磁盘保存。

影响：区块卸载/重启后可能回滚到旧状态；特定保存时序下可重复收粉或小精灵状态不一致。尚未做存档复现，不声称每次都丢档或必定复制。

验证方法：先保存ready状态，再收粉；确保无其他区块写入，卸载并重载，检查HasProduced及实体数量。房屋进出做相同对照。

后续修复方向：关键状态转移统一封装并setChanged；动画计时与持久化计时区分，避免每帧/每tick盲目存盘。

### A01 [P1] 七种AI的全局模数节流可能永久错过搜索

状态：**仍存在**。证据：当前源码＋26.1.2 Mob/GoalSelector调度确认＋控制流模型；未做实体GameTest。

定位：[CockatriceAITargetItemsGoal.java:46](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/CockatriceAITargetItemsGoal.java:46)；[DragonAITargetItemsGoal.java:66](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAITargetItemsGoal.java:66)。

版本复核：26.1.2 Mob.serverAiStep 仍隔tick交替调用完整GoalSelector与tickRunningGoals；全局模4门槛仍可能被固定相位错过。

触发条件：完整GoalSelector调度长期落在奇数gameTime，搜索列表初始为空或需要刷新。

原因：这些Goal只在gameTime%4==0时更新列表。Mob按(tickCount+entityId)%2隔tick执行完整selector，另一相位仅tick运行Goal；canUse不保证经过模4窗口。

影响：部分实体的食物搜索、躲避、拾物或找马可能长期不启动/列表不刷新，而不只是延迟4tick。首次tick例外、随机门槛、竞争Goal和加载相位会影响实际表现，不是简单“奇数ID必坏”。

验证方法：模型400tick：两相位均有200次完整调度，其中一相位100次允许搜索，另一相位0次。游戏中生成多只同类，满足同样前置条件，观察不同实例并记录canUse时间。

后续修复方向：用实例级上次扫描时间/到期时间，首次必扫；不要以绝对gameTime取模充当调用频率保证。

### A02 [P2] DragonAITargetGoal初始目标为空却先要求非空

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonAITargetGoal.java:32](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAITargetGoal.java:32)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：该Goal刚创建、target字段尚未选定时。

原因：只有this.target!=null才调用super.canUse，而负责搜索并写入target的正是父类canUse。初始null无法通过首次搜索。

影响：这个主动寻猎Goal不启动，驯服龙等依赖它的行为受影响。野龙另有DragonAITargetNonTamedGoal和反击Goal，因此不能推成“所有龙完全不攻击”。

验证方法：给龙满足移动/饥饿/猎物条件，单独观察该Goal首次canUse及父类findTarget是否被调用。

后续修复方向：先让父类选目标，再验证所选目标的体型、主人、食物等条件。

### A03 [P2] 龙繁殖Goal使用旧生命周期方法名

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonAIMateGoal.java:48](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/DragonAIMateGoal.java:48)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：龙繁殖Goal运行期间。

原因：定义continueExecuting而非canContinueToUse，没有override。父类canContinueToUse默认再调用canUse，重新getNearbyMate并覆盖targetMate。

影响：原定配偶存活、发情和spawnBabyDelay<60继续条件没有被selector使用；运行中重复大范围查询、可能换配偶，且超时结束规则失效。

验证方法：让两条龙发情但先隔开足够距离，记录运行中getNearbyMate频率及60步后的Goal状态；再让最近配偶变化。

后续修复方向：正确覆写生命周期方法并保证运行期目标稳定、目标失效及时停止。

### A04 [P2] 羊跟随独眼巨人的继续条件写反

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[EntitySheepAIFollowCyclopsGoal.java:50](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntitySheepAIFollowCyclopsGoal.java:50)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：羊找到活着的独眼巨人并启动跟随。

原因：canContinueToUse在cyclops.isAlive时直接false。

影响：活巨人导致跟随Goal立即停止并可能再启动，增加重复实体搜索与寻路；已死亡目标反而进入距离判断。

验证方法：活巨人附近放羊，记录Goal的start/stop和路径查询次数；与死亡状态对照。

后续修复方向：纠正存活判断，并审视MOVE flags及目标移除条件。

### A05 [P3] 羽蛇观察邻近实体Goal的主体字段从未赋值

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[EntityAIWatchClosestIgnoreRiderGoal.java:25](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/EntityAIWatchClosestIgnoreRiderGoal.java:25)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：羽蛇尝试使用EntityAIWatchClosestIgnoreRiderGoal。

原因：构造参数entity只传给super，没有存入this.entity；乘客遍历最终全部与null比较，正常实体乘客列表不会匹配。

影响：这个观察Goal的canUse永远不成功；不影响全部羽蛇AI。条件的正负方向也与IgnoreRider名称不一致，需按设计一起核对。

验证方法：在羽蛇附近放不同实体，检查该Goal.canUse最终条件。

后续修复方向：初始化主体引用并按真实设计排除乘骑关系。

### A06 [P2] 小精灵逃跑Goal只在接近终点时继续

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[PixieAIFleeGoal.java:70](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/ai/PixieAIFleeGoal.java:70)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：持物、未驯服小精灵选到较远躲避位置。

原因：继续条件是distanceToSqr(hidePlace+0.5)<2，远离终点反而false。stop清slowSpeed，却没有清MoveControl目标。

影响：Goal提前退出、重复选点与状态切换；移动控制可能仍继续移动，故不直接断言完全不能逃跑。另受A01搜索相位问题影响。

验证方法：小精灵偷取物品后靠近它，记录离目标距离及Goal运行/停止状态。

后续修复方向：明确以未到达、安全距离和目标有效性作为继续条件，stop清理相关移动状态。

### E01 [P2] 海蛇缩放替换部件对象，世界部件索引可能保留旧对象

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[SeaSerpentEntity.java:198](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/SeaSerpentEntity.java:198)；[SeaSerpentEntity.java:230](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/SeaSerpentEntity.java:230)。

版本复核：已适配 NeoForge PartEntity 和连续ID，但 updateScale 仍整体替换9个部件，未同步刷新两端世界索引。

触发条件：海蛇已登记到Level后refreshDimensions检测scale与lastScale不同；构造时lastScale仍为0，初次tick也值得验证。

原因：updateScale一开始重建9元素数组，随后创造全新部件；Level仅在onTrackingStart把原getParts对象登记到map，更新位置只遍历新数组，未重登记。

影响：世界查询/攻击射线与当前移动部件可能指向不同对象，导致部件碰撞盒、命中或兼容模组识别错误。ID相同不等于对象相同。

验证方法：记录登记时getParts与世界part map对象身份，首tick、缩放和重新跟踪后比较；观察F3+B与尾部攻击。

后续修复方向：保留已注册部件身份并原地更新尺寸，或严格按框架生命周期重登记。龙和九头蛇并非都存在相同重建模式。

### E02 [P2] 死亡蠕虫体积寻路只以最后一个位置决定阻塞

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DeathWormNodeMaker.java:59](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/pathfinding/DeathWormNodeMaker.java:59)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：蠕虫寻路体积跨多个方块，其中有不可通行实心方块，而最后采样位置为沙/空气。

原因：isFree循环没有对不可通行体素立即返回BLOCKED，除特定BREACH条件外最终只检查mutable停留的最后位置。

影响：混合体积可能被判可通行，产生走进障碍、卡住或重复重算路径风险。实际碰撞系统是否阻止穿入仍需游戏验证。

验证方法：用较大蠕虫测试一侧沙、一侧石墙通道，对照所有体素的判定与生成路径。

后续修复方向：按完整实体体积聚合通行性，并区分沙内移动与破沙出口。

### E03 [P2] 金属羽鸟群成员从不清理已移除实体

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[StymphalianBirdFlock.java:54](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/util/StymphalianBirdFlock.java:54)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：鸟群部分成员死亡、卸载，仍有其他成员存活并保留flock。

原因：members只add，无remove清理；leader死后从含死亡/已卸载成员的整个列表随机选。多处群体指令也遍历全部旧成员。

影响：可保留已移除鸟引用、浪费遍历，并选出无效leader。该问题随存活群对象寿命持续，不是已证明跨所有世界永久泄漏。

验证方法：建立鸟群，杀死/卸载多数成员后观察members、leader和存活鸟行为，检查引用能否释放。

后续修复方向：按Level及实体生命周期移除成员，只从有效活成员选leader。

### W01 [P2] 龙息转化地形恢复逻辑写在错误的tick回调

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[ReturningStateBlock.java:45](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/ReturningStateBlock.java:45)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：龙息把地形变为revert=true的焦土、冻土等，期望随后随机恢复。

原因：属性启用了randomTicks，但恢复写在scheduled tick而非randomTick；BlockBehaviour.randomTick为空，不会转调tick。普通ReturningStateBlock没有相应调度来源。

影响：普通转化块不会按预期自然恢复；掉落方块/路径可能在放置或邻居变化触发的计划tick偶尔恢复，但也不是持续随机恢复。

验证方法：对普通返回块设置revert=true，隔离场景提高randomTickSpeed并等待；再用显式scheduled tick对照。

后续修复方向：恢复逻辑使用正确随机回调；下落与路径生存检查仍按各自scheduled tick语义处理。

### W02 [P3] 小精灵村庄随机范围使一个道路方向和一种房屋不可生成

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[PixieVillageStructure.java:86](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/world/structure/PixieVillageStructure.java:86)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：生成小精灵村庄。

原因：方向用nextInt(3)进入四方向from2DDataValue，houseColor用nextInt(5)却有case5深色橡木房。

影响：生成分布有偏，一个方向和case5房屋分支不可达；属于内容正确性问题，不是运行时崩溃。

验证方法：检查随机数上界即可证明；游戏内统计多村庄道路/房屋分布可验证体验。

后续修复方向：根据实际可选集合大小随机选择，避免硬编码数量脱节。

### C01 [P2] 鸡蛇权杖将耐久累计放在全局Item实例

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[CockatriceScepterItem.java:36](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/CockatriceScepterItem.java:36)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：两名玩家或两把权杖交错/同时使用。

原因：specialWeaponDmg是Item单例字段，每目标每秒递增，任意玩家releaseUsing扣自己当前栈后把共享字段归零。

影响：A累计消耗可由B松手时承担，A随后松手可能不扣；同一进程逻辑两端还可能共同访问此实例。

验证方法：两玩家对不同数量目标使用不同时间，交错松手，对比每把耐久变化。

后续修复方向：使用者/ItemStack独立存储使用状态，耐久结算由服务端一次性完成。

### C02 [P2] 权杖目标列表清理绕过附件同步，未找到的UUID也不回收

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[CockatriceScepterItem.java:140](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/CockatriceScepterItem.java:140)；[MiscData.java:70](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/data/component/MiscData.java:70)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：松开权杖、目标不再被注视，或目标已卸载/移除。

原因：直接clear/iterator.remove可变列表不markDirty；checkScepterTarget也未标记，且getEntity返回null时不删除。服务端attackTargets遇未找到实体直接continue。

影响：远端可能保留旧目标/光束状态，未解析UUID可持续存在并参与后续遍历。客户端自行按凋零清理只能缓解部分情形。

验证方法：第三客户端旁观权杖开始、松手和目标卸载，比较两端附件UUID列表和同步包。

后续修复方向：通过封装的添加/删除接口同步状态；明确卸载目标的保留期限和失效规则。

### C03 [P2] 连锁闪电BFS重复入队、重复查询相同范围

状态：**仍存在**。证据：当前源码＋队列控制流模型；未做伤害GameTest。

定位：[LightningMultihitAbility.java:34](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/ability/LightningMultihitAbility.java:34)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：范围内多个可见Mob，数量低于搜索预算且尚未出队的Mob会再次被发现。

原因：只在出队后加入attacked，入队时不去重。每一轮还围绕固定attacker.position查询同一范围，attacked使用LinkedList.contains。

影响：模型4只Mob、上限10时处理顺序A,B,C,D,C,D,D，重复hurt尝试、闪电连线及实体查询。伤害免疫帧可能阻止实际重复扣血，不能把hurt调用次数等同伤害倍数。静态可见集合大于上限时首轮已填满队列，本例不会因此漏掉原本预算内独特目标。

验证方法：用4只Mob和默认上限10，记录出入队、hurt尝试及lightnings；模型已验证重复入队。

后续修复方向：入队时维护已发现集合，固定范围可一次查询；若设计是真正逐目标扩散，则按当前节点位置及明确距离/伤害规则处理。

### C04 [P2] 幽灵剑能力忽略触发手，始终使用主手物品

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[SummonGhostSwordAbility.java:29](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/ability/SummonGhostSwordAbility.java:29)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：副手物品带SUMMON_GHOST_SWORD标签，并触发副手swing。

原因：Mixin按传入hand检查标签，active却丢掉hand，改取MAIN_HAND并用于伤害、耐久和冷却。

影响：副手能力可能读取/损耗无关主手武器，伤害和冷却对象错误。默认组件为空对象与被明确移除组件的null应区分，未另报普通物品必NPE。

验证方法：主手普通武器、副手幽灵剑，触发副手挥动，记录投射物伤害、耐久及冷却所属物品。

后续修复方向：传递触发手和实际栈，服务端核对后生成投射物与结算。

### R01 [P2] 图鉴每帧两次打开并解析文本，输入流未关闭

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[BestiaryScreen.java:677](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/screen/gui/bestiary/BestiaryScreen.java:677)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：打开图鉴章节页面并持续渲染。

原因：drawPerPage先imageFromTxt再writeFromTxt，两处对同语言/章节/页调用resource.open和IOUtils.readLines，无try-with-resources。两段分别解析图片指令与正文。

影响：产生重复资源读取、字符串解析和对象分配，输入流未显式关闭。60FPS下理论是每秒120次资源打开；这是调用量推算，不是磁盘物理读取次数或FPS提升数据。资源缺失/解析异常还可能逐帧打日志。

验证方法：用JFR/分配分析观察图鉴开启前后resource.open、readLines、字符串与ItemStack分配，跟踪close与句柄。

后续修复方向：按语言、章节、页缓存解析后的内容，资源重载时失效；用try-with-resources关闭流。

### R02 [P2] 石像每次render都重新烘焙备用猪模型

状态：**已解决**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[StoneStatueEntityRenderer.java:99](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/StoneStatueEntityRenderer.java:99)。

版本复核：getModel 先查 modelMap，命中直接返回；PigModel/bakeLayer 只在类型首次缓存未命中时建立。

触发条件：任意石像进入渲染，包括modelMap已命中。

原因：26版getModel先查询缓存，只有未命中才new PigModel(context.bakeLayer(PIG))。

影响：旧的每帧无条件烘焙已解决；仍存在其他模型分配热点，见性能观察。

验证方法：固定视角观察同类型多座石像，缓存建立后确认没有重复PigModel烘焙。

后续修复方向：保留按需烘焙；另行处理R03的状态隔离问题。

### R03 [P2] 石像假实体按类型缓存，串用第一座石像的NBT

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[StoneStatueEntityRenderer.java:83](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/render/entity/StoneStatueEntityRenderer.java:83)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：同屏或先后渲染相同实体类型、不同NBT的多座石像。

原因：hollowEntityMap只按实体类型字符串索引，只有缓存首次创建才load trappedTag，后续石像复用第一次的fakeEntity。

影响：例如不同巨魔类型使用第一座的石像纹理、不同九头蛇头数使用第一座的头数。持有旧ClientLevel的风险需核对渲染器重建生命周期，未当成已实测跨世界内存泄漏。

验证方法：制作两种巨魔或不同头数九头蛇石像，交换渲染先后顺序，比较外观。

后续修复方向：模型模板可按类型缓存，但可变实体状态应按石像身份/数据版本隔离或正确更新。

### N05 [P2] 开始跟踪鸡或村民就触发受攻击警报

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[ServerEvents.java:162](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/event/handler/ServerEvents.java:162)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：生存玩家走近、重进区块或其他原因开始跟踪鸡/村民，附近有非自己所有的鸡蛇/羽蛇。

原因：onLivingSetTarget实际订阅PlayerEvent.StartTracking，直接把开始跟踪的玩家作为attacker交给同一套攻击警报函数；不是只在攻击事件调用。函数会为附近守卫setTarget(player)。

影响：正常加载/接近也可能被误当作攻击村民或鸡，引起仇恨；每个被跟踪目标还附带范围实体查询。函数仍有创造模式/同主人等过滤，因此不是所有玩家必定受攻击。

验证方法：在鸡旁放非己驯服鸡蛇，或村民旁放羽蛇，生存玩家从跟踪范围外走入，期间不攻击任何实体，记录守卫target。

后续修复方向：把受攻击警报绑定实际攻击/伤害事件，开始跟踪仅承担需要的同步。

### S04 [P3] 锻炉进度修改仍未完整标记持久化

状态：**部分解决**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[DragonForgeBlockEntity.java:184](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/DragonForgeBlockEntity.java:184)；[DragonForgeBlockEntity.java:329](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/item/block/entity/DragonForgeBlockEntity.java:329)。

版本复核：保存改为 int，旧short回绕消除；运行中的进度/供能修改仍未完整标记dirty。

触发条件：区块刚完成保存后只改变锻炉进度，且没有其他触发保存的方块/实体修改。

原因：CookTime现在直接转int保存，short窄化已消除；tick仅在完成或isBurning转变等有限分支setChanged，常规递增、归零以及transferPower仍没有一致dirty策略。

影响：稳定区块中进度变化可能不触发重新保存，重载可回退到旧进度；不再存在32768处short回绕。默认短配方是否出现回退依赖实际保存时序。

验证方法：为长配方设置32768以上进度，确认序列化保持正数；另在区块保存后改变进度，检查BE/区块dirty并卸载重载。

后续修复方向：定义合理持久化粒度，在进度和供能状态改变时标记需要保存。

### L01 [P3] 独眼巨人破坏树木的减速计算被丢弃

状态：**仍存在**。证据：当前源码及相关目标API核对；未做该问题的游戏内定向复现。

定位：[CyclopsEntity.java:359](E:/Java/IceAndFire-CE/src/main/java/com/iafenvoy/iceandfire/entity/CyclopsEntity.java:359)。

版本复核：在26.1.2当前实现中仍保留相关控制流；已按当前源码重新定位。

触发条件：开启griefing，独眼巨人撞到可破坏的树叶/原木。

原因：getDeltaMovement().scale(0.6)返回新Vec3，返回值没有传回setDeltaMovement。

影响：该行无实际减速效果；仅确认局部逻辑，不扩展为全部移动控制失效。

验证方法：比较命中树木前后该调用处速度向量；确认scale前后原对象不变。

后续修复方向：将计算结果赋给运动状态，并确认是否应该每方块重复减速。

## 7. 十个优化Mixin在26.1.2的适用性

原JAR `IceAndFireUltimateOptimization-1.21.1-1.0.0.jar` 的Minecraft依赖明确为 **`[1.21.1]`**。SHA256：`AB35B359BAE5D8FE98841F560A41643CC578EF6611CE4F5130DB14474E4B67A2`。它不是26.1.2产物，当前构建/运行没有加载该JAR。以下是对机制和当前目标源码的复核，不代表该二进制能在26.1.2注入成功；不能只修改版本声明来证明兼容。

默认总开关开启；诱龙间隔20、assembled缓存开启、选点间隔4、AI搜索间隔8、部件推挤间隔4；吐息限流默认关闭，开启后默认上限6。原说明文档中的行为保证属于待核对的声明，未作为用户指令或真实性依据。

| Mixin | 当前目标与真实收益机制 | 失效/行为代价与26版结论 |
| --- | --- | --- |
| DragonForgeInputBlockEntityMixin | lureDragons仍有100边长范围查询及逐候选射线；默认每20次调用执行1次 | 可减少查询，但选龙/清理延迟，不省外层core.assembled/canSmelt，且不修F02旧核心引用 |
| DragonForgeBlockEntityMixin | 同BE同gameTime复用assembled，当前server tick完整结构路径仍可多次调用 | 可减少重复结构读取；同tick结构变化须失效，不可宣称无条件等价；不修F01/F03/F04 |
| DragonUtilsMixin | 空中/水中选点复用4tick，当前FlightManager仍非所有tick无条件重选 | 单静态槽、多龙覆盖、缺Level/维度key和时间回退校验；不应按每龙每tick省十次射线估算 |
| DragonAIMateGoalMixin | 180格扩展配偶查询可缓存8tick，null结果也缓存 | A03旧生命周期仍在，运行期重复查询仍有减负空间，但缓存不能修复Goal；目标存活/发情/位置改变须重查 |
| HippogryphAIMateGoalMixin | 8格扩展查询缓存8tick | 当前正确覆写canContinueToUse，正常交配进行中不反复找配偶，收益主要在启动/无配偶阶段 |
| DeathWormAIFindSandTargetGoalMixin | 半开区间枚举20³=8000或50³=125000位置并创建候选列表 | 有显著局部减负空间；移动、家位置、地形、目标改变应失效。不是原说明的51³ |
| SirenAIFindWaterTargetGoalMixin | 当前10³=1000位置及部分射线搜索可缓存 | 歌唱意图、追击目标、是否在水、地形等变化须重查；不能修V05昼夜错判 |
| CockatriceEntityMixin | 持续凝视时getFriendsCount可少做范围查询 | 按调用次数缓存而非绝对tick；切换敌人仍可用旧计数，直接影响战斗强度 |
| MultipartPartEntityMixin | SlowPart碰撞查询节流确可减少工作；当前海蛇9段、蠕虫7段，龙部件有空碰撞覆写 | 同时减少真实推挤次数/累计力度；26版PartEntity、转发伤害和世界部件索引需要重新运行验证。原描述的海蛇2段/蠕虫1段不正确 |
| IafDragonDestructionManagerMixin | 开启后限制整个destroyAreaBreath调用，默认不生效 | 不仅取消方块处理，也可取消区域伤害和锻炉供能；不能描述为纯方块优化或无玩法变化 |

建议移植时优先做同一tick、同一次运算内可证明等价的去重，再对每种实体设计带世界/状态/时间的缓存。对会改变碰撞、战斗、供能的节流作为显式玩法选项。任何“减少95%调用”都不是“提升95%TPS/CPU”的测量结论。

## 8. 其他性能与兼容性观察

- R01图鉴重复读取/解析且未关闭流、C03连锁闪电重复查询、E03鸟群无效成员保留，仍是确定的可改进成本来源；先修正确性再测实际耗时。
- Podium与EggInIce每次submit构建DragonEggModel，Jar的Supplier实际上每次new PixieModel。这些热点在1.21.1基线已存在，未冒充26新增回归。LegacyMobRenderer新增按模型部件创建ModelPose/ModelPartPose快照，也会增加每帧分配；但快照有正确性用途，不能简单删除。可用可复用状态/不可变网格方案降低分配，需JFR验证。
- `asset-references.json`列出copper_block、copper_ore两个旧item模型的缺失parent及pixie_jar_5缺失纹理；当前未注册对应内容，也没有当前item定义引用，所以记录为资源清理项，没有据此断言正常物品必紫黑。
- DREAD_SCUTTLER重复注册同renderer工厂，属于低优先级冗余；未单列功能bug。
- Curios15.0.0+26.1.2、JEI29.29.0.77、Uranus/Jupiter/Jade固定Curse文件是当前依赖基线。GameTest运行含Curios/Uranus/Jupiter/Jade，JEI仅compileOnly；JEI菜单和配方同步需另在客户端安装实际JEI运行验证。
- 注册ID上下文使用ThreadLocal并在finally恢复旧值，已检查嵌套/异常清理；未把这一26版Properties适配本身当作bug。Java25工程的Mixin compatibilityLevel JAVA_21也不能单凭数字不同认定错误，本次common mixin启动通过；client mixin仍需客户端应用验证。
- ValueInput/ValueOutput、EntityReference、hurtServer和连续部件ID已按26版API改写；能编译不等于同步、生命周期正确。S01、S02、E01与V06说明各阶段仍需分别验证。

## 9. 建议的集中测试方法

以下是一组覆盖多个问题的验收场景，无需为每个问题单建测试。建议全部在备份/新建测试世界中进行。

1. **构建与独立启动**：Java25执行 `.\gradlew.bat build`；用独立gameDirectory执行 `runGameTestServer`。应区分默认测试与模组测试。后续修复应把1.21.1回归场景按26版测试API迁移后运行，不能直接认为原17组测试适用。
2. **两客户端、多维度、重连**：同坐标不同维度放罐/房/底座，改变一端；另一客户端首次跟踪、不打开菜单、重连/卸载后再比较BE展示字段。覆盖N02、S02、S03、V06。讲台另测试离开距离/破坏方块后菜单有效性，以及本地处理器中buttonId=-1/3的边界（不向公共服发送构造包）。
3. **锻炉与长配方**：完成/拆坏结构、替换核心、停止龙息、组件不同的同类输出物、满栈、区块保存后进度变化再重载；覆盖F01–F04和S04。记录方块ID、BE身份、库存组件、进度和dirty，不能只看GUI进度条。
4. **AI与多部件**：不同实体ID/出生tick各测试拾取、逃跑和骑马；繁殖/狩猎测试初次目标为空；混合沙石大体积寻路；海蛇改变Scale前后核对getParts与世界部件表对象身份，再测试尾部受击/交互。覆盖A01–A06、E01/E02。
5. **新版视觉**：关闭额外渲染优化先建立基线，检查全套恐惧护甲、双九头蛇不同断头状态、两枚不同孵化状态的蛋、两把不同开合手套、同类型不同NBT石像、底座普通物品、隐形实体、龙护甲/旗帜/骑手；执行F3+T再对比。之后再测试Sodium等组合。覆盖V01–V04/V07及R03。
6. **昼夜与存档生命周期**：暂停时间，1000/6000/13000/18000分段观察带自然生成标签的塞壬；水晶分别召唤已加载、卸载、重启后的龙，查看强制区块是否被解除。覆盖V05和S01。
7. **性能对照**：固定硬件、种子、实体/锻炉数量、相机和预热时间，各方案至少数次重复；服务端比较MSPT p50/p95/p99、JFR CPU/分配、实体查询/射线次数，客户端比较帧时间和GC。区分正确性修复、同tick去重和有玩法变化的节流；不以平均TPS恒20证明没有性能成本。

## 10. 附件与可复核证据

- [逐文件覆盖清单](audit-26.1.2/coverage.csv)：编译纳入状态、行数、文件哈希与扫描命中数量。
- [结构化全部发现](audit-26.1.2/findings.json)：33项复核＋8项新增，包含当前行号、触发、影响与验证方法。
- [验证清单](audit-26.1.2/validation-manifest.json)、[目标API证据](audit-26.1.2/api-evidence.md)、[控制流模型结果](audit-26.1.2/model-check-results.json)、[资源引用检查](audit-26.1.2/asset-references.json)。
- 完整日志、静态分析XML、复核脚本和风险扫描位于：`C:/Users/tanks/.codex/visualizations/2026/11/04/01a1a6c7-fe80-7ab2-b99a-4c729142a3a9/optimization-audit/project-audit-26.1.2-2026-11-05`。构建原始日志为其父目录 `build-26.1.2-audit.log`；本报告没有把未执行场景标记为测试通过。

本次报告是对当前master的审查基线。生产Java、构建配置和资源未修改；新增的报告与附件留在工作区，未创建第二个提交。
