function http() {
    let xhr = new XMLHttpRequest();
    xhr.open("GET", 'https://www.baidu.com/');
    xhr.send();
}

function updateTime(){
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById("time").innerHTML = xhr.responseText;
        }
    }
    xhr.open("GET","time",true );
    xhr.send();
}