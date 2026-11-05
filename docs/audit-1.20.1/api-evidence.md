# 1.20.1 API 证据摘录

来源为本次 Gradle 实际 compileClasspath 解析到的依赖；javap 使用 JDK 17。
外部原始证据目录：C:/Users/tanks/.codex/visualizations/2026/11/04/01a1a6c7-fe80-7ab2-b99a-4c729142a3a9/optimization-audit/project-audit-1.20.1-2026-11-05/api-bytecode

## net.minecraft.entity.ai.goal.Goal.txt

SHA256: c9800d511b8673ad92969de02d67e1334d5216ae97d4fd970186ba9e64828785

```text
  public boolean shouldContinue();
    Code:
       0: aload_0
       1: invokevirtual #30                 // Method canStart:()Z
       4: ireturn

```

## net.minecraft.block.AbstractBlock.txt

SHA256: cf3fb7d86891441416c141c9c6c266280573df09ec6c384121c5f3dba78c6b96

```text
  public void randomTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, net.minecraft.util.math.random.Random);
    Code:
       0: aload_0
       1: aload_1
       2: aload_2
       3: aload_3
       4: aload         4
       6: invokevirtual #433                // Method scheduledTick:(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V
       9: return

```

```text
  public void scheduledTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, net.minecraft.util.math.random.Random);
    Code:
       0: return

```

## net.minecraft.entity.mob.MobEntity.txt

SHA256: fbe18e4a94bdfcde541c11079ff29d37604e8057f61f46ca2a653bad2df4bc39

