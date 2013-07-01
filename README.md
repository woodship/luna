#eclipse环境搭建说明

1.该项目为MAVEN项目，因此需要安装maven插件

2.项目中引用到的*_文件，为JPA元模型（metamodel，相关概念请百度）,由maven命令根据法实体类生成，生成方法如下

    a.右键项目 Run as>>Maven clear
    
    b.右键项目 Run as>>Maven install
      
    c.右键target/generated-sources/annotations, build path>>Use as source folder

3.测试用户：

admin  

user

密码都是111，user可看到部分功能


#该项目的目的：

使能够在该项目基础上，快速进行业务开发。该项目应该是一个真正能使用的项目，不仅只是为了学习。


#初步需求暂定

1.人员管理<red>(已完成)</red>

2.机构管理<red>(已完成)</red>

3.权限管理<red>(已完成)</red>

4.工作流集成，如activiti

5.报表集成

#技术选型

vaadin + spring + shiro + hibernate + h2 +osgi + maven

欢迎感兴趣的人加入，更欢迎架构设计能力强，有开源项目经验的人加入，QQ群36377430。

===========================================

#参与开发方法

1.注册github帐号并登录

2.点击本页右上角的fork，创建一个分支到自己的帐户下

3.clone自己帐户下的分支到本地，进行修改，提交

4.在自己帐户点pull request提交自己的修改申请

5.管理员审核通过后会合并到源代码中

6.如何保持自己的fork与原始库的同步：再建立一个指向原始库的remote,然后fetch,merge


