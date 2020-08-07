package be4rjp.sclat;
import java.lang.reflect.Field;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

public class Glow extends EnchantmentWrapper{

    private static Glow glow = null;

    public Glow() {
        super("sclatg");
    }

    public boolean canEnchantItem(ItemStack item) {
        return true;
    }

    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    public EnchantmentTarget getItemTarget() {
        return null;
    }

    public int getMaxLevel() {
        return 10;
    }

    public String getName() {
        return "sclatg";
    }

    public int getStartLevel() {
        return 1;
    }


    public ItemStack enchantGlow(ItemStack is) {
        enableGlow();
        is.addEnchantment(glow, 1);
        return is;
    }

    public ItemStack removeGlow(ItemStack is) {
        enableGlow();
        is.removeEnchantment(glow);
        return is;
    }


    public Boolean isGlowing(ItemStack is) {
        enableGlow();
        return is.getEnchantments().keySet().contains(glow);
    }


    @SuppressWarnings("unchecked")
    public void enableGlow() {
        try {
            if (glow == null) {
                glow = new Glow();
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
                Field hmapf = Enchantment.class.getDeclaredField("byName");
                hmapf.setAccessible(true);
                Map<String, Enchantment> hmap = (Map<String, Enchantment>) hmapf.get(hmapf);
                if (!hmap.keySet().contains("sclatg")) {
                    Enchantment.registerEnchantment(glow);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
