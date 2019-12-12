package net.epicorp.epicore.guns;

import net.epicorp.items.CustomItem;
import net.epicorp.items.ItemEventHandler;
import net.epicorp.persistance.Persistent;
import net.epicorp.persistance.util.BukkitPersistents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class AbstractKrunkerGun extends CustomItem {

	public AbstractKrunkerGun(Plugin plugin, String id) {
		super(plugin, id, (e) -> {
			if (e instanceof PlayerItemHeldEvent) {
				PlayerItemHeldEvent event = (PlayerItemHeldEvent) e;
				return event.getPlayer().getInventory().getItem(event.getNewSlot());
			} else if (e instanceof PlayerInteractEvent) {
				return ((PlayerInteractEvent) e).getItem();
			} else if (e instanceof PlayerInteractEntityEvent) {
				PlayerInteractEntityEvent event = (PlayerInteractEntityEvent) e;
				return event.getPlayer().getInventory().getItemInMainHand();
			} else if(e instanceof EntityPoseChangeEvent) {
				Entity entity = ((EntityPoseChangeEvent) e).getEntity();
				if(entity instanceof Player)
					return ((Player) entity).getInventory().getItemInMainHand();
				return null;
			}

			throw new UnsupportedOperationException(e + " not accounted for!");
		});
	}

	public void fire(ItemStack stack, Player player) {
		GunStats stats = new GunStats();
		if (stack != null) {
			BukkitPersistents.read(stats, stack);
			long current = player.getWorld().getFullTime();
			if (stats.fireDelay <= current) {
				if (stats.ammo <= 0) {
					stats.fireDelay = this.reload() + current;
					stats.ammo = this.ammo();
					stack.removeEnchantment(Enchantment.DURABILITY);
					Bukkit.getScheduler().runTaskLater(this.plugin, () -> stack.addEnchantment(Enchantment.DURABILITY, 5), this.reload());
				} else {
					this.launchProjectile(player);
					stats.ammo--;
					player.sendMessage(this.getLevel(((double) stats.ammo) / this.ammo()) + "" + stats.ammo + "/" + this.ammo());
					stats.fireDelay = this.fireDelay() + current;
					player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(-this.knockback())));
				}
				player.setCooldown(stack.getType(), (int) (stats.fireDelay - current));
			}

			BukkitPersistents.write(stats, stack);
		}
	}

	public abstract void launchProjectile(Player player);

	@ItemEventHandler
	public void onClick(PlayerInteractEvent right, ItemStack stack) {
		Action action = right.getAction();
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			this.fire(stack, right.getPlayer());
	}

	@ItemEventHandler
	public void onClick(PlayerInteractEntityEvent right, ItemStack stack) {
		this.fire(stack, right.getPlayer());
	}

	@ItemEventHandler
	public void croutch(EntityPoseChangeEvent poseChangeEvent) {
		this.zoom((Player) poseChangeEvent.getEntity(), poseChangeEvent.getPose());
	}

	public void zoom(Player player, Pose pose) {
		if(pose == Pose.SNEAKING)
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12000, this.zoom()), true);
		else if(pose == Pose.STANDING)
			player.removePotionEffect(PotionEffectType.SLOW);
	}


	@ItemEventHandler
	public void onSwap(PlayerItemHeldEvent event, ItemStack stack) {
		GunStats stats = new GunStats();
		BukkitPersistents.read(stats, stack);
		stats.fireDelay = this.swapTime() + event.getPlayer().getWorld().getFullTime();
		event.getPlayer().setCooldown(stack.getType(), this.swapTime());
		BukkitPersistents.write(stats, stack);
	}


	@Override
	protected ItemStack baseStack() {
		ItemStack stack = new ItemStack(Material.STONE);
		ItemMeta meta = stack.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier("speed", this.speed(), AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		stack.setItemMeta(meta);
		return this.transform(stack);
	}

	protected abstract ItemStack transform(ItemStack stack);

	protected abstract int swapTime();

	protected abstract int zoom();

	protected abstract float speed();

	protected abstract int ammo();

	protected abstract int reload();

	protected abstract float damage();

	protected abstract int bulletTime();

	protected abstract float power();

	protected abstract int fireDelay();

	protected abstract float spread();

	protected abstract float knockback();

	public static final class GunStats implements Persistent {
		public long fireDelay;
		public int ammo;

		@Override
		public void load(DataInputStream input) throws IOException {
			this.fireDelay = input.readLong();
			this.ammo = input.readInt();
		}

		@Override
		public void writeTo(DataOutputStream output) throws IOException {
			output.writeLong(this.fireDelay);
			output.writeInt(this.ammo);
		}

		@Override
		public void close() {}
	}

	private ChatColor getLevel(double val) {
		if (val > .5) return ChatColor.GREEN;
		else if (val > .25) return ChatColor.YELLOW;
		else if (val > .1) return ChatColor.GOLD;
		else return ChatColor.RED;
	}
}
