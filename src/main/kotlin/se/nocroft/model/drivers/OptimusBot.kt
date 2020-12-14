package se.nocroft.model.drivers

import se.nocroft.model.cars.FragileCar
import se.nocroft.model.drivers.util.BotPoint
import se.nocroft.util.CfgParser
import se.nocroft.util.Geom
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import kotlin.math.*
import kotlin.random.Random

class OptimusBot(car: FragileCar, trackName: String?) : Driver(car, trackName) {

    private inner class TapePoint(x: Int, y: Int, val angle: kotlin.Double = 0.0) : BotPoint(x, y, 30.0) {
        var confirmed: Boolean = false

        constructor(prev: TapePoint, angle: kotlin.Double = 0.0) : this(
                prev.x + ((prev.radius * 2) * sin(prev.angle + angle)).toInt(),
                prev.y + ((prev.radius * 2) * -cos(prev.angle + angle)).toInt(),
                angle
        )
    }

    private var lastX: Int = car.middleX.toInt() ?: 0
    private var lastY: Int = car.middleY.toInt() ?: 0
    private var tape = mutableListOf<TapePoint>()

    private val crashPoints = mutableListOf<BotPoint>()
    private val debugMode = CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled")

    override fun update(deltaTime: Double) {

        if (car.acceleration < 100) {
            car.accelerate()
        } else {
            car.brake()
        }

        followTape(deltaTime)

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
                val s = (scale * 10).toInt()
                g.fillRoundRect((p.x * scale).toInt() - s / 2 + scaleX, (p.y * scale).toInt() - s / 2, s, s, s, s)

                val dist = (scale * p.radius * 2).toInt()
                g.drawRoundRect((p.x * scale).toInt() - dist / 2 + scaleX, (p.y * scale).toInt() - dist / 2, dist, dist,
                        dist, dist)
            }

            for (p in tape) {
                val s = (scale * 10).toInt()
                g.fillRoundRect((p.x * scale).toInt() - s / 2 + scaleX, (p.y * scale).toInt() - s / 2, s, s, s, s)

                val dist = (scale * p.radius * 2).toInt()
                g.drawRoundRect((p.x * scale).toInt() - dist / 2 + scaleX, (p.y * scale).toInt() - dist / 2, dist, dist,
                        dist, dist)
            }
        }
    }

    private fun followTape(deltaTime: Double) {
        val leftX = car.getRelX(0.0, 0.0)
        val leftY = car.getRelY(0.0, 0.0)
        val rightX = car.getRelX(car.width.toDouble(), 0.0)
        val rightY = car.getRelY(car.width.toDouble(), 0.0)
        val tapeLeft = onTape(leftX, leftY)
        val tapeRight = onTape(rightX, rightY)

        tapeLeft?.confirmed = true
        tapeRight?.confirmed = true

        when {
            tapeRight != null && tapeLeft == null -> {
                car.turnRight(deltaTime)
            }
            tapeLeft != null && tapeRight == null -> {
                car.turnLeft(deltaTime)
            }
            else -> {

            }
        }
    }

    private fun onTape(x: Int, y: Int): TapePoint? {
        return tape.find { it.distance(x.toDouble(), y.toDouble()) < it.radius }
    }

    private fun predictTape() {
        for (i in 0..200) {

            val randomAngle = Random.nextDouble(-PI / 4, PI / 4)

            val steps = 20
            val step = (PI / 2) / steps
            val angles = (0..steps).map { (PI / 4) - it * step }
            val angle = angles.maxBy { distToClosestCrashByAngle(it) } ?: 0.0

            val newPiece = TapePoint(tape.last(), angle)
            tape.add(newPiece)
        }
    }

    private fun collidesWithCrashPoint(tapePoint: TapePoint): Boolean {
        return crashPoints.last().let { it.distance(tapePoint) < tapePoint.radius + it.radius }
    }

    private fun distToClosestCrashByAngle(angle: Double): Double {
        return crashPoints.map { it.distance(TapePoint(tape.last(), angle)) - it.radius }.min() ?: Double.MAX_VALUE
    }

    private fun distToThreeClosestCrashesByAngle(angle: Double): Double {
        return crashPoints.map { it.distance(TapePoint(tape.last(), angle)) - it.radius }.sorted().take(3).sum() ?: Double.MAX_VALUE
    }

    private fun detectCrash() {
        val middleX = car.middleX
        val middleY = car.middleY

        if (Point.distance(middleX, middleY, lastX.toDouble(), lastY.toDouble()) > 20) {
            crashPoints.add(BotPoint(lastX, lastY, car.width.toDouble() * 0.6))

            tape.clear()
            tape.add(TapePoint(middleX.toInt(), middleY.toInt()).apply { confirmed = true })
            tape.add(TapePoint(tape.last(), PI / 4))
            tape.add(TapePoint(tape.last()))

            predictTape()
        }
    }

    private fun headingToPoint(pointA: BotPoint, pointB: BotPoint): Double {
        return Geom.getPI(atan2((pointA.y - pointB.y).toDouble(), (pointA.x - pointB.x).toDouble()) - (PI / 2))
    }
}