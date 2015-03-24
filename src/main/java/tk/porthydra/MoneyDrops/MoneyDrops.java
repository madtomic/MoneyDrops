package tk.porthydra.MoneyDrops;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyDrops extends JavaPlugin implements Listener {
	
	//TODO: Custom message for picking up money!
	//TODO: Boss mob support!
	//TODO: MobType Support (Such as wither skeletons, currently drop normal skeleton money)
	//TODO: Move stuff to other classes
	
	public static Economy economy = null;
	public static Entity entity;
	String cprefix = getConfig().getString("currencyPrefix");
	Double money;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		loadPlugin();
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
	}
	
	public void loadPlugin() {
		this.saveDefaultConfig();
		this.reloadConfig();
	    setupEconomy();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void createMoney(EntityDeathEvent e) {
		
		LivingEntity deadentity = e.getEntity();
		Player killer = deadentity.getKiller();
		
		Location location = deadentity.getLocation();
		World world = deadentity.getWorld();
	
		ItemStack imoney = new ItemStack(Material.getMaterial(getConfig().getString("item")), (byte)1);
		
		List<String> lore = new ArrayList<String>();
		lore.add("wtf how are you holding this");

		ItemMeta meta = imoney.getItemMeta();
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);

		meta.setDisplayName(ChatColor.RED + "put this item down it will break your life");
		meta.setLore(lore);

		if (getConfig().getBoolean("creativeEnabled") == false || ((getConfig().getBoolean("creativeEnabled") == true) && killer.getGameMode() != GameMode.CREATIVE)) {
			if	((killer != null) && (deadentity instanceof LivingEntity)) {
			
				String[] mobmoneymin = {"bat.min", "chicken.min", "cow.min", "mooshroom.min", "pig.min", "rabbit.min", "sheep.min", "squid.min", "villager.min",
					"cavespider.min", "enderman.min", "spider.min", "zombiepigman.min", "blaze.min", "creeper.min", "endermite.min", "ghast.min", "guardian.min", "magmacube.min", "silverfish.min", "skeleton.min", "slime.min", "witch.min", "zombie.min",
					"horse.min", "ocelot.min", "wolf.min",
					"irongolem.min", "snowgolem.min"};
			
				String[] mobmoneymax = {"bat.max", "chicken.max", "cow.max", "mooshroom.max", "pig.max", "rabbit.max", "sheep.max", "squid.max", "villager.max",
					"cavespider.max", "enderman.max", "spider.max", "zombiepigman.max", "blaze.max", "creeper.max", "endermite.max", "ghast.max", "guardian.max", "magmacube.max", "silverfish.max", "skeleton.max", "slime.max", "witch.max", "zombie.max",
					"horse.max", "ocelot.max", "wolf.max",
					"irongolem.max", "snowgolem.max"};
			
				String[] mobisenabled = {"bat.enabled", "chicken.enabled", "cow.enabled", "mooshroom.enabled", "pig.enabled", "rabbit.enabled", "sheep.enabled", "squid.enabled", "villager.enabled",
					"cavespider.enabled", "enderman.enabled", "spider.enabled", "zombiepigman.enabled", "blaze.enabled", "creeper.enabled", "endermite.enabled", "ghast.enabled", "guardian.enabled", "magmacube.enabled", "silverfish.enabled", "skeleton.enabled", "slime.enabled", "witch.enabled", "zombie.enabled",
					"horse.enabled", "ocelot.enabled", "wolf.enabled",
					"irongolem.enabled", "snowgolem.enabled"};
	
				Boolean[] isMob = {deadentity instanceof Bat, deadentity instanceof Chicken, deadentity instanceof Cow, deadentity instanceof MushroomCow, deadentity instanceof Pig, deadentity instanceof Rabbit, deadentity instanceof Sheep, deadentity instanceof Squid, deadentity instanceof Villager,
					deadentity instanceof CaveSpider, deadentity instanceof Enderman, deadentity instanceof Spider, deadentity instanceof PigZombie, deadentity instanceof Blaze, deadentity instanceof Creeper, deadentity instanceof Endermite, deadentity instanceof Ghast, deadentity instanceof Guardian, deadentity instanceof MagmaCube, deadentity instanceof Silverfish, deadentity instanceof Skeleton, deadentity instanceof Slime, deadentity instanceof Witch, deadentity instanceof Zombie,
					deadentity instanceof Horse, deadentity instanceof Ocelot, deadentity instanceof Wolf,
					 deadentity instanceof IronGolem, deadentity instanceof Snowman};
			
				for (int i = 0; i <= (isMob.length - 1); i++) {
					if (isMob[i] && getConfig().getBoolean(mobisenabled[i])) {
					
						Double randmoney = randDouble(getConfig().getDouble(mobmoneymin[i]), getConfig().getDouble(mobmoneymax[i]));
						Double randm = round(randmoney, 2);
					
						meta.setDisplayName(Double.toString(randm));
					
						imoney.setItemMeta(meta);
					
						Item emoney = world.dropItemNaturally(location, imoney);
					
						emoney.setCustomName(ChatColor.AQUA + cprefix + randm);
						emoney.setCustomNameVisible(true);
					}
				}
			}
		
			if ((killer != null) && (deadentity instanceof Player) && getConfig().getBoolean("formulae.enabled")) {
				Player deadplayer = (Player) deadentity;
				
				Double deadbal = economy.getBalance(deadplayer);
				Double formulae = getConfig().getDouble("formulae.x") / getConfig().getDouble("formulae.y");
				Double reward = deadbal * formulae;
				Double rew = round(reward, 2);
				
				economy.withdrawPlayer(deadplayer, reward);
				
				meta.setDisplayName(Double.toString(rew));
				
				imoney.setItemMeta(meta);
				
				Item emoney = world.dropItemNaturally(location, imoney);
				
				emoney.setCustomName(ChatColor.AQUA + cprefix + Double.toString(rew));
				emoney.setCustomNameVisible(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void addMoney(PlayerPickupItemEvent e) {
		
		Item emoney = e.getItem();
		
		Player p = e.getPlayer();
		
		Enchantment sharpness = Enchantment.DAMAGE_ALL;

		if (emoney.getItemStack().getEnchantmentLevel(sharpness) == 1 && emoney.getCustomName().contains(cprefix)) {
			e.setCancelled(true);
			emoney.remove();
		
			economy.depositPlayer(p, doubleOf(emoney.getItemStack().getItemMeta().getDisplayName()));
			p.sendMessage(ChatColor.AQUA + emoney.getCustomName() + ChatColor.GREEN + " has been added to your account!");
		}	
	}
    
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        
        return (economy != null);
    }
    private Double doubleOf(String s) {
    	Double result = Double.parseDouble(s);
    	return result;
    }
    
    public static double randDouble(double min, double max) {

        Random rand = new Random();
        double randomValue = min + (max - min) * rand.nextDouble();
        return randomValue;
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
