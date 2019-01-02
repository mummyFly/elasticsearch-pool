##ElasticSerach连接池

###2018年12月14日10:31:16
- 0.2.2
- 新增ElasticSearchClient,实现try-with-resource功能
- 重构ElasticSearchPool，修改返回值为ElasticSearchClient
- 重构ElasticSearchTemplate，改为builder模式查询参数

###2018年11月27日15:32:36
- 修改ElasticSearchPoolConfig中testOnBorrow不生效的问题，阅读源码可知，
BaseObjectPoolConfig中对该属性的get方法仍然是传统模式，idea自动生成
的get方法无法识别
- 修改创建连接池ElasticSearchPool时自动注入的两个属性，config和factory，
对象中不再保存两者依赖，直接注入到pool中，封闭外界对config和factory的
操作能力
- 修改对象有效性判断时间，现在设置其他三个参数均为false，依赖testWhileIdle:true
进行对象有效性判断

###2018年11月27日16:06:40
- 创建连接池，构造方法中加入双重锁机制，防止多例模式下的初始化遭遇并发问题
- 创建连接池时，预热连接池
- 增加默认集群选项

###2018年11月29日15:38:21
- 新增jsonobj，jsonarr，方便lowlevel客户端封装请求参数
- 新增querycount方法，利用lowlevel客户端实现
