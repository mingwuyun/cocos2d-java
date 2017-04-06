# cocos2d-java
cocos2d java api base libgdx

# 特别说明
作者最近用unity去了，暂时不更新了
------------------------------

# 环境部署
目录结构调整：<br>
src/ —— 引擎代码 <br>
platform/{platform}/src —— 引擎与平台相关的代码，各平台对应的ApplicationStartup <br>
tests/src —— 测试工程的代码 <br>
tests/src-{platform} —— 测试工程在指定平台上的启动代码 <br>
({platform} = desktop,android)  <br>

## 桌面版本运行
source: 导入src/,platform/desktop/src,tests/src,tests/src-desktop；运行位于src-desktop中的Main启动函数 <br>
libs: 导入libs/下jar包，所有platform/desktop中的jar包 <br>

## android版本运行
source: 导入src/,platform/android/src,tests/src,tests/src-android; 继承Activity启动类或者直接设置为mainActivity启动； <br>
libs: 导入libs/下jar包，所有platform/android中的jar包，导入所有会指定平台的 **/*.so文件夹 <br>

# 模块系统
Cocos2dJava引擎自带了一套模块系统，可以方便的进行扩展。模块系统位于src/com/cocos2dj/module/路径下。引擎自带的模块源码在该目录下面；扩展模块的相关代码在external/com/cocos2dj/module/路径下。 <br>
当前引擎自带模块：gdxui/base2d/typefactory <br>
扩展模块：visui/btree(behaviorTree)/box2d(未完成)/spine（计划中） <br>

模块系统已经集成到了s2d/scene中，直接在scene的onEnter方法或者回调中添加模块即可，切换场景时引擎负责清理模块。 <br>
详细用法之后我会在wiki中补充。 <br>

## gdxui
该模块封装了gdx的scene2d相关内容。可以直接应用libgdx提供的组件。除此之外，引擎提供了两个方便调试的组件：GdxUIConsole和GdxUIDebugInfo。console添加后可以按下~按键呼叫。具体的用法在tests/TestAppDelegate_GdxUI中有例子 <br>

## base2d
该模块是系统默认的物理引擎。与box2d不同，该物理引擎基本不提供物理模拟功能，可以方便的制作平台类游戏。我在tests/testcase/Base2dTests中添加了几个例子。分别是：使用node的action驱动物理引擎；地面判定以及斜面跳跃解决方案；碰撞监听和对象休眠。该引擎适合非物理类但需要碰撞检测的游戏。

## TypeFactory
该模块是系统提供的原生对象池管理类。NodeType对象定义了一个对象池，调用getInstance()从池中回去对象，所有的node都可以调用pushBack将对象放回对象池中。该机制的使用在tests/testcase/TypeFactoryTests中有演示

