import { menuItems } from '../data/dashboardData'

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand__logo">1</div>
        <div className="brand__title">AI Document Search</div>
      </div>

      <nav className="menu">
        {menuItems.map((item) => (
          <button key={item.key} className={`menu__item ${item.active ? 'menu__item--active' : ''}`}>
            <span className="menu__icon">{item.icon}</span>
            <span className="menu__label">{item.label}</span>
            {item.hasArrow && <span className="menu__arrow">›</span>}
          </button>
        ))}
      </nav>
    </aside>
  )
}
