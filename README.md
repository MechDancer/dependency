# dependency
![Maven Central](https://img.shields.io/maven-central/v/org.mechdancer/dependency)

[Legacy README](README.legacy.md)

This library defines a series of interfaces that regulate cases of looking up global resources
dynamically and handling weak coupling components.

## Concepts

* `Component` is what can be found in scope, despite it is stateful or has dependencies.
* `Dependent` is a type of `Component`. It wants to find other components in the scope. When a new
  component is set up to the scope, dependents' `handle` will be called to get the information of
  this component, and the dependent may save the reference of that component.
* `DynamicScope` is the scope we talked in the two previous concepts. It resolves the dependency
  relationship between components.

## Example

There is an example that shows how this library can be used to build a robot hierarchy.

Imagine that we have a such definition of motors:

```kotlin
class Motor(name: String, inverse: Boolean) : NamedComponent<Motor>(name) {
    fun setPower(power: Double): Unit = {
        // ...
    }
}
```

`NamedComponent` is a type of `Component` which has a name. We can find it by name in the scope. A
motor can be set a power to run on. It also has a `name` and `inverse` which indicates that if the
direction of the motor should be inverted. This depends on how the motor was installed in real life.

Next, we have the definition encoders:

```kotlin
class Encoder(name: String, inverse: Boolean) : NamedComponent<Encoder>(name) {
    fun getPosition(): Double {
        //...
    }
    fun getSpeed(): Double {
        //...
    }
}
```

We can get the position and speed of an encoder. Similar to motors, encoders may need to `inverse`
as well. `Motor` and `Encoder` are real devices installed on our robot. In order to implement a
feedback control, we can assemble them together in a new structure:

```kotlin
class MotorWithEncoder(name: String) : Dependent,
    NamedComponent<MotorWithEncoder>(name), ManagedHandler by managedHandler() {

    private val pid = PID(/* args */)

    private val motor: Motor by manager.must(name)

    private val encoder: Encoder by manager.must(name)

    var targetPosition: Double = .0

    fun run() {
        val delta = targetPosition - encoder.getPosition()
        val output = pid.run(delta)
        motor.setPower(output)
    }
}
```

`Dependent` means this component depends on other components,
and `ManagedHandler by managedHandler()` creates a delegate that handles the dependencies. This is
the case that an encoder is installed with a motor, so we can know how much the motor run and
implement PID control. Two motors can driver a simple chassis:

```kotlin
class Chassis : Dependent, UniqueComponent<Chassis>(), ManagedHandler by managedHandler() {
    private val left: MotorWithEncoder by manager.must("left")
    private val right: MotorWithEncoder by manager.must("right")

    fun translateToPosition(position: Double) {
        left.targetPosition = position
        right.targetPosition = position
    }
}
```

`UniqueComponent` indicates that `Chassis` is a unique component in the scope. Also, it is
a `Dependent`, where we use `manager.must` to find `MotorWithEncoder` in scope. In addition, We have
a distance sensor:

```kotlin
class DistanceSensor : UniqueComponent<DistanceSensor>() {
    fun getDistanceToWall(): Double {
        // ...
    }
}
```

It is unique component, and can measure the distance to wall. A remote control can command our
robot:

```kotlin
class RemoteControl : Dependent, UniqueComponent<RemoteControl>(),
    ManagedHandler by managedHandler() {
    private val chassis: Chassis by manager.must()
    private val distanceSensor: DistanceSensor by manager.must()

    fun translateRobot(position: Double) {
        if (distanceSensor.getDistanceToWall() > position) {
            chassis.translateToPosition(position)
        }
    }
}
```

It depends on `Chassis` and `DistanceSensor`. Again, we use the same trick to handle dependencies.

Finally, our robot is basically a dynamic scope:

```kotlin
val robot = scope {
    fun setupMotorWithEncoder(name: String, inverse: Boolean) {
        setup(Motor(name, inverse))
        setup(Encoder(name, inverse))
        setup(MotorWithEncoder(name))
    }
    setupMotorWithEncoder("left", false)
    setupMotorWithEncoder("right", true)
    setup(DistanceSensor())
    setup(RemoteControl())
}
val remoteControl = robot.components.must<RemoteControl>().translateRobot(x)
```

We set up everything to the `DynamicScope`, and the scope will help us deal with all dependencies.
No references passed through constructors, nor worries about instantiation orders!

## Manager style

We have seen how the manager was used to declare dependency. `ManagedHandler by managedHandler()` is
trivial, and the following two code snippets are identical:

Manually:

```kotlin
class AAA : UniqueComponent<AAA>()
class BBB : Dependent, UniqueComponent<BBB>() {
    val manager = DependencyManager()
    val aaa: AAA by manager.must()
    override fun handle(dependency: Component): Boolean = manager.handle(dependency)
}
```

`managedHandler()`:

```kotlin
class AAA : UniqueComponent<AAA>()
class BBB : Dependent, UniqueComponent<BBB>(), ManagedHandler by managedHandler() {
    val aaa: AAA by manager.must()
}
```

## Annotation style

The library also provides an annotation style dependency injection:

```kotlin
class AAA : UniqueComponent<AAA>()
class CCC(name: String) : NamedComponent<CCC>(name)
class BBB : Dependent, UniqueComponent<BBB>() {

    @Must
    lateinit var aaa: AAA

    @Maybe
    @Name("ccc1")
    var ccc: CCC? = null

    @Must
    lateinit var ccc2: CCC

    private val injector by annotatedInjector()

    override fun handle(dependency: Component): Boolean = injector.handle(dependency)

}

scope {
    setup(AAA())
    setup(CCC("ccc1"))
    setup(CCC("ccc2"))
    setup(BBB())
}
```

We need use `annotatedInjector()` to create an injector for the dependent, and manually
delegate `hanlde` method to the injector. `@Must` declares a strict dependency with the type of the
field, and `@Maybe` declares a weak dependency. `@Name` can specify dependency's name. Field's name
will be used if no `@Name` annotated. 