import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.ai.goal.Goal;
import com.iafenvoy.iceandfire.data.component.MiscData;
import java.lang.reflect.Field;
public class AuditProbes {
  static int checks;
  static void check(boolean ok, String label) {
    if (!ok) throw new AssertionError(label);
    System.out.println("CONFIRMED: " + label); checks++;
  }
  public static void main(String[] args) throws Exception {
    NbtCompound tag=new NbtCompound();
    NbtCompound misc=new NbtCompound(); misc.putInt("loveTicks",7);
    tag.put("miscData",misc); tag.putIntArray("targetedByScepterIds",new int[]{10,11});
    MiscData data=new MiscData(); data.deserialize(tag);
    Field ids=MiscData.class.getDeclaredField("targetedByScepterIds"); ids.setAccessible(true);
    check(data.loveTicks==7 && ids.get(data)==null,"B04 actual MiscData.deserialize ignores IDs written at outer tag level");
    Goal goal=new Goal(){int calls; public boolean canStart(){return ++calls==1;}};
    check(goal.shouldContinue() && !goal.shouldContinue(),"A03 actual Goal.shouldContinue delegates to canStart each call");
    System.out.println("TOTAL_CONFIRMED="+checks);
  }
}
