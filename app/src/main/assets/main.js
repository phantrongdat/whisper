var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var mysql =require('mysql');
var crypto =require('crypto');
var fs=require('fs');
var request=require('request');
var uuid = require('node-uuid');
var _ = require('underscore');
var async = require('async');
var server_port = process.env.OPENSHIFT_NODEJS_PORT || 8000;
var server_ip_address = process.env.OPENSHIFT_NODEJS_IP || '127.0.0.1';




server.listen(server_port,server_ip_address, function() {
    console.log((new Date()) + ' Server is listening on port: '+server_port);
});

// config database.
var DB_CONFIG={
    host     : '127.11.33.130',
    port     : '3306',
    user     : 'adminNyW6MN2',
    password : 'rc2ITBBt4t3m',
    database : 'server2'
}

var db=mysql.createConnection(DB_CONFIG);



// bắt sự kiện đã kết nối.
io.on('connection', function(socket){
    console.log('your are online!\n'+socket.request._query['userId']);

		socket.userid=socket.request._query['userId'];
    	Object.keys(io.sockets.sockets).forEach(function(id) {
    		var soc = io.sockets.sockets[id];
    		console.log("aaaaaaaaaaaaaaaaa "+soc.userid);
    	});

    // timeline requesting...........................................
    // get timetime
    socket.on('reqtimelineget', function (params) {
    	console.log(new Date()+"")
    	db.query("SELECT * FROM tblTimeline WHERE userID="+params, function (err, rows, fields) {
    		console.log(rows);
	        if(!err){
	        	 socket.emit('restimelineget', rows);
	        	 // result.json(rows);
	        	}
	        else socket.emit('restimelineget', {"result": "false"});
    	});

    });

    // add timetime
    socket.on('reqtimelineadd', function (params) {

    	db.query("INSERT INTO tblTimeline VALUES (null, '"+params.userID+"', '"+params.text+"', '"+params.data+
	        		"', '"+params.time+"')",
				function (err, rows, fields) {
					console.log(rows);
	        		db.query("SELECT * FROM tblTimeline WHERE userID="+params.userID+" ORDER BY timelineID DESC LIMIT 1", function (err1, rows1, fields1) {
			    		console.log(rows1);
			        	socket.emit('restimelineadd', rows1);
			        	// result.json(rows);

			    	});

		    	});
    });

// update timetime
    socket.on('reqtimelineupdate', function (params) {
    	db.query("INSERT INTO tblTimeline VALUES (null, '"+params.userID+"', '"+params.text+"', '"+params.data+
	        		"', '"+params.time+"')",
				function (err1, rows1, fields1) {
			        if(!err) socket.emit('restimelineupdate', {"result": "true"});
			        else socket.emit('restimelineupdate', {"result": "false"});
		    	});
    });


    // user requesting...............................................
    // user info request.
    socket.on('requserget',function (params, result) {

    	db.query("SELECT * FROM tblUser WHERE userID="+params, function (err, rows, fields) {
			console.log(rows);
	        if(rows.length>0){
	        	 socket.emit('resuserget', rows);
	        	 // result.json(rows);
	        	 console.log(rows[0].userID);
	        	}
	        else socket.emit('resuserget', {"result": "false", "userID":0});
    	});
    });

    // login request.
    socket.on('login',function (params, result) {

    	db.query("SELECT * FROM tblUser WHERE phoneNumber='"+params.phoneNumber+"' AND password='"+params.password+"'", function (err, rows, fields) {
			console.log(rows);
	        if(rows[0]!=null){
	        	 socket.emit('loginresponse', {"result": "true", "user":rows});
	        	 // result.json(rows);
	        	 console.log({"result": "true", "user":rows});
	        	}
	        else socket.emit('loginresponse', {"result": "false", "user":rows});
    	});
    });


    // signup request.
    socket.on('signup',function (params) {

    	db.query("SELECT * FROM tblUser WHERE phoneNumber='"+params.phoneNumber+"'", function (err, rows, fields) {

	        if(rows.length==0) {
	        	db.query("INSERT INTO tblUser VALUES (null, '"+params.phoneNumber+"', '"+params.password+"', '"+params.fullName+
	        		"', 'http://trongdat.info/images/telefono.png', '"+params.address+"', '"+params.birthDay+"', '"+params.email+"', '"+params.intro+"', '"+params.lastLocation+"')",
				function (err1, rows1, fields1) {
			        if(!err) socket.emit('signupresponse', {"result": "true"});
			        else socket.emit('signupresponse', {"result": "false"});
		    	});
	        }
	        else{
	        	console.log(rows);
	        	socket.emit('signupresponse', {"result": "existed"});
	        }
    	});

    });

    // update request.
    socket.on('userupdate',function (params) {

    	db.query("SELECT * FROM tblUser WHERE phoneNumber='"+params.phoneNumber+"'", function (err, rows, fields) {
			console.log(rows);
	        if(rows.length>0) {
	        	db.query("UPDATE tblUser SET password= '"+params.password+"', fullName='"+params.fullName+"', address='"+params.address
				+"', birthDay='"+params.birthDay+"', email='"+params.email+"', intro='"+params.intro+"' WHERE phoneNumber='"+params.phoneNumber+"'",
				function (err1, rows1, fields1) {
			        if(rows1.length>0) socket.emit('userupdateresponse', {"result": "true"});
			        else socket.emit('userupdateresponse', {"result": "false"});
		    	});
	        }
	        else socket.emit('userupdateresponse', {"result": "false"});
    	});

    });

// update location.
    socket.on('userlocation',function (params) {

    	db.query("UPDATE tblUser SET lastLocation= '"+params.lastLocation+"' WHERE userID="+params.userID+"",
		function (err1, rows1, fields1) {
			console.log(rows1);
    	});
    });

// request get friends
    socket.on('reqfriendsget', function (params) {

    	db.query("SELECT * FROM tblFriend WHERE userID="+params, function (err, rows, fields) {
    		console.log(rows);
	        // if(rows.length>0){
	        	 socket.emit('resfriendsget', rows);
	        	 // result.json(rows);
	        	// }
	        // else socket.emit('resfriendsget', {"result": "false"});
    	});
    });

    // add add friends
    socket.on('reqfriendadd', function (params) {
    	console.log(params.userID+", "+params.friendID);

    	db.query("INSERT INTO tblFriend VALUES ("+params.userID+", "+params.friendID+")",
				function (err, rows, fields) {
					console.log(err);
					if(!err){
			        	 socket.emit('resfriendadd', [{"userID": params.userID, "friendID":params.friendID}]);
			        	 console.log(params.friendID);
			        }

    	});
	});

	// add timetime
    // socket.on('reqfriendadd', function (params) {

    // 	db.query("INSERT INTO tblTimeline VALUES (null, '"+params.userID+"', '"+params.text+"', '"+params.data+
	   //      		"', '"+params.time+"')",
				// function (err, rows, fields) {
				// 	console.log(rows);
	   //      		db.query("SELECT * FROM tblTimeline WHERE userID="+params.userID+" ORDER BY timelineID DESC LIMIT 1", function (err1, rows1, fields1) {
			 //    		console.log(rows1);
			 //        	socket.emit('restimelineadd', rows1);
			 //        	// result.json(rows);

			 //    	});

		  //   	});
    // });

	//request chat bot
    socket.on('reqsimsimi',function (params,result) {
    	console.log(params.fullName);
    	var text={"result": "Hãy cho Simi biết "+params.fullName+" đang nghĩ gì nhé !"};
		socket.emit('reschatbot', text);
	});
    //request chat bot
    socket.on('reqchatbot',function (params, result) {
    	// var language =params.language!=null?params.language:"vn";
    	// var filter=params.filter!=null?params.filter:1;;
		var text;
		var SimsimiAnswered;
		// var botkey = "http://www.simsimi.com/getRealtimeReq?uuid=UwmPMKoqosEETKleXWGOJ6lynN1TQq18wwvrmCy6IRt&lc=vn&ft=1&reqText=";
		// var botkey = "http://api.simsimi.com/request.p?key=e5fbeb60-09d7-40e3-96ef-7a6495da1832&lc=vn&ft=1.0&text=";
		// var botkey = "http://api.vina4u.pro/api.php?ngonngu="+language+"&loctuxau="+filter+"&tenbot=simsimi&msg=";
		var botkey = "http://sandbox.api.simsimi.com/request.p?key=d354a777-2470-44b9-bf8a-89a383efbd7f&lc=vn&ft=1.0&text=";
		// var botkey = "http://api.vina4u.pro/api.php?ngonngu=vn&loctuxau=1&tenbot=simsimi&msg=";
		// https://api.trolyfacebook.com/chat/?tenbot=simi=&loctuxau=1&noidung=
		console.log('Message: '+params.message);
    	request(botkey + encodeURI(params.message),
				function(error, response, body)
				{
					console.log(body)
					if (error) socket.emit('reschatbot', {"result": "Bot đang ngủ rồi, quay lại sau nha !!"});
					// if (body.indexOf("502 Bad Gateway") > 0 || body.indexOf("respSentence") < 0)
					// 	// api.sendMessage("Cái ji đây ?? :D ??: " + params.message);
					// 	socket.emit('reschatbot', {"result": "Cái ji đây ?? :D ??: " + params.message});
					// text = JSON.parse(body);
					// if (text.status == "200")
					// {
					// 	SimsimiAnswered = text.respSentence;
					// 	if (params.message===text.respSentence) {
					// 		return;
					// 	} else
					// 		SimsimiAnswered = text.respSentence;
					// 		socket.emit('reschatbot', {"result": SimsimiAnswered});
					// 	// api.sendMessage(SimsimiAnswered+"\n------------------ auto reply :3", message.threadID);
					// 	// api.markAsRead(message.threadID);
					// 	console.log("Answered:"+SimsimiAnswered);
					// }

					text=JSON.parse(body);
 					if (body.indexOf("502 Bad Gateway") > 0 || body.indexOf("response") < 0)
						socket.emit('reschatbot', {"result": "@@"});
					else
					if (text.result=="100") {
						SimsimiAnswered = text.response;
						socket.emit('reschatbot', {"result": SimsimiAnswered});
						console.log("Answered:"+SimsimiAnswered);
					}else
					{
						socket.emit('reschatbot', {"result": "Simsimi không biết bạn muốn nói gì? @@"});
					}
				});
    })

    //realtime chat
    var rooms=db.query("SELECT conversationID FROM tblConversation", function (err, rows, fields) {

    });


 //  	//Emit the rooms array
  	// socket.emit('setup', rooms);

	// //Listens for switch room
  	socket.on('switch_room', function(params) {
	    //Handles joining and leaving rooms
	    //console.log(params);
	    socket.join(params.conversationID);
	    // io.in(params.conversationID).emit('user_joined', params.userID);

  });

  //Listens for a new chat message
  socket.on('req_new_message', function(params) {

	io.in("w"+params.conversationID).emit('res_new_message', {"conversationID":params.conversationID, "userID": params.userID, "text":params.text,"data":params.data,"time":params.time});
    db.query("INSERT INTO tblMessage VALUES (null,'"+params.conversationID+"', '"+params.userID+"', '"+params.text+"', '"+params.data+
	        		"', '"+params.time+"')",
				function (err, rows, fields) {
					console.log(err);
					console.log(rows);

	        	});
    db.query("UPDATE tblMemOfCon SET lastUpdate='"+params.time+"' WHERE conversationID= '"+params.conversationID+"'",
				function (err, rows, fields) {
					console.log(rows);

	        	});
   });


  //Listens for a new conversation
  socket.on('req_new_conversation', function(params) {
	var new_conversation_id = uuid.v4();
    db.query("INSERT INTO tblConversation VALUES ('"+new_conversation_id+"', '"+params.title+"', '"+params.image+"')",
				function (err, rows, fields) {
					if (!err) {
						socket.join("w"+new_conversation_id);
						socket.emit('res_new_conversation',{"conversationID":new_conversation_id})
					}
	        	});
    db.query("INSERT INTO tblMessage VALUES (null,'"+new_conversation_id+"', '"+params.userID+"', '"+params.text+"', '"+params.data+
	        		"', '"+params.time+"')",
				function (err, rows, fields) {
					console.log(rows);

	        	});
   });


  //Listens for a new conversation
  socket.on('req_add_member', function(params) {

    db.query("INSERT INTO tblMemOfCon VALUES (null,'"+params.conversationID+"', '"+params.userID+"', '"+params.lastUpdate+"')",
				function (err, rows, fields) {
					if (!err) {
						Object.keys(io.sockets.sockets).forEach(function(id) {
				    		var soc = io.sockets.sockets[id];
				    		if (soc.userid==params.userID) {
						    	soc.join(params.conversationID);
								soc.emit('res_add_member', {"conversationID":params.conversationID});
						    }
				    	});
					}

	        	});
   });
  // request conversation
    socket.on('req_get_conversation', function (params) {

    	db.query("SELECT * FROM tblMemOfCon WHERE userID="+params.userID, function (err, rows, fields) {
    		console.log(err);
	        if(rows.length>0){
    			console.log(rows);
	        	socket.emit('res_get_conversation', rows);
	        	for (var i = rows.length - 1; i >= 0; i--) {
	        	 	socket.join("w"+rows[i].conversationID);

    				console.log("joined "+"w"+rows[i].conversationID);
	        	}
	        }
	    });
    });

  // // request conversation
  //   socket.on('req_get_conversation', function (params) {

  //   	db.query("SELECT * FROM tblMemOfCon WHERE userID="+params.userID, function (err, rows, fields) {

	 //    	db.query("SELECT * FROM tblConversation WHERE userID="+params.userID, function (err, rows, fields) {
	 //    		console.log(err);
		//         if(rows.length>0){
	 //    			console.log(rows);
		//         	socket.emit('res_get_conversation', rows);
		//         	for (var i = rows.length - 1; i >= 0; i--) {
		//         	 	socket.join(rows[i].conversationID);
		//         	}
		//         }
		//         // else socket.emit('resfriendsget', {"result": "false"});
	 //    	});
	 //    });
  //   });

  // request message
    socket.on('req_get_message', function (params) {

    	db.query("SELECT * FROM tblMessage WHERE conversationID='"+params.conversationID+"'", function (err, rows, fields) {
    		console.log(err);
	        if(rows.length>0){
    			console.log(rows);
	        	socket.emit('res_get_message', rows);
	        }
	        // else socket.emit('resfriendsget', {"result": "false"});
    	});
    });

});// end connection listener.
// function getFileName(id) {
// 	// body...
// 	return id+getMillis()+".jpg";
// }
// function getMillis() {
// 	// body...
// 	var date=new Date();
// 	var millis=date.getTime();
// 	console.log(millis);
// 	return millis;
// }

