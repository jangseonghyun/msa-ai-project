import { useEffect, useState } from "react";
import api from "../api/interceptor";
import { useAuth } from "../context/AuthContext"

export default function Topbar({ onLoginClick }) {

  const { user, setUser, logout } = useAuth();

  return (
    <header className="topbar">
      <div className="topbar__searchWrap">
        <span className="topbar__searchIcon">⌕</span>
        <input className="topbar__search" placeholder="검색어를 입력하세요..." />
      </div>

      <div className="topbar__actions">
        <span className="topbar__admin"></span>
        <span className="topbar__divider" />

        <span className="topbar__userIcon">◉</span>

        {user ? (
          <>
          <span className="topbar__userName">
            {user.userId}님
          </span>
          <button className="topbar__logout" onClick={logout}>
            로그아웃
          </button>
          </>
        ) : (
          <button className="topbar__login" onClick={onLoginClick}>
            로그인
          </button>
        )}
      </div>
    </header>
  );
}