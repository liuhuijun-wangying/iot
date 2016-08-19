# 客户端
- 建立tcp连接后，读写idle后(idle时间一般为30s)，需要发送心跳包
- 通用协议格式为 |--head--| |--data--|,head为4byte，代表data的长度，这个长度不包括head自己
- data的格式为 |--cmd--| |--msg id--| |--compress type--| |--is encrypt--| |--msg--|。
cmd为2byte;msg id为8byte，不需要msg id可以设置为0 or -1,所以需要的时候从1开始；compress type为1byte， 0/1/2分别代表不压缩/gzip/zip;
is encrypt代表是否为data加密，1byte，1加密0不加密；msg是真正的要发送的数据
- 说明：一般不需要开启压缩，保留此功能即可；服务器响应的msg id和发送的msg id是一样的

## 心跳包
- |--0--| |--0--| |--none--| |--false--| |--null--|

## 密钥协商过程和协议
- tcp连接建立成功后，会收到服务器端推送来的RSA公钥
|--1--| |--0--| |--none--| |--false--| |--rsa pub key--|
- 客户端把自己的AES密钥用收到的RSA公钥加密，发送给server
|--2--| |--msgid--| |--none--| |--false--| |--aes key--|
- 服务器端收到之后，用rsa private key解密出client aes key，保存在会话中，给客户端响应
|--2--| |--msgid--| |--none--| |--false--| |--resp--|
resp是返回的状态,为json，格式为：{"code":code,"msg":msg}，code 100/200/300分别代表ok/key为空/解密异常
- 客户端收到响应后，若成功，进行认证(app为登录，设备为汇报自己信息)

## APP注册账号
- 请求：|--102--| |--msgid--| |--none--| |--true--| |--json--|
- 请求json:{"username":username,"password":password(进行md5)}
- 响应：|--102--| |--msgid--| |--none--| |--false--| |--json--|
- 响应json：{"code":code,"msg":msg}
code 100/200/300/1001分别代表ok/信息为空/异常/用户已经存在

## APP登录
- 请求：|--100--| |--msgid--| |--none--| |--true--| |--json--|
- 请求json:{"version":version,"id":id,username":username,"password":password(进行md5)}
- 响应: |--100--| |--msgid--| |--none--| |--false--| |--json--|
- 响应json：{"code":code,"msg":msg}
code 100/200/300/1002分别代表ok/信息为空/异常/用户名或密码错误

## 设备认证
- 请求：|--101--| |--msgid--| |--none--| |--true--| |--json--|
- 请求json:{"version":version,"id":id,"abilities": jsonArray}
- 响应: |--101--| |--msgid--| |--none--| |--false--| |--json--|
- 响应json：{"code":code,"msg":msg}
code 100/200/300分别代表ok/信息为空/异常