// cấu hình link
app.get('/', function(req, res){
    res.sendFile(__dirname+"/index.html");
});




app.get('/sim/:message', function(req, res){
	var text;
	var SimsimiAnswered;
	var botkey = "http://sandbox.api.simsimi.com/request.p?key=d354a777-2470-44b9-bf8a-89a383efbd7f&lc=vn&ft=1.0&text=";
	// var botkey = "http://api.vina4u.pro/api.php?ngonngu=vn&loctuxau=1&tenbot=simsimi&msg=";
	// http://api.vina4u.pro/api.php?ngonngu=vn&loctuxau=1&tenbot=simsimi&msg=sim%20%C6%A1i
	request(botkey + encodeURI(req.params.message),
			function(error, response, body)
			{
				// if (error) socket.emit('reschatbot', {"result": "false"});
				// if (body.indexOf("502 Bad Gateway") > 0 || body.indexOf("respSentence") < 0)
				// 	// api.sendMessage("Cái ji đây ?? :D ??: " + params.message);
				// 	socket.emit('reschatbot', {"result": "Cái ji đây ?? :D ??: " + params.message});
				// text = JSON.parse(body);
				// if (text.status == "200")
				// {
				// 	SimsimiAnswered = text.respSentence;
				// 	if (params.message===text.respSentence) {
				// 		return;
				// 	} else
				// 		SimsimiAnswered = text.respSentence;
				// 		socket.emit('reschatbot', {"result": SimsimiAnswered});
				// 	// api.sendMessage(SimsimiAnswered+"\n------------------ auto reply :3", message.threadID);
				// 	// api.markAsRead(message.threadID);
				// 	console.log("Answered:"+SimsimiAnswered);
				// }
				res.send(JSON.stringify({"result": body}));
				console.log(body);
			});
});

