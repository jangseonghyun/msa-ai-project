// LoginPopup.jsx
import { useState, useEffect } from "react";
import api from "../api/interceptor";
import ReactDOM from "react-dom";
import "../styles/loginPopup.css";
import { setAccessToken } from '../api/token'
import { useAuth } from "../context/AuthContext";

export default function LoginPopup({ onClose }) {
    // 1. state
    const [mode, setMode] = useState("login");

    const [debounceId, setDebounceId] = useState("");

    const { setUser } = useAuth();

    // form 값 저장
    const [form, setForm] = useState({
        id: "",
        pw: ""
    })

    // 검증 결과
    const [ errors, setErrors ] = useState({
        id: "",
    })

    // 2. 함수
    // INPUT 초기화
    useEffect(() => {
        setForm({
            id: "",
            pw: ""
        })
        setErrors({})
    }, [mode])

    // useEffect는 랜더링 시에 실행되고, [id] 내부의 값 변화를 감지하는 함수이다.
    useEffect(() => {
        const timer = setTimeout(() => {
            setDebounceId(form.id);
        }, 500);

        return () => clearTimeout(timer);
    }, [form.id])
    
    // 중복실행 체크
    useEffect(() => {
        if(!debounceId) return;
        if (debounceId.length < 3) return;
        if (mode !== "signup") return;

        const checkDuplicate = async () => {
            try {
                const result = await api.get("/auth/duplicate-id", {
                    params: { id: form.id }
                })


                if(result.data.exists) {
                    setErrors(prev => ({
                        ...prev,
                        id: "이미 사용중인 아이디입니다."
                    }));
                } else {
                    setErrors(prev => ({
                        ...prev,
                        id: "사용 가능한 아이디입니다."
                    }))
                }

                console.log("중복 체크 실행: ", debounceId);
                console.log("중복 체크 실행: ", result);
            } catch (e) {
                console.error(e);
            }
        }
        
        checkDuplicate();

    }, [debounceId]);

    const handleSubmit = () => {
        mode === "login" ? login() : signUp();
    }

    // 로그인
    const login = async () => {
        
        try {
            if(!form.id || !form.pw) {
                alert('아이디와 비밀번호를 입력하세요.')
                return;
            }

            const result = await api.post("/auth/login", {
                id: form.id,
                pw: form.pw
            })

            setAccessToken(result.data.accessToken);
            setUser({ userId: form.id });

            onClose();

        } catch(e) {
            const message =
                e.response?.data?.message || "로그인 실패";

            alert(message);
        }
        
    }

    // 회원가입
    const signUp = async () => {
        
        try {

            if(!form.id || !form.pw) {
                alert('아이디와 비밀번호를 입력하세요.')
                return;
            }

            if (errors.id.includes("사용중")) {
                alert("중복된 아이디입니다.");
                return;
            }

            const result = await api.post("/auth/signup", {
                id: form.id,
                pw: form.pw
            })

            // 3. 성공 처리
            if (result.data.success) {
                alert("회원가입 완료");

                // 로그인 화면으로 전환
                setMode("login");

                // 입력값 초기화
                setForm({
                    id: "",
                    pw: ""
                });

                setErrors({ id: "" });
            } 
        } catch (e) {
            console.error(e);
            alert("회원가입 실패");
        }
        
    }

    return ReactDOM.createPortal(
        <div className="popup-overlay">
            <div className="popup">
                <h2>{mode === "login" ? "로그인" : "회원가입"}</h2>

                <input 
                    name="userId"
                    className="popup-input" 
                    placeholder="아이디"
                    value={form.id} 
                    onChange={ (e) => 
                        setForm({
                            ...form,
                            id: e.target.value
                        })
                    }
                />

                {errors.id && (
                    <div className={`input-msg ${errors.id.includes("가능") ? "success" : "error"}`}>
                        {errors.id}
                    </div>
                )}

                <input 
                    name="upw"
                    className="popup-input" 
                    type="password" 
                    placeholder="비밀번호"
                    value={form.pw}
                    onChange={ (e) => 
                        setForm({
                            ...form,
                            pw: e.target.value
                        })
                    }
                />

                <div className="popup-btn-wrap">
                <button type="button" className="popup-btn login" onClick={handleSubmit}>
                    {mode === "login" ? "로그인" : "회원가입"}
                </button>
                <button className="popup-btn close" onClick={onClose}>
                    닫기
                </button>
                </div>

                <div className="popup-switch">
                {mode === "login" ? (
                    <span onClick={() => setMode("signup")}>
                    회원가입 하러가기
                    </span>
                ) : (
                    <span onClick={() => setMode("login")}>
                    로그인 하러가기
                    </span>
                )}
                </div>

            </div>
        </div>,
        document.body
    );
}