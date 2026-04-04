import { useState } from "react";
import Sidebar from './components/Sidebar'
import Topbar from './components/Topbar'
import StatsRow from './components/StatsRow'
import RecentDocumentsCard from './components/RecentDocumentsCard'
import DocumentRegisterCard from './components/DocumentRegisterCard'
import RecentTableCard from './components/RecentTableCard'
import LoginPopup from "./components/LoginPopup"
import { AuthProvider } from "./context/AuthContext"
import "./api/interceptor";

export default function App() {
  const [isLoginOpen, setIsLoginOpen] = useState(false);

  return (
    <AuthProvider>
      <div className="appShell">
        <Sidebar />

        <div className="appMain">
          <Topbar onLoginClick={() => setIsLoginOpen(true)} />

          <main className="dashboardPage">
            <div className="pageHeader">
              <h1>Dashboard</h1>
            </div>

            <StatsRow />

            <div className="contentGrid">
              <div className="contentGrid__left">
                <RecentDocumentsCard />
                <RecentTableCard />
              </div>

              <DocumentRegisterCard />
            </div>
          </main>
        </div>
        
        {isLoginOpen && (
          <LoginPopup onClose={() => setIsLoginOpen(false)} />
        )}
      </div>
    </AuthProvider>
  )
}
