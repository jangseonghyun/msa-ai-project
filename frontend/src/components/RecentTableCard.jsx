import { recentTable } from '../data/dashboardData'

export default function RecentTableCard() {
  return (
    <section className="panel panel--table">
      <div className="panel__header panel__header--space">
        <h3>최근 등록 문서</h3>
        <button className="panel__more">•••</button>
      </div>

      <div className="tableWrap">
        <div className="tableHead">
          <div>제목</div>
          <div>카테고리</div>
          <div>상태</div>
          <div>등록일</div>
        </div>

        {recentTable.map((row) => (
          <div key={row.title} className="tableRow">
            <div className="tableTitleCell">
              <span className="tableFileIcon">▤</span>
              <div>
                <div className="tableTitle">{row.title}</div>
                <div className="tableSub">2024-11-29 ・ 4.7MB ・ 고객지원</div>
              </div>
            </div>
            <div><span className="badge">{row.category}</span></div>
            <div>{row.status}</div>
            <div className="tableDate">{row.owner}</div>
          </div>
        ))}
      </div>
    </section>
  )
}
