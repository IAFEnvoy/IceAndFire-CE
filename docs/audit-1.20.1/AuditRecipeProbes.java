import net.minecraft.SharedConstants;
import net.minecraft.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
public class AuditRecipeProbes {
  public static void main(String[] args) {
    SharedConstants.createGameVersion(); Bootstrap.initialize();
    ItemStack a=new ItemStack(Items.DIAMOND), b=new ItemStack(Items.DIAMOND);
    a.getOrCreateNbt().putString("audit","a"); b.getOrCreateNbt().putString("audit","b");
    if (!ItemStack.areItemsEqual(a,b) || ItemStack.canCombine(a,b)) throw new AssertionError("NBT semantics");
    System.out.println("CONFIRMED: F04 areItemsEqual accepts different NBT; canCombine rejects it");
    DragonForgeRecipe recipe=new DragonForgeRecipe(new Identifier("audit","shared_output"),Ingredient.EMPTY,Ingredient.EMPTY,new ItemStack(Items.DIAMOND),"fire",100);
    ItemStack output=recipe.craft(null,null); output.increment(4);
    if (recipe.craft(null,null).getCount()!=5 || output!=recipe.getResultItem()) throw new AssertionError("aliasing");
    System.out.println("CONFIRMED: F05 mutating craft result mutates shared recipe output");
    System.out.println("TOTAL_CONFIRMED=2");
  }
}
