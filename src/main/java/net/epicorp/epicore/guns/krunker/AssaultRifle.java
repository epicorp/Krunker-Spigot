package net.epicorp.epicore.guns.krunker;

import net.epicorp.epicore.guns.AbstractArrowGun;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import java.util.Arrays;

public class AssaultRifle extends AbstractArrowGun {
	public AssaultRifle(Plugin plugin, String id) {
		super(plugin, id);
	}

	@Override
	protected ItemStack transform(ItemStack stack) {
		stack.setType(Material.WOODEN_AXE);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.RED+"Assault rifel!");
		meta.setLore(Arrays.asList("10/10, very nice"));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	protected int swapTime() {
		return 20;
	}

	@Override
	protected int zoom() {
		return 2;
	}

	@Override
	protected float speed() {
		return -.05f;
	}

	@Override
	protected int ammo() {
		return 30;
	}

	@Override
	protected int reload() {
		return 24;
	}

	@Override
	protected float damage() {
		return 4.6f;
	}

	@Override
	protected int bulletTime() {
		return 5;
	}

	@Override
	protected float power() {
		return 4;
	}

	@Override
	protected int fireDelay() {
		return 2;
	}

	@Override
	protected float spread() {
		return .2f;
	}

	@Override
	protected float knockback() {
		return .15f;
	}
}
