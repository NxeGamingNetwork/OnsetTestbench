class WebUIManager {
    constructor(host){
        this.uiList = [];
        this.socket = new WebSocket("ws://"+host+"/socket");
        this.socket.onmessage = event => {
            let data = JSON.parse(event.data);
            this.onEvent(data);
        };
        this.socket.onerror = () => setTimeout(() => window.location.reload(), 1000);
        this.socket.onclose = () => setTimeout(() => window.location.reload(), 1000);
    }
    getUI(id){
        for(let ui of this.uiList){
            if(ui.id === id){
                return ui;
            }
        }
    }
    sendEvent(name, data){
        this.socket.send(JSON.stringify({
            name: name,
            data: data
        }));
    }
    onEvent(event){
        if(event.name === 'create'){
            let ui = new WebUI(this, event.data.id, event.data.x, event.data.y, event.data.width, event.data.height);
            this.uiList.push(ui);
            ui.create();
            return;
        }
        let ui = this.getUI(event.data.id);
        if(ui === undefined){
            return;
        }
        switch(event.name){
            case 'destroy':
                this.uiList = this.uiList.filter(value=>value!==ui);
                ui.destroy();
                break;
            case 'load':
                ui.loadFile(event.data.url);
                break;
            case 'execute':
                ui.execute(event.data.code);
                break;
            case 'size':
                ui.setSize(event.data.width, event.data.height);
                break;
            case 'location':
                ui.setLocation(event.data.x, event.data.y);
                break;
            case 'visibility':
                ui.setVisible(event.data.visible);
                break;
        }
    }
}