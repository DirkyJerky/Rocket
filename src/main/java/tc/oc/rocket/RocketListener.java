package tc.oc.rocket;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RocketListener implements Listener {
    public RocketListener(RocketPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void interact(final EntityDamageEvent event) {
        if(!this.plugin.ENABLED) return;
        if(!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent realEvent = (EntityDamageByEntityEvent) event;
        if(!(realEvent.getDamager() instanceof Player) || !(realEvent.getEntity() instanceof Player)) return;

        Player damager = (Player) realEvent.getDamager();
        Player victim = (Player) realEvent.getEntity();

        if(!damager.hasPermission(RocketConfig.ROCKET_PERMISSION)) return;
        if(victim.hasPermission(RocketConfig.EXEMPT_PERMISSION)) return;

        List<Firework> fireworks = Lists.newArrayList();
        for(int i = 0; i < RocketConfig.FIREWORK_COUNT; i++) {
            Firework firework = RocketUtils.getRandomFirework(victim.getLocation());
            firework.setVelocity(firework.getVelocity().multiply(new Vector(1, RocketConfig.ROCKET_VELOCITY_MOD, 1)));
            fireworks.add(firework);
        }

        this.plugin.rockets.add(new Rocket(damager, victim, fireworks));

        RocketUtils.fakeDelta(damager, victim, new Vector(0, 3, 0));
        RocketUtils.takeOff(damager, victim.getLocation());
        RocketUtils.exclusiveEntities(fireworks, damager);

        event.setCancelled(true);
    }

    @EventHandler
    public void join(final PlayerJoinEvent event) {
        for(Iterator<Rocket> it = this.plugin.rockets.iterator(); it.hasNext();) {
            for(Firework firework : it.next().getFireworks()) {
                event.getPlayer().hide(firework);
            }
        }
    }

    final RocketPlugin plugin;
}