```text
  protected final void tickNewAi();
    Code:
       0: aload_0
       1: dup
       2: getfield      #1070               // Field despawnCounter:I
       5: iconst_1
       6: iadd
       7: putfield      #1070               // Field despawnCounter:I
      10: aload_0
      11: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
      14: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
      17: ldc_w         #1103               // String sensing
      20: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
      25: aload_0
      26: getfield      #210                // Field visibilityCache:Lnet/minecraft/entity/mob/MobVisibilityCache;
      29: invokevirtual #1106               // Method net/minecraft/entity/mob/MobVisibilityCache.clear:()V
      32: aload_0
      33: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
      36: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
      39: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
      44: aload_0
      45: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
      48: invokevirtual #1110               // Method net/minecraft/world/World.getServer:()Lnet/minecraft/server/MinecraftServer;
      51: invokevirtual #1115               // Method net/minecraft/server/MinecraftServer.getTicks:()I
      54: aload_0
      55: invokevirtual #1118               // Method getId:()I
      58: iadd
      59: istore_1
      60: iload_1
      61: iconst_2
      62: irem
      63: ifeq          74
      66: aload_0
      67: getfield      #513                // Field age:I
      70: iconst_1
      71: if_icmpgt     145
      74: aload_0
      75: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
      78: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
      81: ldc_w         #1119               // String targetSelector
      84: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
      89: aload_0
      90: getfield      #176                // Field targetSelector:Lnet/minecraft/entity/ai/goal/GoalSelector;
      93: invokevirtual #1120               // Method net/minecraft/entity/ai/goal/GoalSelector.tick:()V
      96: aload_0
      97: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     100: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     103: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     108: aload_0
     109: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     112: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     115: ldc_w         #1121               // String goalSelector
     118: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     123: aload_0
     124: getfield      #174                // Field goalSelector:Lnet/minecraft/entity/ai/goal/GoalSelector;
     127: invokevirtual #1120               // Method net/minecraft/entity/ai/goal/GoalSelector.tick:()V
     130: aload_0
     131: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     134: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     137: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     142: goto          215
     145: aload_0
     146: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     149: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     152: ldc_w         #1119               // String targetSelector
     155: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     160: aload_0
     161: getfield      #176                // Field targetSelector:Lnet/minecraft/entity/ai/goal/GoalSelector;
     164: iconst_0
     165: invokevirtual #1124               // Method net/minecraft/entity/ai/goal/GoalSelector.tickGoals:(Z)V
     168: aload_0
     169: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     172: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     175: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     180: aload_0
     181: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     184: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     187: ldc_w         #1121               // String goalSelector
     190: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     195: aload_0
     196: getfield      #174                // Field goalSelector:Lnet/minecraft/entity/ai/goal/GoalSelector;
     199: iconst_0
     200: invokevirtual #1124               // Method net/minecraft/entity/ai/goal/GoalSelector.tickGoals:(Z)V
     203: aload_0
     204: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     207: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     210: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     215: aload_0
     216: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     219: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     222: ldc_w         #1125               // String navigation
     225: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     230: aload_0
     231: getfield      #205                // Field navigation:Lnet/minecraft/entity/ai/pathing/EntityNavigation;
     234: invokevirtual #1128               // Method net/minecraft/entity/ai/pathing/EntityNavigation.tick:()V
     237: aload_0
     238: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     241: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     244: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     249: aload_0
     250: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     253: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     256: ldc_w         #1130               // String mob tick
     259: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     264: aload_0
     265: invokevirtual #1133               // Method mobTick:()V
     268: aload_0
     269: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     272: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     275: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     280: aload_0
     281: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     284: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     287: ldc_w         #1135               // String controls
     290: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     295: aload_0
     296: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     299: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     302: ldc_w         #1137               // String move
     305: invokeinterface #413,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     310: aload_0
     311: getfield      #188                // Field moveControl:Lnet/minecraft/entity/ai/control/MoveControl;
     314: invokevirtual #1138               // Method net/minecraft/entity/ai/control/MoveControl.tick:()V
     317: aload_0
     318: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     321: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     324: ldc_w         #1140               // String look
     327: invokeinterface #1143,  2         // InterfaceMethod net/minecraft/util/profiler/Profiler.swap:(Ljava/lang/String;)V
     332: aload_0
     333: getfield      #183                // Field lookControl:Lnet/minecraft/entity/ai/control/LookControl;
     336: invokevirtual #1144               // Method net/minecraft/entity/ai/control/LookControl.tick:()V
     339: aload_0
     340: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     343: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     346: ldc_w         #1146               // String jump
     349: invokeinterface #1143,  2         // InterfaceMethod net/minecraft/util/profiler/Profiler.swap:(Ljava/lang/String;)V
     354: aload_0
     355: getfield      #193                // Field jumpControl:Lnet/minecraft/entity/ai/control/JumpControl;
     358: invokevirtual #1147               // Method net/minecraft/entity/ai/control/JumpControl.tick:()V
     361: aload_0
     362: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     365: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     368: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     373: aload_0
     374: invokevirtual #401                // Method getWorld:()Lnet/minecraft/world/World;
     377: invokevirtual #405                // Method net/minecraft/world/World.getProfiler:()Lnet/minecraft/util/profiler/Profiler;
     380: invokeinterface #436,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     385: aload_0
     386: invokevirtual #1150               // Method sendAiDebugData:()V
     389: return

```

## net.minecraft.entity.ai.goal.GoalSelector.txt

SHA256: f2eb18d4a984c1c7b070e824fe1f43f464bc285d5893f60817d7a6d3885727eb

