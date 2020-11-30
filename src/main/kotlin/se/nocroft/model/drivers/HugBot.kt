package se.nocroft.model.drivers

import se.nocroft.model.cars.FragileCar
import se.nocroft.model.drivers.util.BotPoint
import se.nocroft.util.CfgParser
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point

class HugBot(car: FragileCar?, trackName: String?) : Driver(car, trackName) {

    private var lastX: Int = car?.getMiddleX(car.x.toDouble())?.toInt() ?: 0
    private var lastY: Int = car?.getMiddleY(car.y.toDouble())?.toInt() ?: 0
    private val crashPoints = mutableListOf<BotPoint>()
    private val debugMode = CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled")

    override fun update(deltaTime: Double) {
        car.accelerate()
        detectCrash()
        lastX = car.getMiddleX(car.x.toDouble()).toInt()
        lastY = car.getMiddleY(car.y.toDouble()).toInt()
    }

    override fun paint(g: Graphics2D, scale: Double, scaleX: Int) {
        if (debugMode) {
            g.color = when (car.name.split(" ").first()) {
                "BLUE" -> Color.decode("#78A2CC")
                "RED" -> Color.RED
                "GREEN" -> Color.decode("#80C904")
                else -> Color.YELLOW
            }
            for (p in crashPoints) {
                val s = (scale * 10).toInt()
                g.fillRoundRect((p.x * scale).toInt() - s / 2 + scaleX, (p.y * scale).toInt() - s / 2, s, s, s, s)

                val dist = (scale * p.radius * 2).toInt()
                g.drawRoundRect((p.x * scale).toInt() - dist / 2 + scaleX, (p.y * scale).toInt() - dist / 2, dist, dist,
                        dist, dist)
            }
        }
    }

    private fun detectCrash() {
        val middleX = car.getMiddleX(car.x.toDouble())
        val middleY = car.getMiddleY(car.y.toDouble())
        val d = Point.distance(middleX, middleY, lastX.toDouble(), lastY.toDouble())

        if (Point.distance(middleX, middleY, lastX.toDouble(), lastY.toDouble()) > 50) {
            crashPoints.add(BotPoint(lastX, lastY, car.width.toDouble()))
        }
    }
}