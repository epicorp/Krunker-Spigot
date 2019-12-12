package net.epicorp.epicore;

import net.epicorp.epicore.guns.KrunkerFactory;
import net.epicorp.items.IItemRegistry;
import net.epicorp.items.ItemRegistry;
import net.epicorp.items.enchantment.EnchantmentRegistry;
import net.epicorp.items.enchantment.IEnchantmentRegistry;
import net.epicorp.persistance.registry.IPersistenceRegistry;
import net.epicorp.persistance.registry.PersistenceRegistry;
import net.epicorp.utilities.TimedList;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.File;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Material.*;

public final class Epicore extends JavaPlugin implements Listener {
	public static final Random RANDOM = new Random();
	public static final TimedList<UUID> PROJECTILES = new TimedList<>();
	public static final File PERSISTENT_REGISTRY_FILE = new File("persistents.yml");
	public static final IEnchantmentRegistry ENCHANTMENT_REGISTRY = new EnchantmentRegistry();
	public static final IItemRegistry ITEM_REGISTRY = new ItemRegistry();
	//public static final AnvilManager ANVIL_MANAGER = new AnvilManager();
	//public static final CraftingManager CRAFTING_MANAGER = new CraftingManager();
	public static final IPersistenceRegistry PERSISTENCE_REGISTRY = new PersistenceRegistry(PERSISTENT_REGISTRY_FILE);
	//public static BlockDatabase database;
	//public static BlockManager manager;

	@Override
	public void onEnable() {
		//database = new BlockDatabase(PERSISTENCE_REGISTRY, "standard", this);
		//database.init();
		//manager = new BlockManager(database, PERSISTENCE_REGISTRY, this);
		//ANVIL_MANAGER.setPlugin(this);
		//CRAFTING_MANAGER.setPlugin(this);
		PROJECTILES.setPlugin(this);
		//this.registerListeners(ANVIL_MANAGER);
		//this.registerListeners(CRAFTING_MANAGER);
		//this.registerListeners(database);
		this.registerListeners(this);

		KrunkerFactory.createGun(ITEM_REGISTRY, this, WOODEN_AXE, "assault_rifle", GOLD + "Assault Rifle", 300, .95f, 30, 2000, 20, 700, 4, 160, .1f, 2, .05f);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, IRON_HOE, "sniper_rifle", GOLD + "Sniper Rifle", 300, .95f, 3, 1500, 24, 1000, 8, 900, 0.1f, 5, 0);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, STONE_AXE, "sub_machine_gun", GOLD + "Sub Machine Gun", 300, 1.04f, 28, 1000, 18, 700, 4, 70, .1f, 2, .05f);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, IRON_AXE, "light_machine_gun", GOLD + "Light Machine Gun", 700, .79f, 60, 3500, 20, 700, 4, 120, .1f, 2, .05f);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, WOODEN_PICKAXE, "shotgun", GOLD + "Shotgun", 300, 1, 2, 1500, 19, 700, 1.8f, 400, .13f, 0, 1f, () -> RANDOM.nextInt(4) + 3);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, STONE_SHOVEL, "revolver", GOLD + "Revolver", 20, 1.04f, 6, 900, 15, 700, 4, 260, .05f, 2, .05f);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, DIAMOND_SHOVEL, "semi_auto", GOLD + "Semi Auto", 300, 1, 8, 1500, 17, 1000, 6, 480, .01f, 2, .05f);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, WOODEN_AXE, "akimbo_uzi", GOLD + "Akimbo Uzi", 300, 1.04f, 18, 1200, 13, 300, 4, 400, .01f, 1, .05f, () -> 2);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, GOLDEN_AXE, "famas", GOLD + "FAMAS", 300, .95f, 30, 1200, 25, 700, 3, 50, .05f, 2, .05f);
		KrunkerFactory.createGun(ITEM_REGISTRY, this, DIAMOND_AXE, "retard", GOLD + "retard rifle", 300, .95f, 1000, 1000, 5, 700, 5, 50, .3f, 3, .1f);


		// TODO help command
		// TODO gun sounds

		Objects.requireNonNull(this.getCommand("krunker")).setExecutor((sender, command, label, args) -> {
			if (args.length != 1) return false;
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.getInventory().clear();
				player.setGameMode(GameMode.SURVIVAL);
				((Player) sender).getInventory().addItem(ITEM_REGISTRY.getItem(new NamespacedKey(this, args[0])).createNewStack());
				player.getInventory().addItem(this.unbreakable(IRON_HELMET), this.unbreakable(IRON_CHESTPLATE), this.unbreakable(IRON_LEGGINGS), this.unbreakable(IRON_BOOTS), new ItemStack(GOLDEN_APPLE, 64));
			} else return false;
			return true;
		});

		Objects.requireNonNull(this.getCommand("startkrunker")).setExecutor(((sender, command, label, args) -> {
			if (sender instanceof Player) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.sendMessage(RED + "do /krunker to choose your weapons! 2 min grace period!");
					player.setHealth(20f);
					player.teleport(((Player) sender).getLocation());
					player.setGameMode(GameMode.SURVIVAL);
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 4), true);
				}
			}
			return true;
		}));
	}

	private void registerListeners(Listener object) {
		this.getServer().getPluginManager().registerEvents(object, this);
	}

	@Override
	public void onDisable() {
		//database.save(true);
	}

	@EventHandler
	public void damage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof LivingEntity) {
			Bukkit.getScheduler().runTaskLater(this, () -> {
				((LivingEntity) entity).setNoDamageTicks(0);
			}, 1);
		}
	}

	@EventHandler
	public void arrow(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		Location location = projectile.getLocation();
		if(event.getHitEntity() instanceof Player) {
			Location elocation = ((Player) event.getHitEntity()).getEyeLocation().subtract(location);
			if (Math.abs(elocation.getY()) > .25f) {
				if (projectile instanceof Arrow) {
					((Arrow) projectile).setDamage(((Arrow) projectile).getDamage() * 2);
					event.getHitEntity().sendMessage(RED + "You were headshotted!");
				}
			}
		}
	}

	private ItemStack unbreakable(Material type) {
		ItemStack stack = new ItemStack(type);
		ItemMeta meta = stack.getItemMeta();
		assert meta != null;
		stack.addEnchantment(Enchantment.PROTECTION_FALL, 4);
		meta.setUnbreakable(true);
		stack.setItemMeta(meta);
		return stack;
	}
}
