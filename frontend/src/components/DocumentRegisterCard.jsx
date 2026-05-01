import { useState } from "react"
import { getAccessToken } from '../api/token'
import { useAlert } from "../context/CustomAlert"
import api from "../api/interceptor";

export default function DocumentRegisterCard() {

    const { showAlert } = useAlert();

    const [title, setTitle] = useState("")
    const [file, setFile] = useState(null)

    const handleFileChange = (e) => {
        if (!e.target.files) return
        setFile(e.target.files[0])
    }

    const handleSubmit = async () => {
        const token = getAccessToken();

        if (!token) {
            showAlert("로그인이 필요합니다.")
            return
        }

        if (!file) {
            showAlert("파일을 선택해주세요.")
            return
        }

        const allowedExtensions = ["pdf", "docx", "txt"]
        const ext = file.name.split(".").pop()?.toLowerCase()

        if (!ext || !allowedExtensions.includes(ext)) {
            showAlert("PDF, DOCX, TXT 파일만 업로드 가능합니다.")
            return
        }

        const formData = new FormData()
        formData.append("title", title)
        formData.append("file", file)

        try {
            await api.post("/doc/upload", formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })

            showAlert("업로드 완료")
            setTitle("")
            setFile(null)
        } catch (e) {
            console.error(e)
            showAlert("업로드 실패")
        }
    }

    return (
        <aside className="panel panel--register">
            <div className="panel__header panel__header--space">
                <h3>문서 등록</h3>
                <button className="panel__more">•••</button>
            </div>

            <div className="registerForm">
                <input
                    className="field"
                    placeholder="문서 제목"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />

                <div className="selectField">
                    <span>카테고리</span>
                    <span>▾</span>
                </div>

                <label className="uploadBox">
                    <input type="file" hidden onChange={handleFileChange} />
                    <span>{file ? file.name : "파일을 드래그하거나 클릭하여 업로드"}</span>
                </label>

                <button className="primaryButton" onClick={handleSubmit}>
                    등록
                </button>
            </div>
        </aside>
    )
}