app.get('/dat', function(req, res){
    console.log('phan trong dat!');
    res.send('hello !  phan trong dat here!');
});

app.get('/getusers', function(req, res){
  db.query("SELECT * FROM tblUser ", function (err, result) {
    res.json(result);
  });
});

app.get('/getconversation/:conversationID', function(req, res){
  db.query("SELECT * FROM tblConversation WHERE conversationID='"+req.params.conversationID+"'", function (err, result) {
    res.json(result[0]);
  });
});

app.get('/getmember/:conversationID', function(req, res){
  db.query("SELECT * FROM tblMemOfCon WHERE conversationID='"+req.params.conversationID+"'", function (err, result) {
    res.json(result[0]);
  });
});


app.get('/checkconversation/:userID/:friendID', function (req, res) {
	db.query("SELECT * FROM tblMemOfCon WHERE userID='"+req.params.userID+"'", function (err, results) {
		var convID="null";
		var length= results.length;
		var count=0;
		async.forEach(results, processEachTask, afterAllTasks);

		  function processEachTask(result, callback) {
		    console.log(result);
				db.query("SELECT * FROM tblMemOfCon WHERE conversationID='"+result.conversationID+"' AND userID='"+req.params.friendID+"'", function (error, rows,fields) {
					if (rows.length>0)
						// convID=rows[0].conversationID;
		    			callback(rows[0].conversationID);
		    			else callback("null");
				});
		  }

		  function afterAllTasks(id) {
		  	console.log("id:--- "+id);
		  	count++;
		  	if (count==length && id=="null") {
		       res.send(JSON.stringify({"conversationID": "null"}));
		  	}else res.send(JSON.stringify({"conversationID": id}));
		  }

	});
});

