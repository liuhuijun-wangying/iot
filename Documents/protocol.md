# 客户端
- 建立tcp连接后，读写idle后(idle时间一般为30s)，需要发送心跳包
- 通用协议格式为 |--head--| |--data--|,head为4byte，代表data的长度，这个长度不包括head自己
- data为protobuf/BaseMsgPb
- 说明：服务器响应的cmd/msgId和发送的cmd/msgId是一样的,

# 获取服务器地址
- 由于存在多台tcp server，所以先通过http get请求server地址
- 请求 http://ip:port/?id=xx
- 响应json,{"code":code,"msg":msg}，code 100/200/1000分别代表ok/exception/no available server，
当code=100时，带有server信息，{"ip":ip,"port":port}

## 心跳包
- cmd=0
- 一般在无数据读写(idle)之后的30s发一次，连续N(一般N=4)个收不到重连，收到数据应把未收到心跳包个数清0重新计数

## 服务器内部错误
- 会收到cmd=3, data=json：{"code":200,"msg":err msg}

## 密钥协商过程和协议
- tcp连接建立成功后，会收到服务器端推送来的RSA公钥,cmd=1, data=rsa pub key(byte[])
- 客户端把自己的AES密钥用收到的RSA公钥加密，发送给server,cmd=2, data=aes key(byte[])
- 服务器端收到之后，用rsa private key解密出client aes key，保存在会话中，给客户端响应,
cmd=2, data=json：{"code":code,"msg":msg}，code 100/200分别代表ok/解密异常
- 客户端收到响应后，若成功，进行auth(app为登录，设备为汇报自己信息)

## APP注册
- 请求：cmd=102, data=json:{"username":username,"password":password(进行md5)}
- 响应：cmd=102, data=json：{"code":code,"msg":msg},code 100/200/1001分别代表ok/异常/用户已经存在

## 设备认证
- 同一个id同时只能在同一个客户端auth,,见多点登录冲突
- 请求：cmd=101, data=json:{"version":version,"id":id,"abilities": jsonArray}
- 响应: cmd=101, data=json：{"code":code,"msg":msg},code 100/200分别代表ok/异常

## APP登录
- 同一个id同时只能在同一个客户端login,见多点登录冲突
- 请求：cmd=100, data=json:{"version":version,username":username,"password":password(进行md5)}
- 响应: cmd=100, data=json：{"code":code,"msg":msg},code 100/200/1002分别代表ok/异常/用户名或密码错误

## 多点登录冲突
- 会收到cmd=103，此时客户端应关闭连接并提示用户

## APP退出
- 请求 cmd=104，无响应

***
以下的业务，都需要登录，若没有登录，都会返回一个通用错误码300

## 添加设备
- 请求：cmd=203, data=json:{"deviceId":要添加的设备的id}
- 响应: cmd=203, data=json：{"code":code,"msg":msg},code 100/200/2001分别代表ok/异常/已经添加过

## 删除设备
- 请求：cmd=204, data=json:{"deviceId":要删除的设备的id}
- 响应: cmd=204, data=json：{"code":code,"msg":msg},code 100/200/2002分别代表ok/异常/未添加过此设备

## 获取离线消息
- 请求：cmd=202，登录后立即发送即可
- 响应：见收到消息推送

## 发送im消息
- 请求：cmd=200，msgid=uuid, data=json:{"to":要发送给的对象的deviceid/username，"msg":msg}
发送之后，需要以msgid为key，这个json为value，存在客户端本地的map中，收到server端ack或者超时未收到ack后，从map移除，并对用户做相应提示
- 响应：cmd=200，收到的msgid和发送的是一样的

## 收到消息推送
- 会收到cmd=201,data=jsonarray,每一个jsonarray里面是jsonobject：{"from":from,"to":to,msg":msg,"msgid":msgid}
,客户端收到之后,应向server回应ack
- 回应给server：cmd=201，data=jsonarray,每一个jsonarray里面是jsonobject：{"msgid":msgid}
