# 26.1.2.97 API evidence

Source SHA256: c3a2ec91ddc4511d62a6ef9030009cefc4035fa8d84f17dfeef805b41c542a79

## net/minecraft/world/entity/Mob.java / int idBasedTickCount
```java
715:         this.sensing.tick();
716:         profiler.pop();
717:         int idBasedTickCount = this.tickCount + this.getId();
718:         if (idBasedTickCount % 2 != 0 && this.tickCount > 1) {
719:             profiler.push("targetSelector");
720:             this.targetSelector.tickRunningGoals(false);
721:             profiler.pop();
722:             profiler.push("goalSelector");
723:             this.goalSelector.tickRunningGoals(false);
724:             profiler.pop();
725:         } else {
726:             profiler.push("targetSelector");
727:             this.targetSelector.tick();
728:             profiler.pop();
729:             profiler.push("goalSelector");
730:             this.goalSelector.tick();
731:             profiler.pop();
732:         }
733:
734:         profiler.push("navigation");
735:         this.navigation.tick();
736:         profiler.pop();
737:         profiler.push("mob tick");
738:         this.customServerAiStep((ServerLevel)this.level());
```

## net/minecraft/world/entity/ai/goal/Goal.java / boolean canContinueToUse
```java
12:     public abstract boolean canUse();
13:
14:     public boolean canContinueToUse() {
15:         return this.canUse();
16:     }
17:
18:     public boolean isInterruptable() {
19:         return true;
20:     }
21:
22:     public void start() {
23:     }
24:
25:     public void stop() {
26:     }
27:
28:     public boolean requiresUpdateEveryTick() {
29:         return false;
30:     }
31:
32:     public void tick() {
33:     }
34:
35:     public void setFlags(EnumSet<Goal.Flag> requiredControlFlags) {
```

## net/minecraft/client/renderer/feature/CustomFeatureRenderer.java / void renderSolid
```java
20: @OnlyIn(Dist.CLIENT)
21: public class CustomFeatureRenderer {
22:     public void renderSolid(SubmitNodeCollection nodeCollection, MultiBufferSource.BufferSource bufferSource) {
23:         CustomFeatureRenderer.Storage storage = nodeCollection.getCustomGeometrySubmits();
24:
25:         for (Entry<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> entry : storage.solidCustomGeometrySubmits.entrySet()) {
26:             VertexConsumer buffer = bufferSource.getBuffer(entry.getKey());
27:
28:             for (SubmitNodeStorage.CustomGeometrySubmit customGeometrySubmit : entry.getValue()) {
29:                 customGeometrySubmit.customGeometryRenderer().render(customGeometrySubmit.pose(), buffer);
30:             }
31:         }
32:     }
33:
34:     public void renderTranslucent(SubmitNodeCollection nodeCollection, MultiBufferSource.BufferSource bufferSource) {
35:         CustomFeatureRenderer.Storage storage = nodeCollection.getCustomGeometrySubmits();
36:
37:         for (Entry<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> entry : storage.translucentCustomGeometrySubmits.entrySet()) {
38:             VertexConsumer buffer = bufferSource.getBuffer(entry.getKey());
39:
40:             for (SubmitNodeStorage.CustomGeometrySubmit customGeometrySubmit : entry.getValue()) {
41:                 customGeometrySubmit.customGeometryRenderer().render(customGeometrySubmit.pose(), buffer);
42:             }
43:         }
```

## net/minecraft/client/renderer/feature/CustomFeatureRenderer.java / void add(PoseStack
```java
51:         private final Set<RenderType> translucentCustomGeometrySubmitsUsage = new ObjectOpenHashSet<>();
52:
53:         public void add(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
54:             SubmitNodeStorage.CustomGeometrySubmit submit = new SubmitNodeStorage.CustomGeometrySubmit(poseStack.last().copy(), customGeometryRenderer);
55:             if (!renderType.hasBlending()) {
56:                 this.solidCustomGeometrySubmits.computeIfAbsent(renderType, rt -> new ArrayList<>()).add(submit);
57:             } else {
58:                 this.translucentCustomGeometrySubmits.computeIfAbsent(renderType, rt -> new ArrayList<>()).add(submit);
59:             }
60:         }
61:
62:         public void clear() {
63:             for (Entry<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> entry : this.solidCustomGeometrySubmits.entrySet()) {
64:                 if (!entry.getValue().isEmpty()) {
65:                     this.solidCustomGeometrySubmitsUsage.add(entry.getKey());
66:                     entry.getValue().clear();
67:                 }
68:             }
69:
70:             for (Entry<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> entryx : this.translucentCustomGeometrySubmits.entrySet()) {
71:                 if (!entryx.getValue().isEmpty()) {
72:                     this.translucentCustomGeometrySubmitsUsage.add(entryx.getKey());
73:                     entryx.getValue().clear();
74:                 }
```