```text
  public void tick();
    Code:
       0: aload_0
       1: getfield      #76                 // Field profiler:Ljava/util/function/Supplier;
       4: invokeinterface #215,  1          // InterfaceMethod java/util/function/Supplier.get:()Ljava/lang/Object;
       9: checkcast     #217                // class net/minecraft/util/profiler/Profiler
      12: astore_1
      13: aload_1
      14: ldc           #219                // String goalCleanup
      16: invokeinterface #223,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
      21: aload_0
      22: getfield      #64                 // Field goals:Ljava/util/Set;
      25: invokeinterface #224,  1          // InterfaceMethod java/util/Set.iterator:()Ljava/util/Iterator;
      30: astore_2
      31: aload_2
      32: invokeinterface #188,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
      37: ifeq          82
      40: aload_2
      41: invokeinterface #192,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
      46: checkcast     #84                 // class net/minecraft/entity/ai/goal/PrioritizedGoal
      49: astore_3
      50: aload_3
      51: invokevirtual #146                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.isRunning:()Z
      54: ifeq          79
      57: aload_3
      58: aload_0
      59: getfield      #72                 // Field disabledControls:Ljava/util/EnumSet;
      62: invokestatic  #226                // Method usesAny:(Lnet/minecraft/entity/ai/goal/PrioritizedGoal;Ljava/util/EnumSet;)Z
      65: ifne          75
      68: aload_3
      69: invokevirtual #229                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.shouldContinue:()Z
      72: ifne          79
      75: aload_3
      76: invokevirtual #155                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.stop:()V
      79: goto          31
      82: aload_0
      83: getfield      #56                 // Field goalsByControl:Ljava/util/Map;
      86: invokeinterface #233,  1          // InterfaceMethod java/util/Map.entrySet:()Ljava/util/Set;
      91: invokeinterface #224,  1          // InterfaceMethod java/util/Set.iterator:()Ljava/util/Iterator;
      96: astore_2
      97: aload_2
      98: invokeinterface #188,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
     103: ifeq          140
     106: aload_2
     107: invokeinterface #192,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     112: checkcast     #16                 // class java/util/Map$Entry
     115: astore_3
     116: aload_3
     117: invokeinterface #236,  1          // InterfaceMethod java/util/Map$Entry.getValue:()Ljava/lang/Object;
     122: checkcast     #84                 // class net/minecraft/entity/ai/goal/PrioritizedGoal
     125: invokevirtual #146                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.isRunning:()Z
     128: ifne          137
     131: aload_2
     132: invokeinterface #238,  1          // InterfaceMethod java/util/Iterator.remove:()V
     137: goto          97
     140: aload_1
     141: invokeinterface #241,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     146: aload_1
     147: ldc           #243                // String goalUpdate
     149: invokeinterface #223,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
     154: aload_0
     155: getfield      #64                 // Field goals:Ljava/util/Set;
     158: invokeinterface #224,  1          // InterfaceMethod java/util/Set.iterator:()Ljava/util/Iterator;
     163: astore_2
     164: aload_2
     165: invokeinterface #188,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
     170: ifeq          300
     173: aload_2
     174: invokeinterface #192,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     179: checkcast     #84                 // class net/minecraft/entity/ai/goal/PrioritizedGoal
     182: astore_3
     183: aload_3
     184: invokevirtual #146                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.isRunning:()Z
     187: ifne          164
     190: aload_3
     191: aload_0
     192: getfield      #72                 // Field disabledControls:Ljava/util/EnumSet;
     195: invokestatic  #226                // Method usesAny:(Lnet/minecraft/entity/ai/goal/PrioritizedGoal;Ljava/util/EnumSet;)Z
     198: ifne          164
     201: aload_3
     202: aload_0
     203: getfield      #56                 // Field goalsByControl:Ljava/util/Map;
     206: invokestatic  #245                // Method canReplaceAll:(Lnet/minecraft/entity/ai/goal/PrioritizedGoal;Ljava/util/Map;)Z
     209: ifeq          164
     212: aload_3
     213: invokevirtual #248                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.canStart:()Z
     216: ifne          222
     219: goto          164
     222: aload_3
     223: invokevirtual #179                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.getControls:()Ljava/util/EnumSet;
     226: invokevirtual #183                // Method java/util/EnumSet.iterator:()Ljava/util/Iterator;
     229: astore        4
     231: aload         4
     233: invokeinterface #188,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
     238: ifeq          293
     241: aload         4
     243: invokeinterface #192,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     248: checkcast     #11                 // class net/minecraft/entity/ai/goal/Goal$Control
     251: astore        5
     253: aload_0
     254: getfield      #56                 // Field goalsByControl:Ljava/util/Map;
     257: aload         5
     259: getstatic     #202                // Field REPLACEABLE_GOAL:Lnet/minecraft/entity/ai/goal/PrioritizedGoal;
     262: invokeinterface #206,  3          // InterfaceMethod java/util/Map.getOrDefault:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
     267: checkcast     #84                 // class net/minecraft/entity/ai/goal/PrioritizedGoal
     270: astore        6
     272: aload         6
     274: invokevirtual #155                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.stop:()V
     277: aload_0
     278: getfield      #56                 // Field goalsByControl:Ljava/util/Map;
     281: aload         5
     283: aload_3
     284: invokeinterface #251,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
     289: pop
     290: goto          231
     293: aload_3
     294: invokevirtual #254                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.start:()V
     297: goto          164
     300: aload_1
     301: invokeinterface #241,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
     306: aload_0
     307: iconst_1
     308: invokevirtual #258                // Method tickGoals:(Z)V
     311: return

```

