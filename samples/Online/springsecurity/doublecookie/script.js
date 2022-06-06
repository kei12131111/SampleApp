window.onload = function() {

  var xhr = new XMLHttpRequest();
  
  // 「get通信」ボタンを押したとき
  document.getElementById('get_button').onclick = function() {
    //xhr.withCredentials = true;
    xhr.open('GET', 'http://localhost:8080/todo-api-mybatis3-multi-web/api/v1/todos', true);
    xhr.send(null);
    

    // サーバからのデータ受信を行った際の動作
    xhr.onload = function (e) {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          var response = document.getElementById('disp_response');
          
          // c_start = document.cookie.indexOf("XSRF-TOKEN=");
          //console.log(c_start);
          //console.log(document.cookie);
          
          console.log(getCookie("XSRF-TOKEN"));
          
          //response.value = document.cookie;

          
          var header = xhr.getResponseHeader("Set-Cookie")
          console.log(header);
          response.value = xhr.responseText;
        }
      }
    };
  };
  

};


function getCookie(cname) {
  let name = cname + "=";
  let decodedCookie = decodeURIComponent(document.cookie);
  //console.log(decodedCookie);
  let ca = decodedCookie.split(';');
  for(let i = 0; i <ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}