## net/minecraft/client/renderer/entity/LivingEntityRenderer.java / boolean isBodyVisible
```java
89:         this.scale(state, poseStack);
90:         poseStack.translate(0.0F, -1.501F, 0.0F);
91:         boolean isBodyVisible = this.isBodyVisible(state);
92:         boolean forceTransparent = !isBodyVisible && !state.isInvisibleToPlayer;
93:         RenderType renderType = this.getRenderType(state, isBodyVisible, forceTransparent, state.appearsGlowing());
94:         if (renderType != null) {
95:             int overlayCoords = getOverlayCoords(state, this.getWhiteOverlayProgress(state));
96:             int baseColor = forceTransparent ? 654311423 : -1;
97:             int tintedColor = ARGB.multiply(baseColor, this.getModelTint(state));
98:             submitNodeCollector.submitModel(
99:                 this.model, state, poseStack, renderType, state.lightCoords, overlayCoords, tintedColor, null, state.outlineColor, null
100:             );
101:         }
102:
103:         if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
104:             this.model.setupAnim(state);
105:
106:             for (RenderLayer<S, M> layer : this.layers) {
107:                 layer.submit(poseStack, submitNodeCollector, state.lightCoords, state, state.yRot, state.xRot);
108:             }
109:         }
110:
111:         poseStack.popPose();
112:         super.submit(state, poseStack, submitNodeCollector, camera);
```
```java
124:     public abstract Identifier getTextureLocation(final S state);
125:
126:     protected @Nullable RenderType getRenderType(S state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
127:         Identifier texture = this.getTextureLocation(state);
128:         if (forceTransparent) {
129:             return RenderTypes.entityTranslucentCullItemTarget(texture);
130:         } else if (isBodyVisible) {
131:             return this.model.renderType(texture);
132:         } else {
133:             return appearGlowing ? RenderTypes.outline(texture) : null;
134:         }
135:     }
136:
137:     public static int getOverlayCoords(LivingEntityRenderState state, float whiteOverlayProgress) {
138:         return OverlayTexture.pack(OverlayTexture.u(whiteOverlayProgress), OverlayTexture.v(state.hasRedOverlay));
139:     }
140:
141:     protected boolean isBodyVisible(S state) {
142:         return !state.isInvisible;
143:     }
144:
145:     private static float sleepDirectionToRotation(Direction direction) {
146:         switch (direction) {
147:             case SOUTH:
```

## net/minecraft/client/renderer/entity/LivingEntityRenderer.java / RenderType getRenderType
```java
124:     public abstract Identifier getTextureLocation(final S state);
125:
126:     protected @Nullable RenderType getRenderType(S state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
127:         Identifier texture = this.getTextureLocation(state);
128:         if (forceTransparent) {
129:             return RenderTypes.entityTranslucentCullItemTarget(texture);
130:         } else if (isBodyVisible) {
131:             return this.model.renderType(texture);
132:         } else {
133:             return appearGlowing ? RenderTypes.outline(texture) : null;
134:         }
135:     }
136:
137:     public static int getOverlayCoords(LivingEntityRenderState state, float whiteOverlayProgress) {
138:         return OverlayTexture.pack(OverlayTexture.u(whiteOverlayProgress), OverlayTexture.v(state.hasRedOverlay));
139:     }
140:
141:     protected boolean isBodyVisible(S state) {
142:         return !state.isInvisible;
143:     }
144:
145:     private static float sleepDirectionToRotation(Direction direction) {
146:         switch (direction) {
147:             case SOUTH:
```

