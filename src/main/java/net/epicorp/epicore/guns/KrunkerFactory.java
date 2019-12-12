package net.epicorp.epicore.guns;

import net.epicorp.items.IItemRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import java.util.Arrays;
import java.util.function.IntSupplier;

public class KrunkerFactory {
	public static void createGun(IItemRegistry registry, Plugin plugin, Material gun, String id, String name, int swap, float speed, int ammo, int reloadTime, float damage, int btime, float power, int mps, float spread, int zoom, float kb, String... lore) {
		createGun(registry, plugin, gun, id, name, swap, speed, ammo, reloadTime, damage, btime, power, mps, spread, zoom, kb, () -> 1, lore);
	}

	public static void createGun(IItemRegistry registry, Plugin plugin, Material gun, String id, String name, int swap, float speed, int ammo, int reloadTime, float damage, int btime, float power, int mps, float spread, int zoom, float kb, IntSupplier projectiles, String... lore) {
		registry.register(new AbstractArrowGun(plugin, id) {

			@Override
			protected ItemStack transform(ItemStack stack) {
				stack.setType(gun);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(name);
				meta.setLore(Arrays.asList(lore));
				stack.setItemMeta(meta);
				return stack;
			}

			@Override
			protected int swapTime() {
				return swap / 10;
			}

			@Override
			protected int zoom() {
				return zoom;
			}

			@Override
			protected float speed() {
				return speed - 1;
			}

			@Override
			protected int ammo() {
				return ammo;
			}

			@Override
			protected int reload() {
				return reloadTime / 50;
			}

			@Override
			protected float damage() {
				return damage / 18;
			}

			@Override
			protected int bulletTime() {
				return btime / 50;
			}

			@Override
			protected float power() {
				return power;
			}

			@Override
			protected int fireDelay() {
				return mps / 50;
			}

			@Override
			protected float spread() {
				return spread;
			}

			@Override
			protected float knockback() {
				return kb;
			}

			@Override
			protected int projectiles() {
				return projectiles.getAsInt();
			}
		});
	}
}
