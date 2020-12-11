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
import kotlin.math.max

class HugBotV2(car: FragileCar, trackName: String?) : Driver(car, trackName) {

    private class HugBotPoint(x: Int, y: Int, radius: kotlin.Double) : BotPoint(x, y, radius) {
        var speed: Int = 300;
    }

    private var lastX: Int = car.middleX.toInt() ?: 0
    private var lastY: Int = car.middleY.toInt() ?: 0
    private var closestPoint: HugBotPoint? = null
    private var passedFirst = false
    private var deathsInsideWall = 0

    private val initialHeading: Double = car.heading
    private val crashPoints = mutableListOf<HugBotPoint>()
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

    private fun closestWallPoint(): HugBotPoint? {
        return crashPoints.filter {
            it.distance(car.middleX, car.middleY) < it.radius
        }.minBy { it.distance(car.middleX, car.middleY) }
    }

    private fun findWall(deltaTime: Double) {
        if ((car.heading - initialHeading).absoluteValue < PI / 2 || passedFirst) {
            car.turnRight(deltaTime)
        }
        if (car.acceleration < 200) {
            car.accelerate()
        } else {
            car.brake()
        }
    }

    private fun hugWall(point: HugBotPoint, deltaTime: Double) {
        val headingToPoint = headingToPoint(point)
        val multiplier = max(10 - deathsInsideWall, 0)
        if (headingToPoint < PI / 2) {
            car.turnLeft(deltaTime)
        } else if (headingToPoint > PI / 2 && point.distance(car.middleX, car.middleY) > point.radius * 0.9) {
            car.turnRight(deltaTime)
        }

        if (car.acceleration < point.speed) {
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
            val possibleExistingPoint = crashPoints.find {
                it.distance(lastX.toDouble(), lastY.toDouble()) < it.radius * 0.8
            }?.apply {
                speed = max(speed - 50, 50)
                radius *= 1.1
            }

            // Only add new point if not crashing into another one
            if (possibleExistingPoint == null) {
                crashPoints.add(HugBotPoint(lastX, lastY, car.width.toDouble() * (if (crashPoints.isEmpty()) 1.0 else 0.6)))
            }
            passedFirst = false

        }
    }
}