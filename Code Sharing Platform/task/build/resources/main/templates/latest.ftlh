<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Latest</title>
  <link rel="stylesheet" href="/css/styles.css">
  <link rel="stylesheet"
         href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
  <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
  <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
  <div>
    <#list wrappers?sort_by("date")?reverse as wrapper>
      <p id="date_p">Load date:<span id="load_date">${wrapper.date}</span></p>
      <pre id="code_snippet"><code>${wrapper.code}</code></pre>
      <p> Time Restriction (in s): ${wrapper.time} </p>
      <p> Views Restriction: ${wrapper.views} </p>
    </#list>
  </div>

</body>
</html>