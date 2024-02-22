//로컬 스토리지에 저장
function saveTokenToLocalStorage(token) {
  localStorage.setItem("token", token);
}

//로컬 스토리지에서 토큰 가져오기
function getTokenFromLocalStorage() {
  return localStorage.getItem("token");
}

function deleteTokenFromLocalStorage() {
  localStorage.setItem("token", "");
}

document
  .getElementById("loginForm")
  .addEventListener("submit", function (event) {
    event.preventDefault(); // 폼의 기본 제출 방지

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);

    fetch("http://localhost:8080/login", {
      method: "POST", // HTTP 메소드 지정
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const authorizationHeader = response.headers.get("Authorization");
        if (authorizationHeader) {
          alert("로그인 성공!!");
          const token = authorizationHeader.split(" ")[1];
          saveTokenToLocalStorage(token);
          console.log("토큰을 로컬 스토리지에 저장했습니다:", token);
        } else {
          throw new Error("응답에 Authorization 헤더가 없습니다.");
        }
        return response.text();
      })
      .catch((error) => console.error("로그인 오류:", error));
  });

function getAdmin() {
  const token = getTokenFromLocalStorage();
  if (!token) {
    alert("토큰이 없습니다.");
    return;
  }

  fetch("http://localhost:8080/admin", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`, //토큰 삽입
    },
  })
    .then((response) => {
      if (!response.ok) {
        if (response.status === 403) {
          alert("권한 없음");
        } else if (response.status === 404) {
          alert("잘못된 url");
        }

        return;
      }
      const authorizationHeader = response.headers.get("Authorization");
      if (authorizationHeader) {
        const token = authorizationHeader.split(" ")[1];
        saveTokenToLocalStorage(token);
        console.log("새로운 토큰을 저장했습니다:", token);
      }
      alert("admin 접근 성공");
      return response.text();
    })
    .catch((error) => {
      console.error("접근 오류", error);
    });
}

function logout() {
  const token = getTokenFromLocalStorage();
  if (!token) {
    alert("토큰이 없습니다.");
    return;
  }
  fetch("http://localhost:8080/logout", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`, //토큰 삽입
    },
  })
    .then((response) => {
      if (!response.ok) {
        alert("로그아웃 실패");
        return;
      }
      alert("로그아웃 성공");
      deleteTokenFromLocalStorage();
      return response.text();
    })
    .catch((error) => {
      console.error("접근 오류", error);
    });
}
