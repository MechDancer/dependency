## 0.1.2

* Add `UniqueComponentWrapper` that wraps an object unique-like component

* Create interfaces `IUniqueComponent` and `INamedComonent` with default implementation, in case the
  class supposed to be a unique component or named component already has its super class

* Rework generic finding algorithm on `UniqueComponent`

* Fix the bug that property delegate created by `annotatedInjector()`
  instantiated `AnnotatedInjector` many times

* Support removing components from scope

## 0.1.1

* Initial release