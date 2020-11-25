package se.nocroft.model.cars;

public class CarSetup {
    public final Car.Cars type;
    public final Class driver;

    public CarSetup(Car.Cars type, Class driver) {
        this.type = type;
        this.driver = driver;
    }

    public Car.Cars getType() {
        return type;
    }

    public Class getDriver() {
        return driver;
    }
}
