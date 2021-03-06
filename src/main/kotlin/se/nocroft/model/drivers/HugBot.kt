package se.nocroft.model.drivers

import se.nocroft.model.cars.FragileCar
import se.nocroft.model.drivers.util.BotPoint
import se.nocroft.util.CfgParser
import se.nocroft.util.Geom.getPI
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2

class HugBot(car: FragileCar, trackName: String?) : Driver(car, trackName) {

    private var lastX: Int = car.middleX.toInt() ?: 0
    private var lastY: Int = car.middleY.toInt() ?: 0
    private var closestPoint: BotPoint? = null
    private var passedFirst = false

    private val initialHeading: Double = car.heading
    private val crashPoints = mutableListOf<BotPoint>()
    private val debugMode = CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled")

    override fun update(deltaTime: Double) {

        closestPoint = closestWallPoint()
        when {
            closestPoint == null -> {
                findWall(deltaTime)
            }
            closestPoint!!.distance(car.middleX, car.middleY) > closestPoint!!.radius -> {
                findWall(deltaTime)
            }
            else -> {
                hugWall(closestPoint!!, deltaTime)
                passedFirst = true
            }
        }

        detectCrash()
        lastX = car.middleX.toInt()
        lastY = car.middleY.toInt()
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
                val prevColor = g.color
                if (closestPoint == p) {
                    g.color = Color.ORANGE
                }

                val s = (scale * 10).toInt()
                g.fillRoundRect((p.x * scale).toInt() - s / 2 + scaleX, (p.y * scale).toInt() - s / 2, s, s, s, s)

                val dist = (scale * p.radius * 2).toInt()
                g.drawRoundRect((p.x * scale).toInt() - dist / 2 + scaleX, (p.y * scale).toInt() - dist / 2, dist, dist,
                        dist, dist)

                if (closestPoint == p) {
                    g.color = prevColor
                }
            }
        }
    }

    private fun closestWallPoint(): BotPoint? {
        return crashPoints.filter {
            it.distance(car.middleX, car.middleY) < it.radius
        }.lastOrNull()
    }

    private fun findWall(deltaTime: Double) {
        if ((car.heading - initialHeading).absoluteValue < PI / 2 || passedFirst) {
            car.turnRight(deltaTime)
        }
        car.accelerate()
    }

    private fun hugWall(point: BotPoint, deltaTime: Double) {
        val headingToPoint = headingToPoint(point)
        car.turnLeft(deltaTime)
        if (headingToPoint < PI / 2) {
            car.turnLeft(deltaTime)
        } else {
            car.turnRight(deltaTime)
        }

        if (car.acceleration < 200) {
            car.accelerate()
        } else {
            car.brake()
        }

    }

    private fun headingToPoint(point: BotPoint): Double {
        return getPI(atan2(car.middleY - point.y, car.middleX - point.x) - (PI / 2) - getPI(car.heading))
    }

    private fun detectCrash() {
        val middleX = car.middleX
        val middleY = car.middleY

        if (Point.distance(middleX, middleY, lastX.toDouble(), lastY.toDouble()) > 100) {
            crashPoints.add(BotPoint(lastX, lastY, car.width.toDouble() * (if (crashPoints.isEmpty()) 2.0 else 0.6)))
            passedFirst = false
        }
    }
}