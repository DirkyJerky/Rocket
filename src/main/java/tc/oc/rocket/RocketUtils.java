package tc.oc.rocket;

import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet31RelEntityMove;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RocketUtils {
    static Random random = new Random();
    static List<Color> colors = Lists.newArrayList(Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.TEAL, Color.YELLOW);

    public static Firework getRandomFirework(Location loc) {
        FireworkMeta fireworkMeta = (FireworkMeta) (new ItemStack(Material.FIREWORK)).getItemMeta();
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);

        fireworkMeta.setPower(RocketConfig.FIREWORK_POWER);
        fireworkMeta.addEffect(FireworkEffect.builder()
                    .with(RocketUtils.randomFireworkType())
                    .withColor(RocketUtils.randomColor())
                    .trail(RocketConfig.FIREWORK_TRAIL)
                    .build());

        firework.setFireworkMeta(fireworkMeta);
        return firework;
    }

    public static FireworkEffect.Type randomFireworkType() {
        return FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
    }

    public static Color randomColor() {
        return colors.get(random.nextInt(colors.size()));
    }

    public static void takeOff(Player observer, Location loc) {
        for(int i = 0; i < RocketConfig.SMOKE_COUNT; i++) {
            double angle = 2 * Math.PI * i / RocketConfig.SMOKE_COUNT;
            Location base = loc.clone().add(new Vector(RocketConfig.SMOKE_RADIUS * Math.cos(angle), 0, RocketConfig.SMOKE_RADIUS * Math.sin(angle)));
            for(int j = 0; j <= 8; j++) {
                observer.playEffect(base, Effect.SMOKE, j);
            }
        }
    }

    public static void fakeDelta(Player observer, Player victim, Vector delta) {
        Packet packet = new Packet31RelEntityMove(((CraftPlayer) victim).getHandle().id, (byte) (delta.getX() * 32), (byte) (delta.getY() * 32), (byte) (delta.getZ() * 32));

        sendPacket((CraftPlayer) observer, packet);
    }

    private static void sendPacket(CraftPlayer player, Packet packet) {
        player.getHandle().playerConnection.sendPacket(packet);
    }

    public static void exclusiveEntities(List<? extends Entity> entities, Player observer) {
        for(Entity entity : entities) {
            exclusiveEntity(entity, observer);
        }
    }

    public static void exclusiveEntity(Entity entity, Player observer) {
        for(Player player : observer.getWorld().getPlayers()) {
            if(player == observer || !player.canSee(entity)) continue;
            player.hide(entity);
        }
    }
}
