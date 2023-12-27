package live.kill00.nuke

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Particle
import org.bukkit.SoundCategory
import java.util.Random

class CommandCollection {
    fun register() {
        CommandAPICommand("nuke")
            .withArguments(IntegerArgument("range", 0))
            .withArguments(IntegerArgument("count", 0))
            .withArguments(IntegerArgument("startMS", 0))
            .withArguments(IntegerArgument("decreaseRate", 0))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                var running = true
                sender.sendMessage("Nuking started!")

                val centerLocation = sender.location.add(.0, .25, .0)
                var horn = args[2] as Int

                // x sign
                val dustOpt = Particle.DustOptions(org.bukkit.Color.RED, 1f)
                HeartbeatScope().launch {
                    while (running) {
                        var i = -2.5
                        while (i <= 2.5) {
                            sender.world.spawnParticle(Particle.REDSTONE, centerLocation.clone().add(i, .0, i), 1, 0.0, 0.0, 0.0, 0.0, dustOpt, true)
                            sender.world.spawnParticle(Particle.REDSTONE, centerLocation.clone().add(-i, .0, i), 1, 0.0, 0.0, 0.0, 0.0, dustOpt, true)
                            sender.world.spawnParticle(Particle.REDSTONE, centerLocation.clone().add(i, .0, -i), 1, 0.0, 0.0, 0.0, 0.0, dustOpt, true)
                            sender.world.spawnParticle(Particle.REDSTONE, centerLocation.clone().add(-i, .0, -i), 1, 0.0, 0.0, 0.0, 0.0, dustOpt, true)
                            i += 0.25
                        }
                        delay(100)
                    }
                }

                // Horn sound
                HeartbeatScope().launch {
                    while (running) {
                        sender.world.playSound(centerLocation, "minecraft:item.goat_horn.sound.5", SoundCategory.MASTER, 100f, 0.1f)
                        delay(12000)
                    }
                }

                // Alert sound & Explosion
                HeartbeatScope().launch {
                    delay(1000)
                    while (running && horn > 0) {
                        sender.world.playSound(centerLocation, "minecraft:block.bell.use", SoundCategory.MASTER, 100f, 1f)
                        delay(horn.toLong())
                        horn -= (args[3] as Int)
                    }

                    var i = 0
                    val random = Random()
                    // Center Explosion
                    sender.world.createExplosion(centerLocation, 200f, true, true)
                    sender.sendMessage("Nuked! (${Math.round(centerLocation.x)}, ${Math.round(centerLocation.y)}, ${Math.round(centerLocation.z)})")

                    while (running && i <= args[1] as Int) {
                        val x = random.nextInt((args[0] as Int) * 2 + 1) - (args[0] as Int)
                        val z = random.nextInt((args[0] as Int) * 2 + 1) - (args[0] as Int)

                        var loc = centerLocation.clone().add(x.toDouble(), .0, z.toDouble())

                        sender.sendMessage("Nuked! (${Math.round(loc.x)}, ${Math.round(loc.y)}, ${Math.round(loc.z)})")

                        sender.world.createExplosion(loc, 150f, true, true)
                        i++

                        delay(250)
                    }
                    sender.sendMessage("Finished the Nuke!")
                    running = false
                }
            }).withPermission("op").register()
    }
}