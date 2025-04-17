# 使用Docker部署Apache Doris

最近一个项目使用了Aapache Doris作为数据仓库，需要在本地搭建测试环境进行开发，按照官方文档部署的时候遇到了一些问题，我做了一些修改。


本次搭建选用的是2.0.9版本，相关文件已上传到github。


## 构建FE镜像

从 https://apache-doris-releases.oss-accelerate.aliyuncs.com/apache-doris-2.1.9-bin-arm64.tar.gz 下载doris二进制包保存到 docker-build/fe/resource 目录下，Architecture 根据需要选择，我这里使用的是 x64(avx2)。



执行如下脚本：

```shell
cd docker-build/fe
docker build -t apache-doris:2.1.9-fe .
```

## 构建BE镜像


将之前下载的doris二进制包保存到 docker-build/be/resource 目录下。

执行如下脚本：

```shell
cd docker-build/be
docker build -t apache-doris:2.1.9-be .
```

官方的init_be.sh脚本有一些问题，无法正常启动，我做了一些修改。

start_be.sh也做了修改，不再需要关闭swap memory。

## 启动

创建一个doris安装目录用于保存doris的数据，然后执行如下脚本：

```shell
cd /path/to/doris
docker-compose up -d
```

使用 docker-compose logs -f be 观察日志，当看到“Ready to start BE！”日志后，说明启动成功。

## 连接doris

doris默认自带两个账号，root和admin，密码都是空。其中 root 拥有整个集群的超级权限，可以对集群完成各种管理操作，比如添加节点，去除节点。admin 用户没有管理权限，是集群中的 Superuser，拥有除集群管理相关以外的所有权限。

使用mysql客户端链接：

```shell
mysql -uroot -P9031 -h127.0.0.1
```

Doris 内置的 Web UI：http://127.0.0.1:8031

## 关闭

```shell
docker-compose down
```