## net/minecraft/world/level/block/entity/BlockEntity.java / CompoundTag getUpdateTag
```java
242:     }
243:
244:     public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
245:         return new CompoundTag();
246:     }
247:
248:     public boolean isRemoved() {
249:         return this.remove;
250:     }
251:
252:     public void setRemoved() {
253:         this.remove = true;
254:         this.invalidateCapabilities();
255:         requestModelDataUpdate();
256:     }
257:
258:     public void clearRemoved() {
259:         this.remove = false;
260:         // Neo: invalidate capabilities on block entity placement
261:         invalidateCapabilities();
262:     }
263:
264:     public void preRemoveSideEffects(BlockPos pos, BlockState state) {
265:         if (this instanceof Container container && this.level != null) {
```

## net/minecraft/world/level/block/entity/BlockEntity.java / void preRemoveSideEffects
```java
262:     }
263:
264:     public void preRemoveSideEffects(BlockPos pos, BlockState state) {
265:         if (this instanceof Container container && this.level != null) {
266:             Containers.dropContents(this.level, pos, container);
267:         }
268:     }
269:
270:     public boolean triggerEvent(int b0, int b1) {
271:         return false;
272:     }
273:
274:     public void fillCrashReportCategory(CrashReportCategory category) {
275:         category.setDetail("Name", this::getNameForReporting);
276:         category.setDetail("Cached block", this.getBlockState()::toString);
277:         if (this.level == null) {
278:             category.setDetail("Block location", () -> this.worldPosition + " (world missing)");
279:         } else {
280:             category.setDetail("Actual block", this.level.getBlockState(this.worldPosition)::toString);
281:             CrashReportCategory.populateBlockLocationDetails(category, this.level, this.worldPosition);
282:         }
283:     }
284:
285:     public String getNameForReporting() {
```

## net/minecraft/world/level/chunk/LevelChunk.java / boolean blockChanged
```java
302:                 }
303:
304:                 boolean blockChanged = !oldState.is(newBlock);
305:                 boolean movedByPiston = (flags & 64) != 0;
306:                 boolean sideEffects = (flags & 256) == 0;
307:                 if (blockChanged && oldState.hasBlockEntity() && !state.shouldChangedStateKeepBlockEntity(oldState)) {
308:                     if (!this.level.isClientSide() && sideEffects) {
309:                         BlockEntity blockEntity = this.level.getBlockEntity(pos);
310:                         if (blockEntity != null) {
311:                             blockEntity.preRemoveSideEffects(pos, oldState);
312:                         }
313:                     }
314:
315:                     this.removeBlockEntity(pos);
316:                 }
317:
318:                 if ((blockChanged || newBlock instanceof BaseRailBlock) && this.level instanceof ServerLevel serverLevel && ((flags & 1) != 0 || movedByPiston)
319:                     )
320:                  {
321:                     oldState.affectNeighborsAfterRemoval(serverLevel, pos, movedByPiston);
322:                 }
323:
324:                 if (!section.getBlockState(localX, localY, localZ).is(newBlock)) {
325:                     return null;
```

## net/minecraft/server/level/ServerLevel.java / ServerLevel.this.dragonParts.put
```java
1965:             if (entity.isMultipartEntity()) {
1966:                 for(net.neoforged.neoforge.entity.PartEntity<?> subEntity : entity.getParts()) {
1967:                     ServerLevel.this.dragonParts.put(subEntity.getId(), subEntity);
1968:                 }
1969:             }
1970:
1971:             entity.updateDynamicGameEventListener(DynamicGameEventListener::add);
1972:         }
1973:
1974:         public void onTrackingEnd(Entity entity) {
1975:             ServerLevel.this.getChunkSource().removeEntity(entity);
1976:             if (entity instanceof ServerPlayer player) {
1977:                 ServerLevel.this.players.remove(player);
1978:                 ServerLevel.this.getWaypointManager().removePlayer(player);
1979:                 ServerLevel.this.updateSleepingPlayerList();
1980:             }
1981:
1982:             if (entity instanceof Mob mob) {
1983:                 if (ServerLevel.this.isUpdatingNavigations) {
1984:                     String message = "onTrackingStart called during navigation iteration";
1985:                     Util.logAndPauseIfInIde(
1986:                               "onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration")
1987:                     );
1988:                 }
```