```text
  public void tickGoals(boolean);
    Code:
       0: aload_0
       1: getfield      #76                 // Field profiler:Ljava/util/function/Supplier;
       4: invokeinterface #215,  1          // InterfaceMethod java/util/function/Supplier.get:()Ljava/lang/Object;
       9: checkcast     #217                // class net/minecraft/util/profiler/Profiler
      12: astore_2
      13: aload_2
      14: ldc_w         #269                // String goalTick
      17: invokeinterface #223,  2          // InterfaceMethod net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
      22: aload_0
      23: getfield      #64                 // Field goals:Ljava/util/Set;
      26: invokeinterface #224,  1          // InterfaceMethod java/util/Set.iterator:()Ljava/util/Iterator;
      31: astore_3
      32: aload_3
      33: invokeinterface #188,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
      38: ifeq          80
      41: aload_3
      42: invokeinterface #192,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
      47: checkcast     #84                 // class net/minecraft/entity/ai/goal/PrioritizedGoal
      50: astore        4
      52: aload         4
      54: invokevirtual #146                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.isRunning:()Z
      57: ifeq          77
      60: iload_1
      61: ifne          72
      64: aload         4
      66: invokevirtual #272                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.shouldRunEveryTick:()Z
      69: ifeq          77
      72: aload         4
      74: invokevirtual #274                // Method net/minecraft/entity/ai/goal/PrioritizedGoal.tick:()V
      77: goto          32
      80: aload_2
      81: invokeinterface #241,  1          // InterfaceMethod net/minecraft/util/profiler/Profiler.pop:()V
      86: return

```

## net.minecraft.block.entity.BlockEntity.txt

SHA256: 83c1753e4cf3157a769c8cef2d94c9cf4a2f1e68fb158473e93add72cf57ec27

```text
  public net.minecraft.nbt.NbtCompound toInitialChunkDataNbt();
    Code:
       0: new           #52                 // class net/minecraft/nbt/NbtCompound
       3: dup
       4: invokespecial #89                 // Method net/minecraft/nbt/NbtCompound."<init>":()V
       7: areturn

```

## net.minecraft.item.ItemStack.txt

SHA256: 0dd3cd67c7d79c8411cbc187b457b4893c22870c30e996aea1dd068fe1e256c5

```text
  public static boolean areItemsEqual(net.minecraft.item.ItemStack, net.minecraft.item.ItemStack);
    Code:
       0: aload_0
       1: aload_1
       2: invokevirtual #134                // Method getItem:()Lnet/minecraft/item/Item;
       5: invokevirtual #674                // Method isOf:(Lnet/minecraft/item/Item;)Z
       8: ireturn

```

```text
  public static boolean canCombine(net.minecraft.item.ItemStack, net.minecraft.item.ItemStack);
    Code:
       0: aload_0
       1: aload_1
       2: invokevirtual #134                // Method getItem:()Lnet/minecraft/item/Item;
       5: invokevirtual #674                // Method isOf:(Lnet/minecraft/item/Item;)Z
       8: ifne          13
      11: iconst_0
      12: ireturn
      13: aload_0
      14: invokevirtual #291                // Method isEmpty:()Z
      17: ifeq          29
      20: aload_1
      21: invokevirtual #291                // Method isEmpty:()Z
      24: ifeq          29
      27: iconst_1
      28: ireturn
      29: aload_0
      30: getfield      #257                // Field nbt:Lnet/minecraft/nbt/NbtCompound;
      33: aload_1
      34: getfield      #257                // Field nbt:Lnet/minecraft/nbt/NbtCompound;
      37: invokestatic  #681                // Method java/util/Objects.equals:(Ljava/lang/Object;Ljava/lang/Object;)Z
      40: ireturn

```

