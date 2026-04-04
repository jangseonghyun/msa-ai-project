export default function DocumentRegisterCard() {
  return (
    <aside className="panel panel--register">
      <div className="panel__header panel__header--space">
        <h3>문서 등록</h3>
        <button className="panel__more">•••</button>
      </div>

      <div className="registerForm">
        <input className="field" placeholder="문서 제목" />

        <div className="selectField">
          <span>카테고리</span>
          <span>▾</span>
        </div>

        <textarea className="field field--textarea" placeholder="메모" />

        <label className="uploadBox">
          <input type="file" hidden />
          <span>파일을 드래그하거나 클릭하여 업로드</span>
        </label>

        <button className="primaryButton">등록</button>
      </div>
    </aside>
  )
}
