package se.nocroft.model.drivers

import se.nocroft.model.cars.FragileCar

class GateBot(car: FragileCar?, trackName: String?) : Driver(car, trackName) {

    override fun update(deltaTime: Double) {
        //super.update(deltaTime)
        car.accelerate()
    }
}