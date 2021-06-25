# Wonder MC Auth Server

本项目是一个基于 [authlib-injector](https://github.com/yushijinhun/authlib-injector) 开发的 Minecraft Java Edition 外置登录系统。服务器采用 SpringBoot 开发，目前已完成大部分 [Yggdrasil API](https://github.com/yushijinhun/authlib-injector/wiki/Yggdrasil%20%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83) 的开发，可以正常认证登录。后期将继续补充用户管理，皮肤上传等功能。

## API 完成进度

-   [x] 所有模型的封装
-   [x] 认证
    -   [x] 登录（POST /authserver/authenticate）
    -   [x] 刷新（POST /authserver/refresh）
    -   [x] 验证令牌（POST /authserver/validate）
    -   [x] 吊销令牌（POST /authserver/invalidate）
    -   [x] 注销（POST /authserver/signout）
-   [ ] 会话
    -   [x] 客户端加入服务器（POST /sessionserver/session/minecraft/join）
    -   [x] 服务器验证客户端（GET /sessionserver/session/minecraft/hasJoined?username={username}&serverId={serverId}&ip={ip}）
-   [ ] 角色
    -   [ ] 服务器获取角色信息（GET /sessionserver/session/minecraft/profile/{uuid}?unsigned={unsigned}）
    -   [x] 批量查询角色（POST /api/profiles/minecraft）
-   [ ] 材质
    -   [ ] 上传材质（PUT /api/user/profile/{uuid}/{textureType}）
    -   [ ] 删除材质（DELETE /api/user/profile/{uuid}/{textureType}）
-   [x] 扩展 API
    -   [x] API 元数据（/）

> <br>由于暂未提供较为友好的设置界面（后期加入），就不介绍具体的部署方法了。在编译之前需要你根据实际情况完成 springboot 的配置文件。在 `conf` 目录中提供了项目使用的数据库结构。开发时使用的是 MySQL 数据库。这是一个 `maven` 项目，可以直接使用 `mvn package` 完成编译。得到的是适用于 tomcat 的 war 包，你当然也可以直接使用 `java -jar xxxx.war` 启动项目。由于项目只提供了 API，未提供配置界面。你可能需要在第一次启动项目后，在你配置的 `resource-dir` 目录中找到 `apimeta.json` 文件，对一些内容进行修改，并重启服务器，以保证服务器可以正常运行。<br><br>
> 项目还在积极开发中，目前还有很多问题需要解决。欢迎各位大大提交 PR 或各种建议。<br><br>
> 项目仅供学习研究，请勿用于商业用途，以免造成不必要的纠纷。<br><br>