## net.minecraft.util.ItemScatterer.txt

SHA256: 66f1e4e6259c064da24074a7485f4cc25d5d011e8899d5f67be8941e47b0825e

```text
  public static void spawn(net.minecraft.world.World, double, double, double, net.minecraft.item.ItemStack);
    Code:
       0: getstatic     #107                // Field net/minecraft/entity/EntityType.ITEM:Lnet/minecraft/entity/EntityType;
       3: invokevirtual #111                // Method net/minecraft/entity/EntityType.getWidth:()F
       6: f2d
       7: dstore        8
       9: dconst_1
      10: dload         8
      12: dsub
      13: dstore        10
      15: dload         8
      17: ldc2_w        #112                // double 2.0d
      20: ddiv
      21: dstore        12
      23: dload_1
      24: invokestatic  #119                // Method java/lang/Math.floor:(D)D
      27: aload_0
      28: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
      31: invokeinterface #130,  1          // InterfaceMethod net/minecraft/util/math/random/Random.nextDouble:()D
      36: dload         10
      38: dmul
      39: dadd
      40: dload         12
      42: dadd
      43: dstore        14
      45: dload_3
      46: invokestatic  #119                // Method java/lang/Math.floor:(D)D
      49: aload_0
      50: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
      53: invokeinterface #130,  1          // InterfaceMethod net/minecraft/util/math/random/Random.nextDouble:()D
      58: dload         10
      60: dmul
      61: dadd
      62: dstore        16
      64: dload         5
      66: invokestatic  #119                // Method java/lang/Math.floor:(D)D
      69: aload_0
      70: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
      73: invokeinterface #130,  1          // InterfaceMethod net/minecraft/util/math/random/Random.nextDouble:()D
      78: dload         10
      80: dmul
      81: dadd
      82: dload         12
      84: dadd
      85: dstore        18
      87: aload         7
      89: invokevirtual #136                // Method net/minecraft/item/ItemStack.isEmpty:()Z
      92: ifne          190
      95: new           #138                // class net/minecraft/entity/ItemEntity
      98: dup
      99: aload_0
     100: dload         14
     102: dload         16
     104: dload         18
     106: aload         7
     108: aload_0
     109: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
     112: bipush        21
     114: invokeinterface #142,  2          // InterfaceMethod net/minecraft/util/math/random/Random.nextInt:(I)I
     119: bipush        10
     121: iadd
     122: invokevirtual #145                // Method net/minecraft/item/ItemStack.split:(I)Lnet/minecraft/item/ItemStack;
     125: invokespecial #147                // Method net/minecraft/entity/ItemEntity."<init>":(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V
     128: astore        20
     130: ldc           #148                // float 0.05f
     132: fstore        21
     134: aload         20
     136: aload_0
     137: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
     140: dconst_0
     141: ldc2_w        #149                // double 0.11485000171139836d
     144: invokeinterface #154,  5          // InterfaceMethod net/minecraft/util/math/random/Random.nextTriangular:(DD)D
     149: aload_0
     150: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
     153: ldc2_w        #155                // double 0.2d
     156: ldc2_w        #149                // double 0.11485000171139836d
     159: invokeinterface #154,  5          // InterfaceMethod net/minecraft/util/math/random/Random.nextTriangular:(DD)D
     164: aload_0
     165: getfield      #125                // Field net/minecraft/world/World.random:Lnet/minecraft/util/math/random/Random;
     168: dconst_0
     169: ldc2_w        #149                // double 0.11485000171139836d
     172: invokeinterface #154,  5          // InterfaceMethod net/minecraft/util/math/random/Random.nextTriangular:(DD)D
     177: invokevirtual #160                // Method net/minecraft/entity/ItemEntity.setVelocity:(DDD)V
     180: aload_0
     181: aload         20
     183: invokevirtual #164                // Method net/minecraft/world/World.spawnEntity:(Lnet/minecraft/entity/Entity;)Z
     186: pop
     187: goto          87
     190: return

```

