### `Database design`

CREATE TABLE `t_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(64) DEFAULT NULL COMMENT '登录名',
  `password` varchar(256) DEFAULT NULL COMMENT '密码',
  `nick` varchar(64) DEFAULT NULL COMMENT '昵称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息';

CREATE TABLE `t_menu` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `route` varchar(64) DEFAULT NULL COMMENT '权限CODE',
  `logo` varchar(64) DEFAULT NULL COMMENT 'logo',
  `sort` int(11) DEFAULT NULL COMMENT '排序，从小到大',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单信息';

CREATE TABLE `t_user_menu` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `menu_id` int(11) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户菜单关系';

CREATE TABLE `t_excel_filter_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) DEFAULT NULL COMMENT '规则名称',
  `user_id` int(11) DEFAULT NULL COMMENT '归属人员',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='EXCEL过滤规则';

CREATE TABLE `t_excel_filter_rule_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_id` int(11) DEFAULT NULL COMMENT 't_excel_filter_rule表id',
  `left_bracket` varchar(64) DEFAULT NULL COMMENT '左括号',
  `col_num` int(11) DEFAULT NULL COMMENT '列数',
  `matched` smallint(6) DEFAULT NULL COMMENT '是否满足 0-不满足 1-满足',
  `regex` varchar(256) DEFAULT NULL COMMENT '值或正则表达式',
  `right_bracket` varchar(64) DEFAULT NULL COMMENT '右括号',
  `conj` varchar(8) DEFAULT NULL COMMENT '连接符 &-并且 |-或者',
  PRIMARY KEY (`id`),
  KEY `idx_rule_id` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='EXCEL过滤规则详细';


### `How to use lombok in idea`

Settings -> Plugins -> Lombok Plugin