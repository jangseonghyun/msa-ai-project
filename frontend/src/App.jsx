import { useState, useRef } from "react";
import Sidebar from './components/Sidebar'
import Topbar from './components/Topbar'
import StatsRow from './components/StatsRow'
import DocumentRegisterCard from './components/DocumentRegisterCard'
import RecentTableCard from './components/RecentTableCard'
import LoginPopup from "./components/LoginPopup"
import { AuthProvider } from "./context/AuthContext"
import { AlertProvider } from "./context/CustomAlert";
import "./api/interceptor";

export default function App() {

  const [isLoginOpen, setIsLoginOpen] = useState(false);

  const recentTableRef = useRef(null)

  return (
      <AuthProvider>

        <AlertProvider>

          <div className="appShell">

            <Sidebar />

            <div className="appMain">

              <Topbar onLoginClick={() => setIsLoginOpen(true)} />

              <main className="dashboardPage">

                <StatsRow />

                <div className="contentGrid">

                  <div className="contentGrid__left">

                    <RecentTableCard ref={recentTableRef} />

                  </div>

                  <DocumentRegisterCard
                      onUploadSuccess={() => {
                        recentTableRef.current?.fetchDocs(1)
                      }}
                  />

                </div>

              </main>

            </div>

            {isLoginOpen && (
                <LoginPopup onClose={() => setIsLoginOpen(false)} />
            )}

          </div>

        </AlertProvider>

      </AuthProvider>
  )
}