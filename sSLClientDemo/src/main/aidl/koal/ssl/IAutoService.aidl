package koal.ssl;


interface IAutoService {

	/**
	获取应用
	返回值：
		应用名称1=监听地址:监听端口
		应用名称2=监听地址:监听端口
		...
		应用名称n=监听地址:监听端口
	*/
	String getApps();
	
	/**
	获取证书项信息（成功启动服务后才能获取）
	[IN]opt: 0=证书内容（Base64编码） 1=SN，2=CN，3=DN, 4=上海CA扩展
		DN中的对应项：
			"CN"	姓名
			"T" 	TF卡标识号
			"G"		警号
			"ALIAS"	身份证号码
			"S"		省
			"L"		市
			"O"		组织
			"OU"	机构
			"E"		电子邮件
			"I"		容器名称
	返回值：指定项信息 
	*/
	String getCertInfo(int opt);

	/**
	服务是否已启动
	返回值：true=已启动，false=未启动
	*/
	boolean isStarted();

	/**
	设置服务器地址
	[IN]ip： 服务器地址
	[IN]port：服务器端口
	*/
	void setServerAddr(String ip, String port);

	/**
	启动服务
	*/
	void start();

	/**
	停止服务
	*/
	void stop();

	/**
	自动升级
	*/
	void upgrade();
	
	/**
	获取应用服务状态
	[IN]appName： 应用服务名称（即本地IP，例如：127.0.0.1:10001）
	返回值：0 = 未链接，1=已链接
	*/
	int getAppStatus(String appName);

	/**
	设置PIN码
	[IN]pin： pin码
	[IN]save： 是否保存PIN码
	*/
	void setPin(String pin, boolean save);
	
	/**
	修改PIN码
	[IN]oldPin： 老pin码
	[IN]newPin： 新pin码
	*/
	boolean changePin(String oldPin, String newPin);
	
	/**
	设置服务检查间隔（秒）
	[IN]seconds： 间隔时间(秒)；0则不检查远端服务
	*/
	void setCheckAppInterval(int seconds);
		
	/**
	设置显示toast消息
	[IN]display： true=显示；false=不显示
	*/
	void setDisplayToast(boolean display);
	
	/**
	退出程序
	*/
	void quit();
	
	/**
	获取证书名称
	返回值：
		证书名称1
		证书名称2
		...
		证书名称n
	*/
	String getCerts();
	
	/**
	设置当前证书（默认为第一张证书）
	*/
	void setCurCert(String cert);
	
	/**
	使用PIN码检查设备
	*/
	void checkDevice(String pin);
	
	/**
	设置TF卡型号
	*/
	void setTFModel(String tf);
	
	/**
	检查证书状态（代理服务需要在服务端上配置检测服务，端口：54321）
	[IN]type: 0=代理服务
	返回值：
		0 = 成功
		1 = 找不到对应的CA证书
		2 = 无法对证书中的签名进行解密
		3 = 无法解密对应CA证书中的公钥
		4 = 证书签名错误
		5 = 证书有效期错误
		6 = 证书已经过期
		7 = 证书有效期的起始时间格式错误
		8 = 证书有效期的终止时间格式错误
		9 = 内存错误
		10 = 这是一张自签名证书
		11 = 证书链中存在自签名证书
		12 = 在本地无法获取对应的CA证书
		13 = 无法根据CA证书验证客户端证书
		14 = 证书链过长
		15 = 非法的CA证书
		16 = 非法的非CA证书(有CA标记)
		17 = 超出了路径的最长界限
		18 = 超出了代理路径的最长界限
		19 = 不允许使用代理证书
		20 = 不支持的证书用法
		21 = 证书不被信任
		22 = 证书被拒绝
		23 = 应用验证失败
		24 = 证书即将过期
		25 = 证书主题项不正确
		26 = 证书主题密钥标识不正确
		27 = 证书发行者序列号不正确
		28 = 该密钥不能作为CA签名密钥
		29 = 未知的关键扩展项
		30 = 该密钥不能作为签名密钥
		31 = 不正确或不完整的扩展项
		32 = 不正确或不完整的证书策略扩展项
		33 = 缺少严格的策略
		34 = 设备认证错误
		35 = 禁止应用程序访问
		500 = 代理服务未启动
		600 = 未知错误
	*/
	int checkCert(int type);
	
