package se.nocroft.model.cars;

import se.nocroft.model.drivers.Driver;

public class CarSetup {
    public final Car.Cars type;
    public final Class<? extends Driver> driver;

    public CarSetup(Car.Cars type, Class<? extends Driver> driver) {
        this.type = type;
        this.driver = driver;
    }
}
