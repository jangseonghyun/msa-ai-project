import { stats } from '../data/dashboardData'

export default function StatsRow() {
  return (
    <section className="statsRow">
      {stats.map((item) => (
        <article key={item.label} className={`statCard statCard--${item.tone}`}>
          <div className="statCard__icon">{item.icon}</div>
          <div>
            <div className="statCard__label">{item.label}</div>
            <div className="statCard__value">{item.value}</div>
          </div>
        </article>
      ))}
    </section>
  )
}