	/**
	获取配置信息
	[IN] key:配置键名
	当前提供的键名以及含义如下：
	"server_addr"  当前服务器IP地址 （即上一次成功连接的地址）
	"server_type"  当前服务器使用链路类型 （0:全部  1:WIFI 2:2G 3:3G 4:4G）
	"server_port"  当前服务器策略端口
	"server_addr1" 服务器配置1的IP地址
	"server_port1" 服务器配置1的策略端口
	"server_type1" 服务器配置1的链路类型（0:全部  1:WIFI 2:2G 3:3G 4:4G）
	"server_addr2" 服务器配置2的IP地址
	"server_port2" 服务器配置2的策略端口
	"server_type2" 服务器配置2的链路类型（0:全部  1:WIFI 2:2G 3:3G 4:4G）
	"server_addr3" 服务器配置3的IP地址
	"server_port3" 服务器配置3的策略端口
	"server_type3" 服务器配置3的链路类型（0:全部  1:WIFI 2:2G 3:3G 4:4G）
	"server_addr4" 服务器配置4的IP地址
	"server_port4" 服务器配置4的策略端口
	"server_type4" 服务器配置4的链路类型（0:全部  1:WIFI 2:2G 3:3G 4:4G）
	"connect_timeout" 连接超时时间
	"device"       加密设备类型
	"cert"         证书名称
	"rapid_access" 快速访问方式（-1: 进入访问列表   0:访问第1个B/S应用 .... 9:访问第10个B/S应用）
	"save_pin"     是否保存口令
	"auto_policy"  是否自动更新策略
	"policy_only_http" 是否仅在应用列表中显示B/S应用
	"auto_proxy"     是否开启透明模式
	"tunnel"		  是否开启隧道模式
	"check_app"      是否定时检查应用状态
	"check_app_interval"  检查应用间隔
	"display_notify" 是否显示通知
	"display_toast"  是否显示提示消息
	"notify_netstat" 网络不可用时是否发送通知
	"notify_appstat" 应用无法连接时是否发送通知
	"debug"          是否记录日志
	"log"			  是否记录代理服务日志
	"tun_log"        是否记录隧道服务日志
	"auto_start"     有网络链接时是否自动启动服务
	"ui_theme"       界面主题
	
	[OUT] 指定项信息 若搜索项不存在，则返回"" 或者"false"
	*/
	String getConfig(String key);

	/**
	获取手机，TF卡，证书信息
	[IN] key:配置键名
	当前提供的键名以及含义如下：
	"DEV_PHONE_IMEI"  手机的国际移动设备身份码
	"DEV_PHONE_IMSI"  国际移动用户识别码
	"DEV_TF_MODEL"    当前所选TF卡的名称 （"自动检测":没有选择TF卡类型）
	"DEV_TF_ID"       当前所选TF卡的ID （-:id不存在）
	"DEV_TF_CERT"     当前所选择的证书的名称（""：未选择证书）
	
	[OUT] 指定项信息，若搜索项不存在，则返回"-" 或者""
	*/
	String getDevInfo(String key);

	/**
	获取服务的相关信息。
	[IN] key:配置键名
	当前提供的键名以及含义如下：
	"SERVICE_VPN_STATE"  隧道的当前状态如"未启动"。
	"SERVICE_PROXY_STATE" 代理的当前状态如"未启动"
	"SERVICE_TUN_IP"  虚拟网卡所分配的ipv4地址（null:未分配）。
	"SERVICE_TUN_NETMASK" 分配的ip地址的子网掩码的长度（null:未分配）。
	"SERVICE_TUN_MTU" 取得MTU值（0:服务未建立时）。
	"SERVICE_TUN_DNS" 分配的DNS的ip地址，多个ip之间用","分开（null:未分配）。
	"SERVICE_TUN_ROUTES" 分配的ipv4的路由，多个路由之间","分开如：192.168.1.8/24,192.168.1.9/24（null:未分配）。
	"SERVICE_PROXY_LOG" 代理运行过程产生的日志文件的存放位置如：/storage/sdcard1/Android/data/koal.ssl/proxy_log.txt。
	"SERVICE_TUN_LOG" 隧道运行过程产生的日志文件的存放位置如：/storage/sdcard1/Android/data/koal.ssl/tunnel_log.txt。
	"SERVICE_VPN_CONF_PROTO" 隧道所采用的协议，如UDP。
	"SERVICE_VPN_CONF_REMOTE" 隧道服务端的IP地址
	"SERVICE_VPN_CONF_RPORT" 隧道服务端所采用的端口号。
	[OUT] 指定项信息，若搜索项不存在，则返回，"0"或者null
	*/
	String getServiceInfo(String key);
}