## net/minecraft/server/level/ServerLevel.java / ServerLevel.this.dragonParts.remove
```java
1993:             if (entity.isMultipartEntity()) {
1994:                 for(net.neoforged.neoforge.entity.PartEntity<?> subEntity : entity.getParts()) {
1995:                     ServerLevel.this.dragonParts.remove(subEntity.getId());
1996:                 }
1997:             }
1998:
1999:             entity.updateDynamicGameEventListener(DynamicGameEventListener::remove);
2000:             ServerLevel.this.debugSynchronizers.dropEntity(entity);
2001:
2002:             entity.onRemovedFromLevel();
2003:             net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent(entity, ServerLevel.this));
2004:         }
2005:
2006:         public void onSectionChange(Entity entity) {
2007:             entity.updateDynamicGameEventListener(DynamicGameEventListener::move);
2008:         }
2009:     }
2010:
2011:     @Override
2012:     public final void syncData(net.neoforged.neoforge.attachment.AttachmentType<?> type) {
2013:         net.neoforged.neoforge.attachment.AttachmentSync.syncLevelUpdate(this, type);
2014:     }
2015:
2016:     private final net.neoforged.neoforge.capabilities.CapabilityListenerHolder capListenerHolder = new net.neoforged.neoforge.capabilities.CapabilityListenerHolder();
```

## net/minecraft/client/multiplayer/ClientLevel.java / ClientLevel.this.dragonParts.add
```java
1145:                     break;
1146:                 case EnderDragon dragon:
1147:                     ClientLevel.this.dragonParts.addAll(Arrays.asList(dragon.getSubEntities()));
1148:                     break;
1149:                 default:
1150:                     if (entity.isMultipartEntity()) {
1151:                         ClientLevel.this.dragonParts.addAll(Arrays.asList(entity.getParts()));
1152:                     }
1153:             }
1154:         }
1155:
1156:         public void onTrackingEnd(Entity entity) {
1157:             entity.unRide();
1158:             switch (entity) {
1159:                 case AbstractClientPlayer player:
1160:                     ClientLevel.this.players.remove(player);
1161:                     break;
1162:                 case EnderDragon dragon:
1163:                     ClientLevel.this.dragonParts.removeAll(Arrays.asList(dragon.getSubEntities()));
1164:                     break;
1165:                 default:
1166:                     if (entity.isMultipartEntity()) {
1167:                         ClientLevel.this.dragonParts.removeAll(Arrays.asList(entity.getParts()));
1168:                     }
```
```java
1149:                 default:
1150:                     if (entity.isMultipartEntity()) {
1151:                         ClientLevel.this.dragonParts.addAll(Arrays.asList(entity.getParts()));
1152:                     }
1153:             }
1154:         }
1155:
1156:         public void onTrackingEnd(Entity entity) {
1157:             entity.unRide();
1158:             switch (entity) {
1159:                 case AbstractClientPlayer player:
1160:                     ClientLevel.this.players.remove(player);
1161:                     break;
1162:                 case EnderDragon dragon:
1163:                     ClientLevel.this.dragonParts.removeAll(Arrays.asList(dragon.getSubEntities()));
1164:                     break;
1165:                 default:
1166:                     if (entity.isMultipartEntity()) {
1167:                         ClientLevel.this.dragonParts.removeAll(Arrays.asList(entity.getParts()));
1168:                     }
1169:             }
1170:
1171:             entity.onRemovedFromLevel();
1172:             net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent(entity, ClientLevel.this));
```

