exports.service=function(webserver,dbdb,events,logined,io){
        io=io.listen(webserver);
        console.log('io listen');
        console.log('dbdb in webservice: ' + dbdb);

        io.configure(function(){
                io.enable('browser client etag');
                io.set('log level',1);
                io.set('transports',[
                        'websocket'
                        ,'flashsocket'
                        ,'htmlfile'
                        ,'xhr-polling'
                        ,'jsonp-polling'
                ]);
                console.log('io config ok');
        });

        io.sockets.on('error',function(err){
                console.log('io sockets error:' + err);
        });

        io.sockets.on('uncaughtException',function(err){
                console.log('io sockets exception:' + err);
        });

        io.sockets.on('end',function(){
                io.sockets.end();
                console.log('io sockets end');
        });

        io.sockets.on('connection',function(socket){
                GLOBAL.webclient=socket;
                console.log('GLOBAL.webclient: ' + GLOBAL.webclient);

                console.log('web page connected');

                socket.on('disconnect',function(){
                        console.log('good-bye');
                });

                socket.on('error',function(err){
                        console.log('web socket error: ' + err);
                });

                socket.on('uncaughtException',function(err){
                        console.log('web socket exception: ' + err);
                });

                socket.on('message',function(msg){
                        console.log('msg event in web: ' + msg.cmd);
                        var packet;
                        var jsonStr;
                        var toServer=0;

                        switch(msg.cmd){
                                case 'userinsert':
                                        console.log('userinsert');
                                        InsertTologinuser(dbdb,msg.regnum,msg.passwd);
                                        break;
                                case 'userupdate':
                                        console.log('userupdate');
                                        UpdateTologinuser(dbdb,msg.regnum,msg.passwd);
                                        break;
                                case 'userview':
                                        console.log('userview');
                                        SelectTologinuser(dbdb);
                                        break;
                                case 'userdelete':
                                        console.log('userdelete');
                                        DeleteTologinuser(dbdb,msg.regnum);
                                        break;
                                case 'usersearch':
                                        console.log('usersearch');
                                        SearchTologinuser(dbdb,msg.regnum);
                                        break;
                                case 'blackinsert':
                                        console.log('blackinsert');
                                        InsertToBlack(dbdb,msg.name,msg.package);
                                        break;
                                case 'blackupdate':
                                        console.log('blackupdate');
                                        UpdateToBlack(dbdb,msg.name,msg.package);
                                        break;
                                case 'blackview':
                                        console.log('blackview');
                                        SeleteToBlack(dbdb,msg.name);
                                        break;
                                case 'blackdelete':
                                        console.log('blackdelete');
                                        DeleteToBlack(dbdb,msg.name);
                                        break;
                                case 'blacksearch':
                                        console.log('blacksearch');
                                        SearchToBlack(dbdb,msg.name);
                                        break;
                                case 'whiteinsert':
                                        console.log('whiteinsert');
                                        InsertToWhite(dbdb,msg.name,msg.package);
                                        break;
                                case 'whiteupdate':
                                        console.log('whiteupdate');
                                        UpdateToWhite(dbdb,msg.name,msg.package);
                                        break;
                                case 'whiteview':
                                        console.log('whiteview');
                                        SelectToWhite(dbdb,msg.name);
                                        break;
                                case 'whitedelete':
                                        console.log('whitedelete');
                                        DeleteToWhite(dbdb,msg.name);
                                        break;
                                case 'whitesearch':
                                        console.log('whitesearch');
                                        SearchToWhite(dbdb,msg.name);
                                        break;
                                // send to server, just send cmd
                                case 'classstart':
                                        console.log('classstart');
                                        // 화이트리스트 전송
                                        ClassStart(dbdb);
                                        break;
                                case 'classend':
                                        console.log('classend');
                                        jsonStr={'cmd':'classend'};
                                        toServer=1;
                                        break;
                                case 'surprise':
                                        console.log('surprise in web');
                                        console.log('msg.surprise: ' + msg.surprise);
                                        jsonStr={'cmd':'surprise','surprise':msg.surprise};
                                        toServer=1;
                                        break;
                                case 'forceok': // from web
                                        console.log('force ok req');
                                        jsonStr={'cmd':'forceok','regnum':msg.regnum};
                                        toServer=1;
                                        break;
                                case 'forcefail': // from web
                                        console.log('force fail req');
                                        jsonStr={'cmd':'forcefail','regnum':msg.regnum};
                                        toServer=1;
                                default:
                                        console.log('cmd error in web');
                                        packet=null;
                                        break;
                        }
                        if(toServer==1){
                                // event emit to server
                                events.emit('toserver',jsonStr);
                                toServer=0;
                                console.log('event emited to server');
                        }
                });

                events.on('towebserver',function(msg){
                        // event from server
                        console.log('web events msg.cmd: ' + msg.cmd);
                        console.log('socket: ' + socket);
                        console.log('GLOBAL.webclient' + GLOBAL.webclient);

                        switch(msg.cmd){
                                case 'surprise':
                                        console.log('surprise in events');
                                        jsonStr={'cmd':'surprise','regnum':msg.regnum};
                                        packet=JSON.stringify(jsonStr);
                                        console.log('jsonStr.cmd ' + jsonStr.cmd);
                                        console.log('jsonStr.regnum ' + jsonStr.regnum);
                                        socket.json.send(jsonStr);
                                        break;
                                case 'force':
                                        console.log('force req in events');
                                        jsonStr={'cmd':'force','regnum':msg.regnum};
                                        packet=JSON.stringify(jsonStr);
                                        socket.json.send(packet + "\n");
                                        break;
                                default:
                                        console.error('cmd error in events');                                           break;

                        }
                });

                // db section
                // db query event catch
                dbdb.on('change',function(flag,cmd,result){
                        var jsonStr;
                        var packet;
                        var flag=0;

                        console.log('db event');

                        switch(cmd){
                                case 'userinsert':
                                        console.log('userinsert');
                                        jsonStr={'cmd':'userinsertok'};
                                        break;
                                case 'userupdate':
                                        console.log('userupdate');
                                        jsonStr={'cmd':'userupdateok'};
                                        break;
                                case 'userview':
                                        console.log('userview');
                                        jsonStr=JSON.stringify(result);
                                        break;
                                case 'userdelete':
                                        console.log('userdelete');
                                        jsonStr={'cmd':'userdeleteok'};
                                        break;
                                case 'usersearch':
                                        console.log('usersearch');
                                        var r=result[0];
                                        jsonStr={'cmd':'usersearch','regnum':r.regnum};
                                        break;
                                case 'blackinsert':
                                        console.log('blackinsert');
                                        jsonStr={'cmd':'blackinsertok'};
                                        break;
                                case 'blackupdate':
                                        console.log('blackupdate');
                                        jsonStr={'cmd':'blackupdateok'};
                                        break;
                                case 'blackview':
                                        console.log('blackview');
                                        jsonStr=JSON.stringify(result);
                                        break;
                                case 'blackdelete':
                                        console.log('blackdelete');
                                        jsonStr={'cmd':'blackdeleteok'};
                                        break;
                                case 'blacksearch':
                                        console.log('blacksearch');
                                        var r=result[0];
                                        jsonStr={'cmd':'blacksearch','name':r.name,'package':r.package};
                                        break;
                                case 'whiteinsert':
                                        console.log('whiteinsert');
                                        jsonStr={'cmd':'whiteinsertok'};
                                        break;
                                case 'whiteupdate':
                                        console.log('whiteupdate');
                                        jsonStr={'cmd':'whiteupdateok'};
                                        break;
                                case 'whiteview':
                                        console.log('whiteview');
                                        jsonStr=JSON.stringify(result);
                                        break;
                                case 'whitedelete':
                                        console.log('whitedelete');
                                        jsonStr={'cmd':'whitedeleteok'};
                                        break;
                                case 'whitesearch':
                                        console.log('whitesearch');
                                        var r=result[0];
                                        jsonStr={'cmd':'whitesearch','name':r.name,'package':r.package};
                                        break;
                                default:
                                        console.log('db event cmd error');
                                        flag=1;
                                        break;
                        }
                        if(flag==0){ // not error
                                packet=JSON.stringify(jsonStr);
                                socket.send(packet);
                                flag=0;
                        }
                });

                // class start
                function ClassStart(dbdb){
                        dbdb.query('select package from whitelist'
                                ,function(error,result){
                                if(error){
                                        console.log('classstart error');
                                }
                                else{
                                        console.log('classstart success');
                                        //console.log(result);
                                        var ret;
/*                                      result.forEach(function(r){
                                                console.log('white pack: ' + r.package);

                                        });*/
                                        var jsonStr={'cmd':'classstart','white':result};
                                        //console.log('jsonStr.white: ' + jsonStr.white);
                                        events.emit('toserver',jsonStr);
                                }
                        });
                };

                // select
                function SelectTologinuser(dbdb){
                        dbdb.query('select * from loginuser',function(error,result){
                                if(error){
                                        console.log('selecttologinuser error');
                                }
                                else{
                                        console.log(result);
                                        dbdb.emit('change',0,'userview',result);
                                }
                        });
                };

                // select one object
                function SearchTologinuser(dbdb,regnum){
                        dbdb.qurey('select regnum from loginuser where regnum=?',[regnum]
                        ,function(error,result){
                                if(error){
                                        console.log('searchtologinuser error');
                                }
                                else{
                                        console.log(result);
                                        dbdb.emit('change',0,'usersearch',result);
                                }
                        });
                };

                // insert
                function InsertTologinuser(dbdb,regnum,passwd){
                        dbdb.query('insert into loginuser(regnum,passwd) values(?,?)',
                                [regnum,passwd],function(error,result){
                                if(error){
                                        console.log('inserttologinuser error');
                                }
                                else{
                                        console.log('inserttologinuser success');
                                        dbdb.emit('change',0,'userinsert',result);
                                }
                        });
                };

                // update
                function UpdateTologinuser(dbdb,regnum,passwd){
                        dbdb.query('update loginuser set passwd=? where regnum=?',
                                [passwd, regnum],function(error,result){
                                if(error){
                                        console.log('updatetologinuser error');
                                }
                                else{
                                        console.log('updatetologinuser success');
                                        dbdb.emit('change',0,'userupdate',result);
                                }
                        });
                };

                // delete
                function DeleteTologinuser(dbdb,regnum){
                        dbdb.query('delete from loginuser where regnum=?',
                                [regnum],function(error,result){
                                if(error){
                                        console.log('deletetologinuser error');
                                }
                                else{
                                        console.log('deletetologinuser success');
                                        dbdb.emit('change',0,'userdelete',result);
                                }
                        });
                };

                // select balcklist search
                function SearchToBlack(dbdb,name){
                        dbdb.query('select name,package from blacklist where name=?',
                                [name],function(error,result){
                                if(error){
                                        console.log('searchtoblack error');
                                }
                                else{
                                        console.log('searchtoblack success');
                                        dbdb.emit('change',0,'blacksearch',result);
                                }
                        });
                };

                // select balcklist view
                function SelectToBlack(dbdb,name){
                        dbdb.query('select name,package from blacklist'
                                ,function(error,result){
                                if(error){
                                        console.log('selecttoblack error');
                                }
                                else{
                                        console.log('selecttoblack success');
                                        dbdb.emit('change',0,'blackview',result);
                                }
                        });
                };

                // insert balcklist
                function InsertToBlack(dbdb,name,package){
                        dbdb.query('insert into blacklist(name,package) values(?,?)',
                                [name,package],function(error,result){
                                if(error){
                                        console.log('inserttoblack error');
                                }
                                else{
                                        console.log('inserttoblack success');
                                        dbdb.emit('change',0,'blackinsert',result);
                                }
                        });
                };

                // update balcklist
                function UpdateToBlack(dbdb,name,package){
                        dbdb.query('update blacklist set name=?,package=? where name=?)',
                        [name,package,name],function(error,result){
                                if(error){
                                        console.log('updatetoblack error');
                                }
                                else{
                                        console.log('updatetoblack success');
                                        dbdb.emit('change',0,'blackupdate',result);
                                }
                        });
                };

                // delete balcklist
                function DeleteToBlack(dbdb,name){
                        dbdb.query('delete from blacklist where name=?',
                        [name],function(error,result){
                                if(error){
                                        console.log('deletetoblack error');
                                }
                                else{
                                        console.log('deletetoblack success');
                                        dbdb.emit('change',0,'blackdelete',result);
                                }
                        });
                };

                // select whitelist search
                function SearchToWhite(dbdb,name){
                        dbdb.query('select name,package from whitelist where name=?',
                                [name],function(error,result){
                                if(error){
                                        console.log('searchtowhite error');
                                }
                                else{
                                        console.log('searchtowhite success');
                                        dbdb.emit('change',0,'whitesearch',result);
                                }
                        });
                };

                // select whitelist view
                function SelectToWhite(dbdb,name){
                        dbdb.query('select name,package from whitelist'
                                ,function(error,result){
                                if(error){
                                        console.log('selecttowhite error');
                                }
                                else{
                                        console.log('selecttowhite success');
                                        dbdb.emit('change',0,'whiteview',result);
                                }
                        });
                };

                // insert whitelist
                function InsertToWhite(dbdb,name,package){
                        dbdb.query('insert into whitelist(name,package) values(?,?)',
                                [name,package],function(error,result){
                                if(error){
                                        console.log('inserttowhite error');
                                }
                                else{
                                        console.log('inserttowhite success');
                                        dbdb.emit('change',0,'whiteinsert',result);
                                }
                        });
                };

                // update whitelist
                function UpdateToWhite(dbdb,name,package){
                        dbdb.query('update whitelist set name=?,package=? where name=?)',
                        [name,package,name],function(error,result){
                                if(error){
                                        console.log('updatetowhite error');
                                }
                                else{
                                        console.log('updatetowhite success');
                                        dbdb.emit('change',0,'whiteupdate',result);
                                }
                        });
                };

                // delete whitelist
                function DeleteToWhite(dbdb,name){
                        dbdb.query('delete from whitelist where name=?',
                        [name],function(error,result){
                                if(error){
                                        console.log('deletetowhite error');
                                }
                                else{
                                        console.log('deletetowhite success');
                                        dbdb.emit('change',0,'whitedelete',result);
                                }
                        });
                };

        });

};