package se.nocroft.model.drivers

import se.nocroft.model.cars.FragileCar
import se.nocroft.model.drivers.util.BotPoint
import java.awt.Point

class GateBot(car: FragileCar?, trackName: String?) : Driver(car, trackName) {

    private var lastX: Int = 0
    private var lastY: Int = 0
    private val crashPoints = mutableListOf<BotPoint>()

    override fun update(deltaTime: Double) {

        car.accelerate()
        detectCrash()
    }

    private fun detectCrash() {
        val middleX = car.getMiddleX(car.x.toDouble())
        val middleY = car.getMiddleY(car.y.toDouble())
        if (Point.distance(middleX, middleY, lastX.toDouble(), lastY.toDouble()) > 10) {
            crashPoints.add(BotPoint(middleX.toInt(), middleY.toInt()))
        }
    }
}