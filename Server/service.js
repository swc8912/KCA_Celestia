exports.service=function(socket,dbdb,clients,logined,events){
        console.log('tcpSocket==socket, socket: ' + socket);

        socket.name=socket.remoteAddress + ":" + socket.remotePort;

        socket.on('connect', function(){
                console.log('%s is connected',socket.name);
                clients.push(socket);
        });

        socket.on('end', function(){
                // logout socket delete
                console.log('logined.length: ' + logined.length);
                for(var i=0; i<logined.length; i++){
                        //console.log('in end  loop');
                        if(socket==clients[i]){
                                clients=clients.splice(i,1);
                                console.log('clients spliced');
                        }
                        if(socket==logined[i].socket){
                                logined=logined.splice(i,1);
                                console.log('logined spliced');
                        }
                }
                socket.end();
                console.log('logined.length in end: ' + logined.length);
                console.log('socket closed');
        });

        socket.on('uncaughtException', function(error){
                console.log('exception: ' + error);
        });

        socket.on('error', function(error){
                console.log('error: ' + error);
        });

        socket.on('data', function(data){
                console.log('data event: ' + data);
                var msg=JSON.parse(data);
                var packet;
                var jsonStr;
                var flag=-1;

                switch(msg.cmd){
                        case 'login':
                                console.log('login req');
                                // client ip check
                        //      CheckClinetIP(dbdb,msg.regnum,msg.passwd);
                                CheckLoginuser(dbdb,msg.regnum,msg.passwd);
                                break;
                        case 'logout':
                                console.log('logout req');
                                for(var i=0; i<logined.length; i++){
                                        console.log('in end  loop');
                                        if(socket==logined[i].socket){
                                                logined=logined.splice(i,1);
                                                console.log('logined spliced');
                                        }
                                }
                                break;
                        case 'location': // qrcode
                                console.log('loc req');
                                CheckLocation(dbdb,msg.qrcode);
                                break;
                        case 'info':
                                console.log('info req');
                                InfoOfStudent(dbdb,msg.regnum);
                                break;
                        case 'surprise': // from app
                                console.log('surprise req');
                                console.log('from app, msg.surprise: ' + msg.surprise);
                                packet=JSON.stringify(msg);
                                break;
                        case 'surpriseok': // from app
                        //      console.log('GLOBAL.surnumber: ' + GLOBAL.surnumber);
                                console.log('msg.surpirse: ' + msg.surprise);
                        //      if(msg.surprise==surnumber && surnumber!=0){
                        //              console.log('surpriseok');
                        //              jsonStr={'cmd':'surpriseok','regnum':msg.regnum};
                        //      }
                        //      else{
                        //              console.log('surprisefail');
                        //              jsonStr={'cmd':'surprisefail','regnum':msg.regnum};
                        //      }
                                packet=JSON.stringify(msg);
                                flag=2;
                                break;
                        case 'surprisefail':
                                packet=JSON.stringify(msg);
                                flag=2;
                                break;
                        case 'force': // to webserver
                                console.log('force req');
                                jsonStr={'cmd':'force','regnum':msg.regnum};
                                packet=JSON.stringify(jsonStr);
                                flag=2;
                                break;
                        case 'forceok': // to app
                                console.log('forceok');
                                jsonStr={'cmd':'forceok','regnum':msg.regnum};  
                                packet=JSON.stringify(jsonStr);
                                flag=0;
                                break;
                        case 'forcefail': // to app
                                console.log('forcefail');
                                jsonStr={'cmd':'forcefail','regnum':msg.regnum};
                                packet=JSON.stringify(jsonStr);
                                flag=0;
                                break;
                        case 'live':
                                console.log('live ');
                                break;

                        default:
                                console.log('cmd error in service');
                                packet=null;
                                break;
                }
                if(flag==0){ // to one app
                        logined.forEach(function(loginuser){
                                if(loginuser.regnum==msg.regnum){
                                        try{
                                                loginuser.socket.write(packet + "\n");
                                        }catch(e){
                                                console.error('flag=0 exception: ' + e);
                                        }
                                }
                        });
                        console.log('writed to one app');
                }
        /*      else if(flag==1){ // to app broadcasting
                        console.log('apps broadcasting in service');
                        var sender=this;
                        logined.forEach(function(loginuser){
                                if(loginuser.socket!=sender && loginuser.socket!=null)
                                        try{
                                                loginuser.socket.write(packet + "\n");
                                                console.log('broadcasting send');
                                        }catch(e){
                                                console.error('flag=1 exception: ' + e);
                                        }
                        });
                }*/
                else if(flag==2){ // to webserver
                        events.emit('towebserver',jsonStr);
                        console.log('writed to webserver');
                }
                flag=0;
        });

        function CheckClientIP(dbdb,regnum,passwd){
                var correctIP="203.252.146.";
                var cnt=0;
                var compareIP;
                var flag=0;

                dbdb.query('select ip from serverip', function(error,result){
                        console.log('query done');
                        console.log(result);
                        if(error){
                                console.log('checkclientip error');
                        }
                        else{
                                result.forEach(function(r){
                                        if(socket.remoteAddress[i]=='.')
                                                cnt++;
                                        if(cnt==3){
                                                compareIP=socket.remoteAddress.splice(0,i);
                                                console.log('compareIP: ' + compareIP);
                                                if(compareIP==r)
                                                        flag=1;
                                        }
                                });
                                if(flag=1)
                                        CheckLoginuser(dbdb,regnum,passwd);
                                else
                                        CheckLoginuser(dbdb,111,111);
                        }
                });
                /*for(var i=0; i<=12; i++){
                        if(socket.remoteAddress[i]=='.')
                                cnt++;
                        if(cnt==3){
                                compareIP=socket.remoteAddress.splice(0,i);
                                console.log('compareIP: ' + compareIP);
                                break;
                        }
                }
                if(correctIP==compareIP)
                        return true;
                else
                        return false;
                */
        }

        // db section
        // db query event catch

        dbdb.on('error', function(err){
                console.error('error in dbdb: ' + err);
        });

        dbdb.on('uncaughtException', function(err){
                console.error('exception in dbdb: ' + err);
        });

        dbdb.on('end', function(){
                console.log('end in dbdb');
        });

        dbdb.on('change',function(flag,cmd,result,regnum){
                var jsonStr;
                var packet;
                var error=0;

                var class1=0;

                console.log('db event');

                switch(cmd){
                        case 'login':
                                console.log('login req');
                                console.log('db event flag: ' + flag);
                                if(flag==1){
                                        // login ok
                                        console.log(socket.name + ' is logined');
                                        var here=0;
                                        logined.forEach(function(loginuser){
                                                if(loginuser.regnum==regnum){
                                                        here=1;
                                                }
                                        });
                                        if(here==0){
                                                logined.push({'regnum':regnum,'socket':socket});
                                                jsonStr={'cmd':'loginok'};
                                                console.log(socket.name + ' is logined');
                                        }
                                        else if(here==1){
                                                //jsonStr={'cmd':'loginfail'}; // temp
                                                jsonStr={'cmd':'loginok'};
                                                console.log(socket.name + ' is login failed: same user');
                                        }
                                        here=0;
                                        console.log('logined length: ' + logined.length);
                                }
                                else if(flag==0){
                                        // no user or wrong passwd
                                        console.log(socket.name + ' is login failed');
                                        jsonStr={'cmd':'loginfail'};
                                }
                                packet=JSON.stringify(jsonStr);
                                console.log('socket: ' + socket);
                                break;
                        case 'location': // qrcode
                                console.log('loc req');
                                if(flag==1){
                                        // loc ok
                                        console.log(socket.name + ' is loc ok');
                                        jsonStr={'cmd':'qrcodeok'};
                        //              class1=1;
                                }
                                else if(flag==0){
                                        // loc fail
                                        console.log(socket.name + ' is loc failed');
                                        jsonStr={'cmd':'qrcodefail'};
                                }
                                packet=JSON.stringify(jsonStr);
                                break;
                        case 'info':
                                var r=result[0];
                                console.log('info req');
                                console.log('url: ' + r.infourl);
                                jsonStr={'cmd':'info','url':r.infourl};
                                packet=JSON.stringify(jsonStr);
                                console.log('packet: ' + packet);
                                break;
                        default:
                                console.log('db event cmd error');
                                error=1;
                                break;
                }
                if(error==0 && socket==null)
                        console.error('login socket is null!!');
                else if(error==0 && socket!=null){
                        try{
                                socket.write(packet + "\n");
                                if(class1==1){
                                        jsonStr={'cmd':'classstart'};
                                        packet=JSON.stringify(jsonStr);
                                        socket.write(packet + "\n");
                                        console.log('classstart temp send');
                                }
                        }catch(e){
                                console.log('dbdb exception: ' + e);
                        }
                }
                error=0;
        });

        // check loginuser
        function CheckLoginuser(dbdb,regnum,passwd){
                var k=-1;
                console.log('checkloginuser');
                console.log('regnum: ' + regnum + ' passwd: ' + passwd);
                dbdb.query('select regnum from loginuser where regnum=? and passwd=?',[regnum,passwd], function(error,result){
                        console.log('query done');
                        console.log(result);
                        if(error){
                                console.log('checkloginuser error');
                        }
                        else{
                                var r=result[0];
                                if(r==null){
                                        console.log('user not exist and login fail in db');
                                        k=0;
                                        console.log('k: ' + k);
                                }
                                else if(r.regnum==regnum){ // user exist and login ok
                                        console.log('user exist and login ok in db');
                                        k=1;
                                        console.log(' k: ' + k);
                                }
                        }
                        console.log('checkloginuser dbdb: ' + dbdb);
                        console.log('k: ' + k);
                        console.log('result: ' + result);
                        console.log('regnum: ' + regnum);

                        dbdb.emit('change',k,'login',result,regnum);
                });
                console.log('k: ' + k);
        };

        // check qrcode, user location
        function CheckLocation(dbdb,qrcode){
                var k=-1;
                console.log("checklocation qrcode: " + qrcode);

                dbdb.query('select qrcode from qrtable where qrcode=?',[qrcode]
                        ,function(error,result){
                        console.log('query done');
                        console.log('result: ' + result);
                        if(error){
                                console.log('checklocation error');
                        }
                        else{
                                var r=result[0];
                                if(r!=null){ // qrcode exist and loc ok
                                        console.log('qrcode exist and loc ok in db');
                                        k=1;
                                        console.log(' k: ' + k);
                                }
                                else{
                                        console.log('qrcode not match in db');
                                        k=0;
                                        console.log('k: ' + k);
                                }
                        }
                        dbdb.emit('change',k,'location',result,0);
                });
        };

        // student infomation webpage url
        function InfoOfStudent(dbdb,regnum){
                var k=-1;
                //console.log(": ");

                dbdb.query('select infourl from loginuser where regnum=?',[regnum]
                        ,function(error,result){
                        console.log('query done');
                        console.log('result: ' + result);
                        if(error){
                                console.log('infoofstudent error');
                        }
                        else{
                                var r=result[0];
                                if(r!=null){ // url is exist
                                        console.log('url is exist in db');
                                        k=1;
                                        console.log(' k: ' + k);
                                }
                                else{
                                        console.log('url is not exist in db');
                                        k=0;
                                        console.log('k: ' + k);
                                }
                                dbdb.emit('change',k,'info',result,0);
                        }
                });
        };

};