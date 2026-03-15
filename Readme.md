用ai生成android音乐app。  
使用media3 mediasession架构，要求：
- 1 界面层：compose
- 2 状态管理：viewmodel+单项数据流
- 3 数据层：Flow
- 4 播放逻辑 业务逻辑 数据处理 和 界面分离解藕
- 5 继承MediaSessionService的类需要完善，添加注释。
- 6 播放控制类（封装成单例）全局使用。
- 7 支持本地+在线播放
- 8 页面路由
工具库：
- 1 dagger hilt
- 2 kotlin协程
- 3 网络与序列化 retrofit kotlin.serialization
- 4 本地数据存储 room

根据以上要求列出项目结构，生成核心代码和播放实例。

## bug
### mediastore 扫描歌曲扫不全，只能扫几首

## android 11-16 界面不适配，比如状态栏

ModalBottomSheet菜单。