app.get('/getlastmessage/:conversationID', function(req, res){
  db.query("SELECT * FROM tblMessage  WHERE conversationID='"+req.params.conversationID+"' ORDER BY messageID DESC LIMIT 1", function (err, result) {
    res.json(result[0]);
  });
});



app.get('/getuser/:userID', function(req, res){
  db.query("SELECT * FROM tblUser WHERE userID="+req.params.userID, function (err, result) {
    res.json(result[0]);
  });
});


app.get('/getinfo/:phoneNumber', function(req, res){
  db.query("SELECT * FROM tblUser WHERE phoneNumber='"+req.params.phoneNumber+"'", function (err, rows, fields) {
  	console.log(rows);
  	if (rows.length>0)
    	res.json(rows[0]);
  });
});

app.get('/checksuggestion/:userID/:phoneNumber', function(req, res){
	  db.query("SELECT * FROM tblUser WHERE phoneNumber='"+req.params.phoneNumber+"'", function (err, rows, fields) {
	  	if (rows.length>0){
			console.log(rows);
			checksuggestion(req, res, rows[0]);
		}else res.send(JSON.stringify({"result": "notfound"}));
	});
});



function checksuggestion(req, res , arg) {
	db.query("SELECT * FROM tblFriend WHERE userID="+req.params.userID+" AND friendID="+arg.userID, function (err, rows, fields) {
  			console.log(rows);
  			if (rows.length>0){
  				res.send(JSON.stringify({"result": "true"}));
  			}else res.send(JSON.stringify({"result": "false"}));

		});
}

