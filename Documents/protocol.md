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
- 一般30s发一次，连续N(一般N=5)个收不到应重连，收到数据应把未收到心跳包个数清0重新计数

## 密钥协商过程和协议
- tcp连接建立成功后，会收到服务器端推送来的RSA公钥
cmd=1, isEncrypt=false, data=rsa pub key
- 客户端把自己的AES密钥用收到的RSA公钥加密，发送给server
cmd=2, isEncrypt=false, data=aes key
- 服务器端收到之后，用rsa private key解密出client aes key，保存在会话中，给客户端响应
cmd=2, isEncrypt=false, data=resp json
resp json格式为：{"code":code,"msg":msg}，code 100/200分别代表ok/解密异常
- 客户端收到响应后，若成功，进行auth(app为登录，设备为汇报自己信息)

## 设备认证
- 同一个id同时只能在同一个客户端auth,否则前面的会收到提示,提示cmd=103,无内容,然后被clsoe
- 请求：cmd=101, isEncrypt=true, data=req json
- req json:{"version":version,"id":id,"abilities": jsonArray}
- 响应: cmd=101, isEncrypt=false, data=resp json
- resp json：{"code":code,"msg":msg}
code 100/200分别代表ok/异常

## APP注册
- 请求：cmd=102, isEncrypt=true, data=req json
- req json:{"username":username,"password":password(进行md5)}
- 响应：cmd=102, isEncrypt=false, data=resp json
- resp json：{"code":code,"msg":msg}
code 100/200/1001分别代表ok/异常/用户已经存在

## APP登录
- 同一个id同时只能在同一个客户端login,否则前面的会收到提示,提示cmd=103,无内容,然后被clsoe
- 请求：cmd=100, isEncrypt=true, data=req json
- req json:{"version":version,username":username,"password":password(进行md5)}
- 响应: cmd=100, isEncrypt=false, data=resp json
- resp json：{"code":code,"msg":msg}
code 100/200/1002分别代表ok/异常/用户名或密码错误