## net/minecraft/client/multiplayer/ClientLevel.java / ClientLevel.this.dragonParts.remove
```java
1161:                     break;
1162:                 case EnderDragon dragon:
1163:                     ClientLevel.this.dragonParts.removeAll(Arrays.asList(dragon.getSubEntities()));
1164:                     break;
1165:                 default:
1166:                     if (entity.isMultipartEntity()) {
1167:                         ClientLevel.this.dragonParts.removeAll(Arrays.asList(entity.getParts()));
1168:                     }
1169:             }
1170:
1171:             entity.onRemovedFromLevel();
1172:             net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent(entity, ClientLevel.this));
1173:         }
1174:
1175:         public void onSectionChange(Entity entity) {
1176:         }
1177:     }
1178:
1179:     @Override
1180:     public net.neoforged.neoforge.model.data.ModelDataManager getModelDataManager() {
1181:         return modelDataManager;
1182:     }
1183:
1184:     @Override
```
```java
1165:                 default:
1166:                     if (entity.isMultipartEntity()) {
1167:                         ClientLevel.this.dragonParts.removeAll(Arrays.asList(entity.getParts()));
1168:                     }
1169:             }
1170:
1171:             entity.onRemovedFromLevel();
1172:             net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent(entity, ClientLevel.this));
1173:         }
1174:
1175:         public void onSectionChange(Entity entity) {
1176:         }
1177:     }
1178:
1179:     @Override
1180:     public net.neoforged.neoforge.model.data.ModelDataManager getModelDataManager() {
1181:         return modelDataManager;
1182:     }
1183:
1184:     @Override
1185:     public net.neoforged.neoforge.model.data.ModelData getModelData(BlockPos pos) {
1186:         return modelDataManager.getAt(pos);
1187:     }
1188: }
```

## net/minecraft/server/network/ServerGamePacketListenerImpl.java / void handleContainerButtonClick
```java
2020:
2021:     @Override
2022:     public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {
2023:         PacketUtils.ensureRunningOnSameThread(packet, this, this.player.level());
2024:         this.player.resetLastActionTime();
2025:         if (this.player.containerMenu.containerId == packet.containerId() && !this.player.isSpectator()) {
2026:             if (!this.player.containerMenu.stillValid(this.player)) {
2027:                 LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
2028:             } else {
2029:                 boolean clickAccepted = this.player.containerMenu.clickMenuButton(this.player, packet.buttonId());
2030:                 if (clickAccepted) {
2031:                     this.player.containerMenu.broadcastChanges();
2032:                 }
2033:             }
2034:         }
2035:     }
2036:
2037:     @Override
2038:     public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
2039:         PacketUtils.ensureRunningOnSameThread(packet, this, this.player.level());
2040:         if (this.player.hasInfiniteMaterials()) {
2041:             boolean drop = packet.slotNum() < 0;
2042:             ItemStack itemStack = packet.itemStack();
2043:             if (!itemStack.isItemEnabled(this.player.level().enabledFeatures())) {
```

## net/minecraft/world/timeline/Timelines.java / int nightStart
```java
38:         Holder.Reference<WorldClock> overworldClock = clocks.getOrThrow(WorldClocks.OVERWORLD);
39:         EasingType skyAngleEase = EasingType.symmetricCubicBezier(0.362F, 0.241F);
40:         int nightStart = 12600;
41:         int nightEnd = 23401;
42:         int noon = 6000;
43:         context.register(
44:             OVERWORLD_DAY,
45:             Timeline.builder(overworldClock)
46:                 .setPeriodTicks(24000)
47:                 .addTimeMarker(ClockTimeMarkers.DAY, 1000, true)
48:                 .addTimeMarker(ClockTimeMarkers.NOON, 6000, true)
49:                 .addTimeMarker(ClockTimeMarkers.NIGHT, 13000, true)
50:                 .addTimeMarker(ClockTimeMarkers.MIDNIGHT, 18000, true)
51:                 .addTimeMarker(ClockTimeMarkers.WAKE_UP_FROM_SLEEP, 0)
52:                 .addTimeMarker(ClockTimeMarkers.ROLL_VILLAGE_SIEGE, 18000)
53:                 .addTrack(EnvironmentAttributes.SUN_ANGLE, track -> track.setEasing(skyAngleEase).addKeyframe(6000, 360.0F).addKeyframe(6000, 0.0F))
54:                 .addTrack(EnvironmentAttributes.MOON_ANGLE, track -> track.setEasing(skyAngleEase).addKeyframe(6000, 540.0F).addKeyframe(6000, 180.0F))
55:                 .addTrack(EnvironmentAttributes.STAR_ANGLE, track -> track.setEasing(skyAngleEase).addKeyframe(6000, 360.0F).addKeyframe(6000, 0.0F))
56:                 .addModifierTrack(
57:                     EnvironmentAttributes.FIREFLY_BUSH_SOUNDS, BooleanModifier.OR, track -> track.addKeyframe(12600, true).addKeyframe(23401, false)
58:                 )
59:                 .addModifierTrack(
60:                     EnvironmentAttributes.FOG_COLOR,
61:                     ColorModifier.MULTIPLY_RGB,
```