app.get('/isfriend/:userID/:friendID', function(req, res){
	db.query("SELECT * FROM tblFriend WHERE userID="+req.params.userID+" AND friendID="+req.params.friendID+"", function (err, rows, fields) {
					console.log(rows+req.params.userID+req.params.friendID);
		  			if (rows.length>0){
		  				res.send(JSON.stringify({"result": "true"}));
		  			}else res.send(JSON.stringify({"result": "false"}));
		});
});


app.get('/getnewpost/:userID', function(req, res){
  db.query("SELECT * FROM tblTimeline WHERE userID="+req.params.userID+" ORDER BY timelineID DESC LIMIT 1", function (err, result) {
    res.json(result[0]);
  });
});

var products=[{"Id":1,"Name":"Pen", "Price": 10000},{"Id":2,"Name":"Apple", "Price": 2000000},{"Id":3,"Name":"IPhone", "Price": 10000000} ];
app.get('/jsonproducts', function(req, res){

    res.send(JSON.stringify(products));
});

app.get('/xmlproducts', function(req, res){

	res.contentType('application/xml');
    res.sendFile(__dirname+'/products.xml');
});



app.get('/adduser/:phoneNumber/:password/:fullName/:address/:birthDay/:email/:intro', function(req, res){
  var hash = crypto.createHash('md5').update(req.params.password).digest('hex');

  db.query("INSERT INTO tblUser VALUES(null,'"+req.params.phoneNumber+"','"+hash+"','"+req.params.fullName+"','"
    +req.params.address+"','"+req.params.birthDay+"','"+req.params.email+"','"+req.params.intro+"')", function (err, result) {
    res.json(result);
    console.log(err);
    console.log(result);
    console.log(hash);
  });
});
