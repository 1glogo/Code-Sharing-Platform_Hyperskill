<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Create</title>
  <link rel="stylesheet" href="/css/styles.css">
</head>
<body>
  <br>
  <div><textarea id="code_snippet" name="textarea" rows="5" cols="80" placeholder="// write your code here"></textarea></div>
  <br>
  <div>
      <label for="time_restriction">Time Restriction: </label>
      <input id="time_restriction" label="time" type="text"/>
  </div>
  <br>
  <div>
    <label for= "views_restriction" >Views Restriction: </label>
    <input id="views_restriction" label="views" type="text"/>
  </div>
  <br>
  <button id="send_snippet" type="submit" onclick="send()">Submit</button></div>
  <script>
  function send() {
              let object = {
                  "code": document.getElementById("code_snippet").value,
                  "time": Number(document.getElementById("time_restriction").value),
                  "views": Number(document.getElementById("views_restriction").value)
              };
              let json = JSON.stringify(object);
              let xhr = new XMLHttpRequest();
              xhr.open("POST", '/api/code/new', false)
              xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
              xhr.send(json);
              if (xhr.status == 200) { alert("Success!"); }
          }
  </script>
</body>
</html>