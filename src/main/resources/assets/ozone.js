var socket;

var connect = function(token) {
        var url = 'wss://' + location.hostname + (location.port ? ':' + location.port: '') + '/chat?token=' + token;
        socket = new WebSocket(url);
        socket.onopen = function() {
            console.log("Connection Opened");
        }
        socket.onclose = function() {
            console.log("Connection Closed");
        }
        socket.onmessage = function(jsonMessage) {
        	var message;
        	try {
        	message = JSON.parse(jsonMessage.data);
        	$('.message').append(message.sender + ': ' + message.message);
        	} catch (err) {
        		
        	}
        }  
}

    var sendMessage = function(recipient, sender, message) {
        var message = {
                recipient: recipient,
                sender: sender,
                message, message
        }
        socket.send(JSON.stringify(message));
    }
    
