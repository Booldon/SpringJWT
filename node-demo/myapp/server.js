const http = require("http");
const fs = require("fs");
const path = require("path");
const express = require("express");
const cors = require("cors");
const app = express();

app.use(cors());

const server = http.createServer((req, res) => {
  // 정적 파일 서비스
  let filePath = "." + req.url;
  if (filePath === "./") {
    filePath = "./index.html"; // 기본 페이지 설정
  }

  const extname = String(path.extname(filePath)).toLowerCase();
  const contentType =
    {
      ".html": "text/html",
      ".js": "text/javascript",
      ".css": "text/css",
      ".json": "application/json",
      ".png": "image/png",
      ".jpg": "image/jpg",
      ".gif": "image/gif",
      ".svg": "image/svg+xml",
    }[extname] || "application/octet-stream";

  fs.readFile(filePath, (err, content) => {
    if (err) {
      if (err.code == "ENOENT") {
        // 파일이 없는 경우
        fs.readFile("./404.html", (err, content) => {
          res.writeHead(404, { "Content-Type": "text/html" });
          res.end(content, "utf-8");
        });
      } else {
        // 다른 오류
        res.writeHead(500);
        res.end(`Server Error: ${err.code}`);
      }
    } else {
      // 파일이 존재하는 경우
      res.writeHead(200, { "Content-Type": contentType });
      res.end(content, "utf-8");
    }
  });
});

const PORT = process.env.PORT || 3000; // 기본 포트 3000 사용
server.listen(PORT, () => {
  console.log(`서버가 http://localhost:${PORT} 에서 실행 중입니다.`);
});