## net.minecraft.block.entity.LockableContainerBlockEntity.txt

SHA256: 86a49cf7073af78d21046e3688683deedd59fea1acbf8be349d81aa046268ee6

```text
  protected void writeNbt(net.minecraft.nbt.NbtCompound);
    Code:
       0: aload_0
       1: aload_1
       2: invokespecial #73                 // Method net/minecraft/block/entity/BlockEntity.writeNbt:(Lnet/minecraft/nbt/NbtCompound;)V
       5: aload_0
       6: getfield      #36                 // Field lock:Lnet/minecraft/inventory/ContainerLock;
       9: aload_1
      10: invokevirtual #74                 // Method net/minecraft/inventory/ContainerLock.writeNbt:(Lnet/minecraft/nbt/NbtCompound;)V
      13: aload_0
      14: getfield      #69                 // Field customName:Lnet/minecraft/text/Text;
      17: ifnull        33
      20: aload_1
      21: ldc           #53                 // String CustomName
      23: aload_0
      24: getfield      #69                 // Field customName:Lnet/minecraft/text/Text;
      27: invokestatic  #78                 // Method net/minecraft/text/Text$Serializer.toJson:(Lnet/minecraft/text/Text;)Ljava/lang/String;
      30: invokevirtual #82                 // Method net/minecraft/nbt/NbtCompound.putString:(Ljava/lang/String;Ljava/lang/String;)V
      33: return

```

## com.iafenvoy.uranus.ServerHelper.txt

SHA256: 211a253b4451c71b5506d89d846411068ea33886465e135980ed5ad42c39e504

```text
  public static void sendToAll(net.minecraft.util.Identifier, net.minecraft.network.PacketByteBuf);
    Code:
       0: getstatic     #20                 // Field server:Lnet/minecraft/server/MinecraftServer;
       3: ifnull        59
       6: getstatic     #20                 // Field server:Lnet/minecraft/server/MinecraftServer;
       9: invokevirtual #26                 // Method net/minecraft/server/MinecraftServer.getPlayerManager:()Lnet/minecraft/server/PlayerManager;
      12: invokevirtual #32                 // Method net/minecraft/server/PlayerManager.getPlayerList:()Ljava/util/List;
      15: invokeinterface #38,  1           // InterfaceMethod java/util/List.iterator:()Ljava/util/Iterator;
      20: astore_2
      21: aload_2
      22: invokeinterface #44,  1           // InterfaceMethod java/util/Iterator.hasNext:()Z
      27: ifeq          59
      30: aload_2
      31: invokeinterface #48,  1           // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
      36: checkcast     #50                 // class net/minecraft/server/network/ServerPlayerEntity
      39: astore_3
      40: aload_3
      41: aload_0
      42: new           #52                 // class net/minecraft/network/PacketByteBuf
      45: dup
      46: aload_1
      47: invokevirtual #56                 // Method net/minecraft/network/PacketByteBuf.copy:()Lio/netty/buffer/ByteBuf;
      50: invokespecial #59                 // Method net/minecraft/network/PacketByteBuf."<init>":(Lio/netty/buffer/ByteBuf;)V
      53: invokestatic  #65                 // Method dev/architectury/networking/NetworkManager.sendToPlayer:(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V
      56: goto          21
      59: return

```

## com.iafenvoy.uranus.forge.component.CapabilitySyncHelper.txt

SHA256: a2708fa90d64a06ce01d6992dcc58e3df00f0b31dfd0f0d0a89f1e63ebd9bedb

