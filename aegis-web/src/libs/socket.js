export default class Socket {
	constructor(url, cb) {
		this.socket = null;
		this.url = '';
		this.url = url;
		this.lockReconnet = false;
		this.isReconnet = false;
		this.timeoutObj = null;
		this.serverTimeoutObj = null;
		this.cb = cb;
		this.timeout = 60 * 1000;//心跳时间
	}

	onmessage() {
		if(this.socket)
		this.socket.onmessage = (ev) => {
			this.getMsg(ev);
			this.reset();
			this.isReconnet = false;
		}
	}
	//接收服务器端数据
	getMsg(ev) {
    this.cb(ev);
	}

	onerror() {
		if(this.socket)
		this.socket.onerror = () => {
			console.log('websocket服务出错了---onerror');
			this.start();
			this.reconnet(this.url);
		}
	}

	reconnet() {
		if (this.lockReconnet){
			return false
		}
	  this.isReconnet = true;
	  this.lockReconnet = true
	  setTimeout(() => {
		  new Socket(this.url);
		  this.lockReconnet = false
	  }, 60 * 1000)
	}

	onopen() {
		if(this.socket)
		this.socket.onopen = () => {
			console.log('socket连接成功')
			if (this.isReconnet) {//执行全局回调函数
				console.log('websocket重新连接了');
				this.isReconnet = false
			}
		}
	}

	onclose() {
		if(this.socket)
		this.socket.onclose =() => {
			console.log("socket连接已关闭");
			this.reset();
		}
	}
	sendMsg (arg) { //发送数据,接收数据
		let data = Object.create(null);
		if(this.socket)
		if (this.socket.readyState === 1) {
			data = JSON.stringify(arg);
			this.socket.send(arg);
		} else {
			setTimeout(() => {
				console.log(socket, '等待socket链接成功')
			  this.sendMsg(data)
			}, 1500)
			return false
		}
	}

	reset() {
		clearTimeout(this.timeoutObj)
		clearTimeout(this.serverTimeoutObj)
	}

	start() {
		if(this.socket)
		this.timeoutObj = setTimeout(() => {
			//发送数据，如果onmessage能接收到数据，表示连接正常,然后在onmessage里面执行reset方法清除定时器
			this.socket.send('heart check')
			this.serverTimeoutObj = setTimeout(() => {
				this.socket.close()
			}, this.timeout)
		}, this.timeout)
	}

	init(url, cb) {
		this.url = url;
		this.cb = cb;
		if ('WebSocket' in window) {
			//无nginx
			//this.socket = new WebSocket('ws://' + location.origin.replace(/(http\:\/\/|https:\/\/)/, '') + '/ws' +this.url + `?accessToken=${window.localStorage.getItem("accessToken")}`)
      this.socket = new WebSocket('ws://' + location.hostname +":8888" + '/ws' +this.url + `?accessToken=${window.localStorage.getItem("accessToken")}`)//有nginx
		} else if ('MozWebSocket' in window) {
			//无nginx
			//this.socket = new MozWebSocket('ws://' + location.origin.replace(/(http\:\/\/|https:\/\/)/, '') + '/ws' + this.url + `?accessToken=${window.localStorage.getItem("accessToken")}`)
      this.socket = new MozWebSocket('ws://' + location.hostname +":8888" + this.url + `?accessToken=${window.localStorage.getItem("accessToken")}`)//有nginx
		}
		this.onopen();
		this.onmessage();
		this.onerror();
		this.onclose();
	}
}
