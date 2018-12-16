### `Database design`

CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(64) DEFAULT NULL COMMENT '登录名',
  `password` varchar(256) DEFAULT NULL COMMENT '密码',
  `nick` varchar(64) DEFAULT NULL COMMENT '昵称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户信息'



### `How to use lombok in idea`

Settings -> Plugins -> Lombok Plugin