## net/minecraft/world/item/ItemStackTemplate.java / ItemStack create()
```java
92:     }
93:
94:     public ItemStack create() {
95:         return this.validate(new ItemStack(this.item, this.count, this.components));
96:     }
97:
98:     private ItemStack validate(ItemStack result) {
99:         Optional<Error<ItemStack>> error = ItemStack.validateStrict(result).error();
100:         if (error.isPresent()) {
101:             LOGGER.warn("Can't create item stack with properties {}, error: {}", this, error.get().message());
102:             return ItemStack.EMPTY;
103:         } else {
104:             return result;
105:         }
106:     }
107:
108:     public ItemStack apply(DataComponentPatch additionalPatch) {
109:         return this.apply(this.count, additionalPatch);
110:     }
111:
112:     public ItemStack apply(int count, DataComponentPatch additionalPatch) {
113:         ItemStack result = new ItemStack(this.item, count, additionalPatch);
114:         result.applyComponents(this.components);
115:         return this.validate(result);
```

## net/minecraft/world/level/Level.java / long getDefaultClockTime
```java
877:     }
878:
879:     public long getDefaultClockTime() {
880:         return this.getClockTimeTicks(this.dimensionType().defaultClock());
881:     }
882:
883:     private long getClockTimeTicks(Optional<? extends Holder<WorldClock>> clock) {
884:         return clock.<Long>map(holder -> this.clockManager().getTotalTicks((Holder<WorldClock>)holder)).orElse(0L);
885:     }
886:
887:     public boolean mayInteract(Entity entity, BlockPos pos) {
888:         return true;
889:     }
890:
891:     public void broadcastEntityEvent(Entity entity, byte event) {
892:     }
893:
894:     public void broadcastDamageEvent(Entity entity, DamageSource source) {
895:     }
896:
897:     public void blockEvent(BlockPos pos, Block block, int b0, int b1) {
898:         this.getBlockState(pos).triggerEvent(this, pos, b0, b1);
899:     }
900:
```

## net/minecraft/world/level/block/state/BlockBehaviour.java / void randomTick
```java
332:     }
333:
334:     protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
335:     }
336:
337:     protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
338:     }
339:
340:     protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
341:         float destroySpeed = state.getDestroySpeed(level, pos);
342:         if (destroySpeed == -1.0F) {
343:             return 0.0F;
344:         } else {
345:             int i = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
346:             return player.getDestroySpeed(state, pos) / destroySpeed / (float)i;
347:         }
348:     }
349:
350:     protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
351:     }
352:
353:     protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
354:     }
355:
```
```java
758:         }
759:
760:         public void randomTick(ServerLevel level, BlockPos pos, RandomSource random) {
761:             this.getBlock().randomTick(this.asState(), level, pos, random);
762:         }
763:
764:         public void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
765:             this.getBlock().entityInside(this.asState(), level, pos, entity, effectApplier, isPrecise);
766:         }
767:
768:         public void spawnAfterBreak(ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
769:             this.getBlock().spawnAfterBreak(this.asState(), level, pos, tool, dropExperience);
770:         }
771:
772:         public List<ItemStack> getDrops(LootParams.Builder params) {
773:             return this.getBlock().getDrops(this.asState(), params);
774:         }
775:
776:         public InteractionResult useItemOn(ItemStack itemStack, Level level, Player player, InteractionHand hand, BlockHitResult hitResult) {
777:             var useOnContext = new net.minecraft.world.item.context.UseOnContext(level, player, hand, player.getItemInHand(hand).copy(), hitResult);
778:             var e = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent(useOnContext, net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent.UsePhase.BLOCK));
779:             if (e.isCanceled()) return e.getCancellationResult();
780:             return this.getBlock().useItemOn(itemStack, this.asState(), level, hitResult.getBlockPos(), player, hand, hitResult);
781:         }
```
