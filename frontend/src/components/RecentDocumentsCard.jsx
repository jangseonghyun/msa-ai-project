import { recentDocuments } from '../data/dashboardData'

export default function RecentDocumentsCard() {
  return (
    <section className="panel panel--documents">
      <div className="panel__header">
        <h3>최근 등록 문서</h3>
      </div>

      <div className="docList">
        {recentDocuments.map((doc) => (
          <div key={doc.title} className="docList__item">
            <div className="docList__icon">▤</div>
            <div className="docList__body">
              <div className="docList__title">{doc.title}</div>
              <div className="docList__meta">
                <span>{doc.date}</span>
                <span>•</span>
                <span>{doc.size}</span>
                <span>•</span>
                <span>{doc.category}</span>
              </div>
            </div>
            <button className="docList__arrow">›</button>
          </div>
        ))}
      </div>
    </section>
  )
}