```text
      13: ifeq          17
      16: return
      17: aload_3
      18: invokevirtual #244                // Method java/util/Optional.get:()Ljava/lang/Object;
      21: checkcast     #246                // class com/iafenvoy/uranus/forge/component/ITickableCapability
      24: astore        4
      26: aload         4
      28: invokeinterface #249,  1          // InterfaceMethod com/iafenvoy/uranus/forge/component/ITickableCapability.tick:()V
      33: aload         4
      35: invokeinterface #252,  1          // InterfaceMethod com/iafenvoy/uranus/forge/component/ITickableCapability.isDirty:()Z
      40: ifeq          101
      43: aload_0
      44: invokevirtual #256                // Method net/minecraft/entity/LivingEntity.getWorld:()Lnet/minecraft/world/World;
      47: astore        6
      49: aload         6
      51: instanceof    #258                // class net/minecraft/server/world/ServerWorld
      54: ifeq          101
      57: aload         6
      59: checkcast     #258                // class net/minecraft/server/world/ServerWorld
      62: astore        5
      64: aload         5
      66: aload_0
      67: invokedynamic #268,  0            // InvokeDynamic #1:accept:(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Ldev/architectury/networking/transformers/PacketSink;
      72: getstatic     #272                // Field dev/architectury/networking/NetworkManager$Side.S2C:Ldev/architectury/networking/NetworkManager$Side;
      75: getstatic     #274                // Field CAPABILITY_SYNC:Lnet/minecraft/util/Identifier;
      78: invokestatic  #280                // Method com/iafenvoy/uranus/network/PacketBufferUtils.create:()Lnet/minecraft/network/PacketByteBuf;
      81: aload_1
      82: invokevirtual #286                // Method net/minecraft/network/PacketByteBuf.writeIdentifier:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/network/PacketByteBuf;
      85: aload         4
      87: invokeinterface #290,  1          // InterfaceMethod com/iafenvoy/uranus/forge/component/ITickableCapability.serializeNBT:()Lnet/minecraft/nbt/NbtElement;
      92: checkcast     #292                // class net/minecraft/nbt/NbtCompound
      95: invokevirtual #296                // Method net/minecraft/network/PacketByteBuf.writeNbt:(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;
      98: invokestatic  #300                // Method dev/architectury/networking/NetworkManager.collectPackets:(Ldev/architectury/networking/transformers/PacketSink;Ldev/architectury/networking/NetworkManager$Side;Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V
     101: return

  public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent$Clone);
    Code:
       0: aload_0
```

## dev.onyxstudios.cca.api.v3.component.ComponentKey.txt

SHA256: 88b65563fcd8c059d3fe5177750be6d38b1dbdbd558a233a787ec53dcbe804ba

```text
  public void sync(java.lang.Object, dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter, dev.onyxstudios.cca.api.v3.component.sync.PlayerSyncPredicate);
    Code:
       0: aload_1
       1: checkcast     #52                 // class dev/onyxstudios/cca/api/v3/component/ComponentProvider
       4: invokeinterface #153,  1          // InterfaceMethod dev/onyxstudios/cca/api/v3/component/ComponentProvider.getRecipientsForComponentSync:()Ljava/lang/Iterable;
       9: invokeinterface #159,  1          // InterfaceMethod java/lang/Iterable.iterator:()Ljava/util/Iterator;
      14: astore        4
      16: aload         4
      18: invokeinterface #165,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
      23: ifeq          53
      26: aload         4
      28: invokeinterface #169,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
      33: checkcast     #171                // class net/minecraft/server/network/ServerPlayerEntity
      36: astore        5
      38: aload_0
      39: aload         5
      41: aload_1
      42: checkcast     #52                 // class dev/onyxstudios/cca/api/v3/component/ComponentProvider
      45: aload_2
      46: aload_3
      47: invokevirtual #175                // Method syncWith:(Lnet/minecraft/server/network/ServerPlayerEntity;Ldev/onyxstudios/cca/api/v3/component/ComponentProvider;Ldev/onyxstudios/cca/api/v3/component/sync/ComponentPacketWriter;Ldev/onyxstudios/cca/api/v3/component/sync/PlayerSyncPredicate;)V
      50: goto          16
      53: return

```

