import '../styles/recentTable.css'
import { recentTable } from '../data/dashboardData'
import { useState, useEffect } from "react";
import api from "../api/api";

const PAGE_SIZE = 5;

export default function RecentTableCard() {

    // 문서 조회
    const [docList, setDocList] = useState([]);
    // 페이징
    const [page, setPage] = useState(1)
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        fetchDocs(page)
    }, [page])

    const fetchDocs = async (page) => {
        try {
            const res = await api.get("/doc/list", {
                params: {
                    page: page - 1,
                    size: PAGE_SIZE
                }
            })

            setDocList(res.data.content)
            setTotalPages(res.data.totalPages);
        } catch(e) {
            console.error(e)
        }

    }

  return (
    <section className="panel panel--table">
      <div className="panel__header panel__header--space">
        <h3>최근 등록 문서</h3>
        <button className="panel__more">•••</button>
      </div>

      <div className="tableWrap">
        <div className="tableHead">
          <div>제목</div>
          <div>상태</div>
          <div>등록일</div>
        </div>

        {docList.map((row) => (
          <div key={row.id} className="tableRow">
            <div className="tableTitleCell">
              <span className="tableFileIcon">▤</span>
              <div>
                <div className="tableTitle">{row.title}</div>
                <div className="tableSub">{row.fileSize}KB</div>
              </div>
            </div>
            <div>{row.status}</div>
            <div className="tableDate">{row.createdAt}</div>
          </div>
        ))}
      </div>

        <div className="pagination">
            <button
                disabled={page === 1}
                onClick={() => setPage(page - 1)}
            >
                이전
            </button>

            <span>{page} / {totalPages}</span>

            <button
                disabled={page === totalPages}
                onClick={() => setPage(page + 1)}
            >
                다음
            </button>
        </div>
    </section>
  )
}
