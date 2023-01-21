package me.dolphin2410.boatice

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.monun.kommand.wrapper.BlockPosition3D
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title.title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class BoaticePlugin: JavaPlugin() {
    lateinit var arena: Pair<Block, Block>
    val boats = HashMap<Player, Boat>()
    lateinit var runningTask: BukkitRunnable

    private infix fun Int.toward(to: Int): IntProgression {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to, step)
    }

    fun check() = object: BukkitRunnable() {
        override fun run() {
            val iter = boats.iterator()
            while (iter.hasNext()) {
                val (player, boat) = iter.next()
                if (boat.location.y <= maxOf(arena.first.y, arena.second.y)) {
                    boat.remove()
                    iter.remove()
                    player.showTitle(title(text("You Lost"), text("")))
                }
            }

            if (boats.size == 1) {
                val winner = boats.entries.first()
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    onlinePlayer.showTitle(title(text("Game Over"), text("Winner: ${winner.key.name}", NamedTextColor.AQUA)))
                    winner.value.remove()
                    boats.clear()
                    cancel()
                }
            }
        }
    }

    override fun onEnable() {
        kommand {
            register("boatice") {
                then("setarena") {
                    then("l1" to blockPosition()) {
                        then("l2" to blockPosition()) {
                            executes {
                                val l1: BlockPosition3D by it
                                val l2: BlockPosition3D by it

                                arena = l1.toBlock(world) to l2.toBlock(world)
                            }
                        }
                    }
                }

                then("start") {
                    if (::runningTask.isInitialized) {
                        runningTask.cancel()
                    }
                    runningTask = check()
                    runningTask.runTaskTimer(this@BoaticePlugin, 0, 1)
                    executes {
                        for (x in arena.first.x toward arena.second.x) {
                            for (y in arena.first.y toward arena.second.y) {
                                for (z in arena.first.z toward arena.second.z) {
                                    world.setBlockData(x, y, z, Material.ICE.createBlockData())
                                }
                            }
                        }
                    }
                }

                then("boat") {
                    executes {
                        if (boats.contains(player)) {
                            player.sendMessage("RESTART THE GAME. YOUR BOAT ALREADY EXISTS")
                        } else {
                            val entity = player.world.spawn(player.location, Boat::class.java)
                            entity.isInvulnerable = true
                            boats[player] = entity
                        }
                    }
                }
            }
        }
    }
}