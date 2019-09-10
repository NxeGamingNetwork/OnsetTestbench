class WebUI {
    constructor(manager, id, x, y, width, height){
        this.manager = manager;
        this.id = id;
        this.frame = null;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    create(){
        if(this.frame !== null){
            this.destroy();
        }
        this.frame = document.createElement("iframe");
        this.frame.style.position = 'absolute';
        this.setLocation(this.x, this.y);
        this.setSize(this.width, this.height);
        this.frame.style.border = '0px solid black';
        this.frame.onload = () => this.prepareJS();
        this.frame.scrolling = 'no';
        this.frame.style.overflow = 'hidden';
        document.body.appendChild(this.frame);
        this.prepareJS();
    }
    destroy(){
        document.body.removeChild(this.frame);
        this.frame = null;
    }
    execute(code){
        this.frame.contentWindow.eval(code);
    }
    setSize(width, height){
        this.width = width;
        this.height = height;
        this.frame.style.width = this.width+'px';
        this.frame.style.height = this.height+'px';
    }
    setLocation(x, y){
        this.x = x;
        this.y = y;
        this.frame.style.top = this.y+'px';
        this.frame.style.left = this.x+'px';
    }
    setVisibile(visible){
        if(visible){
            this.frame.style.display = '';
        }else{
            this.frame.style.display = 'none';
        }
    }
    loadFile(url){
        if(url.startsWith('http://asset/')){
            url = url.replace('http://asset/', 'http://'+window.location.host+'/asset/');
        }
        this.frame.src = url;
    }
    prepareJS(){
        this.frame.contentWindow.ue = {
            game: {
                callevent: (name, params) => {
                    if(params.length === 0){
                        params = "[]";
                    }
                    this.manager.sendEvent("callevent", {
                        name: name,
                        params: JSON.parse(params)
                    });
                }
            }
        }
    }
}