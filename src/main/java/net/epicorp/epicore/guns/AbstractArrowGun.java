package net.epicorp.epicore.guns;

import net.epicorp.epicore.Epicore;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import java.util.Random;

public abstract class AbstractArrowGun extends AbstractKrunkerGun {
	private static final Random RANDOM = new Random();
	public AbstractArrowGun(Plugin plugin, String id) {
		super(plugin, id);
	}

	@Override
	public void launchProjectile(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, this.power()/8f, .03f);
		Vector vector = player.getEyeLocation().getDirection();
		for (int i = 0; i < this.projectiles(); i++) {
			float factor = player.getPose() == Pose.SNEAKING ? 1f/(this.zoom()+1) : 1;
			Vector newVec = vector.add(new Vector(this.computeSpread()*factor, this.computeSpread()*factor, this.computeSpread()*factor)).multiply(this.power());
			Arrow arrow = player.launchProjectile(Arrow.class, newVec);
			arrow.setDamage(this.damage());
			arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
			Epicore.PROJECTILES.add(arrow.getUniqueId(), this.bulletTime(), u -> {
				Entity entity = Bukkit.getEntity(u);
				if(entity != null)
					entity.remove();
			});
		}
	}

	private float computeSpread() {
		return (RANDOM.nextFloat()-.5f)*1000 % this.spread();
	}

	protected int projectiles() {
		return 1;
	}
}