## Architectury 9.2.14 architectury-forge\dev\architectury\networking\forge\NetworkManagerImpl.java

SHA256: 056a5abbc00bb49ec644428de64d5973c105c1065dc3d895dbe9b24de863ecb4

```java
127:                     @Override
128:                     public Player getPlayer() {
129:                         return getEnvironment() == Env.CLIENT ? getClientPlayer() : context.getSender();
130:                     }
131:
132:                     @Override
133:                     public void queue(Runnable runnable) {
134:                         context.enqueueWork(runnable);
135:                     }
136:
137:                     @Override
138:                     public Env getEnvironment() {
139:                         return context.getDirection().getReceptionSide() == LogicalSide.CLIENT ? Env.CLIENT : Env.SERVER;
140:                     }
141:
142:                     private Player getClientPlayer() {
145:                 };
146:                 transformer.inbound(side, type, buffer, packetContext, (side1, id1, buf1) -> {
147:                     NetworkReceiver networkReceiver = side == NetworkManager.Side.C2S ? C2S.get(id1) : S2C.get(id1);
148:                     if (networkReceiver == null) {
149:                         throw new IllegalArgumentException("Network Receiver not found! " + id1);
150:                     }
151:                     networkReceiver.receive(buf1, packetContext);
152:                 });
153:             } else {
154:                 LOGGER.error("Unknown message ID: " + type);
155:             }
156:
157:             context.setPacketHandled(true);
158:         };
159:     }
```

## Architectury 9.2.14 architectury-fabric\dev\architectury\networking\fabric\NetworkManagerImpl.java

SHA256: 2c917afd21d8f003b7929859023fa25fd01bd39fd9bd56bce76e0a11145c7dcb

```java
75:             var context = context(player, server, false);
76:             transformer.inbound(NetworkManager.Side.C2S, id, buf, context, (side, id1, buf1) -> {
77:                 NetworkReceiver networkReceiver = side == NetworkManager.Side.C2S ? C2S_RECEIVER.get(id1) : S2C_RECEIVER.get(id1);
78:                 if (networkReceiver == null) {
79:                     throw new IllegalArgumentException("Network Receiver not found! " + id1);
80:                 }
81:                 networkReceiver.receive(buf1, context);
82:             });
83:         });
84:         C2S_TRANSFORMERS.put(id, transformer);
85:     }
86:
87:     @SuppressWarnings("Convert2Lambda")
88:     @Environment(EnvType.CLIENT)
89:     private static void registerS2CReceiver(class_2960 id, List<PacketTransformer> packetTransformers, NetworkReceiver receiver) {
96:                 var context = context(client.field_1724, client, true);
97:                 transformer.inbound(NetworkManager.Side.S2C, id, buf, context, (side, id1, buf1) -> {
98:                     NetworkReceiver networkReceiver = side == NetworkManager.Side.C2S ? C2S_RECEIVER.get(id1) : S2C_RECEIVER.get(id1);
99:                     if (networkReceiver == null) {
100:                         throw new IllegalArgumentException("Network Receiver not found! " + id1);
101:                     }
102:                     networkReceiver.receive(buf1, context);
103:                 });
104:             }
105:         });
106:         S2C_TRANSFORMERS.put(id, transformer);
107:     }
108:
109:     private static NetworkManager.PacketContext context(class_1657 player, class_1255<?> taskQueue, boolean client) {
110:         return new NetworkManager.PacketContext() {
111:             @Override
112:             public class_1657 getPlayer() {
113:                 return player;
114:             }
115:
116:             @Override
117:             public void queue(Runnable runnable) {
118:                 taskQueue.execute(runnable);
119:             }
120:
121:             @Override
122:             public Env getEnvironment() {
123:                 return client ? Env.CLIENT : Env.SERVER;
124:             }
125:         };
126:     }
```

