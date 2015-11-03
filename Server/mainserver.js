var tcpPort=8124;
var webPort=80;
var serviceModule=require('./service.js');
var webModule=require('./web.js');
var net=require('net');
var mysql=require('mysql');
var http=require('http');
var fs=require('fs');
var path=require('path');
var io=require('socket.io');
var events=new process.EventEmitter();
GLOBAL.events=events;
//var dbdb=mysql.createClient({ // 시발 mysql 버전이 최신이면 밑에걸로 해줘야돌아감 npm으로깔면 이런다
//        user: 'root',
//        password: 'qwer1234',
//        database: 'managed'
//});
var dbdbconfig={
	user: 'root',
        password: 'qwer1234',
        database: 'managed'
};
var dbdb=mysql.createConnection(dbdbconfig);
var clients=[];
var logined=[];
var tcpSocket;

var server=net.createServer(function(e){
        tcpSocket=e; // client socket
        GLOBAL.tcpSocket=e;

        tcpSocket.setEncoding("utf8");

//      console.log('webserver ?: ' + webserver);
//      webserver.emit('message','surprise');
//      console.log('GLOBAL.events: ' + GLOBAL.events);
//      console.log('events: ' + events);
//      if(GLOBAL.events==events)
//              console.log('same events');
        serviceModule.service(tcpSocket,dbdb,clients,logined,GLOBAL.events);

});

server.listen(tcpPort,function(){
        console.log('server on');
//      console.log('in server listen, tcpSocket: ' + tcpSocket);

        // db connection
        dbdb.query('USE managed',function(error, results){
                if(error){
                        console.log('db connection failed: ' + error);
                        return;
                }
                console.log('db connection success!');
        });

        events.on('toserver',function(msg){
                console.log('events in server msg.cmd: ' + msg.cmd);
                var packet;
                var jsonStr;
                var flag=0;

                switch(msg.cmd){
                        case 'classstart':
                                console.log('classstart in events');
                                packet=JSON.stringify(msg);
                                flag=1;
                                break;
                       case 'classend':
                                console.log('classend in events');
                                packet=JSON.stringify(msg);
                                flag=1;
                                break;
                        case 'surprise':
                                console.log('surprise in events');
                             //   GLOBAL.surnumber=msg.surprise;
                        //      console.log('GLOBAL.surnumber: ' + GLOBAL.surnumber);
                              //  jsonStr={'cmd':'surprise'}; // 웹서버에 가나 >임시 체크용
                                packet=JSON.stringify(msg);
                                console.log('surprise msg.cmd: ' + msg.cmd);
                                flag=1;
                                break;
                        default:
                                console.error('cmd error in events service');
                                break;
                }
                if(flag==1){
                        console.log('apps broadcasting in event service');
                        var sender=this;
                        logined.forEach(function(loginuser){
                                if(loginuser.socket!=sender && loginuser.socket!=null){
                                        try{
                                                loginuser.socket.write(packet + "\n");
                                                console.log('broadcasting send');
                                        }catch(e){
                                                console.log('events exception: ' + e);
                                        }
                                }

                        });
                }
                else if(flag==3){ // to webserver send jsonStr
                        events.emit('towebserver',jsonStr);
                        console.log('send to webserver');
                }
                flag=0;
        });


});

var webserver=http.createServer(function(req, res){
        var filename=path.join(process.cwd(),req.url);
        GLOBAL.webServersocket=webserver;
//      console.log(GLOBAL.webServersocket);

        path.exists(filename,function(exists){
                if(!exists){
                        res.writeHead(404,{"Content-Type":"text/plain"});
                        res.write("404 Not Found\n");
                        res.end();
                        return;
                }

                fs.readFile('./quiz2.html'
                        ,encoding='utf-8'
                        ,function(err,data){
                        if(err){
                                res.writeHead(500,{"Content-Type":"text/plain"});
                                res.write(err + "\n");
                                res.end();
                                return;
                        }
                        res.writeHead(200,{
                                "Content-Type":"text/html; charset=utf-8"
                        });
                        res.end(data);
                });

        });

//      console.log('dbdb in web: ' + dbdb);
//      console.log('webModule webserver: ' + webserver);
//      console.log('webModule server: ' + server);
//      console.log('webModule GLOBAL.tcpSocket: ' + GLOBAL.tcpSocket);
//      console.log('webModule tcpSocket: ' + tcpSocket);
//      console.log('webModule logined: ' + logined);
//      console.log('webModule io: ' + io);

        webModule.service(webserver,dbdb,events,logined,io);
});

webserver.listen(webPort,function(){
        console.log('webserver start');
//      console.log('GLOBAL.webclient: ' + GLOBAL.webclient);
});

