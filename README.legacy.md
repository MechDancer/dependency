# 依赖管理器

[ ![Download](https://api.bintray.com/packages/mechdancer/maven/dependency/images/download.svg) ](https://bintray.com/mechdancer/maven/dependency/_latestVersion)

本项目是对项目开发中常见的 *动态查找程序组件* 的抽象。通过定义一系列接口，规范了开发者对相互依赖的项目组件（全局资源和功能模块）进行弱耦合定义的操作。一些算法被用于支持开发者并发地将 **组件** `Component` **装载** `setup` 到到 **域** `DynamicScope` 中，并在装载时确定组件依赖关系。

这个项目非常小，核心只包含 3 个重要的类，总代码量不到 200 行。

## 核心定义

* 组件 `Component`

  所有的能从域中查找的东西，无论是否有状态或是否有依赖，都被归结为组件。这个接口除了标记作用，还声明了解决依赖冲突的方法。

  ```kotlin
  interface Component {
      override fun equals(other: Any?): Boolean
      override fun hashCode(): Int
  }
  ```

  所有依赖项会被保存在域中的一个哈希集合中，而集合中不会包含两个相等的元素。这样，通过重载 `equals` ，组件就可以管理自身的冲突问题。

* 依赖者 `Dependent`

  依赖者是希望找到域中其他组件的一类组件。

  ```kotlin
  interface Dependent : Component {
      fun sync(dependency: Component): Boolean
  }
  ```

  当一个新的组件被装载到动态域，所有尚未找齐依赖项的依赖者的 `sync` 方法会被调用，这时每个依赖者都可以检查新的组件是否自身的依赖项、保存依赖项的引用或利用其中的一些信息，并返回自身是否还关心以后到来的其他组件。

* 动态域

  动态域是一个可继承的类，实现了上述的方法，当新组件到来时，更新组件表并通知依赖者更新依赖关系。

  ```kotlin
  open class DynamicScope {
      //组件集
      //  用于查找特定组件类型和判定类型冲突
      //  其中的组件只增加不减少
      private val _components = ConcurrentHashSet<Component>()
  
      //依赖者列表
      //  用于在在新的依赖项到来时接收通知
      //  其中的组件一旦集齐依赖项就会离开列表，不再接收通知
      private val dependents = mutableListOf<(Component) -> Boolean>()
  
      /** 浏览所有组件 */
      val components = _components.view
  
      /**
       * 将一个新的组件加入到动态域，返回是否成功添加
       * @return 若组件被添加到域，返回`true`
       *         与已有的组件发生冲突时返回`false`
       */
      open infix fun setup(component: Component) =
          _components
              .add(component)
              .also {
                  // 更新依赖关系
                  if (it) synchronized(dependents) {
                      dependents.removeIf { it(component) }
  
                      if (component is Dependent)
                          component::sync
                              .takeIf { sync -> _components.none(sync) }
                              ?.let(dependents::add)
                  }
              }
      
      // ...
  }
  ```
## 开始使用

* Gradle
* Maven
* Bintray

您需要将其添加至  [仓库和依赖](https://docs.gradle.org/current/userguide/declaring_dependencies.html) 中。

### Gradle

```groovy
repositories {
    jcenter()
}
dependencies {
    compile 'org.mechdancer:dependency:0.1.0-rc-3'
}
```

### Maven

```xml
<repositories>
   <repository>
     <id>jcenter</id>
     <name>JCenter</name>
     <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependency>
  <groupId>org.mechdancer</groupId>
  <artifactId>dependency</artifactId>
  <version>0.1.0-rc-3</version>
  <type>pom</type>
</dependency>
```

### Bintray

您总可以从 bintray 直接下载 jar： [![Download](https://api.bintray.com/packages/mechdancer/maven/dependency/images/download.svg)](https://bintray.com/mechdancer/maven/dependency/_latestVersion)