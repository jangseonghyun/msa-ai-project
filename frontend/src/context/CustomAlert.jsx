import { createContext, useContext, useState } from "react";
import '../styles/customAlert.css'

const AlertContext = createContext(null);

export function AlertProvider({ children }) {
    const [alert, setAlert] = useState({
        open: false,
        message: "",
    });

    const showAlert = (message) => {
        setAlert({ open: true, message });
    };

    const closeAlert = () => {
        setAlert({ ...alert, open: false });
    };

    return (
        <AlertContext.Provider value={{ showAlert }}>
            {children}
            {alert.open && (
                <div className="custom-alert">
                    <div className="alert-box">
                        <p>{alert.message}</p>
                        <button onClick={closeAlert}>확인</button>
                    </div>
                </div>
            )}
        </AlertContext.Provider>
    );
}

export function useAlert() {
    return useContext(AlertContext);
}